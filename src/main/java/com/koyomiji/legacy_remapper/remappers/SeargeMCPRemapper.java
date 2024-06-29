package com.koyomiji.legacy_remapper.remappers;

import com.koyomiji.legacy_remapper.ILocalVariableRemapper;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mappings.SeargeMCPMapping;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.commons.Remapper;

public class SeargeMCPRemapper
    extends Remapper implements ILocalVariableRemapper {
  private final SeargeMCPMapping mapping;
  private final Map<String, String> fieldMap = new HashMap<>();
  private final Map<String, String> methodMap = new HashMap<>();
  private final Map<String, String> paramMap = new HashMap<>();
  private final Side side;

  public SeargeMCPRemapper(SeargeMCPMapping mapping, Side side) {
    this.mapping = mapping;
    this.side = side;

    for (SeargeMCPMapping.MemberEntry e : mapping.fields) {
      if (e.side.includes(side)) {
        fieldMap.put(e.seargeName, e.mcpName);
      }
    }

    for (SeargeMCPMapping.MemberEntry e : mapping.methods) {
      if (e.side.includes(side)) {
        methodMap.put(e.seargeName, e.mcpName);
      }
    }

    for (SeargeMCPMapping.ParamEntry e : mapping.params) {
      if (e.side.includes(side)) {
        paramMap.put(e.seargeName, e.mcpName);
      }
    }
  }

  public SeargeMCPMapping getMapping() { return mapping; }

  @Override
  public String mapLocalVariableName(String owner, String methodName,
                                     String methodDescriptor, String name,
                                     String descriptor, int index) {
    return paramMap.getOrDefault(name, name);
  }

  @Override
  public String mapFieldName(String owner, String name, String descriptor) {
    return fieldMap.getOrDefault(name, name);
  }

  @Override
  public String mapMethodName(String owner, String name, String descriptor) {
    return methodMap.getOrDefault(name, name);
  }
}
