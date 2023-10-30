package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract sealed class NNumber extends NAtom permits NInt, NFloat
{
}
