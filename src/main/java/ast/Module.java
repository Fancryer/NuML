package ast;

import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public final class Module extends AST
{
	private final String name;
	private final List<ModuleStat> moduleStats;
}
