package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class NBranch extends NExp
{
	private final NExp pred, then, else_;
}
