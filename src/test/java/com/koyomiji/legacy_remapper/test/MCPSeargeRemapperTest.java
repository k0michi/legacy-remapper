package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.*;
import com.koyomiji.legacy_remapper.mappings.SeargeMCPMapping;
import com.koyomiji.legacy_remapper.remappers.MCPSeargeRemapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MCPSeargeRemapperTest {
  @Test
  void test_mapFieldName() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(new SeargeMCPMapping.MemberEntry(
                                 "field_0_a", "field", Side.BOTH, "")),
                             List.of(), List.of());
    ClassIndex dm = new ClassIndex();
    dm.addClass("net/minecraft/test/Test", "java/lang/Object", List.of());
    dm.addField("net/minecraft/test/Test", "field_0_a", "I");
    MCPSeargeRemapper r = new MCPSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals(
        "field_0_a", r.mapFieldName("net/minecraft/test/Test", "field", "I"));
  }

  @Test
  void test_mapFieldName_1() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(new SeargeMCPMapping.MemberEntry(
                                 "field_0_a", "field", Side.BOTH, "")),
                             List.of(new SeargeMCPMapping.MemberEntry(
                                 "func_0_a", "func", Side.BOTH, "")),
                             List.of());
    ClassIndex dm = new ClassIndex();
    dm.addClass("net/minecraft/test/Test", "java/lang/Object", List.of());
    dm.addClass("net/minecraft/test/Test2", "net/minecraft/test/Test",
                List.of());
    dm.addField("net/minecraft/test/Test", "field_0_a", "I");
    dm.addMethod("net/minecraft/test/Test", "func_0_a", "()V");
    MCPSeargeRemapper r = new MCPSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals(
        "field_0_a",
        r.mapFieldName("net/minecraft/test/Test2", "field", "()V"));
  }

  @Test
  void test_mapMethodName() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(new SeargeMCPMapping.MemberEntry(
                                 "field_0_a", "field", Side.BOTH, "")),
                             List.of(new SeargeMCPMapping.MemberEntry(
                                 "func_0_a", "func", Side.BOTH, "")),
                             List.of());
    ClassIndex dm = new ClassIndex();
    dm.addClass("net/minecraft/test/Test", "java/lang/Object", List.of());
    dm.addField("net/minecraft/test/Test", "field_0_a", "I");
    dm.addMethod("net/minecraft/test/Test", "func_0_a", "()V");
    MCPSeargeRemapper r = new MCPSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals(
        "func_0_a", r.mapMethodName("net/minecraft/test/Test", "func", "()V"));
  }

  @Test
  void test_mapMethodName_1() {
    SeargeMCPMapping m =
        new SeargeMCPMapping(List.of(new SeargeMCPMapping.MemberEntry(
                                 "field_0_a", "field", Side.BOTH, "")),
                             List.of(new SeargeMCPMapping.MemberEntry(
                                 "func_0_a", "func", Side.BOTH, "")),
                             List.of());
    ClassIndex dm = new ClassIndex();
    dm.addClass("net/minecraft/test/Test", "java/lang/Object", List.of());
    dm.addClass("net/minecraft/test/Test2", "net/minecraft/test/Test",
                List.of());
    dm.addField("net/minecraft/test/Test", "field_0_a", "I");
    dm.addMethod("net/minecraft/test/Test", "func_0_a", "()V");
    MCPSeargeRemapper r = new MCPSeargeRemapper(m, dm, Side.CLIENT);
    Assertions.assertEquals(
        "func_0_a", r.mapMethodName("net/minecraft/test/Test2", "func", "()V"));
  }
}
