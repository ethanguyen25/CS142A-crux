package crux.frontend.types;

import crux.frontend.Symbol;
import crux.frontend.ast.*;
import crux.frontend.ast.traversal.NullNodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class will associate types with the AST nodes from Stage 2
 */

public final class TypeChecker {
  private final HashMap<Node, Type> typeMap = new HashMap<>();
  private final ArrayList<String> errors = new ArrayList<>();

  public ArrayList<String> getErrors() {
    return errors;
  }

  public void check(DeclarationList ast) {
    var inferenceVisitor = new TypeInferenceVisitor();
    inferenceVisitor.visit(ast);
  }

  /**
   * Helper function, should be used to add error into the errors array
   */

  private void addTypeError(Node n, String message) {
    errors.add(String.format("TypeError%s[%s]", n.getPosition(), message));
  }

  /**
   * Helper function, should be used to add types into the typeMap if the Type is an ErrorType then
   * it will call addTypeError
   */

  private void setNodeType(Node n, Type ty) {
    typeMap.put(n, ty);
    if (ty.getClass() == ErrorType.class) {
      var error = (ErrorType) ty;
      addTypeError(n, error.getMessage());
    }
  }

  /**
   * Helper to retrieve Type from the map
   */

  public Type getType(Node n) {
    return typeMap.get(n);
  }


  /**
   * These calls will visit each AST node and try to resolve it's type with the help of the
   * symbolTable.
   */

  private final class TypeInferenceVisitor extends NullNodeVisitor<Void> {
    private Symbol currentFunctionSymbol;
    private Type currentFunctionReturnType;

    private boolean lastStatementReturns;
    private boolean hasBreak;

    @Override
    public Void visit(Name name) {
      return null;
    }

    @Override
    public Void visit(ArrayDeclaration arrayDeclaration) {
      return null;
    }

    @Override
    public Void visit(Assignment assignment) {
      return null;
    }

    @Override
    public Void visit(Break brk) {
      return null;
    }

    @Override
    public Void visit(Call call) {
      return null;
    }

    @Override
    public Void visit(Continue cont) {
      return null;
    }

    @Override
    public Void visit(DeclarationList declarationList) {
      return null;
    }

    @Override
    public Void visit(Dereference dereference) {
      return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      return null;
    }

    @Override
    public Void visit(IfElseBranch ifElseBranch) {
      return null;
    }

    @Override
    public Void visit(ArrayAccess access) {
      return null;
    }

    @Override
    public Void visit(LiteralBool literalBool) {
      return null;
    }

    @Override
    public Void visit(LiteralInt literalInt) {
      return null;
    }

    @Override
    public Void visit(Loop loop) {
      return null;
    }

    @Override
    public Void visit(OpExpr op) {
      return null;
    }

    @Override
    public Void visit(Return ret) {
      return null;
    }

    @Override
    public Void visit(StatementList statementList) {
      return null;
    }

    @Override
    public Void visit(VariableDeclaration variableDeclaration) {
      return null;
    }
  }
}
