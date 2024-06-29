package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.util.TypeUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

public class TypeUtilsTest {
  @Test
  void test_sizedEnd() {
    Assertions.assertEquals(1, TypeUtils.sizedEnd(List.of(Type.INT_TYPE)));
  }

  @Test
  void test_sizedEnd_1() {
    Assertions.assertEquals(2, TypeUtils.sizedEnd(List.of(Type.LONG_TYPE)));
  }

  @Test
  void test_paramSizedEnd() {
    Assertions.assertEquals(3, TypeUtils.paramSizedEnd("(III)V", true));
  }

  @Test
  void test_paramSizedEnd_1() {
    Assertions.assertEquals(0, TypeUtils.paramSizedEnd("()V", true));
  }

  @Test
  void test_paramSizedEnd_2() {
    Assertions.assertEquals(1, TypeUtils.paramSizedEnd("()V", false));
  }

  @Test
  void test_paramSizedEnd_3() {
    Assertions.assertEquals(3, TypeUtils.paramSizedEnd("(J)V", false));
  }

  @Test
  void test_paramSizedBegin() {
    Assertions.assertEquals(0, TypeUtils.paramSizedBegin("(III)V", true));
  }

  @Test
  void test_paramSizedBegin_1() {
    Assertions.assertEquals(0, TypeUtils.paramSizedEnd("()V", true));
  }

  @Test
  void test_paramSizedBegin_2() {
    Assertions.assertEquals(1, TypeUtils.paramSizedBegin("()V", false));
  }

  @Test
  void test_paramSizedBegin_3() {
    Assertions.assertEquals(1, TypeUtils.paramSizedBegin("(J)V", false));
  }

  @Test
  void test_toUnsizedIndex() {
    Assertions.assertEquals(
        1, TypeUtils.toUnsizedIndex(List.of(Type.INT_TYPE, Type.INT_TYPE), 1));
  }

  @Test
  void test_toUnsizedIndex_1() {
    Assertions.assertEquals(
        1, TypeUtils.toUnsizedIndex(List.of(Type.LONG_TYPE, Type.INT_TYPE), 2));
  }

  @Test
  void test_toSizedIndex() {
    Assertions.assertEquals(
        1, TypeUtils.toSizedIndex(List.of(Type.INT_TYPE, Type.INT_TYPE), 1));
  }

  @Test
  void test_toSizedIndex_1() {
    Assertions.assertEquals(
        2, TypeUtils.toSizedIndex(List.of(Type.LONG_TYPE, Type.INT_TYPE), 1));
  }
}
