package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class NString extends NAtom
{
	private final String value;

	@Override
	public String toString()
	{
		return "(string %s)".formatted(value);
	}
}
