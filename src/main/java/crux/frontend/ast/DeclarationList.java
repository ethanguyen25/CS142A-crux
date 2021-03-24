package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * ast Node for DeclarationList
 */
public final class DeclarationList extends ListNode<Declaration> {
  public DeclarationList(Position position, List<Declaration> declarations) {
    super(position, declarations);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
