package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.ClassIndex;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClassIndexTest {
  @Test
  void test_getParentClasses() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of());
    Assertions.assertEquals(Set.of("A"), m.getParentClasses("B"));
  }

  @Test
  void test_getAncestorClasses() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of());
    m.addClass("C", "B", List.of());
    Assertions.assertEquals(Set.of("A"), m.getAncestorClasses("B"));
  }

  @Test
  void test_getAncestorClasses_1() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of());
    m.addClass("C", "B", List.of());
    Assertions.assertEquals(Set.of("A", "B"), m.getAncestorClasses("C"));
  }

  @Test
  void test_getAncestorClasses_2() {
    ClassIndex m = new ClassIndex();
    m.addClass("C", "B", List.of());
    m.addClass("B", "A", List.of());
    Assertions.assertEquals(Set.of("A", "B"), m.getAncestorClasses("C"));
  }

  @Test
  void test_getAncestorClasses_3() {
    ClassIndex m = new ClassIndex();
    m.addClass("C", "B", List.of("D"));
    m.addClass("B", "A", List.of("E"));
    Assertions.assertEquals(Set.of("A", "B", "D", "E"),
                            m.getAncestorClasses("C"));
  }

  @Test
  void test_removeClass() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of("E"));
    m.addClass("C", "B", List.of("D"));
    m.removeClass("C");
    Assertions.assertFalse(m.containsClass("C"));
  }

  @Test
  void test_removeClass_1() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of("E"));
    m.addClass("C", "B", List.of("D"));
    m.removeClass("B");
    Assertions.assertEquals(Set.of("B", "D"), m.getAncestorClasses("C"));
  }

  @Test
  void test_containsClass() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of("E"));
    Assertions.assertTrue(m.containsClass("B"));
  }

  @Test
  void test_containsClass_1() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of("E"));
    Assertions.assertFalse(m.containsClass("A"));
  }

  @Test
  void test_getClass() {
    ClassIndex m = new ClassIndex();
    m.addClass("B", "A", List.of("E"));
    Assertions.assertEquals(new ClassIndex.Class("B", "A", List.of("E")),
                            m.getClass("B"));
  }

  @Test
  void test_bulk() {
    ClassIndex m = new ClassIndex();
    Set<String> tParents = new HashSet<>();

    for (int i = 0; i < 1000; i++) {
      m.addClass(Integer.toString(i), Integer.toString(i + 1), List.of());
      tParents.add(Integer.toString(i + 1));
    }

    Assertions.assertEquals(tParents, m.getAncestorClasses("0"));
  }

  @Test
  void test_readJSON() {
    ClassIndex dm = ClassIndex.readJSON(TestUtils.readerFromString(
        "{"
        + "\"net/minecraft/test/Test\": {"
        + "\"superName\": \"net/minecraft/test/Super\","
        + "\"interfaces\": [],"
        + "\"fields\": [],"
        + "\"methods\": []"
        + "}"
        + "}"));
    ClassIndex dm2 = new ClassIndex();
    dm2.addClass("net/minecraft/test/Test", "net/minecraft/test/Super",
                 List.of());
    Assertions.assertEquals(dm2, dm);
  }

  @Test
  void test_readJSON_1() {
    ClassIndex dm = ClassIndex.readJSON(TestUtils.readerFromString(
        "{"
        + "\"net/minecraft/test/Test\": {"
        + "\"superName\": \"net/minecraft/test/Super\","
        + "\"interfaces\": [],"
        + "\"fields\": ["
        + "{\"name\": \"field_0_a\", \"descriptor\": \"I\"}"
        + "],"
        + "\"methods\": ["
        + "{\"name\": \"func_0_a\", \"descriptor\": \"()V\"}"
        + "]"
        + "}"
        + "}"));
    ClassIndex dm2 = new ClassIndex();
    dm2.addClass("net/minecraft/test/Test", "net/minecraft/test/Super",
                 List.of());
    dm2.addField("net/minecraft/test/Test", "field_0_a", "I");
    dm2.addMethod("net/minecraft/test/Test", "func_0_a", "()V");
    Assertions.assertEquals(dm2, dm);
  }

  @Test
  void test_roundtrip() {
    ClassIndex dm = new ClassIndex();
    dm.addClass("net/minecraft/test/Test", "net/minecraft/test/Super",
                List.of());
    Assertions.assertEquals(dm, ClassIndex.fromJSON(dm.toJSON()));
  }

  @Test
  void test_roundtrip2() {
    ClassIndex dm = new ClassIndex();
    dm.addClass("net/minecraft/test/Test", "net/minecraft/test/Super",
                List.of());
    dm.addField("net/minecraft/test/Test", "field_0_a", "I");
    dm.addMethod("net/minecraft/test/Test", "func_0_a", "()V");
    Assertions.assertEquals(dm, ClassIndex.fromJSON(dm.toJSON()));
  }
}
