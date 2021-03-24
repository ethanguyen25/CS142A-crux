package crux.frontend.ast;

import crux.frontend.Symbol;
import crux.frontend.ast.traversal.NodeVisitor;

/**
 * AST node for ArrayDeclaration ArrayDeclaration have the symbol
 */
public final class ArrayDeclaration extends BaseNode implements Declaration, Statement {
  private final Symbol symbol;

  public ArrayDeclaration(Position position, Symbol symbol) {
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
