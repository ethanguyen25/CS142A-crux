package crux.frontend.ast;

import crux.frontend.Symbol;
import crux.frontend.ast.traversal.NodeVisitor;

/**
 * AST node for VariableDeclaration
 */
public final class VariableDeclaration extends BaseNode implements Declaration, Statement {
  private final Symbol symbol;

  public VariableDeclaration(Position position, Symbol symbol) {
    super(position);
    this.symbol = symbol;
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }

  public Symbol getSymbol() {
    return symbol;
  }
}
