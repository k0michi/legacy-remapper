package com.koyomiji.legacy_remapper.util;

public class ClassUtils {
  public static boolean isClassFile(String filename) {
    return filename.endsWith(".class");
  }

  public static String classNameToPath(String className) {
    return className + ".class";
  }

  public static String pathToClassName(String path) {
    if (!path.endsWith(".class")) {
      throw new IllegalArgumentException("Given path is not a class file");
    }

    return path.substring(0, path.length() - ".class".length());
  }
}
