package ast;

import lombok.experimental.SuperBuilder;


@SuperBuilder
public final class Bind extends ModuleStat
{
	private final String name;
	private final Exp exp;
}
