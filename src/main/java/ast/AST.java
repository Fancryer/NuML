package ast;

import common.Pair;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public sealed class AST permits CompileUnit, Exp, Module, ModuleStat
{
	private final int depth;
	private final Pair<Integer,Integer> line, column;
}
