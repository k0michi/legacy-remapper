package com.koyomiji.legacy_remapper.adapters;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ClassInfoExtractor extends ClassVisitor {
  private int access;
  private String className;
  private String superName;
  private String[] interfaces;

  public ClassInfoExtractor() { super(Opcodes.ASM9); }

  public ClassInfoExtractor(ClassVisitor classVisitor) {
    super(Opcodes.ASM9, classVisitor);
  }

  public int getAccess() { return access; }

  public String getClassName() { return className; }

  public String getSuperName() { return superName; }

  public String[] getInterfaces() { return interfaces; }

  @Override
  public void visit(int version, int access, String name, String signature,
                    String superName, String[] interfaces) {
    this.access = access;
    this.className = name;
    this.superName = superName;
    this.interfaces = interfaces;
    super.visit(version, access, name, signature, superName, interfaces);
  }
}
