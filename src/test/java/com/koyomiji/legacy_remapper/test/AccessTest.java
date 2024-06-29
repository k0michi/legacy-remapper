package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.Access;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

public class AccessTest {
  @Test
  void test_changeAccess() {
    Assertions.assertEquals(
        Opcodes.ACC_PRIVATE,
        Access.changeAccess(Opcodes.ACC_PUBLIC, Access.PRIVATE));
    Assertions.assertEquals(
        0, Access.changeAccess(Opcodes.ACC_PUBLIC, Access.DEFAULT));
    Assertions.assertEquals(
        Opcodes.ACC_STATIC,
        Access.changeAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                            Access.DEFAULT));
  }
}
