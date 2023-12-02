package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class NBranch extends NExp
{
	private final NExp pred, then, else_;

	@Override
	public String toString()
	{
		return "(branch (pred %s) (then %s) (else %s))".formatted(pred,then,else_);
	}
}
