package crux.midend.ir.core;

import java.util.List;
import java.util.function.Function;
import crux.frontend.Symbol;

/**
 * Allocates a chunk of memory, either of a global variable or of an array (global and local).
 * <p>
 * Operation (pseudo-code):
 * 
 * <pre>
 * {@code
 * if (global)
 *     destVar = allocateInDataSection(numElement)
 * else
 *     destVar = reserveStackMemory(numElement)
 * }
 * </pre>
 */
public final class GlobalDecl {
  Symbol mSymbol;
  Constant mNumElement;

  public GlobalDecl(Symbol symbol, Constant numElement) {
    mSymbol = symbol;
    mNumElement = numElement;
  }

  public Symbol getSymbol() {
    return mSymbol;
  }

  public Constant getNumElement() {
    return mNumElement;
  }

  public String format(Function<Value, String> valueFormatter) {
    var destVar = mSymbol.getName();
    var typeStr = getSymbol().getType().toString();
    var numElement = valueFormatter.apply(getNumElement());
    return String.format("%s = allocate %s, %s", destVar, typeStr, numElement);
  }
}
