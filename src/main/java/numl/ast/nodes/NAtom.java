package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract sealed class NAtom extends NExp permits NNumber, NString, NBool, NNil
{
}
