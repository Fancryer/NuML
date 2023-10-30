package numl.types.checker;

import numl.types.Type;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.SequencedMap;

public class Environment
{
	private final SequencedMap<String,Type> types;

	public Environment()
	{
		this(new LinkedHashMap<>());
	}

	public Environment(final SequencedMap<String,Type> types)
	{
		this.types=types;
	}

	public Optional<Type> get(final String name)
	{
		return Optional.ofNullable(types.get(name));
	}

	public void put(final String name,final Type type)
	{
		types.put(name,type);
	}

	@Override
	public String toString()
	{
		return types.toString();
	}

	public boolean has(final String name)
	{
		return types.containsKey(name);
	}

	public Type remove(final String name)
	{
		return types.remove(name);
	}
}
