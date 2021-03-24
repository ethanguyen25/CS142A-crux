package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for StatementList
 */
public final class StatementList extends ListNode<Statement> {
  public StatementList(Position position, List<Statement> statements) {
    super(position, statements);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
