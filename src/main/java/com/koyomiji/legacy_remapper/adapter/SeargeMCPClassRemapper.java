package com.koyomiji.legacy_remapper.adapter;

import com.koyomiji.legacy_remapper.remapper.SeargeMCPRemapper;
import java.util.Optional;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;

public class SeargeMCPClassRemapper extends ClassRemapper {
  private final SeargeMCPRemapper remapper;
  private String className;
  private boolean annotateDescription = true;
  private String descriptionClassName = "Description";

  public SeargeMCPClassRemapper(ClassVisitor classVisitor,
                                SeargeMCPRemapper remapper) {
    super(classVisitor, remapper);
    this.remapper = remapper;

    if (remapper == null) {
      throw new NullPointerException("remapper is null");
    }
  }

  public boolean doesAnnotateDescription() { return annotateDescription; }

  public void setAnnotateDescription(boolean annotateDescription) {
    this.annotateDescription = annotateDescription;
  }

  public String getDescriptionClassName() { return descriptionClassName; }

  public void setDescriptionClassName(String descriptionClassName) {
    this.descriptionClassName = descriptionClassName;
  }

  @Override
  public void visit(int version, int access, String name, String signature,
                    String superName, String[] interfaces) {
    this.className = name;
    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public FieldVisitor visitField(int access, String name, String descriptor,
                                 String signature, Object value) {
    Optional<String> description =
        remapper.getFieldDescription(className, name, descriptor);

    return new FieldVisitor(
        api, super.visitField(access, name, descriptor, signature, value)) {
      @Override
      public void visitEnd() {
        if (annotateDescription && description.isPresent()) {
          AnnotationVisitor av = super.visitAnnotation(
              Type.getObjectType(descriptionClassName).getDescriptor(), false);
          av.visit("value", description.get());
          av.visitEnd();
        }

        super.visitEnd();
      }
    };
  }

  @Override
  public MethodVisitor visitMethod(int access, String methodName,
                                   String methodDescriptor, String signature,
                                   String[] exceptions) {
    Optional<String> description =
        remapper.getMethodDescription(className, methodName, methodDescriptor);
    return new MethodVisitor(api, super.visitMethod(access, methodName,
                                                    methodDescriptor, signature,
                                                    exceptions)) {
      @Override
      public AnnotationVisitor visitAnnotation(String descriptor,
                                               boolean visible) {
        return super.visitAnnotation(descriptor, visible);
      }

      @Override
      public void visitCode() {
        if (annotateDescription && description.isPresent()) {
          AnnotationVisitor av = super.visitAnnotation(
              Type.getObjectType(descriptionClassName).getDescriptor(), false);
          av.visit("value", description.get());
          av.visitEnd();
        }

        super.visitCode();
      }

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
