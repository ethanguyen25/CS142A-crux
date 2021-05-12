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
    this.start= start;
    this.end=end;
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
    Type retType = ((FuncType) functionDefinition.getSymbol().getType()).getRet();
    TypeList tList = TypeList.of();
    List<LocalVar> args = new ArrayList<>();
    for (var e: functionDefinition.getParameters()){
      tList.append(e.getType());

      LocalVar lvar = mCurrentFunction.getTempVar(e.getType(), e.getName());
      mCurrentLocalVarMap.put(e, lvar);
      args.add(lvar);
    }
    mCurrentFunction = new Function(name, new FuncType(tList, retType));

    mCurrentFunction.setArguments(args);
    mCurrentFunction.setStart(new NopInst());

    Pair temp = functionDefinition.getStatements().accept(this);
    mCurrentFunction.getStart().setNext(0, temp.start);
//    Instruction firstInst = null;
//    Instruction lastInst = null;
//    for (var e: functionDefinition.getStatements().getChildren()){
//      Pair statement = e.accept(this);
//      if (firstInst == null){
//        firstInst = statement.getStart();
//      } else {
//        lastInst.setNext(0, statement.getStart());
//      }
//      lastInst = statement.getEnd();
//      mCurrentFunction.getStart().setNext(0,firstInst);
//    }

    mCurrentProgram.addFunction(mCurrentFunction);

    mCurrentLocalVarMap = null;
    mCurrentFunction = null;

    return null;
  }

  @Override
  public Pair visit(StatementList statementList) {
    Instruction firstInst = null;
    Instruction lastInst = null;
    for (var e: statementList.getChildren()){
      Pair statement = e.accept(this);
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
    } else {
      Type t = variableDeclaration.getSymbol().getType();
      String name = variableDeclaration.getSymbol().getName();
      LocalVar lvar = mCurrentFunction.getTempVar(t, name);
      mCurrentLocalVarMap.put(variableDeclaration.getSymbol(), lvar);
    }

    return null;
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
//    if (!mCurrentLocalVarMap.containsKey(sym)){
//      LocalVar lvar = mCurrentFunction.getTempVar(sym.getType());
//      mCurrentLocalVarMap.put(sym, lvar);
//    }
    Variable var = mCurrentLocalVarMap.get(sym);

    NopInst nop = new NopInst();
    return new Pair(nop, nop, var);
  }

  /**
   * Assignment
   */

  @Override
  public Pair visit(Assignment assignment) {
    Pair lhs = assignment.getLocation().accept(this);
    Pair rhs = assignment.getValue().accept(this);

    if (lhs.getVal() instanceof LocalVar){
      LocalVar lhsVar = mCurrentFunction.getTempVar(lhs.getVal().getType());
      CopyInst copyInst = new CopyInst(lhsVar, rhs.getVal());
      rhs.getEnd().setNext(0,copyInst);
      return new Pair(rhs.getStart(), copyInst, null);
    } else {
      //If the location (lhs) is global AND a Name, create AddressAt !!!!!!
      Value val;
      val = lhs.getVal();
      if (assignment.getLocation() instanceof Name){
        val = mCurrentFunction.getTempAddressVar(((Name) assignment.getLocation()).getSymbol().getType());
        AddressAt aa = new AddressAt((AddressVar) val, ((Name) assignment.getLocation()).getSymbol());
        rhs.getEnd().setNext(0, aa);
      }

      LocalVar rhsVar = mCurrentFunction.getTempVar(rhs.getVal().getType());
      StoreInst storeInst = new StoreInst(rhsVar, (AddressVar) val);
      rhs.getEnd().setNext(0, storeInst);
      return new Pair(rhs.getStart(), storeInst, null);
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
    var callInst = new CallInst(retType, calleeAddr, list);


    previousInst.getEnd().setNext(0,callInst);
    return new Pair(previousInst.getStart(), previousInst.getEnd(), null); //callInst for test3 to pass
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
    LocalVar lhs = mCurrentFunction.getTempVar(left.getVal().getType());
    LocalVar rhs = mCurrentFunction.getTempVar(right.getVal().getType());
    switch (operation.getOp()){
      case GE:
        CompareInst geInst = new CompareInst(tempVar, CompareInst.Predicate.GE, lhs, rhs);
        right.getEnd().setNext(0, geInst);
        return new Pair(left.getStart(), geInst, tempVar);
        //break;
      case LE:
        CompareInst leInst = new CompareInst(tempVar, CompareInst.Predicate.LE, lhs, rhs);
        right.getEnd().setNext(0, leInst);
        return new Pair(left.getStart(), leInst , tempVar);
        //break;
      case NE:
        CompareInst neInst = new CompareInst(tempVar, CompareInst.Predicate.NE, lhs, rhs);
        right.getEnd().setNext(0, neInst);
        return new Pair(left.getStart(), neInst , tempVar);
        //break;
      case EQ:
        CompareInst eqInst = new CompareInst(tempVar, CompareInst.Predicate.EQ, lhs, rhs);
        right.getEnd().setNext(0, eqInst);
        return new Pair(left.getStart(), eqInst , tempVar);
        //break;
      case GT:
        CompareInst gtInst = new CompareInst(tempVar, CompareInst.Predicate.GT, lhs, rhs);
        right.getEnd().setNext(0, gtInst);
        return new Pair(left.getStart(), gtInst , tempVar);
        //break;
      case LT:
        CompareInst ltInst = new CompareInst(tempVar, CompareInst.Predicate.LT, lhs, rhs);
        right.getEnd().setNext(0, ltInst);
        return new Pair(left.getStart(), ltInst , tempVar);
        //break;
      case ADD:
        BinaryOperator addBO = new BinaryOperator(BinaryOperator.Op.Add, tempVar, lhs, rhs);
        return new Pair(left.getStart(), addBO, tempVar);
        //break;
      case SUB:
        BinaryOperator subBO = new BinaryOperator(BinaryOperator.Op.Sub, tempVar, lhs, rhs);
        return new Pair(left.getStart(), subBO, tempVar);
        //break;
      case MULT:
        BinaryOperator mulBO = new BinaryOperator(BinaryOperator.Op.Mul, tempVar, lhs, rhs);
        return new Pair(left.getStart(), mulBO, tempVar);
        //break;
      case DIV:
        BinaryOperator divBO = new BinaryOperator(BinaryOperator.Op.Div, tempVar, lhs, rhs);
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
    Pair addr = dereference.accept(this);

    if (addr.getVal() instanceof LocalVar && addr.getVal() != null){
      LocalVar destTemp = mCurrentFunction.getTempVar(addr.getVal().getType());
      CopyInst copyInst = new CopyInst(destTemp, addr.getVal());
      addr.getEnd().setNext(0, copyInst);   //NOT SURE IF addr is previous Instruction
      return new Pair(addr.getStart(), copyInst, null);
    } else {
      Value val;
      val = addr.getVal();
      if (dereference.getAddress() instanceof Name) {
        Type addrType = ((Name) dereference.getAddress()).getSymbol().getType();
        val = mCurrentFunction.getTempAddressVar(addrType);
        AddressAt aa = new AddressAt((AddressVar) val, ((Name) dereference.getAddress()).getSymbol());
        addr.getEnd().setNext(0, aa);
      }
      LocalVar destTemp = mCurrentFunction.getTempVar(addr.getVal().getType());
      LoadInst lInst = new LoadInst(destTemp, (AddressVar) val);
      addr.getEnd().setNext(0, lInst);
      return new Pair(addr.getStart(), lInst, null);
    }

  }

  private Pair visit(Expression expression) {
    expression.getClass().getName();

    return null;
  }

  /**
   * ArrayAccess
   */

  @Override
  public Pair visit(ArrayAccess access) {
    Pair offset = access.getOffset().accept(this);
    Type baseType = access.getBase().getSymbol().getType();
    AddressVar tempAddrVar = mCurrentFunction.getTempAddressVar(baseType);
    LocalVar lvar = mCurrentFunction.getTempVar(offset.getVal().getType());

    AddressAt aa = new AddressAt(tempAddrVar, access.getBase().getSymbol(), lvar);
    return null;  //WHAT DO WE RETURN
  }

  /**
   * Literal
   */

  @Override
  public Pair visit(LiteralBool literalBool) {
    BooleanConstant value = BooleanConstant.get(mCurrentProgram, literalBool.getValue());
    LocalVar tempVar = mCurrentFunction.getTempVar(new BoolType());
    CopyInst copyInst = new CopyInst(tempVar, value);
//    mCurrentFunction.getStart().getNext(mCurrentFunction.getStart().numNext()).setNext(0, copyInst);
//    if (mCurrentFunction.getStart().numNext() == 0){
//      mCurrentFunction.getStart().setNext(0, copyInst);
//    } else {
//      mCurrentFunction.getStart().getNext(mCurrentFunction.getStart().numNext()-1).setNext(0, copyInst);
//    }

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
    return null;
  }

  /**
   * Break Node
   */

  @Override
  public Pair visit(Break brk) {
    return null;
  }

  /**
   * Continue Node
   */

  @Override
  public Pair visit(Continue cnt) {
    return null;
  }

  /**
   * Control Structures
   */

  @Override
  public Pair visit(IfElseBranch ifElseBranch) {
    //JumpInst: if true go to edge1, (else) the default for JumpInst is edge0

    return null;
  }

  /**
   * Loop
   */

  @Override
  public Pair visit(Loop loop) {
    return null;
  }
}
