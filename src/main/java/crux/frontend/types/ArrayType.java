package crux.frontend.types;

/**
 * This Type will Array data type base is the base of the array, could be int or bool for
 * cruxlang This should implement the equivalent methods Two arrays are equivalent if their bases
 * are equivalent and have same extent
 */
public final class ArrayType extends Type {
  private final Type base;
  private final long extent;

  public ArrayType(long extent, Type base) {
    this.extent = extent;
    this.base = base;
  }

  public Type getBase() {
    return base;
  }

  public long getExtent() {
    return extent;
  }

  @Override
  public String toString() {
    return String.format("array[%d,%s]", extent, base);
  }

  @Override
  public boolean equivalent(Type that) {   // WRONG !!!!!
    if (base.equivalent(that)) {
      if( ((ArrayType) that).getExtent() == extent) {
        return true;
      }
    }
    return false;
  }

}
