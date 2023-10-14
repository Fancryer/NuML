package decorators;

import java.util.function.UnaryOperator;

public class Textorator
{
	private int depth;
	private final UnaryOperator<String> preactHeader, inactHeader, postactHeader;
	private final String sep;

	public Textorator()
	{
		this("$");
	}

	public Textorator(String sep)
	{
		this.sep=sep;
		this.preactHeader=s->"%s+%s%s".formatted("  ".repeat(inc()-1),sep,s);
		this.inactHeader=s->"%s:%s%s".formatted("  ".repeat(depth),sep,s);
		this.postactHeader=s->"%s-%s%s".formatted("  ".repeat(dec()),sep,s);
	}

	public Textorator(UnaryOperator<String> preactHeader,UnaryOperator<String> inactHeader,UnaryOperator<String> postactHeader)
	{
		this("$",preactHeader,inactHeader,postactHeader);
	}

	public Textorator(String sep,UnaryOperator<String> preactHeader,UnaryOperator<String> inactHeader,UnaryOperator<String> postactHeader)
	{
		this.preactHeader=preactHeader;
		this.inactHeader=inactHeader;
		this.postactHeader=postactHeader;
		this.sep=sep;
	}

	private int inc()
	{
		return ++depth;
	}

	private int dec()
	{
		return depth==0?0:--depth;
	}

	public Textorator preact(String text)
	{
		System.out.println(preactHeader.apply(text));
		return this;
	}

	public Textorator inact(String s)
	{
		System.out.println(inactHeader.apply(s));
		return this;
	}

	public Textorator postact(String text)
	{
		System.out.println(postactHeader.apply(text));
		return this;
	}

	public Textorator inactf(String format,Object... args)
	{
		return inact(String.format(format,args));
	}

	public Textorator postactf(String format,Object... args)
	{
		return postact(String.format(format,args));
	}

	public Textorator preactf(String format,Object... args)
	{
		return preact(String.format(format,args));
	}
}
