package numl;

import numl.ast.AntlrTranslator;
import numl.gen.NuMLLexer;
import numl.gen.NuMLParser;
import numl.types.checker.Checker;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

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
		var compileUnitContext=parser.compile_unit();
		var compileUnit=new AntlrTranslator().visitCompile_unit(compileUnitContext);
		System.out.println(compileUnit);
		new Checker().visitCompile_unit(compileUnit);
		//new NuMLCompiler().compileUnit(compileUnit);
	}
}