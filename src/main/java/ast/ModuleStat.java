package ast;

import lombok.experimental.SuperBuilder;


@SuperBuilder
public sealed class ModuleStat extends AST permits Decl, Bind
{
}
