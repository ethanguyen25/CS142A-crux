package crux.frontend.ast;

import crux.frontend.Symbol;
import crux.frontend.ast.traversal.NodeVisitor;

import java.util.List;

/**
 * ast node for FunctionDefinition symbol the function symbol parameters list of symbols for the
 * function's args statements the function's body
 */
public final class FunctionDefinition extends BaseNode implements Declaration {
  private final Symbol symbol;
  private final List<Symbol> parameters;
  private final StatementList statements;

  public FunctionDefinition(Position position, Symbol symbol, List<Symbol> parameters,
      StatementList statements) {
    super(position);
    this.symbol = symbol;
    this.parameters = parameters;
    this.statements = statements;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public List<Symbol> getParameters() {
    return parameters;
  }

  public StatementList getStatements() {
    return statements;
  }

  @Override
  public List<Node> getChildren() {
    return List.of(statements);
  }

  @Override
  public <T> T accept(NodeVisitor<? extends T> visitor) {
    return visitor.visit(this);
  }
}
