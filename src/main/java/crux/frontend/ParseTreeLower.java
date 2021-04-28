//CruxParser.class
//ctx.declarationList()   will return a list of declarations
//   then look at each declaration list one by one
//from the programContext, envoke the visit methods on each of the declarations.


package crux.frontend;

import crux.frontend.ast.*;
import crux.frontend.pt.CruxBaseVisitor;
import crux.frontend.pt.CruxParser;
import crux.frontend.types.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will convert the parse tree generated by ANTLR to AST It follows the visitor pattern
 * where declarations will be by DeclarationVisitor Class Statements will be resolved by
 * StatementVisitor Class Expressions will be resolved by ExpressionVisitor Class
 */

public final class ParseTreeLower {
  private final DeclarationVisitor declarationVisitor = new DeclarationVisitor();
  private final StatementVisitor statementVisitor = new StatementVisitor();
  private final ExpressionVisitor expressionVisitor = new ExpressionVisitor(true);
  private final ExpressionVisitor locationVisitor = new ExpressionVisitor(false);

  private final SymbolTable symTab;

  public ParseTreeLower(PrintStream err) {
    symTab = new SymbolTable(err);
  }

  private static Position makePosition(ParserRuleContext ctx) {
    var start = ctx.start;
    return new Position(start.getLine());
  }

  /**
   *
   * @return True if any errors
   */

  public boolean hasEncounteredError() {
    return symTab.hasEncounteredError();
  }

  /**
   * Lower top-level parse tree to AST
   *
   * @return a {@link DeclarationList} object representing the top-level AST.
   */

  public DeclarationList lower(CruxParser.ProgramContext program) {
    Position pos = makePosition(program);
    List<Declaration> decList = new ArrayList<>();
    for (var decContext : program.declarationList().declaration()) {
      decList.add(decContext.accept(declarationVisitor));
    }
    return new DeclarationList(pos,decList);
  }

  /**
   * Lower statement list by lower individual statement into AST.
   *
   * @return a {@link StatementList} AST object.
   */

    private StatementList lower(CruxParser.StatementListContext statementList) {
      Position pos = makePosition(statementList);
      List<Statement> sList = new ArrayList<>();
      for (var e : statementList.statement()) {
        sList.add(e.accept(statementVisitor));
      }
      return new StatementList(pos, sList);
    }


  /**
   * Similar to {@link #lower(CruxParser.StatementListContext)}, but handling symbol table as well.
   *
   * @return a {@link StatementList} AST object.
   */

    private StatementList lower(CruxParser.StatementBlockContext statementBlock) {
      symTab.enter();
      StatementList sList = lower(statementBlock.statementList());
      symTab.exit();
      return sList;
    }


  /**
   * A parse tree visitor to create AST nodes derived from {@link Declaration}
   */

  private final class DeclarationVisitor extends CruxBaseVisitor<Declaration> {
    /**
     * Visit a parse tree variable declaration and create an AST {@link VariableDeclaration}
     *
     * @return an AST {@link VariableDeclaration}
     */

    @Override
    public VariableDeclaration visitVariableDeclaration(CruxParser.VariableDeclarationContext ctx) {
      Position pos = makePosition(ctx);
      String name = ctx.Identifier().getText();

      String ctxType = ctx.type().getText();
      Type varType = new BoolType();
      if (ctxType.equals("int")) {
        varType = new IntType();
      }

      Symbol symbol = symTab.add(pos, name, varType);
      return new VariableDeclaration(pos, symbol);
    }

    /**
     * Visit a parse tree array declaration and create an AST {@link ArrayDeclaration}
     *
     * @return an AST {@link ArrayDeclaration}
     */

    @Override
    public Declaration visitArrayDeclaration(CruxParser.ArrayDeclarationContext ctx) {
      Position pos = makePosition(ctx);
      String name = ctx.Identifier().getText();

      String ctxType = ctx.type().getText();
      Type varType = new BoolType();
      if (ctxType.equals("int")) {
        varType = new IntType();
      }

      String sExtent = ctx.Integer().getText();
      long lExtent = Long.parseLong(sExtent);
      Type arrayType = new ArrayType(lExtent, varType);

      Symbol symbol = symTab.add(pos, name, arrayType);
      return new ArrayDeclaration(pos, symbol);
    }


    /**
     * Visit a parse tree function definition and create an AST {@link FunctionDefinition}
     *
     * @return an AST {@link FunctionDefinition}
     */

    @Override
    public Declaration visitFunctionDefinition(CruxParser.FunctionDefinitionContext ctx) {
      Position pos = makePosition(ctx);

      //Get return type of function for FuncType()
      Type retType;
      if (ctx.type().getText().equals("int")) {
        retType = new IntType();
      } else if (ctx.type().getText().equals("bool")) {
        retType = new BoolType();
      } else {
        retType = new VoidType();
      }

      TypeList tList = TypeList.of();
      for (var e : ctx.parameterList().parameter()) {
        if (e.type().getText().equals("int")) {
          tList.append(new IntType());
        } else if (e.type().getText().equals("bool")) {
          tList.append(new BoolType());
        }
      }
      Symbol funcSymbol = symTab.add(pos, ctx.Identifier().getText(), new FuncType(tList, retType));
      symTab.enter();

      //TypeList for FuncType()  and  List for FunctionDefinitionNode
      List<Symbol> symList = new ArrayList<>();
      for (var e : ctx.parameterList().parameter()) {
        if (e.type().getText().equals("int")) {
          //tList.append(new IntType());  //Don't need to add again (?)
          symList.add(new Symbol(e.Identifier().getText(), new IntType()));
          symTab.add(pos, e.Identifier().getText(), new IntType()); //Adding func params to symTab
        } else if (e.type().getText().equals("bool")) {
          //tList.append(new BoolType());  //Don't need to add again (?)
          symList.add(new Symbol(e.Identifier().getText(), new BoolType()));
          symTab.add(pos, e.Identifier().getText(), new BoolType()); //Adding func params to symTab
        }
      }

      StatementList funcBody = lower(ctx.statementBlock());

      FunctionDefinition funcDef = new FunctionDefinition(pos, funcSymbol, symList, funcBody);

      symTab.exit();
      return funcDef;
    }
  }

    /**
     * A parse tree visitor to create AST nodes derived from {@link Statement}
     */

    private final class StatementVisitor extends CruxBaseVisitor<Statement> {
      /**
       * Visit a parse tree variable declaration and create an AST {@link VariableDeclaration}. Since
       * {@link VariableDeclaration} is both {@link Declaration} and {@link Statement}, we simply
       * delegate this to
       * {@link DeclarationVisitor#visitArrayDeclaration(CruxParser.ArrayDeclarationContext)} which we
       * implement earlier.
       *
       * @return an AST {@link VariableDeclaration}
       */


      @Override
      public Statement visitVariableDeclaration(CruxParser.VariableDeclarationContext ctx) {
        return declarationVisitor.visitVariableDeclaration(ctx);
      }


      /**
       * Visit a parse tree assignment statement and create an AST {@link Assignment}
       *
       * @return an AST {@link Assignment}
       */

      @Override
      public Statement visitAssignmentStatement(CruxParser.AssignmentStatementContext ctx) {
        Position pos = makePosition(ctx);
        Expression lhs = ctx.designator().accept(locationVisitor);
        Expression rhs = ctx.expression0().accept(expressionVisitor);

        return new Assignment(pos, lhs, rhs);
      }


      /**
       * Visit a parse tree call statement and create an AST {@link Call}. Since {@link Call} is both
       * {@link Expression} and {@link Statement}, we simply delegate this to
       * {@link ExpressionVisitor#visitCallExpression(CruxParser.CallExpressionContext)} that we will
       * implement later.
       *
       * @return an AST {@link Call}
       */

      @Override
      public Statement visitCallStatement(CruxParser.CallStatementContext ctx) {
        return expressionVisitor.visitCallExpression(ctx.callExpression());
      }


      /**
       * Visit a parse tree if-else branch and create an AST {@link IfElseBranch}. The template code
       * shows partial implementations that visit the then block and else block recursively before
       * using those returned AST nodes to construct {@link IfElseBranch} object.
       *
       * @return an AST {@link IfElseBranch}
       */

      @Override
      public Statement visitIfStatement(CruxParser.IfStatementContext ctx) {
        Position pos = makePosition(ctx);
        Expression ifCond = ctx.expression0().accept(expressionVisitor);

        StatementList thenBlock = lower(ctx.statementBlock(0));
        StatementList elseBlock = (ctx.ELSE() != null) ? lower(ctx.statementBlock(1)) : new StatementList(pos, List.of());
        return new IfElseBranch(pos, ifCond, thenBlock, elseBlock);

      }


      /**
       * Visit a parse tree while loop and create an AST {@link Loop}. You'll going to use a similar
       * techniques as {@link #visitIfStatement(CruxParser.IfStatementContext)} to decompose this
       * construction.
       *
       * @return an AST {@link Loop}
       */

      @Override
      public Statement visitLoopStatement(CruxParser.LoopStatementContext ctx) {
        Position pos = makePosition(ctx);

        StatementList sList = lower(ctx.statementBlock());

        return new Loop(pos, sList);
      }


      /**
       * Visit a parse tree return statement and create an AST {@link Return}. Here we show a simple
       * example of how to lower a simple parse tree construction.
       *
       * @return an AST {@link Return}
       */

      @Override
      public Statement visitReturnStatement(CruxParser.ReturnStatementContext ctx) {
        Position pos = makePosition(ctx);

        Expression expr = ctx.expression0().accept(expressionVisitor);

        return new Return(pos, expr);
      }

      /**
       * Creates a Break node
       */

      @Override
      public Statement visitBreakStatement(CruxParser.BreakStatementContext ctx) {
        Position pos = makePosition(ctx);
        return new Break(pos);
      }


      /**
       * Creates a Continue node
       */

      @Override
      public Statement visitContinueStatement(CruxParser.ContinueStatementContext ctx) {
        Position pos = makePosition(ctx);
        return new Continue(pos);
      }

    }

    private final class ExpressionVisitor extends CruxBaseVisitor<Expression> {
      // Flag to enable dereferencing
      private final boolean dereferenceDesignator;

      private ExpressionVisitor(boolean dereferenceDesignator) {
        this.dereferenceDesignator = dereferenceDesignator;
      }

      /**
       * Parse Expression0 to OpExpr Node Parsing the expression should be exactly as described in the
       * grammer
       */

      @Override
      public Expression visitExpression0(CruxParser.Expression0Context ctx) {
        Position pos = makePosition(ctx);

        if (ctx.op0() == null) {
          return ctx.expression1(0).accept(expressionVisitor);
        } else {
          Expression lhs = ctx.expression1(0).accept(expressionVisitor);
          Expression rhs = ctx.expression1(1).accept(expressionVisitor);
          CruxParser.Op0Context op0Context = ctx.op0();
          OpExpr.Operation op;
          if (op0Context.LESSER_EQUAL() != null) {
            op = OpExpr.Operation.LE;
          } else if (op0Context.GREATER_EQUAL() != null) {
            op = OpExpr.Operation.GE;
          } else if (op0Context.NOT_EQUAL() != null) {
            op = OpExpr.Operation.NE;
          } else if (op0Context.EQUAL() != null) {
            op = OpExpr.Operation.EQ;
          } else if (op0Context.GREATER_THAN() != null) {
            op = OpExpr.Operation.GT;
          } else {
            op = OpExpr.Operation.LT;
          }
          return new OpExpr(pos, op, lhs, rhs);
        }
      }

      /**
       * Parse Expression1 to OpExpr Node Parsing the expression should be exactly as described in the
       * grammer
       */

      @Override
      public Expression visitExpression1(CruxParser.Expression1Context ctx) {
        Position pos = makePosition(ctx);

        if (ctx.op1() == null) {
          return ctx.expression2().accept(expressionVisitor);
        } else {
          Expression expr1 = ctx.expression1().accept(expressionVisitor);
          Expression expr2 = ctx.expression2().accept(expressionVisitor);
          CruxParser.Op1Context op1Context = ctx.op1();
          OpExpr.Operation op1;
          if (op1Context.ADD() != null) {
            op1 = OpExpr.Operation.ADD;
          } else if (op1Context.SUB() != null) {
            op1 = OpExpr.Operation.SUB;
          } else {
            op1 = OpExpr.Operation.LOGIC_OR;
          }

          return new OpExpr(pos, op1, expr1, expr2);
        }
      }


      /**
       * Parse Expression2 to OpExpr Node Parsing the expression should be exactly as described in the
       * grammer
       */

      @Override
      public Expression visitExpression2(CruxParser.Expression2Context ctx) {
        Position pos = makePosition(ctx);

        if (ctx.op2() == null) {
          return ctx.expression3().accept(expressionVisitor);
        } else {
          Expression expr2 = ctx.expression2().accept(expressionVisitor);
          Expression expr3 = ctx.expression3().accept(expressionVisitor);
          CruxParser.Op2Context op2Context = ctx.op2();
          OpExpr.Operation op2;
          if (op2Context.MUL() != null) {
            op2 = OpExpr.Operation.MULT;
          } else if (op2Context.DIV() != null) {
            op2 = OpExpr.Operation.DIV;
          } else {
            op2 = OpExpr.Operation.LOGIC_AND;
          }
          return new OpExpr(pos, op2, expr2, expr3);
        }
      }


      /**
       * Parse Expression3 to OpExpr Node Parsing the expression should be exactly as described in the
       * grammer
       */

      @Override
      public Expression visitExpression3(CruxParser.Expression3Context ctx) {
        if (ctx.designator() != null) {
          return ctx.designator().accept(expressionVisitor);
        } else if (ctx.literal() != null) {
          return ctx.literal().accept(expressionVisitor);
        } else if (ctx.callExpression() != null) {
          return ctx.callExpression().accept(expressionVisitor);
        } else if (ctx.expression0() != null) {
          return ctx.expression0().accept(expressionVisitor);
        } else if (ctx.NOT() != null) {
          Position pos = makePosition(ctx);
          OpExpr.Operation operation = OpExpr.Operation.LOGIC_NOT;
          Expression expr = ctx.expression3().accept(expressionVisitor);
          return new OpExpr(pos, operation, expr, null);
        }else {
          return ctx.expression3().accept(expressionVisitor);
        }
      }


      /**
       * Create an Call Node
       */

      @Override
      public Call visitCallExpression(CruxParser.CallExpressionContext ctx) {
        Position pos = makePosition(ctx);

        String name = ctx.Identifier().getText();
        Symbol callee = symTab.lookup(pos, name);

        List<Expression> exprList = new ArrayList<>();
        for (var e : ctx.expressionList().expression0()) {
          exprList.add(e.accept(expressionVisitor));
        }

        return new Call(pos, callee, exprList);
      }


      /**
       * visitDesignator will check for a name or ArrayAccess FYI it should account for the case when
       * the designator was dereferenced
       */

      @Override
      public Expression visitDesignator(CruxParser.DesignatorContext ctx) {
        Position pos = makePosition(ctx);
        String name = ctx.Identifier().getText();

        if (ctx.expression0() == null) {
          Symbol sym = symTab.lookup(pos, name);
          Name n = new Name(pos, sym);
          if (dereferenceDesignator) {
            return new Dereference(pos, n);
          }
          return n;
        } else {
          Symbol sym = symTab.lookup(pos, name);
          Name n = new Name(pos, sym);
          Expression expr0 = ctx.expression0().accept(expressionVisitor);

          ArrayAccess aa = new ArrayAccess(pos, n, expr0);
          if (dereferenceDesignator) {
            return new Dereference(pos, aa);
          } else {
            return aa;
          }
        }
      }


      /**
       * Create an Literal Node
       */

      @Override
      public Expression visitLiteral(CruxParser.LiteralContext ctx) {
        Position pos = makePosition(ctx);

        if (ctx.Integer() != null) {
          String snum = ctx.Integer().getText();
          long lnum = Long.parseLong(snum);
          return new LiteralInt(pos, lnum);
        } else if (ctx.True() != null) {
          return new LiteralBool(pos, true);
        } else {
          return new LiteralBool(pos, false);
        }
      }

    }
  }
