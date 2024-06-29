package com.koyomiji.legacy_remapper.test;

import com.koyomiji.legacy_remapper.Side;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SideTest {
  @Test
  void test_includes() {
    Assertions.assertTrue(Side.BOTH.includes(Side.CLIENT));
    Assertions.assertTrue(Side.BOTH.includes(Side.SERVER));
    Assertions.assertTrue(Side.BOTH.includes(Side.BOTH));
    Assertions.assertTrue(Side.CLIENT.includes(Side.CLIENT));
    Assertions.assertFalse(Side.CLIENT.includes(Side.SERVER));
    Assertions.assertFalse(Side.CLIENT.includes(Side.BOTH));
    Assertions.assertTrue(Side.SERVER.includes(Side.SERVER));
    Assertions.assertFalse(Side.SERVER.includes(Side.CLIENT));
    Assertions.assertFalse(Side.SERVER.includes(Side.BOTH));
  }
}
