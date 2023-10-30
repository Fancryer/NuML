package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;

@SuperBuilder
@Getter
public final class NFlatType extends NType
{
	private final String name;

	@Override
	public String toString()
	{
		return "(flat_type %s)".formatted(name);
	}

	@Override
	public Class<?> asJavaClass()
	{
		return switch(name)
		{
			case "Int" -> BigInteger.class;
			case "Float" -> BigDecimal.class;
			case "String" -> String.class;
			case "Boolean" -> boolean.class;
			case "Nil" -> null;
			default -> throw new IllegalArgumentException("Unknown class: "+name);
		};
	}
}
