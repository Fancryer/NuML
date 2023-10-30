package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public final class NArray extends NExp
{
	private final List<NExp> exps;
}
