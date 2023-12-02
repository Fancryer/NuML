package numl.hindleymilner.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numl.hindleymilner.IType;

/**
 Function application
 */
@Getter
@AllArgsConstructor
public final class Apply implements IType
{
	private IType fn, arg;

	@Override
	public String toString()
	{
		return "(%s %s)".formatted(fn,arg);
	}

	public static Apply Apply(IType fn,IType arg)
	{
		return new Apply(fn,arg);
	}
}