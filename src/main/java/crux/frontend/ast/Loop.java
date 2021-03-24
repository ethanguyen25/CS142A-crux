package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * AST node for Loop body will be a StatementList
 */
public final class Loop extends BaseNode implements Statement {
  private final StatementList body;

  public Loop(Position position, StatementList body) {
    super(position);
    this.body = body;
  }

  public StatementList getBody() {
    return body;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(body);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
