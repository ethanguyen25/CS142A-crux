grammar Crux;
program
 : declarationList EOF
 ;

declarationList
 : declaration*
 ;

declaration
 : variableDeclaration
 | arrayDeclaration
 | functionDefinition
 ;

variableDeclaration
 : type Identifier ';'
 ;

arrayDeclaration
 : type Identifier '[' Integer ']' ';';

functionDefinition
 : type Identifier '(' parameterList ')' statementBlock
 ;

parameterList
 : ( parameter ( ',' parameter )* )?
 ;

parameter
 : type Identifier
 ;

statementBlock
 : '{' statementList '}'
 ;

statementList
 : statement*
 ;

type
 : Identifier
 ;

literal
 : Integer
 | True
 | False
 ;

designator : Identifier ( '[' expression0 ']' )?;

op0 : '>=' | '<=' | '!=' | '==' | '>' | '<';
op1 : '+' | '-' | '||';
op2 : '*' | '/' | '&&';

expression0 : expression1 ( op0 expression1 )?;
expression1 : expression2 | expression1 op1 expression2;
expression2 : expression3 | expression2 op2 expression3;
expression3 : '!' expression3 | '(' expression0 ')' | designator |
              callExpression | literal;

callExpression : Identifier '(' expressionList ')';
expressionList : ( expression0 ( ',' expression0 )* )?;


assignmentStatement : designator '=' expression0 ';';
callStatement : callExpression ';';
ifStatement : 'if' expression0 statementBlock ( 'else' statementBlock )?;
loopStatement : 'loop' statementBlock;
breakStatement : 'break' ';';
continueStatement : 'continue' ';';
returnStatement : 'return' expression0 ';';
statement : variableDeclaration | callStatement | assignmentStatement |
            assignmentStatement | ifStatement | loopStatement |
            breakStatement | continueStatement | returnStatement;



SemiColon: ';';

Integer
 : '0'
 | [1-9] [0-9]*
 ;

True: 'true';
False: 'false';

Identifier
 : [a-zA-Z] [a-zA-Z0-9_]*
 ;

WhiteSpaces
 : [ \t\r\n]+ -> skip
 ;

Comment
 : '//' ~[\r\n]* -> skip
 ;

AND : '&&';
OR : '||';
NOT : '!';
IF : 'if';
ELSE : 'else';
LOOP : 'loop';
CONTINUE : 'continue';
BREAK : 'break';
RETURN : 'return';
OPEN_PAREN : '(';
CLOSE_PAREN : ')';
OPEN_BRACE : '{';
CLOSE_BRACE : '}';
OPEN_BRACKET : '[';
CLOSE_BRACKET : ']';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
GREATER_EQUAL : '>=';
LESSER_EQUAL : '<=';
NOT_EQUAL : '!=';
EQUAL : '==';
GREATER_THAN: '>';
LESS_THAN : '<';
ASSIGN : '=';
COMMA : ',';
