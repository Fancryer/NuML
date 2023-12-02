package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import numl.ast.Position;

@SuperBuilder
@Getter
public sealed abstract class Node permits NArg, NBlock, NCompileUnit, NExp, NModule, NStat, NType
{
	private final Position position;
}
