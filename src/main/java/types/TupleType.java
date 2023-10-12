package types;

import lombok.Getter;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

public class TupleType extends Type
{
	@Getter
	private final List<Type> elements;

	public TupleType(List<Type> elements)
	{
		super("Tuple");
		this.elements=elements;
	}

	public static Type of(List<Type> elements)
	{
		return new TupleType(elements);
	}

	public List<Type> elements()
	{
		return elements;
	}

	@Override
	public String toString()
	{
		return elements.stream().map(Type::toString).collect(joining(" * "));
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof TupleType tup
		       &&tup.elements.size()==elements.size()
		       &&IntStream.range(0,elements.size()).allMatch(i->elements.get(i).equals(tup.elements.get(i)));
	}
}
