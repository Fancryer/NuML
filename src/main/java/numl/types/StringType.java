package numl.types;

public class StringType extends Type
{
	private final static StringType instance=new StringType();

	public static StringType of()
	{
		return instance;
	}
	private StringType()
	{
		super("String");
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof StringType;
	}
}
