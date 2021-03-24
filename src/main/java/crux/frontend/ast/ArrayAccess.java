package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for ArrayAccess base is a Name offset is an Experssion
 */
public final class ArrayAccess extends BaseNode implements Expression {
  private final Name base;
  private final Expression offset;

  public ArrayAccess(Position position, Name base, Expression offset) {
    super(position);
    this.base = base;
    this.offset = offset;
  }

  public Name getBase() {
    return base;
  }

  public Expression getOffset() {
    return offset;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(base, offset);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
