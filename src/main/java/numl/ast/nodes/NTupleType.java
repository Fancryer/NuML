package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public final class NTupleType extends NType
{
	private final List<NType> types;

	@Override
	public String toString()
	{
		var sb=new StringBuilder("(tuple ");
		for(var i=0;i<types.size();i++)
		{
			var type=types.get(i);
			sb.append((type instanceof NTupleType || type instanceof NFunctionType)?"("+type+")":type);
			if(i<types.size()-1)
			{
				sb.append(" * ");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
