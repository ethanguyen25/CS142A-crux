package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;


/**
 * AST node for Assignment Assignment have left side and right side left side is a location right
 * side is a value
 */
public final class Assignment extends BaseNode implements Statement {
  private final Expression location;
  private final Expression value;

  public Assignment(Position position, Expression location, Expression value) {
    super(position);
    this.location = location;
    this.value = value;
  }

  public Expression getLocation() {
    return location;
  }

  public Expression getValue() {
    return value;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(location, value);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
