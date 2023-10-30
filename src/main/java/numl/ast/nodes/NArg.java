package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class NArg extends Node
{
	private final String name;
	private final NType type;

	@Override
	public String toString()
	{
		return "(arg %s %s)".formatted(name,type);
	}
}
