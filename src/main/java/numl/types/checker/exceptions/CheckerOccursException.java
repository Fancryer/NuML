package numl.types.checker.exceptions;

import static numl.types.NuMLChecker.Type;

public class CheckerOccursException extends Exception
{
	public CheckerOccursException(final Type left,final Type right)
	{
		super("Occurs check failed for %s and %s".formatted(left,right));
	}

}
