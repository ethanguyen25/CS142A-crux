package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

/**
 * ast node for LiteralInt
 */
public final class LiteralBool extends BaseNode implements Expression {
  private final boolean value;

  public LiteralBool(Position position, boolean value) {
    super(position);
    this.value = value;
  }

  public boolean getValue() {
    return value;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
