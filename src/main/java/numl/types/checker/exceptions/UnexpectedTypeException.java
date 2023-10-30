package numl.types.checker.exceptions;

import numl.ast.Position;
import numl.types.Type;

import java.util.Objects;

public class UnexpectedTypeException extends InferException
{
	private final Type left, right;
	private final Position position;
	private final String detailedMessage;

	public UnexpectedTypeException(final Type left,final Type right,final Position position)
	{
		this(
				left,
				right,
				position,
				""
		    );
	}

	public UnexpectedTypeException(final Type left,final Type right,final Position position,final String detailedMessage)
	{
		this.left=left;
		this.right=right;
		this.position=position;
		this.detailedMessage=detailedMessage;
	}

	@Override
	public String getMessage()
	{
		return "Expected %s but got %s at lines %s, columns %s%s".formatted(left,right,position.getLines(),position.getColumns(),getDetails());
	}

	public String getDetails()
	{
		return Objects.equals(detailedMessage,"")
		       ?""
		       :"%nDetails: %s".formatted(detailedMessage);
	}
}
