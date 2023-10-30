package numl.ast.nodes;

import io.vavr.control.Either;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@SuperBuilder
public final class NInt extends NNumber
{
	private final Either<Long,BigInteger> value;

	@Override
	public String toString()
	{
		return "(int %s)".formatted(value.fold(Object::toString,BigInteger::toString));
	}
}
