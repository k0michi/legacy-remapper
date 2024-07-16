package com.koyomiji.legacy_remapper.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class NIOPathUtils {
  public static Path of(String first, String... more) {
    return FileSystems.getDefault().getPath(first, more);
  }
}
