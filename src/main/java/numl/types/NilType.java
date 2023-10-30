package numl.types;

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
	public boolean equals(final Object obj)
	{
		return obj instanceof NilType;
	}
}
