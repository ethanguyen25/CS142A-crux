package crux.midend;

import crux.frontend.Symbol;
import crux.frontend.ast.*;
import crux.frontend.ast.OpExpr.Operation;
import crux.frontend.ast.traversal.NodeVisitor;
import crux.frontend.types.*;
import crux.midend.ir.core.*;
import crux.midend.ir.core.insts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Convert AST to IR and build the CFG.
 */

// Hint: You might want to change the Void parameter of the
// NodeVisitor and the return type of the visit methods to some other
// class so that the visit methods can return information to the
// caller.

class Pair {
  Instruction start;
  Instruction end;
  Variable val;

  Pair(Instruction start, Instruction end, Variable val) {
    this.start= start;
    this.end=end;
    this.val = val;
  }

  Instruction getStart() {
    return start;
  }

  Instruction getEnd() {
    return end;
  }

  Variable getVal() {
    return val;
  }
}




public final class ASTLower implements NodeVisitor<Pair> {
  private Program mCurrentProgram = null;
  private Function mCurrentFunction = null;

  private Map<Symbol, Variable> mCurrentLocalVarMap = null;
  private TypeChecker checker;

  //ADDED
  public Instruction mLastInstruction;
  public Value mCurrentValue;


  /**
   * A constructor to initialize member variables
   */

  public ASTLower(TypeChecker checker) {
    this.checker = checker;
  }

  public Program lower(DeclarationList ast) {
    visit(ast);
    return mCurrentProgram;
  }

  @Override
  public Pair visit(DeclarationList declarationList) {
    mCurrentProgram = new Program();
    for (var e: declarationList.getChildren())
    {
      e.accept(this);
    }

    return null;
  }

  /*
   * Function declaration
   */

  @Override
  public Pair visit(FunctionDefinition functionDefinition) {
    return null;
  }

  @Override
  public Pair visit(StatementList statementList) {
    return null;
  }

  /**
   * Declarations, could be either local or Global
   */

  @Override
  public Pair visit(VariableDeclaration variableDeclaration) {
    return null;
  }

  /**
   * Create a declaration for array
   */

  @Override
  public Pair visit(ArrayDeclaration arrayDeclaration) {
    return null;
  }

  @Override
  public Pair visit(Name name) {
    return null;
  }

  /**
   * Assignment
   */

  @Override
  public Pair visit(Assignment assignment) {
    return null;
  }

  /**
   * Function call
   */

  @Override
  public Pair visit(Call call) {
    return null;
  }

  /**
   * Handle Operations like Arithmetics and Comparisons Also to handle logical operations (and, or,
   * not)
   */

  @Override
  public Pair visit(OpExpr operation) {
    Pair left = operation.getLeft().accept(this);
    Pair right = operation.getRight().accept(this);
    left.getEnd().setNext(0, right.getStart());

    LocalVar tempVar = mCurrentFunction.getTempVar(checker.getType(operation));
    LocalVar lhs = mCurrentFunction.getTempVar(left.getVal().getType());
    LocalVar rhs = mCurrentFunction.getTempVar(right.getVal().getType());
    switch (operation.getOp()){
      case GE:
        CompareInst geInst = new CompareInst(tempVar, CompareInst.Predicate.GE, lhs, rhs);
        right.getEnd().setNext(0, geInst);
        return new Pair(left.getStart(), geInst, tempVar);
        //break;
      case LE:
        CompareInst leInst = new CompareInst(tempVar, CompareInst.Predicate.LE, lhs, rhs);
        right.getEnd().setNext(0, leInst);
        return new Pair(left.getStart(), leInst , tempVar);
        //break;
      case NE:
        CompareInst neInst = new CompareInst(tempVar, CompareInst.Predicate.NE, lhs, rhs);
        right.getEnd().setNext(0, neInst);
        return new Pair(left.getStart(), neInst , tempVar);
        //break;
      case EQ:
        CompareInst eqInst = new CompareInst(tempVar, CompareInst.Predicate.EQ, lhs, rhs);
        right.getEnd().setNext(0, eqInst);
        return new Pair(left.getStart(), eqInst , tempVar);
        //break;
      case GT:
        CompareInst gtInst = new CompareInst(tempVar, CompareInst.Predicate.GT, lhs, rhs);
        right.getEnd().setNext(0, gtInst);
        return new Pair(left.getStart(), gtInst , tempVar);
        //break;
      case LT:
        CompareInst ltInst = new CompareInst(tempVar, CompareInst.Predicate.LT, lhs, rhs);
        right.getEnd().setNext(0, ltInst);
        return new Pair(left.getStart(), ltInst , tempVar);
        //break;
      case ADD:
        BinaryOperator addBO = new BinaryOperator(BinaryOperator.Op.Add, tempVar, lhs, rhs);
        return new Pair(left.getStart(), addBO, tempVar);
        //break;
      case SUB:
        BinaryOperator subBO = new BinaryOperator(BinaryOperator.Op.Sub, tempVar, lhs, rhs);
        return new Pair(left.getStart(), subBO, tempVar);
        //break;
      case MULT:
        BinaryOperator mulBO = new BinaryOperator(BinaryOperator.Op.Mul, tempVar, lhs, rhs);
        return new Pair(left.getStart(), mulBO, tempVar);
        //break;
      case DIV:
        BinaryOperator divBO = new BinaryOperator(BinaryOperator.Op.Div, tempVar, lhs, rhs);
        return new Pair(left.getStart(), divBO, tempVar);
        //break;
      case LOGIC_AND:
        //break;
      case LOGIC_OR:
        //break;
      case LOGIC_NOT:
        //break;
    }
    return null;
  }

  @Override
  public Pair visit(Dereference dereference) {
    return null;
  }

  private Pair visit(Expression expression) {
    return null;
  }

  /**
   * ArrayAccess
   */

  @Override
  public Pair visit(ArrayAccess access) {
    return null;
  }

  /**
   * Literal
   */

  @Override
  public Pair visit(LiteralBool literalBool) {
    BooleanConstant value = BooleanConstant.get(mCurrentProgram, literalBool.getValue());
    LocalVar tempVar = mCurrentFunction.getTempVar(new BoolType());
    CopyInst copyInst = new CopyInst(tempVar, value);
    return new Pair(copyInst, copyInst, tempVar);
  }

  /**
   * Literal
   */

  @Override
  public Pair visit(LiteralInt literalInt) {
    IntegerConstant value = IntegerConstant.get(mCurrentProgram, literalInt.getValue());
    LocalVar tempVar = mCurrentFunction.getTempVar(new IntType());
    CopyInst copyInst = new CopyInst(tempVar, value);
    return new Pair(copyInst, copyInst, tempVar);

    /*addEdge(mLastInstruction, copyInst);
    mCurrentValue = tempVar;
    mLastInstruction = copyInst;
    return null;*/
  }

  /**
   * Return
   */

  @Override
  public Pair visit(Return ret) {
    return null;
  }

  /**
   * Break Node
   */

  @Override
  public Pair visit(Break brk) {
    return null;
  }

  /**
   * Continue Node
   */

  @Override
  public Pair visit(Continue cnt) {
    return null;
  }

  /**
   * Control Structures
   */

  @Override
  public Pair visit(IfElseBranch ifElseBranch) {
    return null;
  }

  /**
   * Loop
   */

  @Override
  public Pair visit(Loop loop) {
    return null;
  }
}
