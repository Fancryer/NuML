package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
public final class NModule extends Node
{
	private final String name;
	private final List<NStat> stats;

	@Override
	public String toString()
	{
		return stats.stream()
		            .map(String::valueOf)
		            .collect(Collectors.joining("","("+name+" ",")"));
	}
}
