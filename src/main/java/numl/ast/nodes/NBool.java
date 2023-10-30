package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class NBool extends NAtom
{
	private final boolean value;

	@Override
	public String toString()
	{
		return "(bool %s)".formatted(value);
	}
}
