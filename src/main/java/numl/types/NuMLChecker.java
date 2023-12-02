package numl.types;

import lombok.Getter;
import numl.ast.nodes.*;
import numl.common.Pair;
import numl.common.Zipper;
import numl.types.checker.exceptions.CheckerMismatchException;
import numl.types.checker.exceptions.CheckerOccursException;
import numl.types.checker.exceptions.DuplicateNameException;

import java.util.*;
import java.util.function.Supplier;

@Getter
public class NuMLChecker implements AutoCloseable
{
	public final LinkedHashMap<TVar,Type> bindings;
	public final HashMap<Node,Type> nodeTypeMap;

	public NuMLChecker(NCompileUnit compileUnit)
	{
		bindings=new LinkedHashMap<>();
		nodeTypeMap=new LinkedHashMap<>();
		check(compileUnit);
	}

	public TApply getTuple(Type a,Type b)
	{
		return new TApply(new TApply(new TConst("Pair"),a),b);
	}

	public void check(NCompileUnit compileUnit)
	{
		visitModule(compileUnit.getModules().get(0));
	}

	private void visitModule(NModule module)
	{
		module.getStats().forEach(this::visitStat);
	}

	private void visitStat(NStat stat)
	{
		if(stat instanceof NBind bind){visitBind(bind);}
		else{visitDecl((NDecl)stat);}
	}

	private void visitDecl(NDecl decl)
	{
		switch(decl)
		{
			case NVarDecl var -> visitVarDecl(var);
			case null,default -> visitFunctDecl((NFunctDecl)decl);
		}
	}

	private Type visitFunctDecl(NFunctDecl decl)
	{
		var functType=new TConst("Function");
		nodeTypeMap.put(decl,functType);
		return functType;
	}

	private Type visitVarDecl(NVarDecl var)
	{
		var bind=var.getBind();
		if(isThereDuplicateBinding(var)) throw new DuplicateNameException(bind.getName(),var.getPosition());
		var type=visitExp(bind.getExp());
		var tVar=new TVar(bind.getName());
		bindings.put(tVar,type);
		nodeTypeMap.put(var,tVar);
		return tVar;
	}

	private boolean isThereDuplicateBinding(NVarDecl var)
	{
		for(TVar(String name): bindings.keySet())
				if(name.equals(var.getBind().getName())) return true;
		return false;
	}

	private TVar visitBind(NBind bind)
	{
		var newBinding=new TVar(bind.getName());
		bindings.put(newBinding,visitExp(bind.getExp()));
		return newBinding;
	}

	private Type visitExp(NExp exp)
	{
		//NAtom, NTuple, NCall, NBranch, NLambda, NOp, NVariable, NWhere, NArray
		return switch(exp)
		{
			case NAtom atom -> switch(atom)
			{
				case NNumber n -> n instanceof NInt
				                  ?new TConst("Int")
				                  :new TConst("Float");
				case NBool b -> new TConst("Bool");
				case NString s -> new TConst("String");
				case NNil n -> new TConst("Nil");
			};
			case NCall call ->
			{
				var binding=bindings.keySet()
				                    .stream()
				                    .filter(key->key.name.equals(call.getName()))
				                    .findAny();
				if(binding.isEmpty()) throw new RuntimeException("Function named '%s' is not found!".formatted(call.getName()));
				yield binding.get();
			}
			case NBranch branch ->
			{
				var predType=visitExp(branch.getPred());
				try
				{
					unify(predType,new TConst("Boolean"));
				}
				catch(Exception e)
				{
					throw new RuntimeException("Pred type must me Bool, but found '%s'!".formatted(predType.toString()));
				}
				Type
						thenType=visitExp(branch.getThen()),
						elseType=visitExp(branch.getElse_());
				if(compareStructures(thenType,elseType)) yield thenType;
				throw new RuntimeException("Branch types mismatch: '%s' and '%s' don't seem alike!".formatted(thenType,elseType));
			}
			case NLambda lambda ->
			{
				var argList=lambda.getArgs()
				                  .stream()
				                  .map(NArg::getType)
				                  .map(this::visitType)
				                  .toList();
				var args=new ArrayList<>(argList);
				args.add(visitExp(lambda.getExp()));
				/*
				TODO:
				 - wrap args in TApply f
				 - yield f
				*/
				yield null;
			}
			default -> throw new IllegalStateException("Unexpected value: "+exp);
		};
	}

	//TODO
	private Type visitType(NType typeNode)
	{
		return null;
	}

	private final Type semiprune(Type type)
	{
		LinkedHashMap<TVar,Type> prunedBindings=new LinkedHashMap<>();
		type=semipruneRecursive(type,prunedBindings);
		return type;
	}

	private Type semipruneRecursive(Type type,LinkedHashMap<TVar,Type> prunedBindings)
	{
		if(!(type instanceof TVar tVar))
			return type;
		if(prunedBindings.containsKey(type))
			return prunedBindings.get(type);
		if(!bindings.containsKey(tVar))
			return tVar;
		Type boundType=semipruneRecursive(bindings.get(tVar),prunedBindings);
		bindings.put(tVar,boundType);
		prunedBindings.put(tVar,boundType);
		return boundType;
	}

	private final void unify(Type t1,Type t2) throws CheckerOccursException, CheckerMismatchException
	{
		t1=semiprune(t1);
		t2=semiprune(t2);
		if(t1 instanceof TVar tVar1&&t2 instanceof TVar tVar2)
			bindings.put(tVar1,tVar2);
		else if(t1 instanceof TVar tVar1)
		{
			if(!subst(t2,t1))
				throw new CheckerOccursException(t1,t2);
			bindings.put(tVar1,t2);
		}
		else if(t2 instanceof TVar tVar2)
		{
			if(!subst(t1,t2))
				throw new CheckerOccursException(t1,t2);
			bindings.put(tVar2,t1);
		}
		else if(!compareStructures(t1,t2))
			throw new CheckerMismatchException(t1,t2);
		if(t1.equals(t2)) return;
		List<Type>
				unwrappedT1=unwrap(t1),
				unwrappedT2=unwrap(t2);
		var bound=unwrappedT1.size();
		for(int i=0;i<bound;++i)
			unify(unwrappedT1.get(i),unwrappedT2.get(i));
	}

	//Checks if full substitution of t1 contains t2
	private boolean subst(Type t1,Type t2)
	{
		return unwrap(t1).contains(t2);
	}

	/**

	 */
	private List<Type> unwrap(Type type)
	{
		return switch(type)
		{
			case TVar tVar -> List.of(bindings.getOrDefault(tVar,type));
			case TConst tConst -> List.of(tConst);
			case TApply tApply ->
			{
				var list=new ArrayList<Type>();
				list.add(tApply.funct);
				list.addAll(unwrap(tApply.arg));
				yield list;
			}
		};
	}

	private boolean compareStructures(Type t1,Type t2)
	{
		List<Type>
				unwrappedT1=unwrap(t1),
				unwrappedT2=unwrap(t2);
		return unwrappedT1.equals(unwrappedT2)
		       ||new Zipper<Type,Type>().zip(unwrappedT1,unwrappedT2)
		                                .allMatch(Pair::isSymmetric);
	}

	@Override
	public void close() throws Exception
	{
		bindings.clear();
	}

	public sealed interface Type permits TVar, TConst, TApply{}

	public record TVar(String name) implements Type
	{
		public static TVar of(String name)
		{
			return new TVar(name);
		}

		//		@Override
		//		public String toString()
		//		{
		//			return "V$"+name;
		//		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	public record TConst(String name,Supplier<String> asString) implements Type
	{
		public TConst(String name)
		{
			this(name,()->name);
		}

		@Override
		public String toString()
		{
			return asString.get();
		}
	}

	public record TApply(Type funct,Type arg,Supplier<String> asString) implements Type
	{
		public TApply(Type funct,Type arg)
		{
			this(funct,arg,()->"(%s %s)".formatted(funct,arg));
		}

		@Override
		public String toString()
		{
			return asString.get();
		}
	}
}
