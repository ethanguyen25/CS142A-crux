package crux.frontend;

import crux.frontend.types.Type;

/**
 * Symbol will have name, type and error
 */
public final class Symbol {
  private final String name;
  private final Type type;
  private final String error;

  /**
   *
   * @param name String
   * @param type the Type
   */
  Symbol(String name, Type type) {
    this.name = name;
    this.type = type;
    this.error = null;
  }

  Symbol(String name, String error) {
    this.name = name;
    this.type = null;
    this.error = error;
  }

  /**
   *
   * @return String the name
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @return the type
   */
  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    if (error != null) {
      return String.format("Symbol(%s:%s)", name, error);
    }
    return String.format("Symbol(%s:%s)", name, type);
  }

  public String toString(boolean includeType) {
    if (error != null) {
      return toString();
    }
    return includeType ? toString() : String.format("Symbol(%s)", name);
  }
}
