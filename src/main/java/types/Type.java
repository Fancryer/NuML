package types;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public abstract class Type
{
	@Getter
	private final String name;
	protected final Map<String,Signature> operators=new HashMap<>();

	public Type(String name)
	{
		this.name=name;
	}

	public static Type forName(String name)
	{
		return new CustomType(name);
	}

	public final Signature getOperator(String text)
	{
		return operators.getOrDefault(text,Signature.empty());
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
