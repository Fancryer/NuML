package common;

public record Pair<K,V>(K key,V value)
{
	public Pair<V,K> reverse()
	{
		return new Pair<>(value,key);
	}

	@Override
	public String toString()
	{
		return "{%s;%s}".formatted(key,value);
	}
}
