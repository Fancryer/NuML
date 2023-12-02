package numl.ast.nodes;

import io.vavr.control.Either;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import numl.ast.Position;

import java.math.BigInteger;

@SuperBuilder
@Getter
public final class NInt extends NNumber
{
	private Position position;
	private final Either<Long,BigInteger> value;

	@Override
	public String toString()
	{
		return "(int %s)".formatted(value.fold(Object::toString,BigInteger::toString));
	}
}
