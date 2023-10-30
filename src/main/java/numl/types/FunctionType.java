package numl.types;

import lombok.Getter;
import numl.types.checker.Environment;

import java.util.List;

public class FunctionType extends Type
{
	@Getter
	private final Signature signature;
	private final Environment environment;

	public FunctionType(final Signature signature,final Environment environment)
	{
		super("Function");
		this.signature=signature;
		this.environment=environment;
	}

	public FunctionType(final List<Type> args,final Type returnType,final Environment environment)
	{
		this(new Signature(args,returnType),environment);
	}

	public FunctionType(final List<Type> types,final Environment environment)
	{
		this(new Signature(types),environment);
	}

	@Override
	public String toString()
	{
		return signature.toString();
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof FunctionType f
		       &&f.getSignature().equals(signature);
	}
}
