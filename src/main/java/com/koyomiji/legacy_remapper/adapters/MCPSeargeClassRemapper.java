package com.koyomiji.legacy_remapper.adapters;

import com.koyomiji.legacy_remapper.remappers.MCPSeargeRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;

public class MCPSeargeClassRemapper extends ClassRemapper {
  public MCPSeargeClassRemapper(ClassVisitor classVisitor,
                                MCPSeargeRemapper remapper) {
    super(classVisitor, remapper);

    if (remapper == null) {
      throw new NullPointerException("remapper is null");
    }
  }
}
