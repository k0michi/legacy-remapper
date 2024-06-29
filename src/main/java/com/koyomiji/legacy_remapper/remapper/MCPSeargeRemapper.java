package com.koyomiji.legacy_remapper.remapper;

import com.koyomiji.legacy_remapper.ClassIndex;
import com.koyomiji.legacy_remapper.FieldIdentifier;
import com.koyomiji.legacy_remapper.MethodIdentifier;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mapping.SeargeMCPMapping;
import com.koyomiji.legacy_remapper.util.ObjectUtils;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.commons.Remapper;

public class MCPSeargeRemapper extends Remapper {
  private final SeargeMCPMapping mapping;
  private final ClassIndex classIndex;
  private final Map<String, String> mcpFieldMap = new HashMap<>();
  private final Map<String, String> mcpMethodMap = new HashMap<>();
  private final Map<FieldIdentifier, String> fieldMap = new HashMap<>();
  private final Map<MethodIdentifier, String> methodMap = new HashMap<>();

  public MCPSeargeRemapper(SeargeMCPMapping mapping, ClassIndex classIndex,
                           Side side) {
    this.mapping = mapping;
    this.classIndex = classIndex;

    for (SeargeMCPMapping.MemberEntry e : mapping.fields) {
      if (e.side.includes(side)) {
        mcpFieldMap.put(e.seargeName, e.mcpName);
      }
    }

    for (SeargeMCPMapping.MemberEntry e : mapping.methods) {
      if (e.side.includes(side)) {
        mcpMethodMap.put(e.seargeName, e.mcpName);
      }
    }

    for (ClassIndex.Class c : classIndex.getClasses()) {
      for (ClassIndex.Member f : c.fields) {
        String mapped = mcpFieldMap.get(f.name);

        if (mapped != null) {
          fieldMap.put(new FieldIdentifier(c.name, mapped), f.name);
        }
      }

      for (ClassIndex.Member m : c.methods) {
        String mapped = mcpMethodMap.get(m.name);

        if (mapped != null) {
          methodMap.put(new MethodIdentifier(c.name, mapped, m.descriptor),
                        m.name);
        }
      }
    }
  }

  public SeargeMCPMapping getMapping() { return mapping; }

  public ClassIndex getDeclarationMap() { return classIndex; }

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
}
