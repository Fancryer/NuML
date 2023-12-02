package numl.hindleymilner.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numl.hindleymilner.IType;

/**
 Let binding
 */
@Getter
@AllArgsConstructor
public final class Let implements IType
{
	//name
	private String v;
	//exp in body
	private IType defn, body;

	@Override
	public String toString()
	{
		return "(let %s = %s in %s)".formatted(v,defn,body);
	}

	public static Let Let(String v,IType defn,IType body)
	{
		return new Let(v,defn,body);
	}
}