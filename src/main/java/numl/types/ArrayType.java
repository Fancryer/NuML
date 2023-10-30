package numl.types;

import lombok.Getter;

public class ArrayType extends Type
{
	@Getter
	private final Type elementType;

	public ArrayType(final Type elementType)
	{
		super("Array");
		this.elementType=elementType;
	}

	public static Type of(final Type elementType)
	{
		return new ArrayType(elementType);
	}

	@Override
	public String toString()
	{
		return "[%s]".formatted(elementType);
	}



	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof ArrayType tup&&tup.elementType.equals(elementType);
	}
}
