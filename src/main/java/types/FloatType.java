package types;

import java.util.List;

public class FloatType extends Type
{
	private final static FloatType instance=new FloatType();

	public static FloatType of()
	{
		return instance;
	}

	private FloatType()
	{
		super("Float");
		var signature=new Signature(List.of(this,this,this));
		for(var s: "+-*/".toCharArray())
		{
			operators.put(String.valueOf(s),signature);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof FloatType;
	}
}
