package com.koyomiji.legacy_remapper.test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class TestUtils {
  public static ByteArrayInputStream inputStreamFromString(String str) {
    return new ByteArrayInputStream(str.getBytes());
  }

  public static Reader readerFromString(String str) {
    return new InputStreamReader(new ByteArrayInputStream(str.getBytes()));
  }
}
