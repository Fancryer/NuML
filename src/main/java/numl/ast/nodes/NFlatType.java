package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class NFlatType extends NType
{
	private final String name;

	@Override
	public String toString()
	{
		return "(flat_type %s)".formatted(name);
	}
}
