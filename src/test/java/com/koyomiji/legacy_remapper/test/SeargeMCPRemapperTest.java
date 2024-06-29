package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mapping.SeargeMCPMapping;
import com.koyomiji.legacy_remapper.remapper.SeargeMCPRemapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SeargeMCPRemapperTest {
  @Test
  void test_mapFieldName() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(new SeargeMCPMapping.MemberEntry(
                                 "field_0_a", "field", Side.BOTH, "")),
                             List.of(), List.of());
    SeargeMCPRemapper r = new SeargeMCPRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "field", r.mapFieldName("net/minecraft/test/Test", "field_0_a", "I"));
  }

  @Test
  void test_mapFieldName_1() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(new SeargeMCPMapping.MemberEntry(
                                 "field_0_a", "field", Side.CLIENT, "")),
                             List.of(), List.of());
    SeargeMCPRemapper r = new SeargeMCPRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "field", r.mapFieldName("net/minecraft/test/Test", "field_0_a", "I"));
  }

  @Test
  void test_mapMethodName() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(),
                             List.of(new SeargeMCPMapping.MemberEntry(
                                 "func_0_a", "func", Side.BOTH, "")),
                             List.of());
    SeargeMCPRemapper r = new SeargeMCPRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "func", r.mapMethodName("net/minecraft/test/Test", "func_0_a", "()V"));
  }

  @Test
  void test_mapMethodName_1() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(),
                             List.of(new SeargeMCPMapping.MemberEntry(
                                 "func_0_a", "func", Side.CLIENT, "")),
                             List.of());
    SeargeMCPRemapper r = new SeargeMCPRemapper(m, Side.CLIENT);
    Assertions.assertEquals(
        "func", r.mapMethodName("net/minecraft/test/Test", "func_0_a", "()V"));
  }
}
