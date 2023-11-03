package numl.common;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Zipper<K,V>
{
	public Stream<Pair<K,V>> zip(Stream<K> kStream,Stream<V> vStream)
	{
		return minMap(kStream.toList(),vStream.toList());
	}

	public Stream<Pair<K,V>> minMap(List<K> a,List<V> b)
	{
		return minMap(a,b,getPairMapper(a,b));
	}

	private static <K,V> IntFunction<Pair<K,V>> getPairMapper(List<K> kList,List<V> vList)
	{
		return i->new Pair<>(kList.get(i),vList.get(i));
	}

	public Stream<Pair<K,V>> minMap(List<K> a,List<V> b,IntFunction<Pair<K,V>> mapper)
	{
		return minRange(a,b).mapToObj(mapper);
	}

	private IntStream minRange(List<?> a,List<?> b)
	{
		return IntStream.range(0,Math.min(a.size(),b.size()));
	}

	public List<Pair<K,V>> zip(List<K> kList,List<V> vList)
	{
		return minMap(kList,vList).toList();
	}
}
