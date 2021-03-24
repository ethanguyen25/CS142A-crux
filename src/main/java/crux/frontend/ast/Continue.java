package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for Continue
 */
public final class Continue extends BaseNode implements Statement {
  public Continue(Position position) {
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
