package crux.frontend.types;

/**
 * Types for Integers values This should implement the equivalent methods along with add,sub, mul,
 * div, and compare equivalent will check if the param is instance of IntType
 */
public final class IntType extends Type {
  @Override
  public String toString() {
    return "int";
  }



  @Override
  public Type add(Type other) {
    if (equivalent(other)){
      return new IntType();
    }
    return super.add(other);
  }

  @Override
  public Type sub(Type other){
    if (equivalent(other)){
      return new IntType();
    }
    return super.sub(other);
  }

  @Override
  public Type mul(Type other){
    if (equivalent(other)){
      return new IntType();
    }
    return super.mul(other);
  }

  @Override
  public Type div(Type other){
    if (equivalent(other)){
      return new IntType();
    }
    return super.div(other);
  }

  @Override
  public boolean equivalent(Type other) {
    return other.getClass() == IntType.class;
  }

  @Override
  public Type compare(Type other){
    if (equivalent(other)) {
      return new BoolType();
    }
    return super.compare(other);
  }

}

