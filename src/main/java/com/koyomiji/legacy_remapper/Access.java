package com.koyomiji.legacy_remapper;

import org.objectweb.asm.Opcodes;

public enum Access {
  PRIVATE,
  DEFAULT,
  PROTECTED,
  PUBLIC;

  public static int changeAccess(int accessFlags, Access access) {
    accessFlags &=
        ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED | Opcodes.ACC_PUBLIC);
    accessFlags |= (access == PRIVATE) ? Opcodes.ACC_PRIVATE : 0;
    accessFlags |= (access == PROTECTED) ? Opcodes.ACC_PROTECTED : 0;
    accessFlags |= (access == PUBLIC) ? Opcodes.ACC_PUBLIC : 0;
    return accessFlags;
  }
}
