package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
public final class NLambda extends NExp
{
	private final List<NArg> args;
	private final NExp exp;

	@Override
	public String toString()
	{
		var argsAsStr=args.stream()
		                  .map(NArg::toString)
		                  .collect(Collectors.joining(" ","(args (","))"));
		return "(lambda %s %s)".formatted(argsAsStr,exp);
	}
}
