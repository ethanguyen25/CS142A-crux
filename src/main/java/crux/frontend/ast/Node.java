package crux.frontend.ast;

import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * interface for AST Node
 */
public interface Node {
  Position getPosition();

  List<Node> getChildren();

  <T> T accept(NodeVisitor<? extends T> visitor);
}
