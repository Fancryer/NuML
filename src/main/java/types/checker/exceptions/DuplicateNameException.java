package types.checker.exceptions;

public class DuplicateNameException extends InferException
{
	public DuplicateNameException(String name,int line,int column)
	{
		super("Duplicate name: %s at line %d, column %d".formatted(name,line,column));
	}
}
