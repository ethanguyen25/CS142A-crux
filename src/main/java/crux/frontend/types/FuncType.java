package crux.frontend.types;

/**
 * FuncType for functions args is a TypeList to create a type for each param and push it to the list
 * ret is the type of the function return type, could be int,bool,void Two functions are equivalent
 * if their args and ret are also equivalent This Class should implement Call method
 */
public final class FuncType extends Type {
  private TypeList args;
  private Type ret;

  public FuncType(TypeList args, Type returnType) {
    this.args = args;
    this.ret = returnType;
  }

  public Type getRet() {
    return ret;
  }

  public TypeList getArgs() {
    return args;
  }

  @Override
  public String toString() {
    return "func(" + args + "):" + ret;
  }


  @Override
  public Type call(Type arguments) {
    if (arguments.getClass() == TypeList.class){
      if (arguments.equivalent(args)){
        return this.ret;
      }
    }
    return super.call(arguments);

  }


  @Override
  public boolean equivalent(Type other) {
    if (other instanceof FuncType){
      FuncType ft = (FuncType) other;
      if (this.ret.equivalent(ft.ret)) {
        return this.args.equivalent(ft.args);
      }
    }
    return false;
  }


}



