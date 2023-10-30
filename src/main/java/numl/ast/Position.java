package numl.ast;

import lombok.Getter;
import org.antlr.v4.runtime.Token;

@Getter
public class Position
{
	private final int lineStart, lineEnd, columnStart, columnEnd;

	private Position(int lineStart,int lineEnd,int columnStart,int columnEnd)
	{
		this.lineStart=lineStart;
		this.lineEnd=lineEnd;
		this.columnStart=columnStart;
		this.columnEnd=columnEnd;
	}

	public static Position of(Token start,Token end)
	{
		return new Position(start.getLine(),end.getLine(),start.getCharPositionInLine(),end.getCharPositionInLine());
	}

	public String getLines()
	{
		return "[%d-%d]".formatted(lineStart,lineEnd);
	}

	public String getColumns()
	{
		return "[%d-%d]".formatted(columnStart,columnEnd);
	}
}
