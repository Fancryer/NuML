package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class NOp extends NExp
{
	private final String op;
	private final NExp left, right;

	@Override
	public String toString()
	{
		return "(op %s %s %s)".formatted(op,left,right);
	}
}
