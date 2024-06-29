package com.koyomiji.legacy_remapper.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PrintUtils {
  public static void println(Object... objects) {
    String s = String.join(" ", Arrays.asList(objects)
                                    .stream()
                                    .map(o -> o != null ? o.toString() : "null")
                                    .collect(Collectors.toList()));
    System.out.println(s);
  }
}
