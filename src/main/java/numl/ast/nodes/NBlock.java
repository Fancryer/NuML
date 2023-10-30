package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public final class NBlock extends Node
{
	private final List<NStat> stats;
	private final NExp exp;
}
