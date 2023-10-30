package numl.types.checker.exceptions;

public class UnknownTypeException extends RuntimeException
{
	public UnknownTypeException(String ctx)
	{
		super("Cannot infer type of %s".formatted(ctx));
	}
}
