package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for break
 */
public final class Break extends BaseNode implements Statement {
  public Break(Position position) {
    super(position);
  }

  @Override
  public List<Node> getChildren() {
    return List.of();
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
