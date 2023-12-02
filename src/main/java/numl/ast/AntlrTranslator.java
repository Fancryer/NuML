package numl.ast;

import io.vavr.control.Either;
import numl.ast.nodes.*;
import numl.common.ParseTreeSourcifier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static numl.gen.NuMLParser.*;

public class AntlrTranslator
{
	public AntlrTranslator(){}

	public NCompileUnit visitCompile_unit(Compile_unitContext ctx)
	{
		return NCompileUnit.builder()
		                   .position(Position.of(ctx.start,ctx.stop))
		                   .modules(ctx.module().stream().map(this::visitModule).toList())
		                   .build();
	}

	public NModule visitModule(ModuleContext ctx)
	{
		return NModule.builder()
		              .position(Position.of(ctx.start,ctx.stop))
		              .name(ctx.ID().getText())
		              .stats(ctx.stat().stream().map(this::visitStat).toList())
		              .build();
	}

	public NStat visitStat(StatContext ctx)
	{
		return ctx.bind()!=null
		       ?visitBind(ctx.bind())
		       :visitDecl(ctx.decl());
	}

	public NDecl visitDecl(DeclContext ctx)
	{
		if(ctx instanceof Var_declContext v) return visitVar_decl(v);
		if(ctx instanceof Funct_declContext f) return visitFunct_decl(f);
		throw new RuntimeException("Unknown decl: "+new ParseTreeSourcifier().sourcify(ctx));
	}

	public NVarDecl visitVar_decl(Var_declContext ctx)
	{
		return NVarDecl.builder()
		               .position(Position.of(ctx.start,ctx.stop))
		               .bind(visitBind(ctx.bind()))
		               .build();
	}

	public NFunctDecl visitFunct_decl(Funct_declContext ctx)
	{
		NType type;
		var position=Position.of(ctx.start,ctx.stop);
		type=ctx.type()!=null
		     ?visitType(ctx.type())
		     :NFlatType.builder()
		               .position(position)
		               .name("Nil")
		               .build();
		return NFunctDecl.builder()
		                 .position(position)
		                 .name(ctx.ID().getText())
		                 .args(ctx.funct_args().arg().stream().map(this::visitArg).toList())
		                 .returnType(type)
		                 .block(visitBlock(ctx.block()))
		                 .build();
	}

	public NArg visitArg(ArgContext ctx)
	{
		NType type;
		if(ctx.type()!=null)
		{
			type=visitType(ctx.type());
		}
		else
		{
			type=NFlatType.builder()
			              .position(Position.of(ctx.start,ctx.stop))
			              .name("Nil")
			              .build();
		}
		return NArg.builder()
		           .position(Position.of(ctx.start,ctx.stop))
		           .name(ctx.ID().getText())
		           .type(type)
		           .build();
	}

	public NType visitType(TypeContext ctx)
	{
		if(ctx.tuple_type()!=null) return visitTuple_type(ctx.tuple_type());
		if(ctx.array_type()!=null) return visitArray_type(ctx.array_type());
		if(ctx.ID()!=null)
		{
			return NFlatType.builder()
			                .position(Position.of(ctx.start,ctx.stop))
			                .name(ctx.ID().getText())
			                .build();
		}
		throw new RuntimeException("Unknown type: "+new ParseTreeSourcifier().sourcify(ctx));
	}

	public NTupleType visitTuple_type(Tuple_typeContext ctx)
	{
		return NTupleType.builder()
		                 .position(Position.of(ctx.start,ctx.stop))
		                 .types(ctx.type().stream().map(this::visitType).toList())
		                 .build();
	}

	public NArrayType visitArray_type(Array_typeContext ctx)
	{
		return null;
	}

	public NBlock visitBlock(BlockContext ctx)
	{
		return NBlock.builder()
		             .position(Position.of(ctx.start,ctx.stop))
		             .stats(ctx.stat().stream().map(this::visitStat).toList())
		             .exp(visitExp(ctx.exp()))
		             .build();
	}

	public NBind visitBind(BindContext ctx)
	{
		return NBind.builder()
		            .position(Position.of(ctx.start,ctx.stop))
		            .name(ctx.ID().getText())
		            .exp(visitExp(ctx.exp()))
		            .build();
	}

	/*
	exp: '(' exp ')'                                    #exp_paren
	| atom                                          #atom_exp
	| tuple                                         #tuple_exp
	| exp 'where' bind ((',' bind)* 'end')?         #where_exp
	| lambda                                        #lambda_exp
    | ID                                            #variable
    | left=exp OP_INFIX right=exp                   #infix_op
    | call                                          #function_call
    | 'if' pred=exp 'then' then=exp 'else' else=exp #if_then_else
    | '[' explist? ']'                              #array
    ;
	*/
	public NExp visitExp(ExpContext ctx)
	{
		if(ctx instanceof Exp_parenContext e) return visitExp_paren(e);
		if(ctx instanceof Atom_expContext a) return visitAtom_exp(a);
		if(ctx instanceof Tuple_expContext t) return visitTuple_exp(t);
		if(ctx instanceof Lambda_expContext l) return visitLambda_exp(l);
		if(ctx instanceof Function_callContext f) return visitFunction_call(f);
		if(ctx instanceof Where_expContext w) return visitWhere_exp(w);
		if(ctx instanceof BranchContext i) return visitBranch(i);
		if(ctx instanceof ArrayContext a) return visitArray(a);
		if(ctx instanceof Infix_opContext i) return visitInfix_op(i);
		if(ctx instanceof VariableContext v) return visitVariable(v);
		throw new RuntimeException("Unknown expression: "+new ParseTreeSourcifier().sourcify(ctx));
	}

	public NLambda visitLambda_exp(Lambda_expContext ctx)
	{
		return visitLambda(ctx.lambda());
	}

	public NExp visitExp_paren(Exp_parenContext ctx)
	{
		return visitExp(ctx.exp());
	}

	public NAtom visitAtom_exp(Atom_expContext ctx)
	{
		return visitAtom(ctx.atom());
	}

	public NCall visitFunction_call(Function_callContext ctx)
	{
		return visitCall(ctx.call());
	}

	public NArray visitArray(ArrayContext ctx)
	{
		return NArray.builder()
		             .position(Position.of(ctx.start,ctx.stop))
		             .exps(ctx.explist().exp().stream().map(this::visitExp).toList())
		             .build();
	}

	public NOp visitInfix_op(Infix_opContext ctx)
	{
		return NOp.builder()
		          .position(Position.of(ctx.start,ctx.stop))
		          .op(ctx.OP_INFIX().getText())
		          .left(visitExp(ctx.left))
		          .right(visitExp(ctx.right))
		          .build();
	}

	public NWhere visitWhere_exp(Where_expContext ctx)
	{
		return NWhere.builder()
		             .position(Position.of(ctx.start,ctx.stop))
		             .exp(visitExp(ctx.exp()))
		             .binds(ctx.bind().stream().map(this::visitBind).toList())
		             .build();
	}

	public NBranch visitBranch(BranchContext ctx)
	{
		return NBranch.builder()
		              .position(Position.of(ctx.start,ctx.stop))
		              .pred(visitExp(ctx.pred))
		              .then(visitExp(ctx.then))
		              .else_(visitExp(ctx.else_))
		              .build();
	}

	public NVariable visitVariable(VariableContext ctx)
	{
		return NVariable.builder().name(ctx.ID().getText()).build();
	}

	public NTuple visitTuple_exp(Tuple_expContext ctx)
	{
		return visitTuple(ctx.tuple());
	}

	public NLambda visitLambda(LambdaContext ctx)
	{
		return NLambda.builder()
		              .position(Position.of(ctx.start,ctx.stop))
		              .args(ctx.funct_args().arg().stream().map(this::visitArg).toList())
		              .exp(visitExp(ctx.exp()))
		              .build();
	}

	public NCall visitCall(CallContext ctx)
	{
		return NCall.builder()
		            .position(Position.of(ctx.start,ctx.stop))
		            .name(ctx.ID().getText())
		            .args(ctx.exp().stream().map(this::visitExp).toList())
		            .build();
	}

	public NTuple visitTuple(TupleContext ctx)
	{
		return NTuple.builder()
		             .position(Position.of(ctx.start,ctx.stop))
		             .exps(ctx.explist().exp().stream().map(this::visitExp).toList())
		             .build();
	}

	public List<NExp> visitExplist(ExplistContext ctx)
	{
		return ctx.exp().stream().map(this::visitExp).toList();
	}

	public NAtom visitAtom(AtomContext ctx)
	{
		if(ctx.number()!=null) return visitNumber(ctx.number());
		if(ctx.string()!=null) return visitString(ctx.string());
		if(ctx.bool()!=null) return visitBool(ctx.bool());
		return visitNil(ctx.nil());
	}

	private NNil visitNil(NilContext ctx)
	{
		return NNil.builder()
		           .position(Position.of(ctx.start,ctx.stop))
		           .build();
	}

	private NString visitString(StringContext ctx)
	{
		return NString.builder()
		              .position(Position.of(ctx.start,ctx.stop))
		              .value(ctx.STRING().getText())
		              .build();
	}

	private NNumber visitNumber(NumberContext ctx)
	{
		Position position=Position.of(ctx.start,ctx.stop);
		if(ctx.INT_MOD()!=null)
		{
			var str=ctx.INT_MOD().toString();
			Either<Long,BigInteger> value;
			try
			{
				value=Either.left(Long.valueOf(str));
			}
			catch(Exception e)
			{
				value=Either.right(new BigInteger(str));
			}
			return NInt.builder()
			           .position(position)
			           .value(value)
			           .build();
		}
		var str=ctx.INT_MOD().getText();
		Either<Double,BigDecimal> value;
		try
		{
			value=Either.left(Double.valueOf(str));
		}
		catch(Exception e)
		{
			value=Either.right(new BigDecimal(str));
		}
		return NFloat.builder()
		             .position(position)
		             .value(value)
		             .build();
	}

	public NBool visitBool(BoolContext ctx)
	{
		return NBool.builder()
		            .position(Position.of(ctx.start,ctx.stop))
		            .value(ctx.TRUE()!=null)
		            .build();
	}
}
