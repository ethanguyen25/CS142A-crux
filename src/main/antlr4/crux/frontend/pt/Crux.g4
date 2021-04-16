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
 : type Identifier SemiColon
 ;

OPEN_BRACKET : '[' ;
CLOSE_BRACKET : ']';

OPEN_PAREN : '(';
CLOSE_PAREN : ')';

OPEN_BRACE : '{';
CLOSE_BRACE : '}';

OR : '||';

NOT : '!';

IF : 'if';
ELSE : 'else';
LOOP : 'loop';


CONTINUE : 'continue';
BREAK : 'break';
RETURN : 'return';

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




arrayDeclaration
 : type Identifier OPEN_BRACKET Integer CLOSE_BRACKET SemiColon;

functionDefinition
 : type Identifier OPEN_PAREN parameterList CLOSE_PAREN statementBlock
 ;

parameterList
 : ( parameter ( COMMA parameter )* )?
 ;

parameter
 : type Identifier
 ;

statementBlock
 : OPEN_BRACE statementList CLOSE_BRACE
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

designator : Identifier ( OPEN_BRACKET expression0 CLOSE_BRACKET )?;

op0 : GREATER_EQUAL | LESSER_EQUAL | NOT_EQUAL | EQUAL | GREATER_THAN | LESS_THAN;
op1 : ADD | SUB | OR;
op2 : MUL | DIV | AND;

expression0 : expression1 ( op0 expression1 )?;
expression1 : expression2 | expression1 op1 expression2;
expression2 : expression3 | expression2 op2 expression3;
expression3 : NOT expression3 | OPEN_PAREN expression0 CLOSE_PAREN | designator |
              callExpression | literal;

callExpression : Identifier OPEN_PAREN expressionList CLOSE_PAREN;
expressionList : ( expression0 ( COMMA expression0 )* )?;


assignmentStatement : designator ASSIGN expression0 SemiColon;
callStatement : callExpression SemiColon;
ifStatement : IF expression0 statementBlock ( ELSE statementBlock )?;
loopStatement : LOOP statementBlock;
breakStatement : BREAK SemiColon;
continueStatement : CONTINUE SemiColon;
returnStatement : RETURN expression0 SemiColon;
statement : variableDeclaration | callStatement | assignmentStatement |
            assignmentStatement | ifStatement | loopStatement |
            breakStatement | continueStatement | returnStatement;

AND : '&&';


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

