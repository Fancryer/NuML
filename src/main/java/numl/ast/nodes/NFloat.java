package numl.ast.nodes;

import io.vavr.control.Either;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
public final class NFloat extends NNumber
{
	private final Either<Double,BigDecimal> value;
}
