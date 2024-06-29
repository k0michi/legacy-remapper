package com.koyomiji.legacy_remapper.adapter;

import com.koyomiji.legacy_remapper.ClassIndex;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassIndexVisitor extends ClassVisitor {
  private final ClassIndex classIndex;
  private String className;

  public ClassIndexVisitor(ClassIndex classIndex) {
    super(Opcodes.ASM9);
    this.classIndex = classIndex;
  }

  public ClassIndexVisitor(ClassVisitor classVisitor, ClassIndex classIndex) {
    super(Opcodes.ASM9, classVisitor);
    this.classIndex = classIndex;
  }

  @Override
  public void visit(int version, int access, String name, String signature,
                    String superName, String[] interfaces) {
    classIndex.addClass(name, superName, interfaces);
    this.className = name;
    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public FieldVisitor visitField(int access, String name, String descriptor,
                                 String signature, Object value) {
    classIndex.addField(className, name, descriptor);
    return super.visitField(access, name, descriptor, signature, value);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor,
                                   String signature, String[] exceptions) {
    classIndex.addMethod(className, name, descriptor);
    return super.visitMethod(access, name, descriptor, signature, exceptions);
  }
}
