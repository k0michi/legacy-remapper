package com.koyomiji.legacy_remapper.remappers;

import com.koyomiji.legacy_remapper.ClassIndex;
import com.koyomiji.legacy_remapper.FieldIdentifier;
import com.koyomiji.legacy_remapper.MethodIdentifier;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mappings.NotchSeargeMapping;
import com.koyomiji.legacy_remapper.util.ObjectUtils;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.commons.Remapper;

public class NotchSeargeRemapper extends Remapper {
  private final NotchSeargeMapping mapping;
  private final ClassIndex classIndex;
  private final Map<String, String> classMap = new HashMap<>();
  private final Map<FieldIdentifier, String> fieldMap = new HashMap<>();
  private final Map<MethodIdentifier, String> methodMap = new HashMap<>();

  public NotchSeargeRemapper(NotchSeargeMapping mapping, ClassIndex classIndex,
                             Side side) {
    this.mapping = mapping;
    this.classIndex = classIndex;

    for (NotchSeargeMapping.ClassEntry e : mapping.classes) {
      if (e.side.includes(side)) {
        classMap.put(e.notchName, e.seargeName);
      }
    }

    for (NotchSeargeMapping.FieldEntry e : mapping.fields) {
      if (e.side.includes(side)) {
        fieldMap.put(new FieldIdentifier(e.notchClassName, e.notchName),
                     e.seargeName);
      }
    }

    for (NotchSeargeMapping.MethodEntry e : mapping.methods) {
      if (e.side.includes(side)) {
        methodMap.put(
            new MethodIdentifier(e.notchClassName, e.notchName, e.notchDesc),
            e.seargeName);
      }
    }
  }

  public NotchSeargeMapping getMapping() { return mapping; }

  public ClassIndex getDeclarationMap() { return classIndex; }

  public boolean containsClass(String notchClassName) {
    return classMap.containsKey(notchClassName);
  }

  @Override
  public String map(String notchName) {
    return classMap.getOrDefault(notchName, notchName);
  }

  @Override
  public String mapMethodName(String owner, String name, String descriptor) {
    String mapped =
        methodMap.get(new MethodIdentifier(owner, name, descriptor));

    if (mapped == null) {
      for (String parent : classIndex.getAncestorClasses(owner)) {
        mapped = methodMap.get(new MethodIdentifier(parent, name, descriptor));
        if (mapped != null)
          break;
      }
    }

    return ObjectUtils.orDefault(mapped, name);
  }

  @Override
  public String mapFieldName(String owner, String name, String descriptor) {
    String mapped = fieldMap.get(new FieldIdentifier(owner, name));

    if (mapped == null) {
      for (String parent : classIndex.getAncestorClasses(owner)) {
        mapped = fieldMap.get(new FieldIdentifier(parent, name));
        if (mapped != null)
          break;
      }
    }

    return ObjectUtils.orDefault(mapped, name);
  }
}
