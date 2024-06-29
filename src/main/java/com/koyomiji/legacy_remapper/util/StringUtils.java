package com.koyomiji.legacy_remapper.util;

public class StringUtils {
  public static String[] splitBeforeFirst(String string, String str) {
    int index = string.indexOf(str);
    return new String[] {string.substring(0, index), string.substring(index)};
  }

  public static String[] splitNoEmpty(String string, String str) {
    if (string.isEmpty()) {
      return new String[] {};
    }

    return string.split(str);
  }
}
