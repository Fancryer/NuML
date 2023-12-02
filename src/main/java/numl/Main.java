package numl;

import numl.hindleymilner.Helper;
import numl.hindleymilner.IType;
import numl.hindleymilner.types.BooleanType;
import numl.hindleymilner.types.IntegerType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static numl.hindleymilner.classes.Apply.Apply;
import static numl.hindleymilner.classes.Identifier.Identifier;
import static numl.hindleymilner.classes.Lambda.Lambda;
import static numl.hindleymilner.classes.Let.Let;
import static numl.hindleymilner.classes.Letrec.Letrec;
import static numl.hindleymilner.types.Function.Function;
import static numl.hindleymilner.types.TypeOperator.TypeOperator;
import static numl.hindleymilner.types.TypeVariable.TypeVariable;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		//		var src=Files.readString(Path.of(args[0]));
		//		var charStream=CharStreams.fromString(src);
		//		var lexer=new NuMLLexer(charStream);
		//		var tokenStream=new CommonTokenStream(lexer);
		//		var parser=new NuMLParser(tokenStream);
		//		var compileUnitContext=parser.compile_unit();
		//		var compileUnit=new AntlrTranslator().visitCompile_unit(compileUnitContext);
		//		//System.out.println(compileUnit);
		//		try(var checker=new NuMLChecker(compileUnit))
		//		{
		//			checker.check(compileUnit);
		//			checker.getNodeTypeMap().entrySet().forEach(System.out::println);
		//			//new NuMLCompiler().compileUnit(compileUnit,null,checker.check(compileUnit));
		//		}
		//		catch(Exception e)
		//		{
		//			throw new RuntimeException(e);
		//		}

		var var1=TypeVariable();
		var var2=TypeVariable();
		var pairType=TypeOperator("*",List.of(var1,var2));

		var var3=TypeVariable();
		Map<String,IType> myEnv=Map.of(
				"pair",Function(var1,Function(var2,pairType)),
				"true",new BooleanType(),
				"cond",Function(new BooleanType(),Function(var3,Function(var3,var3))),
				"zero",Function(new IntegerType(),new BooleanType()),
				"pred",Function(new IntegerType(),new IntegerType()),
				"times",Function(new IntegerType(),Function(new IntegerType(),new IntegerType()))
		                              );
		var pair=Apply(
				Apply(
						Identifier("pair"),
						Apply(
								Identifier("f"),
								Identifier("4")
						     )
				     ),
				Apply(
						Identifier("f"),
						Identifier("true")
				     )
		              );
		var examples=List.of(
				// factorial
				Letrec(
						"factorial",  // letrec factorial =
						Lambda(
								"n",  // fn n =>
								Apply(
										Apply(  // cond (zero n) 1
										        Apply(
												        Identifier("cond"),  // cond (zero n)
												        Apply(Identifier("zero"),Identifier("n"))),
										        Identifier("1")
										     ),
										Apply(  // times n
										        Apply(Identifier("times"),Identifier("n")),
										        Apply(
												        Identifier("factorial"),
												        Apply(Identifier("pred"),Identifier("n"))
										             )
										     )
								     )
						      ), // in
						Apply(Identifier("factorial"),Identifier("5"))
				      ),
				// Should fail:
				// fn x => (pair(x(3) (x(true)))
				Lambda("x",
				       Apply(
						       Apply(Identifier("pair"),
						             Apply(Identifier("x"),Identifier("3"))),
						       Apply(Identifier("x"),Identifier("true")))),
				//# pair(f(3), f(true))
				Apply(
						Apply(Identifier("pair"),Apply(Identifier("f"),Identifier("4"))),
						Apply(Identifier("f"),Identifier("true"))),
				//# let f = (fn x => x) in ((pair (f 4)) (f true))
				Let("f",Lambda("x",Identifier("x")),pair),

				//# fn f => f f (fail)
				Lambda("f",Apply(Identifier("f"),Identifier("f"))),

				//# let g = fn f => 5 in g g
				Let("g",
				    Lambda("f",Identifier("5")),
				    Apply(Identifier("g"),Identifier("g"))),
				//# example that demonstrates generic and non-generic variables:
				//# fn g => let f = fn x => g in pair (f 3, f true)
				Lambda("g",
				       Let("f",
				           Lambda("x",Identifier("g")),
				           Apply(
						           Apply(Identifier("pair"),
						                 Apply(Identifier("f"),Identifier("3"))
						                ),
						           Apply(Identifier("f"),Identifier("true"))))),
				//# Function composition
				//# fn f (fn g (fn arg (f g arg)))
				Lambda("f",Lambda("g",Lambda("arg",Apply(Identifier("g"),Apply(Identifier("f"),Identifier("arg"))))))
		                    );
		for(var example:examples)
		{
			Helper.tryExp(myEnv,example);
		}

		//		var bigTuple=checker.getTuple(new TConst("a"),checker.getTuple(new TConst("b"),new TConst("c")));
		//		System.out.println(bigTuple);
		//		var tupleAsString=checker.unwrap(bigTuple)
		//		                         .stream()
		//		                         .map(Object::toString)
		//		                         .collect(Collectors.joining("->"));
		//		System.out.println(tupleAsString);
		//new NuMLChecker().unify(new TConst("a"),new TConst("a"));
	}
}