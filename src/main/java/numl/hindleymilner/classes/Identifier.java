package numl.hindleymilner.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numl.hindleymilner.IType;

/**
 Identifier
 */
@Getter
@AllArgsConstructor
public final class Identifier implements IType
{
	private String name;

	@Override
	public String toString()
	{
		return name;
	}

	public static Identifier Identifier(String name)
	{
		return new Identifier(name);
	}
}