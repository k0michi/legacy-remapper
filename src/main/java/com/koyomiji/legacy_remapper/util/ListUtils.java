package com.koyomiji.legacy_remapper.util;

import java.util.Collections;
import java.util.List;

public class ListUtils {
  public static <E> List<E> safeSubList(List<E> list, int fromIndex,
                                        int toIndex) {
    if (fromIndex >= toIndex) {
      return Collections.emptyList();
    }

    return list.subList(Math.max(0, fromIndex), Math.min(list.size(), toIndex));
  }

  public static <E> List<E> safeSubList(List<E> list, int fromIndex) {
    return safeSubList(list, fromIndex, list.size());
  }
}
