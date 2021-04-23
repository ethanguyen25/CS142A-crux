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
      Type t = name.getSymbol().getType();
      setNodeType(name, new AddressType(t));
      return null;
    }

    @Override
    public Void visit(ArrayDeclaration arrayDeclaration) {
      //if (arrayDeclaration.getSymbol().getType() == new IntType())
      return null;
    }

    @Override
    public Void visit(Assignment assignment) {
      assignment.getLocation().accept(this);
      Type locationType = getType(assignment.getLocation());
      assignment.getValue().accept(this);
      Type valueType = getType(assignment.getValue());

      setNodeType(assignment, locationType.assign(valueType));
      return null;
    }

    @Override
    public Void visit(Break brk) {
      return null;
    }

    @Override
    public Void visit(Call call) {
      TypeList tList = TypeList.of();
      for (var e: call.getArguments()){
        e.accept(this);
        tList.append(getType(e));
      }
      Type t = call.getCallee().getType().call(tList);
      setNodeType(call, t);
      return null;
    }

    @Override
    public Void visit(Continue cont) {
      return null;
    }

    @Override
    public Void visit(DeclarationList declarationList) {
      for (var e: declarationList.getChildren()){
        e.accept(this);
      }
      return null;
    }

    @Override
    public Void visit(Dereference dereference) {
      return null;
    }

    @Override
    public Void visit(FunctionDefinition functionDefinition) {
      if (functionDefinition.getSymbol().getName() == "main") {
        Type retType = ((FuncType)functionDefinition.getSymbol().getType()).getRet();
        if ( retType.getClass() != VoidType.class){
          addTypeError(functionDefinition, "FunctionDefinitionError");
        } else {
          setNodeType(functionDefinition, new VoidType());
        }
      }

      TypeList tl = ((FuncType) functionDefinition.getSymbol().getType()).getArgs();
      for (var e: tl){
        if (!(e.getClass() == IntType.class || e.getClass() == BoolType.class)){
          addTypeError(functionDefinition, "FunctionDefinitionError");
        }
      }

      for (var e: functionDefinition.getStatements().getChildren()){
        e.accept(this);
      }

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
      setNodeType(literalBool, new BoolType());
      return null;
    }

    @Override
    public Void visit(LiteralInt literalInt) {
      setNodeType(literalInt, new IntType());
      return null;
    }

    @Override
    public Void visit(Loop loop) {
      return null;
    }

    @Override
    public Void visit(OpExpr op) {
      op.getLeft().accept(this);
      if (op.getRight() != null){
        op.getRight().accept(this);
      }
      Type leftType = getType(op.getLeft());
      Type rightType = getType(op.getRight());

      switch (op.getOp()){
        case ADD:
          var a = leftType.add(rightType);
          setNodeType(op, a);
        case SUB:
          var s = leftType.sub(rightType);
          setNodeType(op, s);
        case MULT:
          var m = leftType.mul(rightType);
          setNodeType(op, m);
        case DIV:
          var d = leftType.div(rightType);
          setNodeType(op, d);
        case GE:
        case LE:
        case NE:
        case EQ:
        case GT:
        case LT:
          var many = leftType.compare(rightType);
          setNodeType(op, many);
        case LOGIC_AND:
          var la = leftType.and(rightType);
          setNodeType(op, la);
        case LOGIC_OR:
          var lo = leftType.or(rightType);
          setNodeType(op, lo);
        case LOGIC_NOT:
          var ln = leftType.not();
          setNodeType(op, ln);
        default:
          addTypeError(op, "OpExprError");
      }
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
