package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for dereferencing a address
 */
public final class Dereference extends BaseNode implements Expression {
  private final Expression address;

  public Dereference(Position position, Expression address) {
    super(position);
    this.address = address;
  }

  public Expression getAddress() {
    return address;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(address);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
