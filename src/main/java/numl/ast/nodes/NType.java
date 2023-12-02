package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public sealed abstract class NType extends Node permits NArrayType, NFlatType, NFunctionType, NTupleType
{
}
