package numl.hindleymilner.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import numl.hindleymilner.IType;

import java.util.List;
import java.util.stream.Collectors;

/**
 An n-ary type constructor which builds a new type from old
 */
@Getter
@AllArgsConstructor
public class TypeOperator implements IType
{
	private String name;
	private List<IType> types;

	@Override
	public String toString()
	{
		return switch(types.size())
		{
			case 0 -> name;
			case 2 -> String.format("(%s %s %s)",types.get(0),name,types.get(1));
			default -> String.format("%s %s",name,types.stream().map(Object::toString).collect(Collectors.joining(" ")));
		};
	}

	public static TypeOperator TypeOperator(String name,List<IType> types)
	{
		return new TypeOperator(name,types);
	}
}