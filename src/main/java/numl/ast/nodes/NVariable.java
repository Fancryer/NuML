package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class NVariable extends NExp
{
	private final String name;

	@Override
	public String toString()
	{
		return "(var %s)".formatted(name);
	}
}
