package types.checker;

import decorators.Textorator;
import gen.NuMLBaseVisitor;
import gen.NuMLParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import types.*;
import types.checker.exceptions.DuplicateNameException;
import types.checker.exceptions.UnexpectedTypeException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static gen.NuMLParser.Compile_unitContext;

public class
Checker extends NuMLBaseVisitor<Type>
{
	private final Deque<Environment> environment=new ArrayDeque<>();
	private final Textorator textorator=new Textorator("");

	public static java.lang.String toSourceCode(Tree tree,StringBuilder stringBuilder)
	{
		if(tree==null) return "";
		int childCount=tree.getChildCount();
		Consumer<Tree> childToSource=child->
		{
			if(child instanceof TerminalNode parserRuleContext)
				stringBuilder.append(parserRuleContext)
				             .append(" ");
			toSourceCode(child,stringBuilder);
		};
		IntStream.range(0,childCount)
		         .mapToObj(tree::getChild)
		         .forEach(childToSource);
		return stringBuilder.toString().trim().replace("<EOF>","");
	}

	public static List<java.lang.String> toSourceList(List<Tree> trees)
	{
		return trees.stream().map(Checker::toSourceCode).toList();
	}

	public static java.lang.String toSourceCode(Tree tree)
	{
		return toSourceCode(tree,new StringBuilder());
	}

	@Override
	public Type visitCompile_unit(Compile_unitContext ctx)
	{
		textorator.preact("Compile unit");
		ctx.module().forEach(this::visitModule);
		textorator.postact("Compile unit");
		return Type.forName("Compile unit");
	}

	@Override
	public Type visitModule(NuMLParser.ModuleContext ctx)
	{
		textorator.preact("Module");
		environment.push(new Environment());
		ctx.module_stat().forEach(this::visitModule_stat);
		textorator.inactf("Environment: %s",environment)
		          .postact("Module");
		environment.pop();
		return defaultResult();
	}

	@Override
	public Type visitModule_stat(NuMLParser.Module_statContext ctx)
	{
		textorator.preact("Module stat");
		var ret=visitDecl(ctx.decl());
		textorator.postact("Module stat");
		return ret;
	}

	@Override
	public Type visitDecl(NuMLParser.DeclContext ctx)
	{
		textorator.preact("Decl");
		var ret=ctx.var_decl()!=null
		        ?visitVar_decl(ctx.var_decl())
		        :visitFunct_decl(ctx.funct_decl());
		textorator.postact("Decl");
		return ret;
	}

	@Override
	public Type visitVar_decl(NuMLParser.Var_declContext ctx)
	{
		textorator.preact("Var_decl");
		var ret=visitBind(ctx.bind());
		textorator.postact("Var_decl");
		return ret;
	}

	@Override
	public Type visitFunct_decl(NuMLParser.Funct_declContext ctx)
	{
		var name=ctx.name.getText();
		textorator.preact("Funct_decl")
		          .inactf("[name]: '%s'",name);
		List<Type> inputTypeList;
		var functArgsContext=ctx.funct_args();
		inputTypeList=new ArrayList<>();
		for(NuMLParser.ArgContext argContext: functArgsContext.arg_list().arg())
		{
			textorator.inact("[arg]:");
			Type visit=visitArg(argContext);
			textorator.inactf("[name]: %s",ctx.name.getText())
			          .inactf("[type]: '%s'",visit);
			inputTypeList.add(visit);
			environment.getFirst().put(argContext.name.getText(),visit);
		}
		var declaredReturnType=visitType(ctx.type());
		var ret=new FunctionType(inputTypeList,declaredReturnType,environment.getFirst());
		environment.getFirst().put(name,ret);
		var funcRetType=visitBlock(ctx.block());
		if(funcRetType!=declaredReturnType)
		{
			throw new UnexpectedTypeException(declaredReturnType,funcRetType,ctx.start,ctx.stop,"Function return type mismatch");
		}
		textorator.inactf("[type]: %s",ret)
		          .postact("Funct_decl");
		ctx.funct_args()
		   .arg_list()
		   .arg()
		   .stream()
		   .map(NuMLParser.ArgContext::ID)
		   .map(ParseTree::getText)
		   .forEach(environment.getFirst()::remove);
		return ret;
	}

	@Override
	public Type visitArg(NuMLParser.ArgContext ctx)
	{
		textorator.preact("Arg")
		          .inactf("[name]: %s",ctx.ID().getText());
		var ret=visitType(ctx.type());
		textorator.inactf("[type]: %s",ret)
		          .postact("Arg");
		return ret;
	}

	@Override
	public TupleType visitArg_list(NuMLParser.Arg_listContext ctx)
	{
		return new TupleType(ctx.arg().stream().map(this::visitArg).toList());
	}

	@Override
	public Type visitType(NuMLParser.TypeContext ctx)
	{
		if(ctx.ID()==null) return visitTuple_type(ctx.tuple_type());
		return switch(ctx.ID().getText())
		{
			case "Nil" -> NilType.of();
			case "Bool" -> BoolType.of();
			case "Int" -> IntType.of();
			case "Float" -> FloatType.of();
			case "String" -> StringType.of();
			default -> throw new IllegalStateException("Unknown type: "+ctx.ID().getText());
		};
	}

	@Override
	public Type visitAtom_exp(NuMLParser.Atom_expContext ctx)
	{
		return visitAtom(ctx.atom());
	}

	@Override
	public Type visitExp_paren(NuMLParser.Exp_parenContext ctx)
	{
		return visit(ctx.exp());
	}

	@Override
	public Type visitTuple_type(NuMLParser.Tuple_typeContext ctx)
	{
		return new TupleType(ctx.typelist().type().stream().map(this::visitType).toList());
	}

	@Override
	public Type visitTypelist(NuMLParser.TypelistContext ctx)
	{
		textorator.preact("Typelist");
		var ret=new TupleType(ctx.type().stream().map(this::visitType).toList());
		textorator.inactf("[type]: %s",ret)
		          .postact("Typelist");
		return ret;
	}

	@Override
	public Type visitBlock(NuMLParser.BlockContext ctx)
	{
		textorator.preact("Block");
		environment.addFirst(new Environment());
		ctx.module_stat().forEach(this::visitModule_stat);
		var ret=visit(ctx.exp());
		environment.removeFirst();
		textorator.postact("Block");
		return ret;
	}

	@Override
	public Type visitBind(NuMLParser.BindContext ctx)
	{
		var name=ctx.ID().getText();
		textorator.preact("Bind")
		          .inactf("[name]: '%s'",name);
		if(environment.getFirst().has(name))
		{
			throw new DuplicateNameException(name,ctx.start.getLine(),ctx.start.getCharPositionInLine());
		}
		var ret=visit(ctx.exp());
		environment.getFirst().put(name,ret);
		textorator.inactf("[type]: '%s'",ret)
		          .postact("Bind");
		return ret;
	}

	@Override
	public Type visitInfix_op(NuMLParser.Infix_opContext ctx)
	{
		var opName=ctx.OP_INFIX().getText();
		textorator.preact("Infix op //| "+toSourceCode(ctx));
		var left=visit(ctx.left);
		textorator.inactf("[left type]: '%s'",left);
		var opSignature=left.getOperator(opName);
		textorator.inact("[op]:")
		          .preact("name")
		          .inact(opName)
		          .postact("name")
		          .preact("signature")
		          .inact(opSignature.toString())
		          .postact("signature")
		;
		var right=visit(ctx.right);
		textorator.inactf("[right type]: '%s'",right);
		if(opSignature.argAt(1)!=right)
		{
			var info="Wrong right operand type: signature of %s operator on %s is %s".formatted(opName,left,opSignature);
			throw new UnexpectedTypeException(left,right,ctx.start,ctx.stop,info);
		}
		var ret=opSignature.last();
		textorator.postact("Infix op");
		return ret;
	}

	@Override
	public Type visitIf_then_else(NuMLParser.If_then_elseContext ctx)
	{
		textorator.preact("If_then_else");
		var pred=visit(ctx.pred);
		if(pred!=BoolType.of())
		{
			throw new UnexpectedTypeException(BoolType.of(),pred,ctx.start,ctx.stop,"if-then-else condition must be boolean");
		}
		var then=visit(ctx.then);
		var else_=visit(ctx.else_);
		textorator.postact("If_then_else");
		if(then==else_)
			return then;
		else
			throw new UnexpectedTypeException(then,else_,ctx.start,ctx.stop,"Incompatible then and else types");
	}

	@Override
	public Type visitVariable(NuMLParser.VariableContext ctx)
	{
		textorator.preact("Variable");
		String name=ctx.ID().getText();
		for(var env: environment)
		{
			var type=env.get(name);
			if(type.isEmpty()) continue;
			textorator.inactf("[name]: %s",name)
			          .inactf("[type]: %s",type)
			          .postact("Variable");
			return type.get();
		}
		throw new RuntimeException("Type '%s' is unbound".formatted(name));
	}

	@Override
	public Type visitLambda(NuMLParser.LambdaContext ctx)
	{
		textorator.preact("Lambda");
		environment.addFirst(new Environment());
		var argListContext=ctx.funct_args().arg_list();
		var functionArgs=visitArg_list(argListContext).getElements();
		for(int i=0;i<argListContext.arg().size();++i)
		{
			environment.getFirst()
			           .put(argListContext.arg(i).ID().getText(),functionArgs.get(i));
		}
		var expType=visit(ctx.exp());
		environment.removeFirst();
		textorator.postact("Lambda");
		return new FunctionType(functionArgs,expType,environment.getFirst());
	}

	@Override
	public Type visitCall(NuMLParser.CallContext ctx)
	{
		textorator.preact("Call");
		var name=ctx.ID().getText();
		var funct=environment.getFirst().get(name);
		if(funct.isEmpty()) throw new IllegalStateException("Unknown function: "+name);
		var type=funct.get();
		if(!(type instanceof FunctionType functionType))
		{
			throw new RuntimeException("Expected Function, got '%s'".formatted(type));
		}
		//declared parameter types
		var args=functionType.getSignature().getArgs();
		if(ctx.exp().size()!=args.size())
		{
			throw new RuntimeException
					(
							"Cannot infer function call type: expected "
							+args.size()
							+" args but found "
							+ctx.exp().size()
					);
		}
		for(int i=0;i<args.size()-1;++i)
		{
			var exp=ctx.exp(i);
			Type
					expectedArgumentType=args.get(i),
					actualArgumentType=visit(exp);
			if(!expectedArgumentType.equals(actualArgumentType))
			{
				throw new UnexpectedTypeException(expectedArgumentType,actualArgumentType,exp.start,exp.stop);
			}
		}
		textorator.inactf("[name]: '%s'",name)
		          .inactf("[type]: '%s'",type)
		          .postact("Call");
		return functionType.getSignature().last();
	}

	@Override
	public TupleType visitTuple(NuMLParser.TupleContext ctx)
	{
		textorator.preact("Tuple");
		var ret=visitExplist(ctx.explist());
		textorator.postact("Tuple");
		return ret;
	}

	@Override
	public TupleType visitExplist(NuMLParser.ExplistContext ctx)
	{
		textorator.preact("Explist");
		var ret=new TupleType(ctx.exp().stream().map(this::visit).toList());
		textorator.postact("Explist");
		return ret;
	}

	@Override
	public Type visitAtom(NuMLParser.AtomContext ctx)
	{
		textorator.preact("Atom")
		          .inactf("[ctx]: '%s'",ctx.getText());
		Type ret;
		if(ctx.INT_MOD()!=null) ret=IntType.of();
		else if(ctx.FLOAT()!=null) ret=FloatType.of();
		else if(ctx.STRING()!=null) ret=StringType.of();
		else if(ctx.NIL()!=null) ret=NilType.of();
		else ret=BoolType.of();
		textorator.postact("Atom");
		return ret;
	}

	@Override
	public Type visitBool(NuMLParser.BoolContext ctx)
	{
		return BoolType.of();
	}

	@Override
	protected Type defaultResult()
	{
		return NilType.of();
	}
}
