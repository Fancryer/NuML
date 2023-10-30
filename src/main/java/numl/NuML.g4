grammar NuML;

compile_unit: module* EOF;

module: 'module' ID stat* 'end';

stat: decl | bind;

decl: 'let' bind                                        #var_decl
	| 'let' name=ID funct_args ('==>' type)? '=' block  #funct_decl
	;

funct_args: arg+;
arg: ID (':' type)?;
type: ID | tuple_type | array_type;

tuple_type: '(' type (',' type)* ',)';

array_type: '[' type ']';
//array_type: type '[' ']';

block: stat* exp;

bind: ID '=' exp;

exp: '(' exp ')'                                    #exp_paren
	| atom                                          #atom_exp
	| tuple                                         #tuple_exp
	| exp 'where' bind ((',' bind)* 'end')?         #where_exp
	| lambda                                        #lambda_exp
    | ID                                            #variable
    | left=exp OP_INFIX right=exp                   #infix_op
    | call                                          #function_call
    | 'if' pred=exp 'then' then=exp 'else' else=exp #branch
    | '[' explist? ']'                              #array
    ;

lambda: 'funct' funct_args '==>' exp;

call: NATIVE_VAR_PREFIX? ID exp+;

tuple: '(' explist ',)';

explist: exp (',' exp)*;

atom: number | string | bool | nil;

number: INT_MOD | FLOAT;
string: STRING;
nil: NIL;

NATIVE_VAR_PREFIX: '$$';

bool: TRUE | FALSE;

OP_INFIX: '+' | '-' | '*' | '/' | '<' | '>' | '==' | '<>' | '<=' | '>=';

TRUE: 'true';
FALSE: 'false';
NIL: 'nil';

ID: [a-zA-Z_]+ [a-zA-Z0-9_]*;
WS: [ \t\n\r]+ -> channel(HIDDEN);
INT_MOD: ('~' | '+')? INT_POS;

FLOAT: INT_MOD '.' INT_POS EXP?     // 1.35, 1.35E-9, 0.3, -4.5
    | INT_MOD EXP                   // 1e10 -3e4
    | INT_MOD                       // -3, 45
    ;

STRING: '"' (~'"'|'\\"')* '"';

fragment INT_POS: '0' | [1-9] [0-9]*; // no leading zeros
fragment EXP: [Ee] ('+' | '~')? INT_POS;

LINE_COMMENT: '//|' .*? '\n' -> channel(HIDDEN);
COMMENT: '/|' .*? '|\\' -> channel(HIDDEN);
