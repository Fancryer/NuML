package numl.ast.nodes;

import lombok.experimental.SuperBuilder;


@SuperBuilder
public abstract sealed class NStat extends Node permits NDecl, NBind
{
}
