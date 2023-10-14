package ast;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class CompileUnit extends AST
{
	private final Module module;
}



