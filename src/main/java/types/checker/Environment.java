package types.checker;

import types.Type;

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

	public Environment(SequencedMap<String,Type> types)
	{
		this.types=types;
	}

	public Optional<Type> get(String name)
	{
		return Optional.ofNullable(types.get(name));
	}

	public void put(String name,Type type)
	{
		types.put(name,type);
	}

	@Override
	public String toString()
	{
		return types.toString();
	}

	public boolean has(String name)
	{
		return types.containsKey(name);
	}

	public Type remove(String name)
	{
		return types.remove(name);
	}
}
