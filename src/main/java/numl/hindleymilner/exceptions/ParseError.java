package numl.hindleymilner.exceptions;

/**
 * Raised if the type environment supplied for is incomplete
 */
public class ParseError extends RuntimeException
{
	public ParseError(String message)
	{
		super(message);
	}
}