package crux.frontend.types;

/**
 * This Type will Array data type base is the base of the array, could be int, bool, or char for
 * cruxlang This should implement the equivalent methods Two arrays are equivalent if their bases
 * are equivalent and have same extend
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
}
