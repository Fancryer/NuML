package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract sealed class NDecl extends NStat permits NVarDecl, NFunctDecl
{
}
