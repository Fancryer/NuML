package types;

public class MetaVariable extends Type
{
	private Type t;

	public MetaVariable()
	{
		super("?");
		t=null;
	}

	public static MetaVariable of()
	{
		return new MetaVariable();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof MetaVariable m)) return false;
		if(t!=null) return t.equals(m);
		t=m;
		return true;
	}

    @Override
    public String toString()
    {
        return "Meta";
    }
}
