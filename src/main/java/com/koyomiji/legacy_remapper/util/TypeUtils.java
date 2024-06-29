package com.koyomiji.legacy_remapper.util;

import java.util.Arrays;
import java.util.List;
import org.objectweb.asm.Type;

public class TypeUtils {
  public static int toUnsizedIndex(List<Type> types, int sizedIndex) {
    int sum = 0;

    for (int i = 0; i < types.size(); i++) {
      if (sum == sizedIndex) {
        return i;
      }

      sum += types.get(i).getSize();
    }

    return -1;
  }

  public static int toUnsizedIndex(Type[] types, int sizedIndex) {
    return toUnsizedIndex(Arrays.asList(types), sizedIndex);
  }

  public static int toSizedIndex(List<Type> types, int unsizedIndex) {
    int sum = 0;

    for (int i = 0; i < types.size(); i++) {
      if (i == unsizedIndex) {
        return sum;
      }

      sum += types.get(i).getSize();
    }

    return -1;
  }

  public static int toSizedIndex(Type[] types, int unsizedIndex) {
    return toSizedIndex(Arrays.asList(types), unsizedIndex);
  }

  public static int sizedEnd(List<Type> types) {
    int sum = 0;

    for (Type type : types) {
      sum += type.getSize();
    }

    return sum;
  }

  public static int sizedEnd(Type[] types) {
    return sizedEnd(Arrays.asList(types));
  }

  public static int paramUnsizedEnd(String descriptor, boolean isStaticMethod) {
    return Type.getArgumentTypes(descriptor).length + (isStaticMethod ? 0 : 1);
  }

  public static int paramUnsizedBegin(String descriptor,
                                      boolean isStaticMethod) {
    return isStaticMethod ? 0 : 1;
  }

  public static int paramSizedEnd(String descriptor, boolean isStaticMethod) {
    return sizedEnd(Type.getArgumentTypes(descriptor)) +
        (isStaticMethod ? 0 : 1);
  }

  public static int paramSizedBegin(String descriptor, boolean isStaticMethod) {
    return isStaticMethod ? 0 : 1;
  }
}
