package numl.hindleymilner.exceptions;

/**
 * Raised if the type inference algorithm cannot infer types successfully
 */
public class InferenceError extends RuntimeException
{
	public InferenceError(String message)
	{
		super(message);
	}
}