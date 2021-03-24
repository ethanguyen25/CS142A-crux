package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

/**
 * ast node for LiteralInt
 */
public final class LiteralInt extends BaseNode implements Expression {
  private final long value;

  public LiteralInt(Position position, long value) {
    super(position);
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
