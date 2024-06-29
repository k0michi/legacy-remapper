package com.koyomiji.legacy_remapper.adapters;

import com.koyomiji.legacy_remapper.*;
import com.koyomiji.legacy_remapper.mappings.SeargeExceptor;
import com.koyomiji.legacy_remapper.remappers.SeargeExceptorAccessor;
import com.koyomiji.legacy_remapper.util.TypeUtils;
import java.util.*;
import org.objectweb.asm.*;

/*
 * See:
 * - mcp.mcinjector.MCInjectorImpl
 * - de.oceanlabs.mcp.mcinjector.ApplyMapClassAdapter
 * - de.oceanlabs.mcp.mcinjector.GenerateMapClassAdapter
 * - de.oceanlabs.mcp.mcinjector.JsonAttributeClassAdaptor
 */
public class SeargeExceptorApplier extends ClassVisitor {
  private final SeargeExceptorAccessor accessor;
  private final SeargeExceptorGlobal global;

  private String className;
  private boolean visitedOuterClass;
  private Set<String> visitedInnerClasses;

  public SeargeExceptorApplier(ClassVisitor classVisitor,
                               SeargeExceptorAccessor accessor,
                               SeargeExceptorGlobal global) {
    super(Opcodes.ASM9, classVisitor);
    this.accessor = accessor;
    this.global = global;

    if (accessor == null) {
      throw new NullPointerException("accessor is null");
    }

    if (global == null) {
      throw new NullPointerException("global is null");
    }
  }

  @Override
  public void visit(int version, int access, String name, String signature,
                    String superName, String[] interfaces) {
    this.className = name;
    this.visitedOuterClass = false;
    this.visitedInnerClasses = new HashSet<>();
    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public void visitInnerClass(String name, String outerName, String innerName,
                              int access) {
    visitedInnerClasses.add(name);
    super.visitInnerClass(name, outerName, innerName, access);
  }

  @Override
  public void visitOuterClass(String owner, String name, String descriptor) {
    visitedOuterClass = true;
    super.visitOuterClass(owner, name, descriptor);
  }

  @Override
  public MethodVisitor visitMethod(int access, String methodName,
                                   String methodDescriptor, String signature,
                                   String[] exceptions) {
    MethodIdentifier identifier =
        new MethodIdentifier(className, methodName, methodDescriptor);

    if (accessor.containsMethod(identifier)) {
      Set<String> newExceptions = new HashSet<>();

      if (exceptions != null) {
        newExceptions.addAll(Arrays.asList(exceptions));
      }

      newExceptions.addAll(accessor.getExceptions(identifier));
      exceptions = newExceptions.toArray(new String[0]);

      if (accessor.containsMethodAccess(identifier)) {
        access =
            Access.changeAccess(access, accessor.getMethodAccess(identifier));
      }
    }

    boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
    int paramSizedBegin = TypeUtils.paramSizedBegin(methodDescriptor, isStatic);
    int paramSizedEnd = TypeUtils.paramSizedEnd(methodDescriptor, isStatic);

    return new MethodVisitor(api, super.visitMethod(access, methodName,
                                                    methodDescriptor, signature,
                                                    exceptions)) {
      private boolean visitedLocalVariable;
      private Label begin;
      private Label end;
      private int localVariableUnsizedIndex;
      private final Map<Integer, Integer> variableMap = new HashMap<>();

      @Override
      public void visitCode() {
        visitedLocalVariable = false;
        localVariableUnsizedIndex = 0;
        super.visitCode();
        super.visitLabel(begin = new Label());
      }

      @Override
      public void visitMaxs(int maxStack, int maxLocals) {
        super.visitLabel(end = new Label());

        if (!visitedLocalVariable) {
          int sizedIndex = 0;
          int unsizedBase = 0;

          if (!isStatic) {
            super.visitLocalVariable("this", "L" + className + ";", null, begin,
                                     end, sizedIndex);
            sizedIndex++;
            unsizedBase++;
          }

          Type[] paramTypes = Type.getArgumentTypes(methodDescriptor);

          for (int i = 0; i < paramTypes.length; i++) {
            super.visitLocalVariable(getParamName(sizedIndex, unsizedBase + i),
                                     paramTypes[i].getDescriptor(), null, begin,
                                     end, sizedIndex);
            sizedIndex += paramTypes[i].getSize();
          }
        }

        super.visitMaxs(maxStack, maxLocals);
      }

      @Override
      public void visitLocalVariable(String name, String descriptor,
                                     String signature, Label start, Label end,
                                     int index) {
        if (name.equals("☃")) {
          if (index >= paramSizedBegin && index < paramSizedEnd) {
            name = getParamName(index, localVariableUnsizedIndex);
          } else {
            if (global.getLocalVariableNameStyle() ==
                LocalVariableNameStyle.STRIP) {
              return;
            } else {
              name = getLocalVariableName(index, localVariableUnsizedIndex);
            }
          }
        }

        super.visitLocalVariable(name, descriptor, signature, start, end,
                                 index);
        visitedLocalVariable = true;
        localVariableUnsizedIndex++;
      }

      private String getParamName(int sizedIndex, int unsizedIndex) {
        if (global.getParamNameStyle() == ParamNameStyle.PAR_INDEX) {
          return String.format("par%d", sizedIndex);
        }

        String mapped = accessor.getParamName(
            identifier, isStatic ? unsizedIndex : unsizedIndex - 1);

        if (mapped != null) {
          return mapped;
        }

        String funcID;

        if (methodName.matches("func_\\d+_.+")) {
          funcID = methodName.substring(5, methodName.lastIndexOf('_'));
        } else if (methodName.equals("<init>")) {
          funcID = String.format("i%d", global.getConstructorIndex());
          global.incrementConstructorIndex();
        } else {
          funcID = methodName;
        }

        return String.format("p_%s_%d_", funcID, sizedIndex);
      }

      private String getLocalVariableName(int sizedIndex, int unsizedIndex) {
        if (global.getLocalVariableNameStyle() ==
            LocalVariableNameStyle.LVT_INDEX_SUB) {
          int sub = variableMap.getOrDefault(sizedIndex, 1);
          variableMap.put(sizedIndex, sub + 1);
          return String.format("lvt_%d_%d_", sizedIndex, sub);
        } else if (global.getLocalVariableNameStyle() ==
                   LocalVariableNameStyle.VAR_INDEX) {
          return String.format("var%d", sizedIndex);
        }

        throw new RuntimeException("Unsupported name style");
      }
    };
  }

  @Override
  public void visitEnd() {
    if (accessor.containsClass(className)) {
      SeargeExceptor.ClassEntry c = accessor.getClass(className);
      SeargeExceptor.ClassEntry.EnclosingMethod enclosing = c.enclosingMethod;

      if (!visitedOuterClass && enclosing != null && enclosing.name != null &&
          enclosing.desc != null) {
        super.visitOuterClass(enclosing.owner, enclosing.name, enclosing.desc);
      }

      for (SeargeExceptor.ClassEntry.InnerClass ic : c.innerClasses) {
        if (!visitedInnerClasses.contains(ic.innerClass)) {
          super.visitInnerClass(ic.innerClass, ic.outerClass.orElse(null),
                                ic.innerName.orElse(null), ic.access);
        }
      }
    }

    super.visitEnd();
  }

  public SeargeExceptorAccessor getAccessor() { return accessor; }

  public SeargeExceptorGlobal getGlobal() { return global; }
}
