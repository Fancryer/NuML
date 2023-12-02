package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import numl.ast.Position;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Getter
public final class NCompileUnit extends Node
{
	private final List<NModule> modules;
	private final Position position;

	@Override
	public String toString()
	{
		return modules.stream()
		              .map(String::valueOf)
		              .collect(Collectors.joining("","(compile_unit ",")"));
	}
}



