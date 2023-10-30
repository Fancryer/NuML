package numl.types;

import numl.ast.nodes.NCompileUnit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.stream.IntStream;

public class NuMLChecker
{
	public SequencedMap<TVar,Type> bindings=new LinkedHashMap<>();

	public SequencedMap<TVar,Type> check(NCompileUnit compileUnit)
	{
		return bindings;
	}

	public final Type semiprune(Type type)
	{
		SequencedMap<TVar,Type> prunedBindings=new LinkedHashMap<>();
		type=semipruneRecursive(type,prunedBindings);
		return type;
	}

	private Type semipruneRecursive(Type type,SequencedMap<TVar,Type> prunedBindings)
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
			bindings.put(tVar1,tVar2);
		else if(t1 instanceof TVar tVar1&&subst(t2,t1))
			bindings.put(tVar1,t2);
		else if(t2 instanceof TVar tVar2&&subst(t1,t2))
			bindings.put(tVar2,t1);
		else if(compareStructures(t1,t2))
		{
			List<Type>
					unwrappedT1=unwrap(t1),
					unwrappedT2=unwrap(t2);
			IntStream.range(0,unwrappedT1.size())
					.forEach(i->unify(unwrappedT1.get(i),unwrappedT2.get(i)));
		}
	}

	//Checks if full substitution of t1 contains
	public boolean subst(Type t1,Type t2)
	{
		return unwrap(t1).contains(t2);
	}

	public List<Type> unwrap(Type type)
	{
		return switch(type)
		{
			case TVar tVar -> List.of(tVar);
			case TConst tConst -> List.of(tConst);
			case TApply tApply -> tApply.args;
		};
	}

	public boolean compareStructures(Type t1,Type t2)
	{
		List<Type>
				unwrappedT1=unwrap(t1),
				unwrappedT2=unwrap(t2);
		return unwrappedT1.size()==unwrappedT2.size()
		       &&IntStream.range(0,unwrappedT1.size())
		                  .allMatch(i->unwrappedT1.get(i).equals(unwrappedT2.get(i)));
	}

	private sealed interface Type permits TVar, TConst, TApply{}

	private record TVar() implements Type{}

	private record TConst(String name) implements Type{}

	private record TApply(List<Type> args) implements Type{}
}
