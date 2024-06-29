package com.koyomiji.legacy_remapper.remapper;

import com.koyomiji.legacy_remapper.Access;
import com.koyomiji.legacy_remapper.MethodIdentifier;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.SidedMethodIdentifier;
import com.koyomiji.legacy_remapper.mapping.SeargeExceptor;
import java.util.List;

public class SeargeExceptorAccessor {
  private SeargeExceptor exceptor;
  private Side side;

  public SeargeExceptorAccessor(SeargeExceptor exceptor, Side side) {
    this.exceptor = exceptor;
    this.side = side;
  }

  private SidedMethodIdentifier toSided(MethodIdentifier method, Side side) {
    return new SidedMethodIdentifier(method, side);
  }

  public SeargeExceptor.MethodEntry getMethod(MethodIdentifier method) {
    SeargeExceptor.MethodEntry e =
        exceptor.methods.get(new SidedMethodIdentifier(method, side));

    if (e != null) {
      return e;
    }

    return exceptor.methods.get(new SidedMethodIdentifier(method, Side.BOTH));
  }

  public boolean containsMethod(MethodIdentifier method) {
    return exceptor.methods.containsKey(toSided(method, side)) ||
        exceptor.methods.containsKey(toSided(method, Side.BOTH));
  }

  public Access getMethodAccess(MethodIdentifier method) {
    return getMethod(method).access.orElse(null);
  }

  public boolean containsMethodAccess(MethodIdentifier method) {
    return containsMethod(method) && getMethod(method).access.isPresent();
  }

  public List<String> getExceptions(MethodIdentifier method) {
    SeargeExceptor.MethodEntry e = getMethod(method);

    if (e == null) {
      return null;
    }

    return e.exceptions;
  }

  public List<String> getParamNames(MethodIdentifier method) {
    SeargeExceptor.MethodEntry e = getMethod(method);

    if (e == null) {
      return null;
    }

    return e.params;
  }

  public String getParamName(MethodIdentifier method, int paramIndex) {
    List<String> paramNames = getParamNames(method);

    if (paramNames == null) {
      return null;
    }

    if (paramIndex < 0 || paramIndex >= paramNames.size()) {
      return null;
    }

    return paramNames.get(paramIndex);
  }

  public SeargeExceptor.ClassEntry getClass(String className) {
    return exceptor.classes.get(className);
  }

  public boolean containsClass(String className) {
    return exceptor.classes.containsKey(className);
  }

  public SeargeExceptor getExceptor() { return exceptor; }

  public void setExceptor(SeargeExceptor exceptor) { this.exceptor = exceptor; }

  public Side getSide() { return side; }

  public void setSide(Side side) { this.side = side; }
}
