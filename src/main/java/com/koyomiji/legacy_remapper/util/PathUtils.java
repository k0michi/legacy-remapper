package com.koyomiji.legacy_remapper.util;

import org.apache.commons.io.FilenameUtils;

public class PathUtils {
  public static String getParentPath(String path) {
    int lastSep = FilenameUtils.indexOfLastSeparator(path);

    if (lastSep == -1) {
      return null;
    }

    return path.substring(0, lastSep);
  }

  public static String appendBeforeExtension(String path, String string) {
    return FilenameUtils.removeExtension(path) + string + "." +
        FilenameUtils.getExtension(path);
  }

  public static String changeExtension(String path, String extension) {
    return FilenameUtils.removeExtension(path) + extension;
  }
}
