package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import numl.ast.Position;

@SuperBuilder
public abstract sealed class Node permits NArg, NBlock, NCompileUnit, NExp, NModule, NStat, NType
{
	@Getter
	private final Position position;
}
