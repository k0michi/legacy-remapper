package com.koyomiji.legacy_remapper.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.mappings.SeargeMCPMapping;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class SeargeMCPMappingTest {
  @Test
  void test_readMCP2FieldMapping() throws IOException {
    var parsed = SeargeMCPMapping.readMCP2FieldCSV(TestUtils.readerFromString(
        "NULL,NULL,NULL,NULL,NULL,NULL\n"
        + "NULL,NULL,NULL,NULL,NULL,NULL\n"
        + "Class,Field,Name,Class,Field,Name,Name,Notes\n"
        + "Test,*,field_0_a,*,*,*,field,\"Test field\""));
    assertEquals(new SeargeMCPMapping.MemberEntry("field_0_a", "field",
                                                  Side.CLIENT, "Test field"),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP2MethodMapping() throws IOException {
    var parsed = SeargeMCPMapping.readMCP2MethodCSV(TestUtils.readerFromString(
        "NULL,NULL,NULL,NULL,NULL,NULL\n"
        + "NULL,NULL,NULL,NULL,NULL,NULL\n"
        + "NULL,NULL,NULL,NULL,NULL,NULL\n"
        + "class (for reference only),Reference,class (for reference "
        + "only),Reference,Name,Notes\n"
        + "Test,func_0_a,*,*,func,\"Test func\""));
    assertEquals(new SeargeMCPMapping.MemberEntry("func_0_a", "func",
                                                  Side.CLIENT, "Test func"),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP6MemberMapping() throws IOException {
    var parsed = SeargeMCPMapping.readMCP6MemberCSV(
        TestUtils.readerFromString("searge,name,side,desc\n"
                                   + "func_0_a,func,0,Test func"));
    assertEquals(new SeargeMCPMapping.MemberEntry("func_0_a", "func",
                                                  Side.CLIENT, "Test func"),
                 parsed.getFirst());
  }

  @Test
  void test_readMCP6ParamMapping() throws IOException {
    var parsed = SeargeMCPMapping.readMCP6ParamCSV(
        TestUtils.readerFromString("param,name,side\n"
                                   + "p_0_1_,param,0"));
    assertEquals(
        new SeargeMCPMapping.ParamEntry("p_0_1_", "param", Side.CLIENT),
        parsed.getFirst());
  }
}
