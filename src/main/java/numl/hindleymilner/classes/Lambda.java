package numl.hindleymilner.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numl.hindleymilner.IType;

/**
 Lambda abstraction
 */
@Getter
@AllArgsConstructor
public final class Lambda implements IType
{
	private String v;
	private IType body;

	@Override
	public String toString()
	{
		return "(fn %s => %s)".formatted(v,body);
	}

	public static Lambda Lambda(String v,IType body)
	{
		return new Lambda(v,body);
	}
}