package com.koyomiji.legacy_remapper.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.koyomiji.legacy_remapper.Access;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.SidedMethodIdentifier;
import com.koyomiji.legacy_remapper.mapping.SeargeExceptor;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class SeargeExceptorTest {
  @Test
  void test_readExceptorJSON() throws IOException {
    var parsed = SeargeExceptor.readExceptorJSON(TestUtils.readerFromString(
        "{\n"
        + "  \"net/minecraft/test/Test\": {\n"
        + "    \"innerClasses\": [\n"
        + "      {\n"
        + "        \"access\": \"1024\", \n"
        + "        \"inner_class\": \"net/minecraft/test/Test$Inner\", \n"
        + "        \"inner_name\": \"Inner\", \n"
        + "        \"outer_class\": \"net/minecraft/test/Test\", \n"
        + "        \"start\": \"1\"\n"
        + "      }\n"
        + "    ]\n"
        + "  }\n"
        + "}"));
    assertEquals(new SeargeExceptor.ClassEntry(
                     null, List.of(new SeargeExceptor.ClassEntry.InnerClass(
                               0x1024, "net/minecraft/test/Test$Inner",
                               Optional.of("Inner"),
                               Optional.of("net/minecraft/test/Test")))),
                 parsed.get("net/minecraft/test/Test"));
  }

  @Test
  void test_readExceptorJSON_1() throws IOException {
    var parsed = SeargeExceptor.readExceptorJSON(TestUtils.readerFromString(
        "{\n"
        + "  \"net/minecraft/test/Test$1\": {\n"
        + "    \"enclosingMethod\": {\n"
        + "      \"desc\": \"()V\", \n"
        + "      \"name\": \"func_0_a\", \n"
        + "      \"owner\": \"net/minecraft/test/Test\"\n"
        + "    }\n"
        + "  }\n"
        + "}"));
    assertEquals(new SeargeExceptor.ClassEntry(
                     new SeargeExceptor.ClassEntry.EnclosingMethod(
                         "()V", "func_0_a", "net/minecraft/test/Test"),
                     List.of()),
                 parsed.get("net/minecraft/test/Test$1"));
  }

  @Test
  void test_readEXC() throws IOException {
    var parsed = SeargeExceptor.readEXC(TestUtils.readerFromString(
        "#Comment\n"
        + "max_constructor_index=10000\n"
        + "net/minecraft/test/Test.<init>(II)V=|p_i0_1_,p_i0_2_"));
    assertEquals(
        List.of("p_i0_1_", "p_i0_2_"),
        parsed
            .get(new SidedMethodIdentifier("net/minecraft/test/Test", "<init>",
                                           "(II)V", Side.BOTH))
            .params);
    assertEquals(List.of(), parsed
                                .get(new SidedMethodIdentifier(
                                    "net/minecraft/test/Test", "<init>",
                                    "(II)V", Side.BOTH))
                                .exceptions);
    assertEquals(new SeargeExceptor.MethodEntry(List.of(),
                                                List.of("p_i0_1_", "p_i0_2_")),
                 parsed.get(new SidedMethodIdentifier(
                     "net/minecraft/test/Test", "<init>", "(II)V", Side.BOTH)));
  }

  @Test
  void test_readEXC_1() throws IOException {
    var parsed = SeargeExceptor.readEXC(TestUtils.readerFromString(
        "#Comment\n"
        + "max_constructor_index=10000\n"
        + "net/minecraft/test/Test.func(II)V=java/lang/Exception|"));
    assertEquals(new SeargeExceptor.MethodEntry(List.of("java/lang/Exception"),
                                                List.of()),
                 parsed.get(new SidedMethodIdentifier(
                     "net/minecraft/test/Test", "func", "(II)V", Side.BOTH)));
  }

  @Test
  void test_readEXC_2() throws IOException {
    var parsed = SeargeExceptor.readEXC(TestUtils.readerFromString(
        "#Comment\n"
        + "max_constructor_index=10000\n"
        + "net/minecraft/test/Test.func(II)V-Access=PUBLIC"));
    assertEquals(
        new SeargeExceptor.MethodEntry(List.of(), List.of(), Access.PUBLIC),
        parsed.get(new SidedMethodIdentifier("net/minecraft/test/Test", "func",
                                             "(II)V", Side.BOTH)));
  }

  @Test
  void test_merge() throws IOException {
    var parsedC = SeargeExceptor.readEXC(
        TestUtils.readerFromString(
            "net/minecraft/test/Test.func(II)V=java/lang/Exception|"),
        Side.CLIENT);
    var parsedS = SeargeExceptor.readEXC(
        TestUtils.readerFromString(
            "net/minecraft/test/Test.func(II)V=java/lang/Exception|"),
        Side.SERVER);
    var merged = SeargeExceptor.merge(List.of(parsedC, parsedS));
    assertEquals(new SeargeExceptor.MethodEntry(List.of("java/lang/Exception"),
                                                List.of()),
                 merged.get(new SidedMethodIdentifier(
                     "net/minecraft/test/Test", "func", "(II)V", Side.BOTH)));
  }

  @Test
  void test_merge_1() throws IOException {
    var parsedC = SeargeExceptor.readEXC(
        TestUtils.readerFromString(
            "net/minecraft/test/Test.func(II)V=|p_0_1_,p_0_2_"),
        Side.CLIENT);
    var parsedS = SeargeExceptor.readEXC(
        TestUtils.readerFromString(
            "net/minecraft/test/Test.func(II)V=|p_1_1_,p_1_2_"),
        Side.SERVER);
    var merged = SeargeExceptor.merge(List.of(parsedC, parsedS));
    assertEquals(
        new SeargeExceptor.MethodEntry(List.of(), List.of("p_0_1_", "p_0_2_")),
        merged.get(new SidedMethodIdentifier("net/minecraft/test/Test", "func",
                                             "(II)V", Side.CLIENT)));
    assertEquals(
        new SeargeExceptor.MethodEntry(List.of(), List.of("p_1_1_", "p_1_2_")),
        merged.get(new SidedMethodIdentifier("net/minecraft/test/Test", "func",
                                             "(II)V", Side.SERVER)));
  }

  @Test
  void test_merge_2() {
    assertThrows(RuntimeException.class, () -> {
      var parsedC = SeargeExceptor.readEXC(
          TestUtils.readerFromString(
              "net/minecraft/test/Test.func(II)V=|p_0_1_,p_0_2_"),
          Side.BOTH);
      var parsedS = SeargeExceptor.readEXC(
          TestUtils.readerFromString(
              "net/minecraft/test/Test.func(II)V=|p_1_1_,p_1_2_"),
          Side.SERVER);
      var merged = SeargeExceptor.merge(List.of(parsedC, parsedS));
    });
  }

  @Test
  void test_merge_3() {
    assertThrows(RuntimeException.class, () -> {
      var parsedC = SeargeExceptor.readEXC(
          TestUtils.readerFromString(
              "net/minecraft/test/Test.func(II)V=|p_0_1_,p_0_2_"),
          Side.CLIENT);
      var parsedS = SeargeExceptor.readEXC(
          TestUtils.readerFromString(
              "net/minecraft/test/Test.func(II)V=|p_1_1_,p_1_2_"),
          Side.BOTH);
      var merged = SeargeExceptor.merge(List.of(parsedC, parsedS));
    });
  }

  @Test
  void test_merge_4() {
    assertThrows(RuntimeException.class, () -> {
      var parsedC = SeargeExceptor.readEXC(
          TestUtils.readerFromString(
              "net/minecraft/test/Test.func(II)V=|p_0_1_,p_0_2_"),
          Side.BOTH);
      var parsedS = SeargeExceptor.readEXC(
          TestUtils.readerFromString(
              "net/minecraft/test/Test.func(II)V=|p_1_1_,p_1_2_"),
          Side.BOTH);
      var merged = SeargeExceptor.merge(List.of(parsedC, parsedS));
    });
  }
}
