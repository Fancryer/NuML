package ast;

import lombok.experimental.SuperBuilder;


@SuperBuilder
public sealed class Decl extends ModuleStat permits VarDecl, FunctDecl
{
}
