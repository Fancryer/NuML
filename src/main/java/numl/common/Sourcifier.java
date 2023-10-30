package numl.common;

import java.util.List;

public interface Sourcifier<T>
{
	String sourcify(T tree);

	List<String> sourcify(List<T> trees);

	String sourcify(T tree,StringBuilder builder);
}
