package numl.hindleymilner.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numl.hindleymilner.IType;

/**
 Letrec binding
 */
@Getter
@AllArgsConstructor
public final class Letrec implements IType
{
	// name
	private String v;
	// exp in body
	private IType defn, body;

	@Override
	public String toString()
	{
		return "(letrec %s = %s in %s)".formatted(v,defn,body);
	}

	public static Letrec Letrec(String v,IType defn,IType body)
	{
		return new Letrec(v,defn,body);
	}
}
