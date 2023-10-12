grammar NuML;

compile_unit: module* EOF;

module: 'module' ID module_stat* 'endmodule' ID;

module_stat: decl | bind;

decl: var_decl | funct_decl;

var_decl: 'let' bind;
funct_decl: 'let' name=ID funct_args '==>' type '=' block;

funct_args: arg_list;

arg: name=ID ':' type;
arg_list: arg+;

type: ID | tuple_type;

tuple_type: '(' typelist ',)';

typelist: type (',' type)*;

block: module_stat* exp;

bind: ID '=' exp;

exp: '(' exp ')'                                    #exp_paren
	| atom                                          #atom_exp
	| tuple                                         #tuple_exp
	| exp 'where' bind ((',' bind)* 'end')?         #where_exp
	| lambda                                        #lambda_exp
    | ID                                            #variable
    | left=exp OP_INFIX right=exp                   #infix_op
    | call                                          #function_call
    | 'if' pred=exp 'then' then=exp 'else' else=exp #if_then_else
    ;

lambda: 'funct' funct_args '==>' exp;

call: '$$'? ID exp+;

tuple: '(' explist ',)';

explist: exp (',' exp)*;

atom: INT_MOD | FLOAT | STRING | bool | NIL;

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

COMMENT: '/|' .*? '|\\' -> channel(HIDDEN);
LINE_COMMENT: '//|' .*? '\n' -> channel(HIDDEN);