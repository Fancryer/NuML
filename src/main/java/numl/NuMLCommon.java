package numl;

import numl.gen.NuMLLexer;
import numl.gen.NuMLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NuMLCommon
{
	public static String readFileString(String path)
	{
		try
		{
			return Files.readString(Path.of(path));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static NuMLParser getTempParser(java.lang.String source)
	{
		return new NuMLParser(new CommonTokenStream(new NuMLLexer(CharStreams.fromString(source))));
	}
}
