package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * ast node for if stmt condition is the if condition thenBlock is true block elseBlock is false
 * block
 */
public final class IfElseBranch extends BaseNode implements Statement {
  private final Expression condition;
  private final StatementList thenBlock;
  private final StatementList elseBlock;

  public IfElseBranch(Position position, Expression condition, StatementList thenBlock,
      StatementList elseBlock) {
    super(position);
    this.condition = condition;
    this.thenBlock = thenBlock;
    this.elseBlock = elseBlock;
  }

  public Expression getCondition() {
    return condition;
  }

  public StatementList getThenBlock() {
    return thenBlock;
  }

  public StatementList getElseBlock() {
    return elseBlock;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(condition, thenBlock, elseBlock);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
