package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class NBind extends NStat
{
	private final String name;
	private final NExp exp;

	@Override
	public String toString()
	{
		return "(bind %s %s)".formatted(name,exp);
	}
}
