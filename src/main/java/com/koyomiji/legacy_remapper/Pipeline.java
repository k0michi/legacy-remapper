package com.koyomiji.legacy_remapper;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class Pipeline extends ClassVisitor {
  Pipeline(ClassVisitor classVisitor) { super(Opcodes.ASM9, classVisitor); }
}
