package numl.ast.nodes;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract sealed class NType extends Node permits NArrayType, NFlatType, NFunctionType, NTupleType
{
	public abstract Class<?> asJavaClass();
}
