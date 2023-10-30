package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public final class NNil extends NAtom
{

	@Override
	public String toString()
	{
		return "nil";
	}
}
