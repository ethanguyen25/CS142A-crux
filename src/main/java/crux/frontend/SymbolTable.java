package crux.frontend;

import crux.frontend.ast.Position;
import crux.frontend.types.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import java.util.*;

/**
 * Symbol table will map each symbol from Crux source code to its declaration or appearance in the
 * source. the symbol table is made up of scopes, each scope is a map which maps an identifier to
 * it's symbol. Scopes are inserted to the table starting from the first scope (Global Scope). The
 * Global scope is the first scope in each Crux program and it contains all the built in functions
 * and names. The symbol table is an ArrayList of scopes.
 */

final class SymbolTable {
  private final PrintStream err;
  private final ArrayList<Map<String, Symbol>> symbolScopes = new ArrayList<>();

  private boolean encounteredError = false;

  SymbolTable(PrintStream err) {
    this.err = err;
    // TODO. Initialize Global Scope Hash Map

    HashMap<String, Symbol> globalScopes = new HashMap<String, Symbol>();

    //readInt
    TypeList readIntList = TypeList.of();
    globalScopes.put("readInt", new Symbol("readInt", new FuncType(readIntList, new IntType())));
    //readChar
    TypeList readCharList = TypeList.of();
    globalScopes.put("readChar", new Symbol("readChar", new FuncType(readCharList, new IntType())));
    //printBool
    TypeList printBoolList = TypeList.of(new BoolType());
    globalScopes.put("printBool", new Symbol("printBool", new FuncType(printBoolList, new VoidType())));
    //printInt
    TypeList printIntList = TypeList.of(new IntType());
    globalScopes.put("printInt", new Symbol("printInt", new FuncType(printIntList, new VoidType())));
    //printChar
    TypeList printCharList = TypeList.of(new IntType());
    globalScopes.put("printChar", new Symbol("printChar", new FuncType(printCharList, new VoidType())));
    //println
    TypeList printlnList = TypeList.of();
    globalScopes.put("println", new Symbol("println", new FuncType(printlnList, new VoidType())));

    symbolScopes.add(globalScopes);
  }

  boolean hasEncounteredError() {
    return encounteredError;
  }

  void enter() {
    // TODO. Add new scope/HashMap to symbolScopes
    symbolScopes.add(new HashMap<>());
  }

  void exit() {
    // TODO. Remove the most recent scope from symbolScopes
    symbolScopes.remove(symbolScopes.size()-1);
  }

  /**
   * Insert a symbol to the table at the most recent scope. if the name already exists in the
   * current scope that's a declareation error.
   */

  Symbol add(Position pos, String name, Type type) {
    // TODO. Add a symbol to the most recent scope IF the symbol does not exist already.
    // TODO. If it does exist then it should be a declaration error.

    //WHAT IS 'POS' USED FOR

    Map<String, Symbol> currentScope = symbolScopes.get(symbolScopes.size()-1);
    if (currentScope.get(name) == null) {
      Symbol newSymbol = new Symbol(name, type);
      currentScope.put(name, newSymbol);
      return newSymbol;
    } else {
      err.print("DeclarationError");
      encounteredError = true;
      return new Symbol(name, "DeclarationError");
    }
  }

  /**
   * lookup a name in the SymbolTable, if the name not found in the table it should encounter an
   * error and return a symbol with ResolveSymbolError error. if the symbol is found then return it.
   */

  Symbol lookup(Position pos, String name) {
    var symbol = find(name);
    if (symbol == null) {
      err.printf("ResolveSymbolError%s[Could not find %s.]%n", pos, name);
      encounteredError = true;
      return new Symbol(name, "ResolveSymbolError");
    } else {
      return symbol;
    }
  }

  /**
   * Try to find a symbol in the table starting from the most recent scope.
   */

  private Symbol find(String name) {
    // TODO. Search the most recent scope for a symbol. If not found, it will return null.
    for (int i = symbolScopes.size()-1; i >= 0; i--) {
          Map<String, Symbol> current = symbolScopes.get(i);
          if (current.get(name) != null) {
            return current.get(name);
          }
    }
    return null;
  }
}
