package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public sealed abstract class NAtom extends NExp permits NNumber, NString, NBool, NNil
{
}
