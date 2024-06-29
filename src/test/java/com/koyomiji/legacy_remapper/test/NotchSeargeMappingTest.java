package com.koyomiji.legacy_remapper.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mappings.NotchSeargeMapping;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class NotchSeargeMappingTest {
  @Test
  void test_readSRG() throws IOException {
    var parsed = NotchSeargeMapping.readSRG(
        TestUtils.readerFromString("CL: a net/minecraft/test/Test"));
    assertEquals(new NotchSeargeMapping.ClassEntry(
                     "a", "net/minecraft/test/Test", Side.BOTH),
                 parsed.classes.getFirst());
  }

  @Test
  void test_readSRG_2() throws IOException {
    var parsed = NotchSeargeMapping.readSRG(
        TestUtils.readerFromString("FD: a/b net/minecraft/test/Test/field"));
    assertEquals(
        new NotchSeargeMapping.FieldEntry("a", "b", "field", Side.BOTH),
        parsed.fields.getFirst());
  }

  @Test
  void test_readSRG_3() throws IOException {
    var parsed = NotchSeargeMapping.readSRG(TestUtils.readerFromString(
        "MD: a/b ()V net/minecraft/test/Test/func ()V"));
    assertEquals(
        new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func", Side.BOTH),
        parsed.methods.getFirst());
  }

  @Test
  void test_readSRG_4() throws IOException {
    var parsed = NotchSeargeMapping.readSRG(TestUtils.readerFromString(
        "MD: a/b ()V net/minecraft/test/Test/func ()V #C"));
    assertEquals(new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func",
                                                    Side.CLIENT),
                 parsed.methods.getFirst());
  }

  @Test
  void test_readSRG_5() throws IOException {
    var parsed = NotchSeargeMapping.readSRG(TestUtils.readerFromString(
        "MD: a/b ()V net/minecraft/test/Test/func ()V #S"));
    assertEquals(new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func",
                                                    Side.SERVER),
                 parsed.methods.getFirst());
  }

  @Test
  void test_readCSRG() throws IOException {
    var parsed = NotchSeargeMapping.readCSRG(
        TestUtils.readerFromString("net/ net\n"
                                   + "a net/minecraft/test/Test"));
    assertEquals(new NotchSeargeMapping.ClassEntry(
                     "a", "net/minecraft/test/Test", Side.BOTH),
                 parsed.classes.getFirst());
  }

  @Test
  void test_readCSRG_1() throws IOException {
    var parsed = NotchSeargeMapping.readCSRG(
        TestUtils.readerFromString("a b field_0_a"));
    assertEquals(
        new NotchSeargeMapping.FieldEntry("a", "b", "field_0_a", Side.BOTH),
        parsed.fields.getFirst());
  }

  @Test
  void test_readCSRG_2() throws IOException {
    var parsed = NotchSeargeMapping.readCSRG(
        TestUtils.readerFromString("a b ()V func_0_a"));
    assertEquals(new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func_0_a",
                                                    Side.BOTH),
                 parsed.methods.getFirst());
  }

  @Test
  void test_readMCP2SRG() throws IOException {
    var parsed = NotchSeargeMapping.readMCP2RGS(
        TestUtils.readerFromString(".class_map a Test"), Side.BOTH);
    assertEquals(new NotchSeargeMapping.ClassEntry(
                     "a", "net/minecraft/src/Test", Side.BOTH),
                 parsed.classes.getFirst());
  }

  @Test
  void test_readMCP2SRG_1() throws IOException {
    var parsed = NotchSeargeMapping.readMCP2RGS(
        TestUtils.readerFromString(".field_map a/b field"), Side.BOTH);
    assertEquals(
        new NotchSeargeMapping.FieldEntry("a", "b", "field", Side.BOTH),
        parsed.fields.getFirst());
  }

  @Test
  void test_readMCP2SRG_2() throws IOException {
    var parsed = NotchSeargeMapping.readMCP2RGS(
        TestUtils.readerFromString(".method_map a/b ()V func_0_a"), Side.BOTH);
    assertEquals(new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func_0_a",
                                                    Side.BOTH),
                 parsed.methods.getFirst());
  }
}
