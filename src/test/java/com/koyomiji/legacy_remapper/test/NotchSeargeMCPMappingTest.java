package com.koyomiji.legacy_remapper.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mappings.NotchSeargeMCPMapping;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class NotchSeargeMCPMappingTest {
  @Test
  void test_readMCP1RGS() throws IOException {
    var parsed = NotchSeargeMCPMapping.readMCP1RGS(
        TestUtils.readerFromString(".option Application\n"
                                   + "\n"
                                   + ".field_map a/b field\n"
                                   + "\n"
                                   + "### GENERATED MAPPINGS:\n"
                                   + ".field_map a/b field_0_a"),
        Side.BOTH);
    assertEquals(new NotchSeargeMCPMapping.FieldEntry("a", "b", "field_0_a",
                                                      "field", Side.BOTH),
                 parsed.fields.getFirst());
  }

  @Test
  void test_readMCP3ClassCSV() throws IOException {
    var parsed =
        NotchSeargeMCPMapping.readMCP3ClassCSV(TestUtils.readerFromString(
            "\"name\",\"notch\",\"supername\",\"package\",\"side\"\n"
            + "\"Test\",\"a\",\"\",\"net/minecraft/test\",\"0\""));
    assertEquals(new NotchSeargeMCPMapping.ClassEntry(
                     "a", "net/minecraft/test/Test", Side.CLIENT),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP3ClassCSV_1() throws IOException {
    var parsed =
        NotchSeargeMCPMapping.readMCP3ClassCSV(TestUtils.readerFromString(
            "\"name\",\"notch\",\"supername\",\"package\",\"side\"\n"
            +
            "\"Minecraft\",\"Minecraft\",\"\",\"net/minecraft/client\",\"0\""));
    assertEquals(new NotchSeargeMCPMapping.ClassEntry(
                     "net/minecraft/client/Minecraft",
                     "net/minecraft/client/Minecraft", Side.CLIENT),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP3FieldCSV() throws IOException {
    var parsed =
        NotchSeargeMCPMapping.readMCP3FieldCSV(TestUtils.readerFromString(
            "\"searge\",\"name\",\"notch\",\"sig\",\"notchsig\","
            + "\"classname\",\"classnotch\",\"package\",\"side\"\n"
            + "\"field_0_a\",\"field\",\"b\",\"I\",\"I\",\"Test\",\"a\","
            + "\"net/minecraft/test\",\"0\""));
    assertEquals(new NotchSeargeMCPMapping.FieldEntry("a", "b", "field_0_a",
                                                      "field", Side.CLIENT),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP3FieldCSV_1() throws IOException {
    var parsed =
        NotchSeargeMCPMapping.readMCP3FieldCSV(TestUtils.readerFromString(
            "\"searge\",\"name\",\"notch\",\"sig\",\"notchsig\","
            + "\"classname\",\"classnotch\",\"package\",\"side\"\n"
            + "\"field_0_a\",\"field\",\"b\",\"I\",\"I\",\"Minecraft\","
            + "\"Minecraft\",\"net/minecraft/client\",\"0\""));
    assertEquals(new NotchSeargeMCPMapping.FieldEntry(
                     "net/minecraft/client/Minecraft", "b", "field_0_a",
                     "field", Side.CLIENT),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP3MethodCSV() throws IOException {
    var parsed =
        NotchSeargeMCPMapping.readMCP3MethodCSV(TestUtils.readerFromString(
            "\"searge\",\"name\",\"notch\",\"sig\",\"notchsig\","
            + "\"classname\",\"classnotch\",\"package\",\"side\"\n"
            + "\"func_0_a\",\"func\",\"b\",\"()V\",\"()V\",\"Test\",\"a\","
            + "\"net/minecraft/test\",\"0\""));
    assertEquals(new NotchSeargeMCPMapping.MethodEntry(
                     "a", "b", "()V", "func_0_a", "func", Side.CLIENT),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP3MethodCSV_1() throws IOException {
    var parsed =
        NotchSeargeMCPMapping.readMCP3MethodCSV(TestUtils.readerFromString(
            "\"searge\",\"name\",\"notch\",\"sig\",\"notchsig\","
            + "\"classname\",\"classnotch\",\"package\",\"side\"\n"
            + "\"func_0_a\",\"func\",\"b\",\"()V\",\"()V\",\"Minecraft\","
            + "\"Minecraft\",\"net/minecraft/client\",\"0\""));
    assertEquals(new NotchSeargeMCPMapping.MethodEntry(
                     "net/minecraft/client/Minecraft", "b", "()V", "func_0_a",
                     "func", Side.CLIENT),
                 parsed.getFirst());
  }
}
