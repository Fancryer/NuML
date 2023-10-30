package numl.types;

import lombok.Getter;

public class MetaVariable extends Type
{
	@Getter
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
	public boolean equals(final Object obj)
	{
		if(obj instanceof MetaVariable) return false;
		if(t!=null) return t.equals(obj);
		t=(Type)obj;
		return true;
	}

    @Override
    public String toString()
    {
        return "Meta";
    }
}
