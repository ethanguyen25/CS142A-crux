package crux.frontend.types;

import java.util.stream.Stream;

/**
 * This Type will represent memory location base is the type of data that will be stored at that
 * memory locaiton This Type can have the following methods: deref,index, assign, and equivalent
 */
public final class AddressType extends Type {
  private final Type base;

  public AddressType(Type base) {
    this.base = base;
  }

  public Type getBaseType() {
    return base;
  }

  @Override
  public boolean equivalent(Type that) {
    if (that.getClass() != AddressType.class)
      return false;

    var aType = (AddressType) that;
    return base.equivalent(aType.base);
  }

  @Override
  public String toString() {
    return "Address(" + base + ")";
  }

  @Override
  public Type deref(){ //bool, int, array(?)
    if (equivalent(this)){
      return base;
    } else if (base.getClass() == BoolType.class) {
     return base;
    } else if (base.getClass() == IntType.class){
      return  base;
    } else if (base.getClass() == ArrayType.class) {
      return base;
    }
    return super.deref();

  }

  @Override
  public Type index(Type other){
    if (base.getClass() == ArrayType.class){
      return new AddressType(((ArrayType) base).getBase());
    }
    return super.index(other);
  }

  @Override
  public Type assign(Type other){
    if (getBaseType().equivalent(other)){
      return new VoidType();
    }
    return super.assign(other);
  }


}


