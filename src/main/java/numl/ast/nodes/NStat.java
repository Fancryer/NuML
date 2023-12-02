package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public sealed abstract class NStat extends Node permits NDecl, NBind
{
}
