package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public sealed abstract class NExp extends Node permits NAtom, NTuple, NCall, NBranch, NLambda, NOp, NVariable, NWhere, NArray
{
}