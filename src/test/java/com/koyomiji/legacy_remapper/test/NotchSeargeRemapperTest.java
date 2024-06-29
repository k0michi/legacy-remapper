package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.ClassIndex;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mappings.NotchSeargeMapping;
import com.koyomiji.legacy_remapper.remappers.NotchSeargeRemapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotchSeargeRemapperTest {
  @Test
  void test_map() {
    NotchSeargeMapping m =
        new NotchSeargeMapping(List.of(new NotchSeargeMapping.ClassEntry(
                                   "a", "net/minecraft/test/Test", Side.BOTH)),
                               List.of(), List.of());
    ClassIndex dm = new ClassIndex();
    dm.addClass("a", "java/lang/Object", List.of());
    NotchSeargeRemapper r = new NotchSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals("net/minecraft/test/Test", r.map("a"));
  }

  @Test
  void test_mapMethodName() {
    NotchSeargeMapping m =
        new NotchSeargeMapping(List.of(new NotchSeargeMapping.ClassEntry(
                                   "a", "net/minecraft/test/Test", Side.BOTH)),
                               List.of(),
                               List.of(new NotchSeargeMapping.MethodEntry(
                                   "a", "b", "()V", "func_0_a", Side.BOTH)));
    ClassIndex dm = new ClassIndex();
    dm.addClass("a", "java/lang/Object", List.of());
    NotchSeargeRemapper r = new NotchSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals("func_0_a", r.mapMethodName("a", "b", "()V"));
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
    ClassIndex dm = new ClassIndex();
    dm.addClass("a", "java/lang/Object", List.of());
    dm.addClass("b", "a", List.of());
    NotchSeargeRemapper r = new NotchSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals("func_0_a", r.mapMethodName("b", "b", "()V"));
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
    ClassIndex dm = new ClassIndex();
    dm.addClass("a", "java/lang/Object", List.of());
    dm.addClass("b", "a", List.of());
    NotchSeargeRemapper r = new NotchSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals("field_0_a", r.mapFieldName("a", "c", "I"));
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
    ClassIndex dm = new ClassIndex();
    dm.addClass("a", "java/lang/Object", List.of());
    dm.addClass("b", "a", List.of());
    NotchSeargeRemapper r = new NotchSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals("field_0_a", r.mapFieldName("b", "c", "I"));
  }
}
