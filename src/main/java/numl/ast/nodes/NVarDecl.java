package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class NVarDecl extends NDecl
{
	@Getter
	private final NBind bind;

	@Override
	public String toString()
	{
		return "(var_decl %s)".formatted(bind);
	}
}
