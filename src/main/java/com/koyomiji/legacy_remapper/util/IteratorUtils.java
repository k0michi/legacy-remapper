package com.koyomiji.legacy_remapper.util;

import java.util.Iterator;

public class IteratorUtils {
  public static <E> void skip(Iterator<E> it, int count) {
    for (int i = 0; i < count; i++) {
      it.next();
    }
  }
}
