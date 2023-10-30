package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
public final class NTuple extends NExp
{
	private final List<NExp> exps;

	@Override
	public String toString()
	{
		return exps.stream()
		          .map(String::valueOf)
		          .collect(Collectors.joining(" * ","(tuple (","))"));
	}
}
