package com.koyomiji.legacy_remapper;

import java.util.Objects;

public class FieldIdentifier {
  public final String className;
  public final String name;

  public FieldIdentifier(String className, String name) {
    this.className = className;
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    FieldIdentifier that = (FieldIdentifier)o;
    return Objects.equals(className, that.className) &&
        Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(className, name);
  }

  @Override
  public String toString() {
    return "FieldIdentifier{"
        + "className='" + className + '\'' + ", name='" + name + '\'' + '}';
  }
}
