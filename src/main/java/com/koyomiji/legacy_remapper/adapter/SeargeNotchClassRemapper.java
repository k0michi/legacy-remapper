package com.koyomiji.legacy_remapper.adapter;

import com.koyomiji.legacy_remapper.remapper.SeargeNotchRemapper;
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
