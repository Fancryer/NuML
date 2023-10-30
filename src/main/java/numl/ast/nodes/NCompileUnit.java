package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
public final class NCompileUnit extends Node
{
	@Getter
	private final List<NModule> modules;

	@Override
	public String toString()
	{
		return modules.stream()
		              .map(String::valueOf)
		              .collect(Collectors.joining("","(compile_unit ",")"));
	}
}



