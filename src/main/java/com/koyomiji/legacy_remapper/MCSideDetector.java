package com.koyomiji.legacy_remapper;

import com.koyomiji.legacy_remapper.util.ClassUtils;
import java.io.IOException;
import java.util.jar.JarFile;

public class MCSideDetector {
  private final JarFile jarFile;

  public MCSideDetector(JarFile jarFile) { this.jarFile = jarFile; }

  private String getMainClass() throws IOException {
    String withDots =
        jarFile.getManifest().getMainAttributes().getValue("Main-Class");

    if (withDots == null) {
      return null;
    }

    return withDots.replaceAll("\\.", "/");
  }

  private boolean containsClass(String className) {
    return jarFile.getJarEntry(ClassUtils.classNameToPath(className)) != null;
  }

  public Side detect() throws IOException {
    String mainClass = getMainClass();

    if (mainClass != null) {
      if (MCSymbols.clientMains.contains(mainClass)) {
        return Side.CLIENT;
      }
      if (MCSymbols.serverMains.contains(mainClass)) {
        return Side.SERVER;
      }
    }

    for (String c : MCSymbols.clientMains) {
      if (containsClass(c)) {
        return Side.CLIENT;
      }
    }

    for (String c : MCSymbols.serverMains) {
      if (containsClass(c)) {
        return Side.SERVER;
      }
    }

    throw new RuntimeException("Failed to determine side");
  }
}
