package numl.ast.nodes;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public final class NFunctionType extends NType
{
	private final List<NType> types;
}
