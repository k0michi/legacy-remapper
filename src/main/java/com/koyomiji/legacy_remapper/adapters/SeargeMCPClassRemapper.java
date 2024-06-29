package com.koyomiji.legacy_remapper.adapters;

import com.koyomiji.legacy_remapper.remappers.SeargeMCPRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.ClassRemapper;

public class SeargeMCPClassRemapper extends ClassRemapper {
  private final SeargeMCPRemapper remapper;
  private String className;

  public SeargeMCPClassRemapper(ClassVisitor classVisitor,
                                SeargeMCPRemapper remapper) {
    super(classVisitor, remapper);
    this.remapper = remapper;

    if (remapper == null) {
      throw new NullPointerException("remapper is null");
    }
  }

  @Override
  public void visit(int version, int access, String name, String signature,
                    String superName, String[] interfaces) {
    this.className = name;
    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public MethodVisitor visitMethod(int access, String methodName,
                                   String methodDescriptor, String signature,
                                   String[] exceptions) {
    return new MethodVisitor(api, super.visitMethod(access, methodName,
                                                    methodDescriptor, signature,
                                                    exceptions)) {
      @Override
      public void visitLocalVariable(String name, String descriptor,
                                     String signature, Label start, Label end,
                                     int index) {
        super.visitLocalVariable(remapper.mapLocalVariableName(
                                     className, methodName, methodDescriptor,
                                     name, descriptor, index),
                                 descriptor, signature, start, end, index);
      }
    };
  }
}
