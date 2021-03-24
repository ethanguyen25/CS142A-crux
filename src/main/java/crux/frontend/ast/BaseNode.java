package crux.frontend.ast;

import java.util.List;


public abstract class BaseNode implements Node {
  private final Position position;

  BaseNode(Position position) {
    this.position = position;
  }


  /**
   * Returns a list of the children of this node.
   */

  @Override
  public List<Node> getChildren() {
    return List.of();
  }

  /**
   * Gets the position of the code text corresponding this node in the input file.
   */

  @Override
  public Position getPosition() {
    return position;
  }
}
