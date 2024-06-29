package com.koyomiji.legacy_remapper.util;

public class ObjectUtils {
  public static <T> T orDefault(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
