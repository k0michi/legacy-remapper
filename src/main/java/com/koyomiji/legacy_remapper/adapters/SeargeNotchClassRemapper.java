package com.koyomiji.legacy_remapper.adapters;

import com.koyomiji.legacy_remapper.remappers.SeargeNotchRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;

public class SeargeNotchClassRemapper extends ClassRemapper {
  public SeargeNotchClassRemapper(ClassVisitor classVisitor,
                                  SeargeNotchRemapper remapper) {
    super(classVisitor, remapper);

    if (remapper == null) {
      throw new NullPointerException("remapper is null");
    }
  }
}
