package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mapping.NotchSeargeMapping;
import com.koyomiji.legacy_remapper.remapper.SeargeNotchRemapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SeargeNotchRemapperTest {
  @Test
  void test_map() {
    NotchSeargeMapping m =
        new NotchSeargeMapping(List.of(new NotchSeargeMapping.ClassEntry(
                                   "a", "net/minecraft/test/Test", Side.BOTH)),
                               List.of(), List.of());
    SeargeNotchRemapper r = new SeargeNotchRemapper(m, Side.CLIENT);
    Assertions.assertEquals("a", r.map("net/minecraft/test/Test"));
  }

  @Test
  void test_mapMethodName() {
    NotchSeargeMapping m =
        new NotchSeargeMapping(List.of(new NotchSeargeMapping.ClassEntry(
                                   "a", "net/minecraft/test/Test", Side.BOTH)),
                               List.of(),
                               List.of(new NotchSeargeMapping.MethodEntry(
                                   "a", "b", "()V", "func_0_a", Side.BOTH)));
    SeargeNotchRemapper r = new SeargeNotchRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "b", r.mapMethodName("net/minecraft/test/Test", "func_0_a", "()V"));
  }

  @Test
  void test_mapMethodName_1() {
    NotchSeargeMapping m = new NotchSeargeMapping(
        List.of(new NotchSeargeMapping.ClassEntry(
                    "a", "net/minecraft/test/Test", Side.BOTH),
                new NotchSeargeMapping.ClassEntry(
                    "b", "net/minecraft/test/Test2", Side.BOTH)),
        List.of(),
        List.of(new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func_0_a",
                                                   Side.BOTH)));
    SeargeNotchRemapper r = new SeargeNotchRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "b", r.mapMethodName("net/minecraft/test/Test2", "func_0_a", "()V"));
  }

  @Test
  void test_mapFieldName() {
    NotchSeargeMapping m = new NotchSeargeMapping(
        List.of(new NotchSeargeMapping.ClassEntry(
                    "a", "net/minecraft/test/Test", Side.BOTH),
                new NotchSeargeMapping.ClassEntry(
                    "b", "net/minecraft/test/Test2", Side.BOTH)),
        List.of(new NotchSeargeMapping.FieldEntry("a", "c", "field_0_a",
                                                  Side.BOTH)),
        List.of(new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func_0_a",
                                                   Side.BOTH)));
    SeargeNotchRemapper r = new SeargeNotchRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "c", r.mapFieldName("net/minecraft/test/Test", "field_0_a", "I"));
  }

  @Test
  void test_mapFieldName_1() {
    NotchSeargeMapping m = new NotchSeargeMapping(
        List.of(new NotchSeargeMapping.ClassEntry(
                    "a", "net/minecraft/test/Test", Side.BOTH),
                new NotchSeargeMapping.ClassEntry(
                    "b", "net/minecraft/test/Test2", Side.BOTH)),
        List.of(new NotchSeargeMapping.FieldEntry("a", "c", "field_0_a",
                                                  Side.BOTH)),
        List.of(new NotchSeargeMapping.MethodEntry("a", "b", "()V", "func_0_a",
                                                   Side.BOTH)));
    SeargeNotchRemapper r = new SeargeNotchRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "c", r.mapFieldName("net/minecraft/test/Test2", "field_0_a", "I"));
  }
}
