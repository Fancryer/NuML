package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract sealed class NExp extends Node permits NAtom, NTuple, NCall, NBranch, NLambda, NOp, NVariable, NWhere, NArray
{
}