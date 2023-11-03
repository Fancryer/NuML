package numl.common;

public record Pair<K,V>(K key,V value)
{
	public static<K,V> Pair<K,V> of(K k,V v)
	{
		return new Pair<>(k,v);
	}

	public Pair<V,K> reverse()
	{
		return new Pair<>(value,key);
	}

	@Override
	public String toString()
	{
		return "{%s;%s}".formatted(key,value);
	}

	/**
	 @return true if key.equals(value)
	 */
	public boolean isSymmetric()
	{
		return key.equals(value);
	}
}
