package common;

public record Pair<K,V>(K key,V value)
{
	public Pair<V,K> reverse()
	{
		return new Pair<>(value,key);
	}
}
