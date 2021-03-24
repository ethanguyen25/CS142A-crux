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

public final class ASTLower implements NodeVisitor<Void> {
  private Program mCurrentProgram = null;
  private Function mCurrentFunction = null;

  private Map<Symbol, Variable> mCurrentLocalVarMap = null;
  private TypeChecker checker;

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
  public Void visit(DeclarationList declarationList) {
    return null;
  }

  /*
   * Function declaration
   */

  @Override
  public Void visit(FunctionDefinition functionDefinition) {
    return null;
  }

  @Override
  public Void visit(StatementList statementList) {
    return null;
  }

  /**
   * Declarations, could be either local or Global
   */

  @Override
  public Void visit(VariableDeclaration variableDeclaration) {
    return null;
  }

  /**
   * Create a declaration for array
   */

  @Override
  public Void visit(ArrayDeclaration arrayDeclaration) {
    return null;
  }

  @Override
  public Void visit(Name name) {
    return null;
  }

  /**
   * Assignment
   */

  @Override
  public Void visit(Assignment assignment) {
    return null;
  }

  /**
   * Function call
   */

  @Override
  public Void visit(Call call) {
    return null;
  }

  /**
   * Handle Operations like Arithmetics and Comparisons Also to handle logical operations (and, or,
   * not)
   */

  @Override
  public Void visit(OpExpr operation) {
    return null;
  }

  @Override
  public Void visit(Dereference dereference) {
    return null;
  }

  private Void visit(Expression expression) {
    return null;
  }

  /**
   * ArrayAccess
   */

  @Override
  public Void visit(ArrayAccess access) {
    return null;
  }

  /**
   * Literal
   */

  @Override
  public Void visit(LiteralBool literalBool) {
    return null;
  }

  /**
   * Literal
   */

  @Override
  public Void visit(LiteralInt literalInt) {
    return null;
  }

  /**
   * Return
   */

  @Override
  public Void visit(Return ret) {
    return null;
  }

  /**
   * Break Node
   */

  @Override
  public Void visit(Break brk) {
    return null;
  }

  /**
   * Continue Node
   */

  @Override
  public Void visit(Continue cnt) {
    return null;
  }

  /**
   * Control Structures
   */

  @Override
  public Void visit(IfElseBranch ifElseBranch) {
    return null;
  }

  /**
   * Loop
   */

  @Override
  public Void visit(Loop loop) {
    return null;
  }
}
