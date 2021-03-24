package crux.frontend.ast;

import crux.frontend.Symbol;
import crux.frontend.ast.traversal.NodeVisitor;

/**
 * AST node for Name Name will have symbol
 */
public final class Name extends BaseNode implements Expression {
  private final Symbol symbol;

  public Name(Position position, Symbol symbol) {
    super(position);
    this.symbol = symbol;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
