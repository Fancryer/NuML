package types;

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
		var signature=new Signature(List.of(this,this,this));
		for(var s: "+-*/".toCharArray())
		{
			operators.put(String.valueOf(s),signature);
		}
		for(var op: List.of("==","<",">","<>",">=","<="))
		{
			operators.put(op,new Signature(List.of(this,this,BoolType.of())));
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof IntType;
	}
}