package numl.types.checker;

import numl.ast.nodes.*;
import numl.decorators.Textorator;
import numl.types.*;
import numl.types.checker.exceptions.DuplicateNameException;
import numl.types.checker.exceptions.UnexpectedTypeException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Checker
{
	private final Deque<Environment> environment=new ArrayDeque<>();
	private final Textorator textorator=new Textorator("");

	public void visitCompile_unit(final NCompileUnit ctx)
	{
		textorator.preact("Compile unit");
		ctx.getModules().forEach(this::visitModule);
		textorator.postact("Compile unit");
		Type.forName("Compile unit");
	}

	public void visitModule(final NModule ctx)
	{
		textorator.preact("Module");
		environment.push(new Environment());
		ctx.getStats().forEach(this::visitStat);
		textorator.inactf("Environment: %s",environment)
		          .postact("Module");
		environment.pop();
	}

	public void visitStat(final NStat ctx)
	{
		textorator.preact("Stat");
		if(ctx instanceof NDecl d) visitDecl(d);
		else visitBind((NBind)ctx);
		textorator.postact("Stat");
	}

	public void visitDecl(final NDecl ctx)
	{
		textorator.preact("Decl");
		if(ctx instanceof NVarDecl v) visitVar_decl(v);
		else visitFunctDecl((NFunctDecl)ctx);
		textorator.postact("Decl");
	}

	public void visitVar_decl(final NVarDecl ctx)
	{
		textorator.preact("Var_decl");
		visitBind(ctx.getBind());
		textorator.postact("Var_decl");
	}

	public void visitFunctDecl(NFunctDecl ctx)
	{
		final var name=ctx.getName();
		textorator.preact("Funct_decl")
		          .inactf("[name]: '%s'",name);
		List<Type> inputTypeList;
		inputTypeList=new ArrayList<>();
		for(NArg arg: ctx.getArgs())
		{
			textorator.inact("[arg]:");
			Type visit=visitArg(arg);
			textorator.inactf("[name]: %s",ctx.getName())
			          .inactf("[type]: '%s'",visit);
			inputTypeList.add(visit);
			environment.getFirst().put(arg.getName(),visit);
		}
		final var declaredReturnType=new MetaVariable();
		final var ret=new FunctionType(inputTypeList,declaredReturnType,environment.getFirst());
		environment.getFirst().put(name,ret);
		final var funcRetType=visitBlock(ctx.getBlock());
		if(!declaredReturnType.equals(funcRetType))
		{
			throw new UnexpectedTypeException(declaredReturnType,funcRetType,ctx.getPosition(),"Function return type mismatch");
		}
		textorator.inactf("[type]: %s",ret)
		          .postact("Funct_decl");
		ctx.getArgs()
		   .stream()
		   .map(NArg::getName)
		   .forEach(environment.getFirst()::remove);
	}

	public Type visitArg(final NArg ctx)
	{
		textorator.preact("Arg")
		          .inactf("[name]: %s",ctx.getName());
		final var ret=visitType(ctx.getType());
		textorator.inactf("[type]: %s",ret)
		          .postact("Arg");
		return ret;
	}

	public TupleType visitArgList(final List<NArg> args)
	{
		return new TupleType(args.stream().map(this::visitArg).toList());
	}

	public Type visitType(final NType ctx)
	{
		if(ctx instanceof NFlatType f) return visitFlatType(f);
		if(ctx instanceof NTupleType t) return visitTuple_type(t);
		if(ctx instanceof NFunctionType f) return visitFunctionType(f);
		return visitArrayType((NArrayType)ctx);
	}

	private FunctionType visitFunctionType(NFunctionType f)
	{
		return new FunctionType(f.getTypes().stream().map(this::visitType).toList(),environment.getFirst());
	}

	private ArrayType visitArrayType(NArrayType ctx)
	{
		return new ArrayType(visitType(ctx.getType()));
	}

	private Type visitFlatType(NFlatType f)
	{
		return switch(f.getName())
		{
			case "Nil" -> NilType.of();
			case "Bool" -> BoolType.of();
			case "Int" -> IntType.of();
			case "Float" -> FloatType.of();
			case "String" -> StringType.of();
			default -> throw new IllegalStateException("Unknown type: "+f.getName());
		};
	}

	public Type visitTuple_type(final NTupleType ctx)
	{
		return new TupleType(ctx.getTypes().stream().map(this::visitType).toList());
	}

	public Type visitBlock(final NBlock ctx)
	{
		textorator.preact("Block");
		environment.addFirst(new Environment());
		ctx.getStats().forEach(this::visitStat);
		final var ret=visitExp(ctx.getExp());
		environment.removeFirst();
		textorator.postact("Block");
		return ret;
	}

	public Type visitBind(final NBind ctx)
	{
		final var name=ctx.getName();
		textorator.preact("Bind")
		          .inactf("[name]: '%s'",name);
		if(environment.getFirst().has(name))
		{
			throw new DuplicateNameException(name,ctx.getPosition());
		}
		final var ret=visitExp(ctx.getExp());
		environment.getFirst().put(name,ret);
		textorator.inactf("[type]: '%s'",ret)
		          .postact("Bind");
		return ret;
	}

	private Type visitExp(NExp exp)
	{
		if(exp instanceof NAtom a) return visitAtom(a);
		if(exp instanceof NTuple t) return visitTuple(t);
		if(exp instanceof NCall c) return visitCall(c);
		if(exp instanceof NOp o) return visitOp(o);
		if(exp instanceof NBranch i) return visitIf_then_else(i);
		if(exp instanceof NLambda l) return visitLambda(l);
		if(exp instanceof NVariable v) return visitVariable(v);
		if(exp instanceof NWhere w) return visitWhere(w);
		else return visitArray((NArray)exp);
	}

	private ArrayType visitArray(NArray a)
	{
		var types=a.getExps().stream().map(this::visitExp).toList();
		if(types.size()==1)
			return new ArrayType(types.get(0));
		if(types.isEmpty())
			return new ArrayType(NilType.of());
		var t=types.get(0);
		for(int i=0;i<types.size();++i)
		{
			var type=types.get(i);
			if(!type.equals(t))
				throw new UnexpectedTypeException(t,type,a.getExps().get(i).getPosition(),"Array element type mismatch");
		}
		return new ArrayType(t);
	}

	private Type visitWhere(NWhere w)
	{
		return null;
	}

	public Type visitOp(final NOp ctx)
	{
		final var opName=ctx.getOp();
		textorator.preact("Infix op //| "+ctx);
		final var left=visitExp(ctx.getLeft());
		textorator.inactf("[left type]: '%s'",left);
		final var opSignature=left.getOperator(opName);
		textorator.inact("[op]:")
		          .preact("name")
		          .inact(opName)
		          .postact("name")
		          .preact("signature")
		          .inact(opSignature.toString())
		          .postact("signature")
		;
		final var right=visitExp(ctx.getRight());
		textorator.inactf("[right type]: '%s'",right);
		if(!opSignature.argAt(1).equals(right))
		{
			final var info="Wrong right operand type: signature of %s operator on %s is %s".formatted(opName,left,opSignature);
			throw new UnexpectedTypeException(left,right,ctx.getPosition(),info);
		}
		final var ret=opSignature.last();
		textorator.postact("Infix op");
		return ret;
	}

	public Type visitIf_then_else(final NBranch ctx)
	{
		textorator.preact("If_then_else");
		Type pred=visitExp(ctx.getPred());
		if(!(pred instanceof BoolType))
		{
			throw new UnexpectedTypeException(BoolType.of(),pred,ctx.getPosition(),"if-then-else condition must be boolean");
		}
		Type
				then=visitExp(ctx.getThen()),
				else_=visitExp(ctx.getElse_());
		textorator.postact("If_then_else");
		if(else_.equals(then))
			return then;
		else
			throw new UnexpectedTypeException(then,else_,ctx.getPosition(),"Incompatible then and else types");
	}

	public Type visitVariable(final NVariable ctx)
	{
		textorator.preact("Variable");
		String name=ctx.getName();
		for(final var env: environment)
		{
			final var type=env.get(name);
			if(type.isEmpty()) continue;
			textorator.inactf("[name]: %s",name)
			          .inactf("[type]: %s",type)
			          .postact("Variable");
			return type.get();
		}
		throw new RuntimeException("Type '%s' is unbound".formatted(name));
	}

	public Type visitLambda(final NLambda ctx)
	{
		textorator.preact("Lambda");
		environment.addFirst(new Environment());
		final var functionArgs=visitArgList(ctx.getArgs()).getElements();
		for(int i=0;i<ctx.getArgs().size();++i)
		{
			environment.getFirst()
			           .put(ctx.getArgs().get(i).getName(),functionArgs.get(i));
		}
		final var expType=visitExp(ctx.getExp());
		environment.removeFirst();
		textorator.postact("Lambda");
		return new FunctionType(functionArgs,expType,environment.getFirst());
	}

	public Type visitCall(final NCall ctx)
	{
		textorator.preact("Call");
		final var name=ctx.getName();
		final var funct=environment.getFirst().get(name);
		if(funct.isEmpty())
		{
			return MetaVariable.of();
			//throw new IllegalStateException("Unknown function: "+name);
		}
		System.out.println("Funct get");
		final var type=funct.get();
		if(!(type instanceof FunctionType functionType))
		{
			throw new RuntimeException("Expected Function, got '%s'".formatted(type));
		}
		//declared parameter types
		final var passedParameters=functionType.getSignature().getArgs();
		final var functionArguments=ctx.getArgs();
		final int argsSize=functionArguments.size();
		if(argsSize!=passedParameters.size())
		{
			throw new RuntimeException
					(
							"Cannot infer function call type: expected "
							+passedParameters.size()
							+" passedParameters but found "
							+argsSize
					);
		}
		for(int i=0;i<passedParameters.size()-1;++i)
		{
			final var exp=functionArguments.get(i);
			Type
					expectedArgumentType=passedParameters.get(i),
					actualArgumentType=visitExp(exp);
			if(!expectedArgumentType.equals(actualArgumentType))
			{
				throw new UnexpectedTypeException(expectedArgumentType,actualArgumentType,exp.getPosition());
			}
		}
		textorator.inactf("[name]: '%s'",name)
		          .inactf("[type]: '%s'",type)
		          .postact("Call");
		return functionType.getSignature().last();
	}

	public TupleType visitTuple(final NTuple ctx)
	{
		textorator.preact("Tuple");
		final var ret=visitExplist(ctx.getExps());
		textorator.postact("Tuple");
		return ret;
	}

	public TupleType visitExplist(final List<NExp> exps)
	{
		textorator.preact("Explist");
		final var ret=new TupleType(exps.stream().map(this::visitExp).toList());
		textorator.postact("Explist");
		return ret;
	}

	public Type visitAtom(final NAtom ctx)
	{
		textorator.preact("Atom")
		          .inactf("[ctx]: '%s'",ctx);
		final Type ret=switch(ctx)
		{
			case NNumber n -> visitNumber(n);
			case NString s -> StringType.of();
			case NBool b -> visitBool(b);
			default -> NilType.of();
		};
		textorator.postact("Atom");
		return ret;
	}

	public Type visitNumber(NNumber ctx)
	{
		return ctx instanceof NInt
		       ?IntType.of()
		       :FloatType.of();
	}

	public Type visitBool(final NBool ctx)
	{
		return BoolType.of();
	}
}
