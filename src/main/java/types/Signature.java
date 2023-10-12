package types;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Signature
{
	@Getter
	private final List<Type> args;

	public Signature(List<Type> args)
	{
		assert args.size()>0;
		this.args=args;
	}

	public Signature(List<Type> args,Type returnType)
	{
		var argList=new ArrayList<>(args);
		argList.add(returnType);
		this.args=argList;
	}

	public static Signature empty()
	{
		return new Signature(List.of(NilType.of()));
	}

	private static String format(Type type)
	{
		return switch(type)
		{
			case TupleType tupleType -> "(%s)".formatted(tupleType);
			case FunctionType functionType -> "(%s)".formatted(functionType);
			default -> type.toString();
		};
	}

	@Override
	public String toString()
	{
		return args.stream().map(Signature::format).collect(Collectors.joining("->"));
	}

	public Type argAt(int i)
	{
		return args.get(i);
	}

	public Type first()
	{
		return args.getFirst();
	}

	public Type last()
	{
		return args.getLast();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Signature sig
		       &&sig.args.size()==args.size()
		       &&IntStream.range(0,args.size()).allMatch(i->args.get(i).equals(sig.args.get(i)));
	}
}
