package crux.frontend.types;

/**
 * Types for Booleans values This should implement the equivalent methods along with and,or, and not
 * equivalent will check if the param is instance of BoolType
 */
public final class BoolType extends Type {
  @Override
  public boolean equivalent(Type that) {
    return that.getClass() == BoolType.class;
  }

  @Override
  public String toString() {
    return "bool";
  }


  @Override
  public Type and(Type other){
    if (equivalent(other)){
      return new BoolType();
    }
    return super.and(other);
  }

  @Override
  public Type or(Type other){
    if (equivalent(other)){
      return new BoolType();
    }
    return super.or(other);
  }

  @Override
  public Type not(){
    return new BoolType();
  }

  @Override
  public Type compare(Type other){
    if (equivalent(other)){
      return new BoolType();
    }
    return super.compare(other);
  }

}
