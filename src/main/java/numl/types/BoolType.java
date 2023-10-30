package numl.types;

import java.util.List;

public class BoolType extends Type
{
	private final static BoolType instance=new BoolType();
	public BoolType()
	{
		super("Bool");
		for(final var op: List.of("==","<",">","<>",">=","<="))
		{
			operators.put(op,new Signature(List.of(this,this,this)));
		}
	}

	public static BoolType of()
	{
		return instance;
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof BoolType;
	}
}
