package com.koyomiji.legacy_remapper.adapters;

import com.koyomiji.legacy_remapper.remappers.NotchSeargeRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;

public class NotchSeargeClassRemaper extends ClassRemapper {
  private final NotchSeargeRemapper remapper;

  public NotchSeargeClassRemaper(ClassVisitor classVisitor,
                                 NotchSeargeRemapper remapper) {
    super(classVisitor, remapper);
    this.remapper = remapper;

    if (remapper == null) {
      throw new NullPointerException("remapper is null");
    }
  }

  @Override
  public void visitSource(String source, String debug) {
    super.visitSource(null, debug);
  }
}
