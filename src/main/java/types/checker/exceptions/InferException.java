package types.checker.exceptions;

public class InferException extends RuntimeException
{
	public InferException(String message)
	{
		super(message);
	}

	public InferException()
	{
		super();
	}
}
