import gen.NuMLLexer;
import gen.NuMLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import types.checker.Checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		var src=Files.readString(Path.of(args[0]));
		var charStream=CharStreams.fromString(src);
		var lexer=new NuMLLexer(charStream);
		var tokenStream=new CommonTokenStream(lexer);
		var parser=new NuMLParser(tokenStream);
		new Checker().visit(parser.compile_unit());
	}
}