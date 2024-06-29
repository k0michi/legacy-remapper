package com.koyomiji.legacy_remapper.remapper;

import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mapping.NotchSeargeMapping;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.commons.Remapper;

public class SeargeNotchRemapper extends Remapper {
  private final NotchSeargeMapping mapping;
  private final Map<String, String> classMap = new HashMap<>();
  private final Map<String, String> fieldMap = new HashMap<>();
  private final Map<String, String> methodMap = new HashMap<>();

  public SeargeNotchRemapper(NotchSeargeMapping mapping, Side side) {
    this.mapping = mapping;

    for (NotchSeargeMapping.ClassEntry e : mapping.classes) {
      if (e.side.includes(side)) {
        classMap.put(e.seargeName, e.notchName);
      }
    }

    for (NotchSeargeMapping.FieldEntry e : mapping.fields) {
      if (e.side.includes(side)) {
        fieldMap.put(e.seargeName, e.notchName);
      }
    }

    for (NotchSeargeMapping.MethodEntry e : mapping.methods) {
      if (e.side.includes(side)) {
        methodMap.put(e.seargeName, e.notchName);
      }
    }
  }

  public NotchSeargeMapping getMapping() { return mapping; }

  @Override
  public String map(String seargeName) {
    return classMap.getOrDefault(seargeName, seargeName);
  }

  @Override
  public String mapMethodName(String owner, String name, String descriptor) {
    return methodMap.getOrDefault(name, name);
  }

  @Override
  public String mapFieldName(String owner, String name, String descriptor) {
    return fieldMap.getOrDefault(name, name);
  }
}
