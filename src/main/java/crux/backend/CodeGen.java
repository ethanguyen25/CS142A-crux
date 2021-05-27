package crux.backend;

import crux.frontend.Symbol;
import crux.frontend.types.IntType;
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
      long size = ((IntegerConstant) g.getNumElement()).getValue();
      out.printCode(".comm " + g.getSymbol().getName() + ", " + (size * 8) + ", 8");
    }

    for (Iterator<Function> functIt = p.getFunctions(); functIt.hasNext();) {
      Function f = functIt.next();
      genCode(f);
    }

    out.outputBuffer();
    out.printCode("movq $0, %rax");
    out.printCode("leave");
    out.printCode("ret");

    out.close();
  }

  private int labelcount = 1;

  // ADDED //////////

  //argRegisters is a global array that holds the 6 registers for arguments
  String[] argRegisters = new String[]{"%rdi","%rsi","%rdx","%rcx","%r8","%r9"};

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

    out.printLabel("_" + f.getName() + ":");
    int numSlots = stackCount;
    numSlots = (numSlots + 1) &  ~1;
    out.printCode("enter $(8 * " + numSlots + "), $0");

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

  public void visit(AddressAt i) {
    out.bufferCode("/*** AddressAt ***/");
    AddressVar dst = i.getDst();
    Symbol base = i.getBase();
    LocalVar offset = i.getOffset();

//    int dstOffset = getStackSlots(dst);


  }

  public void visit(BinaryOperator i) {
    out.bufferCode("/*** BinaryOperator ***/");
    LocalVar lhs = i.getLeftOperand();
    LocalVar rhs = i.getRightOperand();
    LocalVar dst = i.getDst();

    int dstSlot = getStackSlots(dst);
    int lhsSlot = getStackSlots(lhs);
    int rhsSlot = getStackSlots(rhs);

    int dstOffset = -8 * dstSlot;
    int lhsOffset = -8 * lhsSlot;
    int rhsOffset = -8 * rhsSlot;

    switch(i.getOperator()) {
      case Add:
        out.bufferCode("movq " + lhsOffset + "(%rbp), %r10");
        out.bufferCode("addq " + rhsOffset + "(%rbp), %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case Sub:
        out.bufferCode("movq " + lhsOffset + "(%rbp), %r10");
        out.bufferCode("subq " + rhsOffset + "(%rbp), %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case Mul:
        out.bufferCode("movq " + lhsOffset + "(%rbp), %r10");
        out.bufferCode("imulq " + rhsOffset + "(%rbp), %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case Div:
        out.bufferCode("movq " + lhsOffset + "(%rbp), %rax");
        out.bufferCode("cqto");
        out.bufferCode("idivq " + rhsOffset + "(%rbp)");
        out.bufferCode("movq %rax, "+ dstOffset+"(%rbp)");
        break;
    }

  }

  public void visit(CompareInst i) {
  }

  public void visit(CopyInst i) {
    out.bufferCode("/*** CopyInst ***/");
    LocalVar dst = i.getDstVar();
    Value val = i.getSrcValue();
    //out.bufferCode("// " + dst + " = " + val);
    int slotNum = getStackSlots(dst);
    int offset = -8 * slotNum;

    if (val.getType().equivalent(new IntType())) {
      long value = ((IntegerConstant) val).getValue();
      out.bufferCode("movq $" + value + ", %r10");
    } else {
      boolean value = ((BooleanConstant) val).getValue();
      if (value){
        out.bufferCode("movq $" + 1 + ", %r10");
      } else {
        out.bufferCode("movq $" + 0 + ", %r10");
      }

    }

    out.bufferCode("movq %r10, " + offset + "(%rbp)");
  }

  public void visit(JumpInst i) {}

  public void visit(LoadInst i) {}

  public void visit(NopInst i) {
    //out.bufferCode("// NOP");
  }

  public void visit(StoreInst i) {}

  public void visit(ReturnInst i) {}

  public void moveRegToVar(String reg, Variable var) {
    int offset = getStackSlots(var) * 8;
//    out.bufferCode("movq " + reg + ", -" + (offset * 8) + "(%rbp)");
    out.bufferCode("movq -" + (offset) + "(%rbp), " + reg);
  }

  public void visit(CallInst i) {
    out.bufferCode("/*** CallInst ***/");
    int argIndex = 0;

    for (Value val : i.getParams()) {
      if (argIndex < stackCount) {
        moveRegToVar(argRegisters[argIndex], (Variable) val);
      } else {
        int index = argIndex - argRegisters.length;
        out.bufferCode("movq " + (8 * index + 16) + "(%rbp), %r10");
        moveRegToVar("r10", (Variable) val);
      }
      ++argIndex;
    }

    out.bufferCode("call _" + i.getCallee().getName());

  }

  public void visit(UnaryNotInst i) {}
}
