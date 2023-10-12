package types;

import lombok.Getter;
import types.checker.Environment;

import java.util.List;

public class FunctionType extends Type
{
	@Getter
	private final Signature signature;
	private final Environment environment;

	public FunctionType(Signature signature,Environment environment)
	{
		super("Function");
		this.signature=signature;
		this.environment=environment;
	}

	public FunctionType(List<Type> args,Type returnType,Environment environment)
	{
		this(new Signature(args,returnType),environment);
	}

	public FunctionType(List<Type> types,Environment environment)
	{
		this(new Signature(types),environment);
	}

	@Override
	public String toString()
	{
		return signature.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof FunctionType f
		       &&f.getSignature().equals(signature);
	}
}
