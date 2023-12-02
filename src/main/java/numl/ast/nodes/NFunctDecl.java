package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
@SuperBuilder
@Getter
public final class NFunctDecl extends NDecl
{
	private final String name;
	private final List<NArg> args;
	private final NType returnType;
	private final NBlock block;

	@Override
	public String toString()
	{
		return "(funct_decl %s %s %s %s)".formatted(name,args,returnType,block);
	}
}