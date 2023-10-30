package numl.types.checker.exceptions;

import numl.ast.Position;

public class DuplicateNameException extends InferException
{
	public DuplicateNameException(final String name,Position position)
	{
		super("Duplicate name: %s at lines [%d-%d], columns [%d-%d]".formatted(name,
		                                                                       position.getLineStart(),
		                                                                       position.getLineEnd(),
		                                                                       position.getColumnStart(),
		                                                                       position.getColumnEnd()));
	}
}
