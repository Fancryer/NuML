package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public sealed abstract class NNumber extends NAtom permits NInt, NFloat
{
}
