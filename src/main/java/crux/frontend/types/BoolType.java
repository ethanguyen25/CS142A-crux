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
}
