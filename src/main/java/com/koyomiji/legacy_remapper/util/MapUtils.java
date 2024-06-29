package com.koyomiji.legacy_remapper.util;

import java.util.Map;
import java.util.Optional;

public class MapUtils {
  public static <K, V> Optional<V> getOptional(Map<K, V> map, K key) {
    V v = map.get(key);

    if (v != null) {
      return Optional.of(v);
    } else {
      return Optional.empty();
    }
  }
}
