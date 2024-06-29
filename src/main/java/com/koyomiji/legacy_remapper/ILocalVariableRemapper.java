package com.koyomiji.legacy_remapper;

public interface ILocalVariableRemapper {
  String mapLocalVariableName(String owner, String methodName,
                              String methodDescriptor, String name,
                              String descriptor, int index);
}
