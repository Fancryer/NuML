package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public final class NCall extends NExp
{
	private final boolean isNative;
	private final String name;
	private final List<NExp> args;

	@Override
	public String toString()
	{
		return "(call %s%s (args %s))".formatted(isNative?"$":"",name,args);
	}
}
