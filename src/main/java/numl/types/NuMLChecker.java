package numl.types;

import numl.ast.nodes.*;
import numl.common.Pair;
import numl.common.Zipper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.IntStream;

public class NuMLChecker
{
	public final LinkedHashMap<TVar,Type> bindings;

	private NuMLChecker()
	{
		bindings=new LinkedHashMap<>();
	}

	public TApply getTuple(Type a,Type b)
	{
		return TApply.of(TApply.of(TConst.of("Pair"),a),b);
	}

	public LinkedHashMap<TVar,Type> check(NCompileUnit compileUnit)
	{
		var module=compileUnit.getModules().get(0);
		visitModule(module);
		return bindings;
	}

	private void visitModule(NModule module)
	{
		for(var stat: module.getStats())
		{
			visitStat(stat);
		}
	}

	private Type visitStat(NStat stat)
	{
		return stat instanceof NBind bind
		       ?visitBind(bind)
		       :visitDecl((NDecl)stat);
	}

	private Type visitDecl(NDecl decl)
	{
		return decl instanceof NVarDecl var
		       ?visitVarDecl(var)
		       :visitFunctDecl((NFunctDecl)decl);
	}

	private Type visitFunctDecl(NFunctDecl decl)
	{
		return null;
	}

	private Type visitVarDecl(NVarDecl var)
	{
		if(isThereDuplicateBinding(var))
		{
			throw new IllegalStateException("Duplicate binding: '%s'".formatted(var.getBind().getName()));
		}
		return null;
	}

	private boolean isThereDuplicateBinding(NVarDecl var)
	{
		for(TVar(String name): bindings.keySet())
				if(name.equals(var.getBind().getName())) return true;
		return false;
	}

	private Type visitBind(NBind bind)
	{
		return visitExp(bind.getExp());
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
					unify(predType,TConst.of("Boolean"));
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

	public final Type semiprune(Type type)
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

	public final void unify(Type t1,Type t2)
	{
		t1=semiprune(t1);
		t2=semiprune(t2);
		if(t1 instanceof TVar tVar1&&t2 instanceof TVar tVar2)
		{
			bindings.put(tVar1,tVar2);
		}
		else if(t1 instanceof TVar tVar1&&subst(t2,t1))
		{
			bindings.put(tVar1,t2);
		}
		else if(t2 instanceof TVar tVar2&&subst(t1,t2))
		{
			bindings.put(tVar2,t1);
		}
		else if(compareStructures(t1,t2))
		{
			List<Type>
					unwrappedT1=unwrap(t1),
					unwrappedT2=unwrap(t2);
			IntStream.range(0,unwrappedT1.size())
			                .forEach(i->unify(unwrappedT1.get(i),unwrappedT2.get(i)));
		}
		throw new RuntimeException("Types mismatch: %s and %s".formatted(t1,t2));
	}

	//Checks if full substitution of t1 contains t2
	public boolean subst(Type t1,Type t2)
	{
		return unwrap(t1).contains(t2);
	}

	/**

	 */
	public List<Type> unwrap(Type type)
	{
		return switch(type)
		{
			case TVar tVar -> List.of(bindings.getOrDefault(tVar,type));
			case TConst tConst -> List.of(tConst);
			case TApply tApply -> unwrap(tApply.arg);
		};
	}

	public boolean compareStructures(Type t1,Type t2)
	{
		List<Type>
				unwrappedT1=unwrap(t1),
				unwrappedT2=unwrap(t2);
		return unwrappedT1.size()!=unwrappedT2.size()
		       &&new Zipper<Type,Type>().zip(unwrappedT1,unwrappedT2)
		                                .stream()
		                                .allMatch(Pair::isSymmetric);
	}

	private sealed interface Type permits TVar, TConst, TApply{}

	private record TVar(String name) implements Type
	{
		public static TVar of(String name)
		{
			return new TVar(name);
		}
	}

	private record TConst(String name) implements Type
	{
		public static TConst of(String name)
		{
			return new TConst(name);
		}
	}

	private record TApply(Type funct,Type arg) implements Type
	{
		public static TApply of(Type funct,Type arg)
		{
			return new TApply(funct,arg);
		}
	}
}
