package com.koyomiji.legacy_remapper.mapping;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.koyomiji.legacy_remapper.Access;
import com.koyomiji.legacy_remapper.MethodIdentifier;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.SidedMethodIdentifier;
import com.koyomiji.legacy_remapper.util.MapUtils;
import com.koyomiji.legacy_remapper.util.StringUtils;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

public class SeargeExceptor {
  public final Map<SidedMethodIdentifier, MethodEntry> methods;
  public final Map<String, ClassEntry> classes;

  public SeargeExceptor() {
    this.methods = Map.of();
    this.classes = Map.of();
  }

  public SeargeExceptor(Map<SidedMethodIdentifier, MethodEntry> methods,
                        Map<String, ClassEntry> classes) {
    this.methods = methods;
    this.classes = classes;
  }

  public static Map<SidedMethodIdentifier, MethodEntry>
  merge(Iterable<Map<SidedMethodIdentifier, MethodEntry>> methods) {
    Map<SidedMethodIdentifier, MethodEntry> merged = Map.of();

    for (Map<SidedMethodIdentifier, MethodEntry> m : methods) {
      if (merged.isEmpty()) {
        merged = m;
        continue;
      }

      // Resolve sides, throws if exceptors are incompatible
      for (Map.Entry<SidedMethodIdentifier, MethodEntry> e : m.entrySet()) {
        SidedMethodIdentifier key = e.getKey();
        MethodEntry value = e.getValue();

        if (key.side == Side.BOTH) {
          SidedMethodIdentifier cKey = new SidedMethodIdentifier(
              key.className, key.name, key.desc, Side.CLIENT);
          SidedMethodIdentifier sKey = new SidedMethodIdentifier(
              key.className, key.name, key.desc, Side.CLIENT);

          if (merged.containsKey(cKey)) {
            if (!merged.get(cKey).equals(value)) {
              throw new RuntimeException("Incompatible exceptors");
            } else {
              merged.remove(cKey);
            }
          }

          if (merged.containsKey(sKey)) {
            if (!merged.get(sKey).equals(value)) {
              throw new RuntimeException("Incompatible exceptors");
            } else {
              merged.remove(sKey);
            }
          }

          if (merged.containsKey(key)) {
            if (!merged.get(key).equals(value)) {
              throw new RuntimeException("Incompatible exceptors");
            }
          } else {
            merged.put(key, value);
          }
        } else {
          SidedMethodIdentifier bothKey = new SidedMethodIdentifier(
              key.className, key.name, key.desc, Side.BOTH);
          SidedMethodIdentifier oppositeKey = new SidedMethodIdentifier(
              key.className, key.name, key.desc, key.side.opposite());

          if (merged.containsKey(bothKey)) {
            if (!merged.get(bothKey).equals(value)) {
              throw new RuntimeException("Incompatible exceptors");
            }
          } else {
            if (merged.containsKey(key)) {
              if (!merged.get(bothKey).equals(value)) {
                throw new RuntimeException("Incompatible exceptors");
              }
            }

            if (merged.containsKey(oppositeKey) &&
                merged.get(oppositeKey).equals(value)) {
              merged.remove(oppositeKey);
              merged.put(bothKey, value);
            } else {
              merged.put(key, value);
            }
          }
        }
      }
    }

    return merged;
  }

  private static MethodIdentifier parseMethodModifier(String key) {
    String[] keyParts = key.split("\\.");
    String className = keyParts[0];
    String[] methodParts = StringUtils.splitBeforeFirst(keyParts[1], "(");
    String methodName = methodParts[0];
    String methodDesc = methodParts[1];
    return new MethodIdentifier(className, methodName, methodDesc);
  }

  public static Map<SidedMethodIdentifier, MethodEntry> readEXC(Reader reader)
      throws IOException {
    return readEXC(reader, Side.BOTH);
  }

  public static Map<SidedMethodIdentifier, MethodEntry> readEXC(Reader reader,
                                                                Side side)
      throws IOException {
    Properties properties = new Properties();
    properties.load(reader);
    Map<SidedMethodIdentifier, MethodEntry> entries = new HashMap<>();
    Map<MethodIdentifier, Access> accessMap = new HashMap<>();

    for (Map.Entry<Object, Object> e : properties.entrySet()) {
      String key = (String)e.getKey();
      String value = (String)e.getValue();

      if (key.endsWith("-Access")) {
        MethodIdentifier m = parseMethodModifier(
            key.substring(0, key.length() - "-Access".length()));
        entries.put(
            new SidedMethodIdentifier(m, side),
            new MethodEntry(List.of(), List.of(), Access.valueOf(value)));
      }
    }

    for (Map.Entry<Object, Object> e : properties.entrySet()) {
      String key = (String)e.getKey();
      String value = (String)e.getValue();

      if (key.equals("max_constructor_index")) {
        continue;
      }

      if (!key.contains(".")) {
        // Ignore CL_ markers
        continue;
      }

      if (key.endsWith("-Access")) {
        continue;
      }

      MethodIdentifier m = parseMethodModifier(key);
      Optional<Access> access = MapUtils.getOptional(accessMap, m);

      String[] valueParts = value.split("\\|");
      String[] exceptions = valueParts.length >= 1
                                ? StringUtils.splitNoEmpty(valueParts[0], ",")
                                : new String[] {};
      String[] params = valueParts.length >= 2
                            ? StringUtils.splitNoEmpty(valueParts[1], ",")
                            : new String[] {};
      entries.put(
          new SidedMethodIdentifier(m, side),
          new MethodEntry(List.of(exceptions), List.of(params), access));
    }

    return entries;
  }

  public static Map<String, ClassEntry> readExceptorJSON(Reader reader) {
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(ClassEntry.InnerClass.class,
                                 new ClassEntry.InnerClass.Deserializer())
            .create();
    Type type = new TypeToken<Map<String, ClassEntry>>() {}.getType();

    return gson.fromJson(reader, type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    SeargeExceptor that = (SeargeExceptor)o;
    return Objects.equals(methods, that.methods) &&
        Objects.equals(classes, that.classes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(methods, classes);
  }

  @Override
  public String toString() {
    return "SeargeExceptor{"
        + "methods=" + methods + ", classes=" + classes + '}';
  }

  public static class MethodEntry {
    public final List<String> exceptions;
    public final List<String> params;
    public final Optional<Access> access;

    public MethodEntry(List<String> exceptions, List<String> params) {
      this(exceptions, params, Optional.empty());
    }

    public MethodEntry(List<String> exceptions, List<String> params,
                       Access access) {
      this(exceptions, params, Optional.of(access));
    }

    public MethodEntry(List<String> exceptions, List<String> params,
                       Optional<Access> access) {
      this.exceptions = exceptions;
      this.params = params;
      this.access = access;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      MethodEntry that = (MethodEntry)o;
      return Objects.equals(exceptions, that.exceptions) &&
          Objects.equals(params, that.params) &&
          Objects.equals(access, that.access);
    }

    @Override
    public int hashCode() {
      return Objects.hash(exceptions, params, access);
    }

    @Override
    public String toString() {
      return "MethodEntry{"
          + "exceptions=" + exceptions + ", params=" + params +
          ", access=" + access + '}';
    }
  }

  public static class ClassEntry {
    public final EnclosingMethod enclosingMethod;
    public final List<InnerClass> innerClasses;

    public ClassEntry() {
      this.enclosingMethod = null;
      this.innerClasses = List.of();
    }

    public ClassEntry(EnclosingMethod enclosingMethod,
                      List<InnerClass> innerClasses) {
      this.enclosingMethod = enclosingMethod;
      this.innerClasses = innerClasses;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      ClassEntry that = (ClassEntry)o;
      return Objects.equals(enclosingMethod, that.enclosingMethod) &&
          Objects.equals(innerClasses, that.innerClasses);
    }

    @Override
    public int hashCode() {
      return Objects.hash(enclosingMethod, innerClasses);
    }

    @Override
    public String toString() {
      return "ClassEntry{"
          + ", enclosingMethod=" + enclosingMethod +
          ", innerClasses=" + innerClasses + '}';
    }

    public static class EnclosingMethod {
      public final String desc;
      public final String name;
      public final String owner;

      public EnclosingMethod(String desc, String name, String owner) {
        this.desc = desc;
        this.name = name;
        this.owner = owner;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o)
          return true;
        if (o == null || getClass() != o.getClass())
          return false;
        EnclosingMethod that = (EnclosingMethod)o;
        return Objects.equals(desc, that.desc) &&
            Objects.equals(name, that.name) &&
            Objects.equals(owner, that.owner);
      }

      @Override
      public int hashCode() {
        return Objects.hash(desc, name, owner);
      }

      @Override
      public String toString() {
        return "EnclosingMethod{"
            + "desc='" + desc + '\'' + ", name='" + name + '\'' + ", owner='" +
            owner + '\'' + '}';
      }
    }

    public static class InnerClass {
      public final int access;
      public final String innerClass;
      public final Optional<String> innerName;
      public final Optional<String> outerClass;

      public InnerClass(int access, String innerClass,
                        Optional<String> innerName,
                        Optional<String> outerClass) {
        this.access = access;
        this.innerClass = innerClass;
        this.innerName = innerName;
        this.outerClass = outerClass;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o)
          return true;
        if (o == null || getClass() != o.getClass())
          return false;
        InnerClass that = (InnerClass)o;
        return Objects.equals(access, that.access) &&
            Objects.equals(innerClass, that.innerClass) &&
            Objects.equals(innerName, that.innerName) &&
            Objects.equals(outerClass, that.outerClass);
      }

      @Override
      public int hashCode() {
        return Objects.hash(access, innerClass, innerName, outerClass);
      }

      @Override
      public String toString() {
        return "InnerClass{"
            + "access=" + access + ", innerClass='" + innerClass + '\'' +
            ", innerName=" + innerName + ", outerClass=" + outerClass + '}';
      }

      public static class Deserializer implements JsonDeserializer<InnerClass> {
        private Optional<String> getAsOptionalString(JsonObject o, String key) {
          return o.get(key) != null ? Optional.of(o.get(key).getAsString())
                                    : Optional.empty();
        }

        @Override
        public SeargeExceptor.ClassEntry.InnerClass
        deserialize(JsonElement jsonElement, java.lang.reflect.Type type,
                    JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
          JsonObject o = jsonElement.getAsJsonObject();
          int access = o.get("access") != null
                           ? Integer.parseInt(o.get("access").getAsString(), 16)
                           : 0;
          return new SeargeExceptor.ClassEntry.InnerClass(
              access, o.get("inner_class").getAsString(),
              getAsOptionalString(o, "inner_name"),
              getAsOptionalString(o, "outer_class"));
        }
      }
    }
  }
}
