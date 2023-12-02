package numl.hindleymilner;

import numl.hindleymilner.classes.*;
import numl.hindleymilner.exceptions.InferenceError;
import numl.hindleymilner.exceptions.ParseError;
import numl.hindleymilner.types.Function;
import numl.hindleymilner.types.IntegerType;
import numl.hindleymilner.types.TypeOperator;
import numl.hindleymilner.types.TypeVariable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helper
{

	public static IType analyse(IType node,Map<String,IType> env,Set<TypeVariable> nonGeneric)
	{
		if(nonGeneric==null) nonGeneric=new HashSet<>();
		return switch(node)
		{
			case Identifier identifier -> getType(identifier.getName(),env,nonGeneric);
			case Apply apply ->
			{
				IType
						funType=analyse(apply.getFn(),env,nonGeneric),
						argType=analyse(apply.getArg(),env,nonGeneric);
				var resultType=new TypeVariable();
				unify(new Function(argType,resultType),funType);
				yield resultType;
			}
			case Lambda lambda ->
			{
				var argType=new TypeVariable();
				var newEnv=new HashMap<>(env);
				newEnv.put(lambda.getV(),argType);
				var newNonGeneric=new HashSet<>(nonGeneric);
				newNonGeneric.add(argType);
				var resultType=analyse(lambda.getBody(),newEnv,newNonGeneric);
				yield new Function(argType,resultType);
			}
			case Let let ->
			{
				var defnType=analyse(let.getDefn(),env,nonGeneric);
				var newEnv=new HashMap<>(env);
				newEnv.put(let.getV(),defnType);
				yield analyse(let.getBody(),newEnv,nonGeneric);
			}
			case Letrec letrec ->
			{
				var newType=new TypeVariable();
				var newEnv=new HashMap<>(env);
				newEnv.put(letrec.getV(),newType);
				var newNonGeneric=new HashSet<>(nonGeneric);
				newNonGeneric.add(newType);
				var defnType=analyse(letrec.getDefn(),newEnv,newNonGeneric);
				unify(newType,defnType);
				yield analyse(letrec.getBody(),newEnv,nonGeneric);
			}
			case null,default -> throw new AssertionError("Unhandled syntax node %s".formatted(node));
		};
	}

	public static IType analyse(IType node,Map<String,IType> env)
	{
		return analyse(node,env,null);
	}

	public static void tryExp(Map<String,IType> env,IType node)
	{
		System.out.printf("%s : ",node);
		try
		{
			var t=analyse(node,env);
			System.out.println(t);
		}
		catch(ParseError|InferenceError e)
		{
			e.printStackTrace();
		}
	}

	public static boolean isIntegerLiteral(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}

	public static IType prune(IType t)
	{
		return t instanceof TypeVariable tVar&&tVar.getInstance()!=null?prune(tVar.getInstance()):t;
	}

	public static boolean occursInType(TypeVariable v,IType type2)
	{
		var prunedType2=prune(type2);
		return prunedType2==v
		       ||prunedType2 instanceof TypeOperator tVar2
		         &&occursIn(v,new HashSet<>(tVar2.getTypes()));
	}

	public static boolean occursIn(TypeVariable t,Set<IType> types)
	{
		return types.stream().anyMatch(type->occursInType(t,type));
	}

	public static boolean isGeneric(TypeVariable v,Set<TypeVariable> nonGeneric)
	{
		return !occursIn(v,nonGeneric.stream().map(IType.class::cast).collect(Collectors.toSet()));
	}

	public static IType getType(String name,Map<String,IType> env,Set<TypeVariable> nonGeneric)
	{
		if(env.containsKey(name))
			return fresh(env.get(name),nonGeneric);
		if(isIntegerLiteral(name))
			return new IntegerType();
		throw new ParseError("Undefined symbol "+name);
	}

	public static IType fresh(IType t,Set<TypeVariable> nonGeneric)
	{
		return fresh(t,nonGeneric,new HashMap<>());
	}

	public static IType fresh(IType t,Set<TypeVariable> nonGeneric,Map<TypeVariable,TypeVariable> mappings)
	{
		var p=prune(t);
		return switch(p)
		{
			case TypeVariable pVar -> isGeneric(pVar,nonGeneric)?mappings.computeIfAbsent(pVar,i->new TypeVariable()):p;
			case TypeOperator pOp -> new TypeOperator(pOp.getName(),
			                                          pOp.getTypes()
			                                             .stream()
			                                             .map(x->fresh(x,nonGeneric,mappings))
			                                             .collect(Collectors.toList()));
			case null,default -> p;
		};
	}

	public static void unify(IType t1,IType t2)
	{
		var a=prune(t1);
		var b=prune(t2);
		switch(a)
		{
			case TypeVariable aVar when aVar!=b ->
			{
				if(occursInType(aVar,b))
					throw new InferenceError("recursive unification");
				aVar.setInstance(b);
			}
			case TypeOperator aOp when b instanceof TypeVariable bVar -> unify(bVar,aOp);
			case null,default -> {}
		}
		switch(a)
		{
			case TypeOperator aOp when b instanceof TypeOperator bOp ->
			{
				if(!aOp.getName().equals(bOp.getName())||aOp.getTypes().size()!=bOp.getTypes().size())
					throw new InferenceError("Type mismatch: %s != %s".formatted(a,b));
				IntStream.range(0,aOp.getTypes().size())
				         .forEach(i->unify(aOp.getTypes().get(i),bOp.getTypes().get(i)));
			}
			case null,default ->
			{
				assert false:"Not unified";
			}
		}
	}

	public static void main(String[] args)
	{

	}
}

