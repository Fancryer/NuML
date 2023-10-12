package types.checker;

import common.Pair;
import gen.NuMLBaseVisitor;
import gen.NuMLParser;
import types.Type;

import java.util.List;
import java.util.stream.Collectors;

public class ArgumentVisitor extends NuMLBaseVisitor<List<Pair<String,Type>>>
{
	private final Checker checker;

	public ArgumentVisitor(Checker checker)
	{
		this.checker=checker;
	}

	@Override
	public List<Pair<String,Type>> visitFunct_args(NuMLParser.Funct_argsContext ctx)
	{
		return ctx.arg_list()
		           .arg()
		           .stream()
		           .map
				           (
						           argContext->new Pair<>
								           (
										           argContext.ID().getText(),
										           checker.visitArg(argContext)
								           )
				           )
		           .collect(Collectors.toList());
	}
}
