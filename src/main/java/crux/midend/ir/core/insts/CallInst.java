package crux.midend.ir.core.insts;

import crux.midend.ir.core.AddressVar;
import crux.midend.ir.core.Instruction;
import crux.midend.ir.core.Value;
import crux.midend.ir.core.LocalVar;
import crux.frontend.Symbol;
import java.util.*;
import java.util.function.Function;

/**
 * Calls a function with the provided arguments.
 * <p>
 * Operation (pseudo-code):
 * 
 * <pre>
 * {@code
 * for (var param in params)
 *     push(param)
 * call(callee)
 * }
 * </pre>
 */
public final class CallInst extends Instruction {
  Symbol callee;

  static private List<Value> convert(List<LocalVar> params) {
    Value[] l = new Value[params.size()];
    for (int i = 0; i < l.length; i++)
      l[i] = params.get(i);
    return List.of(l);
  }


  public CallInst(LocalVar destVar, Symbol callee, List<LocalVar> params) {
    super(destVar, convert(params));
    this.callee = callee;
  }

  public CallInst(Symbol callee, List<LocalVar> params) {
    super(convert(params));
    this.callee = callee;
  }

  public Symbol getCallee() {
    return callee;
  }

  public List<Value> getParams() {
    return mOperands;
  }

  public LocalVar getDst() {
    return (LocalVar) mDestVar;
  }

  @Override
  public void accept(InstVisitor v) {
    v.visit(this);
  }

  @Override
  public String format(Function<Value, String> valueFormatter) {
    String paramstr = "";
    List<Value> lparams = getParams();
    for (Value p : lparams) {
      paramstr += valueFormatter.apply(p);
    }
    if (mDestVar != null) {
      var destVar = valueFormatter.apply(mDestVar);
      return String.format("%s = call %s (%s)", destVar, callee, paramstr);
    } else {
      return String.format("call %s (%s)", callee, paramstr);
    }
  }
}
