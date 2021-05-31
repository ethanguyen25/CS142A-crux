package crux.backend;

import crux.frontend.Symbol;
import crux.frontend.types.ArrayType;
import crux.frontend.types.BoolType;
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

      if (functIt.hasNext()){
        out.outputBuffer();
        if (leaveRet == 0){
          out.printCode("leave");
          out.printCode("ret");
        }
        leaveRet = 0;
      }

    }

    out.outputBuffer();
    if (leaveRet == 0){
      out.printCode("leave");
      out.printCode("ret");
    }
    out.close();
  }

  private int labelcount = 1;

  // ADDED - START //////////
  int leaveRet = 0;

  Stack<Instruction> dfsStack = new Stack<>();
  HashSet<Instruction> visited = new HashSet<>();

  HashMap<Instruction, String> currlabelMap = new HashMap<>();

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
  // ADDED - END ////////////


  private String getNewLabel() {
    return "L" + (labelcount++);
  }

  private void genCode(Function f) {
    Instruction inst = f.getStart();
    if (inst.getNext(0) != null){
      currlabelMap = assignLabels(f);
    }


    int i = 0;
    for (var e: f.getArguments()) {
      int slot = getStackSlots(e);
//      ++stackCount;
      if (i < 6) {
        out.bufferCode("movq " + argRegisters[i] + ", " + (-8 * slot) + "(%rbp)"); }
//      } else {
//        int index = i - argRegisters.length;
//        out.bufferCode("movq " + (8 * index + 16) + "(%rbp), %r10");
////        moveRegToVar("%r10", (Variable) val);
//        int valOffset = getStackSlots(e);
//        out.bufferCode("movq %r10, -" + valOffset + "(%rbp)");
//      }
      ++i;
    }


    //Depth-First-Search
    dfsStack.push(inst);

    while (!dfsStack.isEmpty() && inst.getNext(0) != null){
      Instruction currInst = dfsStack.pop();

      if ((currlabelMap.containsKey(currInst)) && (!visited.contains(currInst))){
        String jmplabel = currlabelMap.get(currInst);
        out.bufferLabel(jmplabel + ":");
      }

//      out.bufferCode("currInst = " + currInst);
      visited.add(currInst);
      currInst.accept(this);
      if (currInst.numNext() != 0){
        Instruction next = currInst.getNext(0);

//        if (next.getClass().equals(ReturnInst.class)){
//          int dstSlot = getStackSlots(((ReturnInst) next).getReturnValue());
//          int dstOffset = (-8 * dstSlot);
//          out.bufferCode("movq %rax, " + dstOffset + "(%rbp)");
//        }

        if (!visited.contains(next)){
          dfsStack.push(next);
        } else {
          String jmplabel = currlabelMap.get(next);
          if (jmplabel != null) {
            out.bufferCode("jmp " + jmplabel);
            currlabelMap.remove(next, jmplabel);
          }

        }
      } else {
        if (leaveRet == 0){
          out.bufferCode("leave");
          out.bufferCode("ret");
          ++leaveRet;
        }
        if (currlabelMap.containsKey(currInst)){
          leaveRet = 0;
        }

      }
    }


    out.printCode(".globl " + f.getName());
    out.printLabel(f.getName() + ":");
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

    if (base.getType().getClass() == ArrayType.class) {
      out.bufferCode("movq " + (-8 * stackCount) + "(%rbp), %r11");
//      int offsetVar = getStackSlots(offset);
      out.bufferCode("movq $8, %r10");  //8 or offset ???
      out.bufferCode("imul %r10, %r11");
      out.bufferCode("movq " + base.getName() + "@GOTPCREL(%rip), %r10");
      out.bufferCode("addq %r10, %r11");
      int dstSlot = getStackSlots(dst);
      int dstOffset = -8 * dstSlot;
      out.bufferCode("movq %r11, " + dstOffset + "(%rbp)");
    } else {
      out.bufferCode("movq " + base.getName() + "@GOTPCREL(%rip), %r10");
      int dstSlot = getStackSlots(dst);
      int dstOffset = -8 * dstSlot;
      out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
    }

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
    out.bufferCode("/*** CompareInst ***/");
    LocalVar lhs = i.getLeftOperand();
    LocalVar rhs = i.getRightOperand();
    LocalVar dst = i.getDst();

    int dstSlot = getStackSlots(dst);
    int lhsSlot = getStackSlots(lhs);
    int rhsSlot = getStackSlots(rhs);

    int dstOffset = -8 * dstSlot;
    int lhsOffset = -8 * lhsSlot;
    int rhsOffset = -8 * rhsSlot;

    out.bufferCode("movq $0, %r10");
    out.bufferCode("movq $1, %rax");
    out.bufferCode("movq " + lhsOffset + "(%rbp), %r11");
    out.bufferCode("cmp " + rhsOffset + "(%rbp), %r11");
    switch(i.getPredicate()) {
      case EQ:
        out.bufferCode("cmove %rax, %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case GE:
        out.bufferCode("cmovge %rax, %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case GT:
        out.bufferCode("cmovg %rax, %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case LE:
        out.bufferCode("cmovle %rax, %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case LT:
        out.bufferCode("cmovl %rax, %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
      case NE:
        out.bufferCode("cmovne %rax, %r10");
        out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
        break;
    }
  }

  public void visit(CopyInst i) {
    out.bufferCode("/*** CopyInst ***/");
    LocalVar dst = i.getDstVar();
    Value val = i.getSrcValue();

    if (val.getType().equivalent(new IntType())) {
      if (val.getClass().equals(IntegerConstant.class)) {
        long value = ((IntegerConstant) val).getValue();
        out.bufferCode("movq $" + value + ", %r10");
      } else {
//        out.bufferCode("dst = " + dst);
//        out.bufferCode("val = " + val);
        int valSlot = getStackSlots((Variable) val);
        out.bufferCode("movq " + (-8 * valSlot) + "(%rbp), %r10");
//        int before = stackCount;
//        int dstSlot = getStackSlots(dst);
//        int after = stackCount;
//        if (before == after){
//          out.bufferCode("movq " + (-8 * dstSlot) + "(%rbp), %r10");
//        } else {
//          int valSlot = getStackSlots((Variable) val);
//          out.bufferCode("movq " + (-8 * valSlot) + "(%rbp), %r10");
//        }


      }

    } else {
      if (val.getClass().equals(BooleanConstant.class)) {
        boolean value = ((BooleanConstant) val).getValue();

        if (value){
          out.bufferCode("movq $" + 1 + ", %r10");
        } else {
          out.bufferCode("movq $" + 0 + ", %r10");
        }

      }

    }

    int slotNum = getStackSlots(dst);
    int offset = -8 * slotNum;
    out.bufferCode("movq %r10, " + offset + "(%rbp)");
  }

  public void visit(JumpInst i) {
    out.bufferCode("/*** JumpInst ***/");
    out.bufferCode("movq " + (-8 * stackCount) +  "(%rbp), %r10");

    if (i.getPredicate().getType().equivalent(new BoolType())) {
      out.bufferCode("cmp $1, %r10");
    }
    Instruction trueBlock = i.getNext(1);
    dfsStack.push(trueBlock);
    String jmplabel = currlabelMap.get(trueBlock);
    out.bufferCode("je " + jmplabel);

    Instruction falseBlock = i.getNext(0);
    dfsStack.push(falseBlock);

  }

  public void visit(LoadInst i) {
    out.bufferCode("/*** LoadInst ***/");
    LocalVar dst = i.getDst();
    AddressVar src = i.getSrcAddress();

    int dstSlot = getStackSlots(dst);
    int dstOffset = -8 * dstSlot;
    int srcSlot = getStackSlots(src);
    int srcOffset = -8 * srcSlot;

    out.bufferCode("movq " + srcOffset + "(%rbp), %r10");
    out.bufferCode("movq 0(%r10), %r10");
    out.bufferCode("movq %r10, " + dstOffset + "(%rbp)");
  }

  public void visit(NopInst i) {
//    out.bufferCode("// NOP");
  }

  public void visit(StoreInst i) {
    out.bufferCode("/*** StoreInst ***/");
    AddressVar dst = i.getDestAddress();
    LocalVar src = i.getSrcValue();

    int dstSlot = getStackSlots(dst);
    int dstOffset = -8 * dstSlot;
    int srcSlot = getStackSlots(src);
    int srcOffset = -8 * srcSlot;

    out.bufferCode("movq " + srcOffset + "(%rbp), %r10");
    out.bufferCode("movq " + dstOffset + "(%rbp), %r11");
    out.bufferCode("movq %r10, 0(%r11)");

  }

  public void visit(ReturnInst i) {
    out.bufferCode("/*** ReturnInst ***/");
    LocalVar retValue = i.getReturnValue();
    int retSlot = getStackSlots(retValue);
    int retOffset = -8 * retSlot;

//    out.bufferCode("movq %rax, " + retOffset + "(%rbp)");

    out.bufferCode("movq " + retOffset + "(%rbp), %rax");
    out.bufferCode("leave");
    out.bufferCode("ret");
    ++leaveRet;

  }

  public void moveRegToVar(String reg, Variable var) {
    int offset = getStackSlots(var) * -8;
//    out.bufferCode("movq " + reg + ", " + offset + "(%rbp)");
    out.bufferCode("movq " + offset + "(%rbp), " + reg);
  }

  public void visit(CallInst i) {
    out.bufferCode("/*** CallInst ***/");
    int argIndex = 0;

    for (Value val : i.getParams()) {
      if (argIndex < argRegisters.length) {
        moveRegToVar(argRegisters[argIndex], (Variable) val);
      } else {
        int index = argIndex - argRegisters.length;
        out.bufferCode("movq " + (8 * index + 16) + "(%rbp), %r10");
//        moveRegToVar("%r10", (Variable) val);
        int valOffset = getStackSlots((Variable) val);
        out.bufferCode("movq %r10, -" + valOffset + "(%rbp)");
      }
      ++argIndex;
    }


    out.bufferCode("call " + i.getCallee().getName());

    int before = varMap.size();
    int dstSlot = getStackSlots(i.getDst());
    int after = varMap.size();
    if (before != after){
      int dstOffset = (-8 * dstSlot);
      out.bufferCode("movq %rax, " + dstOffset + "(%rbp)");
    }



  }

  public void visit(UnaryNotInst i) {}
}
