package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import numl.ast.Position;

import java.util.List;

@SuperBuilder
@Getter
public final class NBlock extends Node
{
	private final Position position;
	private final List<NStat> stats;
	private final NExp exp;

	//@Override
	public String toString()
	{
		return "(block (stats %s) (exp %s))".formatted(stats,exp);
	}
}
