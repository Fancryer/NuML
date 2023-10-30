package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public final class NWhere extends NExp
{
	private final NExp exp;
	private final List<NBind> binds;
}
