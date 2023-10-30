package numl.common;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Maybe<T>
{
	private final T value;
	private final boolean hasValue;

	private Maybe(T value)
	{
		this.value=value;
		this.hasValue=true;
	}

	private Maybe()
	{
		this.value=null;
		this.hasValue=false;
	}

	public static <T> Maybe<T> of(T value)
	{
		if(value==null) throw new IllegalArgumentException("Value cannot be null.");
		return new Maybe<>(value);
	}

	public static <T> Maybe<T> ofNullable(T value)
	{
		return value!=null?new Maybe<>(value):new Maybe<>();
	}

	public static <T> Maybe<T> empty()
	{
		return new Maybe<>();
	}

	public static <T> Maybe<T> some(T value)
	{
		return new Maybe<>(value);
	}

	public static <T> Maybe<T> none()
	{
		return new Maybe<>();
	}

	public boolean hasValue()
	{
		return hasValue;
	}

	public T getValue()
	{
		if(!hasValue) throw new IllegalStateException("Maybe does not have a value.");
		return value;
	}

	public void ifPresent(Consumer<T> consumer)
	{
		if(hasValue) consumer.accept(value);
	}

	public T orElse(T defaultValue)
	{
		return hasValue?value:defaultValue;
	}

	public T orElseGet(Supplier<T> supplier)
	{
		return hasValue?value:supplier.get();
	}

	public <R> Maybe<R> map(Function<T,R> mapper)
	{
		return hasValue?Maybe.some(mapper.apply(value)):Maybe.none();
	}
}