package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public sealed abstract class NDecl extends NStat permits NVarDecl, NFunctDecl
{
}
