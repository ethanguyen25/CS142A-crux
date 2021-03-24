package crux.frontend.ast.traversal;

import crux.frontend.ast.*;

public abstract class DefaultNodeVisitor<T> implements NodeVisitor<T> {
  protected abstract T visitDefault(Node node);

  @Override
  public T visit(ArrayAccess access) {
    return visitDefault(access);
  }

  @Override
  public T visit(ArrayDeclaration arrayDeclaration) {
    return visitDefault(arrayDeclaration);
  }

  @Override
  public T visit(Assignment assignment) {
    return visitDefault(assignment);
  }

  @Override
  public T visit(Break brk) {
    return visitDefault(brk);
  }

  @Override
  public T visit(Call call) {
    return visitDefault(call);
  }

  @Override
  public T visit(Continue cont) {
    return visitDefault(cont);
  }

  @Override
  public T visit(DeclarationList declarationList) {
    return visitDefault(declarationList);
  }

  @Override
  public T visit(Dereference dereference) {
    return visitDefault(dereference);
  }

  @Override
  public T visit(FunctionDefinition functionDefinition) {
    return visitDefault(functionDefinition);
  }

  @Override
  public T visit(IfElseBranch ifElseBranch) {
    return visitDefault(ifElseBranch);
  }

  @Override
  public T visit(LiteralBool literalBool) {
    return visitDefault(literalBool);
  }

  @Override
  public T visit(LiteralInt literalInt) {
    return visitDefault(literalInt);
  }

  @Override
  public T visit(Name name) {
    return visitDefault(name);
  }

  @Override
  public T visit(OpExpr op) {
    return visitDefault(op);
  }

  @Override
  public T visit(Return ret) {
    return visitDefault(ret);
  }

  @Override
  public T visit(StatementList statementList) {
    return visitDefault(statementList);
  }

  @Override
  public T visit(VariableDeclaration variableDeclaration) {
    return visitDefault(variableDeclaration);
  }

  @Override
  public T visit(Loop loop) {
    return visitDefault(loop);
  }
}
