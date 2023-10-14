package types.checker.exceptions;

import common.Pair;
import org.antlr.v4.runtime.Token;
import types.Type;

import java.util.Objects;

public class UnexpectedTypeException extends InferException
{
	private final Type left, right;
	private final Pair<Integer,Integer> line, column;
	private final String detailedMessage;

	public UnexpectedTypeException(Type left,Type right,int line,int column,String detailedMessage)
	{
		this(
				left,
				right,
				line,line,
				column,column,
				detailedMessage
		    );
	}

	public UnexpectedTypeException(Type left,Type right,int lineStart,int lineStop,int columnStart,int columnStop,String detailedMessage)
	{
		this.left=left;
		this.right=right;
		this.line=new Pair<>(lineStart,lineStop);
		this.column=new Pair<>(columnStart,columnStop);
		this.detailedMessage=detailedMessage;
	}

	public UnexpectedTypeException(Type left,Type right,int lineStart,int lineStop,int columnStart,int columnStop)
	{
		this(
				left,
				right,
				lineStart,lineStop,
				columnStart,columnStop,
				""
		    );
	}

	public UnexpectedTypeException(Type left,Type right,Token start,Token stop)
	{
		this(left,right,start,stop,"");
	}

	public UnexpectedTypeException(Type left,Type right,Token start,Token stop,String detailedMessage)
	{
		this(
				left,
				right,
				start.getLine(),stop.getLine(),
				start.getCharPositionInLine(),stop.getCharPositionInLine(),
				detailedMessage
		    );
	}

	public UnexpectedTypeException(Type left,Type right,int line,int column)
	{
		this(left,right,line,column,"");
	}

	@Override
	public String getMessage()
	{
		return "Expected %s but got %s at lines %s, columns %s%s".formatted(left,right,line,column,getDetails());
	}

	public String getDetails()
	{
		return Objects.equals(detailedMessage,"")
		       ?""
		       :"%nDetails: %s".formatted(detailedMessage);
	}
}
