package com.koyomiji.legacy_remapper;

import java.util.Objects;

public class SidedMethodIdentifier extends MethodIdentifier {
  public final Side side;

  public SidedMethodIdentifier(MethodIdentifier method, Side side) {
    this(method.className, method.name, method.desc, side);
  }

  public SidedMethodIdentifier(String className, String name, String desc,
                               Side side) {
    super(className, name, desc);
    this.side = side;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    SidedMethodIdentifier that = (SidedMethodIdentifier)o;
    return side == that.side;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), side);
  }

  @Override
  public String toString() {
    return "SidedMethodIdentifier{"
        + "className='" + className + '\'' + ", name='" + name + '\'' +
        ", desc='" + desc + '\'' + ", side=" + side + '}';
  }
}
