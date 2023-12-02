package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import numl.ast.Position;

@SuperBuilder
@Getter
public final class NArg extends Node
{
	private final Position position;
	private final String name;
	private final NType type;

	@Override
	public String toString()
	{
		return "(arg %s %s)".formatted(name,type);
	}
}
