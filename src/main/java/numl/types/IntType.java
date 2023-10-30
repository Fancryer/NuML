package numl.types;

import java.util.List;

public class IntType extends Type
{
	private final static IntType instance=new IntType();
	public static IntType of()
	{
		return instance;
	}

	private IntType()
	{
		super("Int");
		final var signature=new Signature(List.of(this,this,this));
		for(final var s: "+-*/".toCharArray())
		{
			operators.put(String.valueOf(s),signature);
		}
		for(final var op: List.of("==","<",">","<>",">=","<="))
		{
			operators.put(op,new Signature(List.of(this,this,BoolType.of())));
		}
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof IntType;
	}
}