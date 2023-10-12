package types;

public class NilType extends Type
{
	private final static NilType instance=new NilType();

	public static NilType of()
	{
		return instance;
	}

	private NilType()
	{
		super("Nil");
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof NilType;
	}
}
