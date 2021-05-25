package crux.backend;

import crux.frontend.Symbol;
import crux.midend.ir.core.*;
import crux.midend.ir.core.insts.*;
import crux.printing.IRValueFormatter;

import java.util.*;

/**
 * Convert the CFG into Assembly Instructions
 */
public final class CodeGen extends InstVisitor {
  private final IRValueFormatter irFormat = new IRValueFormatter();

  private final Program p;
  private final CodePrinter out;


  public CodeGen(Program p) {
    this.p = p;
    // Do not change the file name that is outputted or it will
    // break the grader!

    out = new CodePrinter("a.s");
  }

  /**
   * It should allocate space for globals call genCode for each Function
   */
  public void genCode() {
    // This function should generate code the entire program
    for (Iterator<GlobalDecl> globIt = p.getGlobals(); globIt.hasNext();) {
      GlobalDecl g = globIt.next();
//      out.printCode(".comm " + name + ", " + size + ", 8");
    }

    for (Iterator<Function> functIt = p.getFunctions(); functIt.hasNext();) {
      Function f = functIt.next();
      genCode(f);
    }

    out.close();
  }

  private int labelcount = 1;

  // ADDED //////////
  private int stackCount = 0;
  HashMap<Variable, Integer> varMap = new HashMap<Variable, Integer>();

  Integer getStackSlots(Variable v) {
    if (!varMap.containsKey(v)) {
      stackCount++;
      varMap.put(v, stackCount);
    }

    return varMap.get(v);
  }
  // ADDED END /////////


  private String getNewLabel() {
    return "L" + (labelcount++);
  }

  private void genCode(Function f) {
    Instruction inst = f.getStart();
    while (inst != null){
      inst.accept(this);
      inst = inst.getNext(0);
    }

    if (f.getName().equals("main")) {
      out.printCode(".globl _" + f.getName());
    }

    out.printCode("_" + f.getName() + ":");
    int numSlots = stackCount;
    numSlots = (numSlots + 1) &  ~1;
    out.printCode("enter $(8 * " + numSlots + "), $0");
    out.outputBuffer(); // ??
    out.close();


  }

  /**
   * Assigns Labels to any Instruction that might be the target of a conditional or unconditional
   * jump.
   */

  private HashMap<Instruction, String> assignLabels(Function f) {
    HashMap<Instruction, String> labelMap = new HashMap<>();
    Stack<Instruction> tovisit = new Stack<>();
    HashSet<Instruction> discovered = new HashSet<>();
    if (f.getStart() != null)
      tovisit.push(f.getStart());
    while (!tovisit.isEmpty()) {
      Instruction inst = tovisit.pop();

      for (int childIdx = 0; childIdx < inst.numNext(); childIdx++) {
        Instruction child = inst.getNext(childIdx);
        if (discovered.contains(child)) {
          // Found the node for a second time...need a label for merge points
          if (!labelMap.containsKey(child)) {
            labelMap.put(child, getNewLabel());
          }
        } else {
          discovered.add(child);
          tovisit.push(child);
          // Need a label for jump targets also
          if (childIdx == 1 && !labelMap.containsKey(child)) {
            labelMap.put(child, getNewLabel());
          }
        }
      }
    }
    return labelMap;
  }

  public void visit(AddressAt i) {}

  public void visit(BinaryOperator i) {}

  public void visit(CompareInst i) {
  }

  public void visit(CopyInst i) {
    LocalVar dst = i.getDstVar();
    Value val = i.getSrcValue();
    out.bufferCode("// " + dst + " = " + val);
    int slotNum = getStackSlots(dst);
    int offset = -8 * slotNum;
    out.bufferCode("movq $" + val + ", %r10");
    out.bufferCode("movq %r10, " + offset + "(%rbp)");
  }

  public void visit(JumpInst i) {}

  public void visit(LoadInst i) {}

  public void visit(NopInst i) {
    out.bufferCode("// NOP");
  }

  public void visit(StoreInst i) {}

  public void visit(ReturnInst i) {}

  public void visit(CallInst i) {}

  public void visit(UnaryNotInst i) {}
}
