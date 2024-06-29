package com.koyomiji.legacy_remapper;

import java.util.Objects;

public class MethodIdentifier {
  public final String className;
  public final String name;
  public final String desc;

  public MethodIdentifier(String className, String name, String desc) {
    this.className = className;
    this.name = name;
    this.desc = desc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MethodIdentifier that = (MethodIdentifier)o;
    return Objects.equals(className, that.className) &&
        Objects.equals(name, that.name) && Objects.equals(desc, that.desc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(className, name, desc);
  }

  @Override
  public String toString() {
    return "MethodIdentifier{"
        + "className='" + className + '\'' + ", name='" + name + '\'' +
        ", desc='" + desc + '\'' + '}';
  }
}
