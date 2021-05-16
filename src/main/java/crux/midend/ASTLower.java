package crux.midend;

import crux.frontend.Symbol;
import crux.frontend.ast.*;
import crux.frontend.ast.OpExpr.Operation;
import crux.frontend.ast.traversal.NodeVisitor;
import crux.frontend.types.*;
import crux.midend.ir.core.*;
import crux.midend.ir.core.insts.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Convert AST to IR and build the CFG.
 */

// Hint: You might want to change the Void parameter of the
// NodeVisitor and the return type of the visit methods to some other
// class so that the visit methods can return information to the
// caller.

class Pair {
  Instruction start;
  Instruction end;
  Variable val;

  Pair(Instruction start, Instruction end, Variable val) {
    this.start = start;
    this.end = end;
    this.val = val;
  }

  Instruction getStart() {
    return start;
  }

  Instruction getEnd() {
    return end;
  }

  Variable getVal() {
    return val;
  }
}




public final class ASTLower implements NodeVisitor<Pair> {
  private Program mCurrentProgram = null;
  private Function mCurrentFunction = null;

  private Map<Symbol, Variable> mCurrentLocalVarMap = null;
  private TypeChecker checker;

//  //ADDED
//  public Instruction mLastInstruction;
//  public Value mCurrentValue;

  public Instruction brkInstruction = null;
  public Instruction contInstruction = null;


  /**
   * A constructor to initialize member variables
   */

  public ASTLower(TypeChecker checker) {
    this.checker = checker;
  }

  public Program lower(DeclarationList ast) {
    visit(ast);
    return mCurrentProgram;
  }

  @Override
  public Pair visit(DeclarationList declarationList) {
    mCurrentProgram = new Program();
    for (var e: declarationList.getChildren())
    {
      e.accept(this);
    }

    return null;
  }

  /*
   * Function declaration
   */

  @Override
  public Pair visit(FunctionDefinition functionDefinition) {
    mCurrentLocalVarMap = new HashMap<>();

    String name = functionDefinition.getSymbol().getName();
    mCurrentFunction = new Function(name, (FuncType) functionDefinition.getSymbol().getType());

    List<LocalVar> args = new ArrayList<>();
    for (var e: functionDefinition.getParameters()){
      LocalVar lvar = mCurrentFunction.getTempVar(e.getType(), e.getName());
      mCurrentLocalVarMap.put(e, lvar);
      args.add(lvar);
    }

    mCurrentFunction.setArguments(args);
    mCurrentFunction.setStart(new NopInst());

    Pair temp = functionDefinition.getStatements().accept(this);
    mCurrentFunction.getStart().setNext(0, temp.start);

    mCurrentProgram.addFunction(mCurrentFunction);

    mCurrentLocalVarMap = null;
    mCurrentFunction = null;

    return null;
  }

  @Override
  public Pair visit(StatementList statementList) {
    Instruction firstInst = null;
    Instruction lastInst = null;
    Instruction loopInst = null;
    for (var e: statementList.getChildren()){
      Pair statement = e.accept(this);
      if (statement.getStart().getClass() == NopInst.class && statement.getEnd().getClass() == NopInst.class && statement.getVal() == null) {
        brkInstruction = statement.getStart();
      }
      if (loopInst != null) {
        brkInstruction.setNext(0, statement.getStart());
      }
      //PLANNED ON CHECKING FOR CONTINUE STATEMENT (HOW IS IT DIFFERENT THAN BREAK
//      if (statement.getStart().getClass() == null && statement.getEnd().getClass() == null && statement.getVal() == null) {
//        contInstruction = statement.getStart();
//      }
      //IF A LOOP PAIR RETURNED, THAN START = END (?)
      //  THEN, NEXT CHECK IF loopInst != null, IF CONTINUE PAIR AND SET NEXT OF CONTINUE PAIR TO LOOP.GETSTART()
//      if (statement.getStart() == statement.getEnd().getNext(0)){
//        loopInst = statement.getStart();
//      }
      if (firstInst == null){
          firstInst = statement.getStart();
      } else {
        lastInst.setNext(0, statement.getStart());
      }
      lastInst = statement.getEnd();
    }

    return new Pair(firstInst, lastInst, null);
  }

  /**
   * Declarations, could be either local or Global
   */

  @Override
  public Pair visit(VariableDeclaration variableDeclaration) {
    if (mCurrentFunction == null){
      IntegerConstant ic = IntegerConstant.get(mCurrentProgram, 1); //For Constant, if not an array, then its supposed to be 1
      var global = new GlobalDecl(variableDeclaration.getSymbol(), ic);
      mCurrentProgram.addGlobalVar(global);
      return null;
    } else {
      Type t = variableDeclaration.getSymbol().getType();
      String name = variableDeclaration.getSymbol().getName();
      LocalVar lvar = mCurrentFunction.getTempVar(t, name);
      mCurrentLocalVarMap.put(variableDeclaration.getSymbol(), lvar);
      NopInst nop = new NopInst();
      return new Pair(nop, nop, lvar);
    }

  }

  /**
   * Create a declaration for array
   */

  @Override
  public Pair visit(ArrayDeclaration arrayDeclaration) {
    long num = ((ArrayType) arrayDeclaration.getSymbol().getType()).getExtent();
    IntegerConstant ic = IntegerConstant.get(mCurrentProgram, num);

    GlobalDecl global = new GlobalDecl(arrayDeclaration.getSymbol(), ic);
    mCurrentProgram.addGlobalVar(global);

    return null;
  }

  @Override
  public Pair visit(Name name) {
    Symbol sym = name.getSymbol();

    Variable var = mCurrentLocalVarMap.get(sym);

    NopInst nop = new NopInst();
    return new Pair(nop, nop, var);
  }

  /**
   * Assignment
   */

  @Override
  public Pair visit(Assignment assignment) {
    Instruction tempInst = null;  //NEW
    Pair lhs = assignment.getLocation().accept(this);
    Pair rhs = assignment.getValue().accept(this);

    lhs.getEnd().setNext(0, rhs.getStart());  //NEW

    if (lhs.getVal() instanceof LocalVar){
//      LocalVar lhsVar = mCurrentFunction.getTempVar(lhs.getVal().getType());  //DONT NEED
      CopyInst copyInst = new CopyInst((LocalVar) lhs.getVal(), rhs.getVal());
      rhs.getEnd().setNext(0,copyInst);
      return new Pair(lhs.getStart(), copyInst, null);  //CHANGED FROM rhs.getStart() to lhs.getStart()
    } else {
      Value val;
      val = lhs.getVal();
      if (assignment.getLocation() instanceof Name){
        val = mCurrentFunction.getTempAddressVar(((Name) assignment.getLocation()).getSymbol().getType());
        AddressAt aa = new AddressAt((AddressVar) val, ((Name) assignment.getLocation()).getSymbol());
//        rhs.getEnd().setNext(0, aa);

        Pair aaPair = new Pair(aa, aa, null); //NEW
        tempInst = aaPair.getStart();  //NEW
      }

      StoreInst storeInst = new StoreInst((LocalVar) rhs.getVal(), (AddressVar) val);
//      rhs.getEnd().setNext(0, storeInst);

      Pair siPair = new Pair(storeInst, storeInst, null);  //NEW
      if (tempInst != null){
        tempInst.setNext(0,storeInst);
      } else {
        tempInst = siPair.getStart();
      }
      rhs.getEnd().setNext(0, tempInst);  //NEW

      return new Pair(lhs.getStart(), storeInst, null);
    }

  }

  /**
   * Function call
   */

  @Override
  public Pair visit(Call call) {
    List<LocalVar> list = new ArrayList<>();
    Pair previousInst = null;
    for (var e: call.getArguments()){
      previousInst = e.accept(this);
      list.add((LocalVar) previousInst.getVal());
    }

    Symbol calleeAddr = call.getCallee();
    Type calleeType = calleeAddr.getType();  //Why cast to FuncType ???

    LocalVar retType = mCurrentFunction.getTempVar(((FuncType) calleeType).getRet());
//    for (mCurrentProgram.getFunctions())
    CallInst callInst = new CallInst(retType, calleeAddr, list);


    if (previousInst != null){
      previousInst.getEnd().setNext(0,callInst);
      return new Pair(previousInst.getStart(), callInst, previousInst.getVal());
    }

//    LocalVar temp = mCurrentFunction.getTempVar(((FuncType) calleeType).getRet());
    return new Pair(callInst, callInst, callInst.getDst()); //temp OR null OR retType OR callInst.getDst()
  }

  /**
   * Handle Operations like Arithmetics and Comparisons Also to handle logical operations (and, or,
   * not)
   */

  @Override
  public Pair visit(OpExpr operation) {
    Pair left = operation.getLeft().accept(this);
    Pair right = operation.getRight().accept(this);
    left.getEnd().setNext(0, right.getStart());

    LocalVar tempVar = mCurrentFunction.getTempVar(checker.getType(operation));
//    LocalVar lhs = mCurrentFunction.getTempVar(left.getVal().getType());
//    LocalVar rhs = mCurrentFunction.getTempVar(right.getVal().getType());
    switch (operation.getOp()){
      case GE:
        CompareInst geInst = new CompareInst(tempVar, CompareInst.Predicate.GE, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, geInst);
        return new Pair(left.getStart(), geInst, tempVar);
        //break;
      case LE:
        CompareInst leInst = new CompareInst(tempVar, CompareInst.Predicate.LE, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, leInst);
        return new Pair(left.getStart(), leInst , tempVar);
        //break;
      case NE:
        CompareInst neInst = new CompareInst(tempVar, CompareInst.Predicate.NE, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, neInst);
        return new Pair(left.getStart(), neInst , tempVar);
        //break;
      case EQ:
        CompareInst eqInst = new CompareInst(tempVar, CompareInst.Predicate.EQ, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, eqInst);
        return new Pair(left.getStart(), eqInst , tempVar);
        //break;
      case GT:
        CompareInst gtInst = new CompareInst(tempVar, CompareInst.Predicate.GT, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, gtInst);
        return new Pair(left.getStart(), gtInst , tempVar);
        //break;
      case LT:
        CompareInst ltInst = new CompareInst(tempVar, CompareInst.Predicate.LT, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, ltInst);
        return new Pair(left.getStart(), ltInst , tempVar);
        //break;
      case ADD:
        BinaryOperator addBO = new BinaryOperator(BinaryOperator.Op.Add, tempVar, (LocalVar) left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, addBO);
        return new Pair(left.getStart(), addBO, tempVar);
        //break;
      case SUB:
        BinaryOperator subBO = new BinaryOperator(BinaryOperator.Op.Sub, tempVar, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, subBO);
        return new Pair(left.getStart(), subBO, tempVar);
        //break;
      case MULT:
        BinaryOperator mulBO = new BinaryOperator(BinaryOperator.Op.Mul, tempVar, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, mulBO);
        return new Pair(left.getStart(), mulBO, tempVar);
        //break;
      case DIV:
        BinaryOperator divBO = new BinaryOperator(BinaryOperator.Op.Div, tempVar, (LocalVar)left.getVal(), (LocalVar) right.getVal());
        right.getEnd().setNext(0, divBO);
        return new Pair(left.getStart(), divBO, tempVar);
        //break;
      case LOGIC_AND:
        //break;
      case LOGIC_OR:
        //break;
      case LOGIC_NOT:
        //break;
    }
    return null;
  }

  @Override
  public Pair visit(Dereference dereference) {
    Instruction tempInst = null; //NEW
    Pair addr = dereference.getAddress().accept(this);

    if (addr.getVal() instanceof LocalVar && addr.getVal() != null){
      LocalVar destTemp = mCurrentFunction.getTempVar(addr.getVal().getType());
      CopyInst copyInst = new CopyInst(destTemp, addr.getVal());
      addr.getEnd().setNext(0, copyInst);
      return new Pair(addr.getStart(), copyInst, addr.getVal());
    } else {
      Value val = addr.getVal();
      if (dereference.getAddress() instanceof Name) {
//        LocalVar offset = mCurrentFunction.getTempVar(((Name) dereference.getAddress()).getSymbol().getType());

        Type addrType = ((Name) dereference.getAddress()).getSymbol().getType();
        val = mCurrentFunction.getTempAddressVar(addrType);
        AddressAt aa = new AddressAt((AddressVar) val, ((Name) dereference.getAddress()).getSymbol());
//        addr.getEnd().setNext(0, aa);

        Pair aaPair = new Pair(aa, aa, null); //NEW
        tempInst = aaPair.getStart(); //NEW
      }

      LocalVar destTemp = mCurrentFunction.getTempVar(val.getType()); //MAYBE NULL !!!
      LoadInst lInst = new LoadInst(destTemp, (AddressVar) val);
//      addr.getEnd().setNext(0, lInst);

      Pair liPair = new Pair(lInst, lInst, null);  //NEW
      if (tempInst != null){
        tempInst.setNext(0, lInst);
      } else {
        tempInst = liPair.getStart();
      }
      addr.getEnd().setNext(0,tempInst);  //NEW

      return new Pair(addr.getStart(), lInst, destTemp);
    }

  }

  private Pair visit(Expression expression) {
    for (var e: expression.getChildren()){
      e.accept(this);
    }

    return null;
  }

  /**
   * ArrayAccess
   */

  @Override
  public Pair visit(ArrayAccess access) {
    Pair offset = access.getOffset().accept(this);

    Type baseType = access.getBase().getSymbol().getType();
    ArrayType arrType = (ArrayType) baseType;
    AddressVar tempAddrVar = mCurrentFunction.getTempAddressVar(arrType.getBase());
//    LocalVar lvar = mCurrentFunction.getTempVar(offset.getVal().getType());

    AddressAt aa = new AddressAt(tempAddrVar, access.getBase().getSymbol(), (LocalVar) offset.getVal());  //Last param supposed to be (LocalVar) offset.getVal() or lvar ???
    offset.getEnd().setNext(0,aa);
    return new Pair(offset.getStart(), aa, tempAddrVar);  //WHAT DO WE RETURN
  }

  /**
   * Literal
   */

  @Override
  public Pair visit(LiteralBool literalBool) {
    BooleanConstant value = BooleanConstant.get(mCurrentProgram, literalBool.getValue());
    LocalVar tempVar = mCurrentFunction.getTempVar(new BoolType());
    CopyInst copyInst = new CopyInst(tempVar, value);

    return new Pair(copyInst, copyInst, tempVar);
  }

  /**
   * Literal
   */

  @Override
  public Pair visit(LiteralInt literalInt) {
    IntegerConstant value = IntegerConstant.get(mCurrentProgram, literalInt.getValue());
    LocalVar tempVar = mCurrentFunction.getTempVar(new IntType());
    CopyInst copyInst = new CopyInst(tempVar, value);
    //mCurrentFunction.getStart().setNext(0, copyInst);
    return new Pair(copyInst, copyInst, tempVar);
  }

  /**
   * Return
   */

  @Override
  public Pair visit(Return ret) {
    Pair retPair = ret.getValue().accept(this);
    ReturnInst retInst = new ReturnInst((LocalVar) retPair.getVal());
    retPair.getEnd().setNext(0, retInst);

    return new Pair(retPair.getStart(), retInst, retPair.getVal());
  }

  /**
   * Break Node
   */

  @Override
  public Pair visit(Break brk) {
    //Point to the first instruction after the loop

    NopInst nop = new NopInst();

    return new Pair(nop, nop, null);
  }

  /**
   * Continue Node
   */

  @Override
  public Pair visit(Continue cnt) {
    //Point back to the start of the loop

    NopInst nop = new NopInst();
    return new Pair(nop, nop, null);
  }

  /**
   * Control Structures
   */

  @Override
  public Pair visit(IfElseBranch ifElseBranch) {
    //JumpInst: if true go to edge1, (else) the default for JumpInst is edge0

    Pair condPair = ifElseBranch.getCondition().accept(this);

    JumpInst jInst = new JumpInst((LocalVar) condPair.getVal());
    condPair.getEnd().setNext(0, jInst);

    NopInst endNop = new NopInst();

    Pair trueBlock = ifElseBranch.getThenBlock().accept(this);
    jInst.setNext(1, trueBlock.getStart());

    //MIGHT NOT HAVE TO CHECK FOR BREAK HERE
    if (trueBlock.getStart().getClass() != NopInst.class || trueBlock.getEnd().getClass() != NopInst.class || trueBlock.getVal() != null) {
      trueBlock.getEnd().setNext(0, endNop);
    } else {
      trueBlock.getEnd().setNext(0, new NopInst());
    }


    Pair falseBlock = ifElseBranch.getElseBlock().accept(this);
    if (falseBlock.getStart() != null && falseBlock.getEnd() != null) {   // && falseBlock.getVal() != null){
      jInst.setNext(0, falseBlock.getStart());
      falseBlock.getEnd().setNext(0, endNop);
    } else {
      jInst.setNext(0, endNop);
    }


    return new Pair(condPair.getStart(), endNop, condPair.getVal());
  }

  /**
   * Loop
   */

  @Override
  public Pair visit(Loop loop) {
    //see break, exit to join block

    NopInst loopStart = new NopInst(); //This represents the start of the loop (?)
    Instruction afterStart = null;
    Instruction lastInst = null;

    for (var e: loop.getBody().getChildren()) {
      Pair loopPairs = e.accept(this);
      if (afterStart == null){
        afterStart = loopPairs.getStart();
      } else {
        lastInst.setNext(0, loopPairs.getStart());
      }
      lastInst = loopPairs.getEnd();
    }
    loopStart.setNext(0,afterStart);
    lastInst.setNext(0,loopStart);
    return new Pair(loopStart, lastInst, null);
  }
}


