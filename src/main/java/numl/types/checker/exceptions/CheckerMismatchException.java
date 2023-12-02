package numl.types.checker.exceptions;

import static numl.types.NuMLChecker.Type;

public class CheckerMismatchException extends Exception
{
	public CheckerMismatchException(final Type left,final Type right)
	{
		super("Types mismatch: %s and %s".formatted(left,right));
	}
}
