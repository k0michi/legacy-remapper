package com.koyomiji.legacy_remapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ClassIndex {
  private HashMap<String, Class> map = new HashMap<>();

  public ClassIndex() {}

  public ClassIndex(List<Class> classes) {
    for (Class c : classes) {
      map.put(c.name, c);
    }
  }

  public ClassIndex(Map<String, Class> classes) {
    map = new HashMap<>(classes);
  }

  public void addClass(Class c) { map.put(c.name, c); }

  public void addClass(String name, String superName, String[] interfaces) {
    addClass(new Class(name, superName, interfaces));
  }

  public void addClass(String name, String superName, List<String> interfaces) {
    addClass(new Class(name, superName, interfaces));
  }

  public void removeClass(String className) { map.remove(className); }

  public Class getClass(String className) { return map.get(className); }

  public boolean containsClass(String className) {
    return map.containsKey(className);
  }

  public Collection<Class> getClasses() { return map.values(); }

  public void addField(String className, String fieldName, String fieldDesc) {
    Class c = map.get(className);
    c.fields.add(new Member(fieldName, fieldDesc));
  }

  public void addMethod(String className, String methodName,
                        String methodDesc) {
    Class c = map.get(className);
    c.methods.add(new Member(methodName, methodDesc));
  }

  public Set<String> getParentClasses(String className) {
    Class c = map.get(className);

    if (c == null) {
      return null;
    }

    HashSet<String> parents = new HashSet<>(c.interfaces);
    parents.add(c.superName);
    return parents;
  }

  private HashSet<String> _getAncestorClasses(String className,
                                              HashSet<String> result) {
    Set<String> parents = getParentClasses(className);

    if (parents == null) {
      return result;
    }

    result.addAll(parents);

    for (String parent : parents) {
      _getAncestorClasses(parent, result);
    }

    return result;
  }

  public Set<String> getAncestorClasses(String className) {
    return _getAncestorClasses(className, new HashSet<>());
  }

  public static ClassIndex readJSON(Reader reader) {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Class>>() {}.getType();
    Map<String, Class> parsed = gson.fromJson(reader, type);

    for (String n : parsed.keySet()) {
      Class c = parsed.get(n);
      parsed.put(n,
                 new Class(n, c.superName, c.interfaces, c.fields, c.methods));
    }

    return new ClassIndex(parsed);
  }

  public static ClassIndex fromJSON(String json) {
    return readJSON(new CharArrayReader(json.toCharArray()));
  }

  public String toJSON() {
    CharArrayWriter w = new CharArrayWriter();

    try {
      writeJSON(w);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new String(w.toCharArray());
  }

  public void writeJSON(Writer writer) throws IOException {
    Gson gson = new Gson();
    writer.write(gson.toJson(this.map));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ClassIndex that = (ClassIndex)o;
    return Objects.equals(map, that.map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(map);
  }

  @Override
  public String toString() {
    return "DeclarationMap{"
        + "map=" + map + '}';
  }

  public static class Class {
    public final String name;
    public final String superName;
    public final List<String> interfaces;
    public final List<Member> fields;
    public final List<Member> methods;

    public Class(String name, String superName, List<String> interfaces) {
      this(name, superName, interfaces, new ArrayList<>(), new ArrayList<>());
    }

    public Class(String name, String superName, String[] interfaces) {
      this(name, superName, Arrays.asList(interfaces), new ArrayList<>(),
           new ArrayList<>());
    }

    public Class(String name, String superName, List<String> interfaces,
                 List<Member> fields, List<Member> methods) {
      this.name = name;
      this.superName = superName;
      this.interfaces = interfaces;
      this.fields = fields;
      this.methods = methods;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      Class aClass = (Class)o;
      return Objects.equals(name, aClass.name) &&
          Objects.equals(superName, aClass.superName) &&
          Objects.equals(interfaces, aClass.interfaces) &&
          Objects.equals(fields, aClass.fields) &&
          Objects.equals(methods, aClass.methods);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, superName, interfaces, fields, methods);
    }

    @Override
    public String toString() {
      return "Class{"
          + "name='" + name + '\'' + ", superName='" + superName + '\'' +
          ", interfaces=" + interfaces + ", fields=" + fields +
          ", methods=" + methods + '}';
    }
  }

  public static class Member {
    public final String name;
    public final String descriptor;

    public Member(String name, String descriptor) {
      this.name = name;
      this.descriptor = descriptor;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      Member member = (Member)o;
      return Objects.equals(name, member.name) &&
          Objects.equals(descriptor, member.descriptor);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, descriptor);
    }

    @Override
    public String toString() {
      return "Member{"
          + "name='" + name + '\'' + ", descriptor='" + descriptor + '\'' + '}';
    }
  }
}
