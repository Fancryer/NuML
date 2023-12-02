package numl.hindleymilner.types;

import numl.hindleymilner.IType;

import java.util.List;

/**
 A binary type constructor which builds function types
 */
public class Function extends TypeOperator
{
	public Function(IType fromType,IType toType)
	{
		super("->",List.of(fromType,toType));
	}

	public static Function Function(IType fromType,IType toType)
	{
		return new Function(fromType,toType);
	}
}