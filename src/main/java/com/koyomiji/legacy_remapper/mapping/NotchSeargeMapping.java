package com.koyomiji.legacy_remapper.mapping;

import com.koyomiji.legacy_remapper.Side;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotchSeargeMapping {
  public final List<ClassEntry> classes;
  public final List<FieldEntry> fields;
  public final List<MethodEntry> methods;

  public NotchSeargeMapping() { this(List.of(), List.of(), List.of()); }

  public NotchSeargeMapping(List<ClassEntry> classes, List<FieldEntry> fields,
                            List<MethodEntry> methods) {
    this.classes = classes;
    this.fields = fields;
    this.methods = methods;
  }

  public static NotchSeargeMapping from(NotchSeargeMCPMapping mapping) {
    List<ClassEntry> classes = new ArrayList<>();
    List<FieldEntry> fields = new ArrayList<>();
    List<MethodEntry> methods = new ArrayList<>();

    for (NotchSeargeMCPMapping.ClassEntry e : mapping.classes) {
      classes.add(new ClassEntry(e.notchName, e.seargeName, e.side));
    }

    for (NotchSeargeMCPMapping.FieldEntry e : mapping.fields) {
      fields.add(
          new FieldEntry(e.notchClassName, e.notchName, e.seargeName, e.side));
    }

    for (NotchSeargeMCPMapping.MethodEntry e : mapping.methods) {
      methods.add(new MethodEntry(e.notchClassName, e.notchName, e.notchDesc,
                                  e.seargeName, e.side));
    }

    return new NotchSeargeMapping(classes, fields, methods);
  }

  public static NotchSeargeMapping
  merge(Iterable<NotchSeargeMapping> mappings) {
    List<ClassEntry> classes = new ArrayList<>();
    List<FieldEntry> fields = new ArrayList<>();
    List<MethodEntry> methods = new ArrayList<>();

    for (NotchSeargeMapping mapping : mappings) {
      classes.addAll(mapping.classes);
      fields.addAll(mapping.fields);
      methods.addAll(mapping.methods);
    }

    return new NotchSeargeMapping(classes, fields, methods);
  }

  private static Side parseSRGSide(String string) {
    if (string.equals("#C")) {
      return Side.CLIENT;
    } else if (string.equals("#S")) {
      return Side.SERVER;
    }

    return Side.BOTH;
  }

  public static NotchSeargeMapping readMCP2RGS(Reader reader, Side side)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);
    List<ClassEntry> classes = new ArrayList<>();
    List<MethodEntry> methods = new ArrayList<>();
    List<FieldEntry> fields = new ArrayList<>();

    while (br.ready()) {
      String line = br.readLine();

      if (line.startsWith("#")) {
        continue;
      }

      String[] parts = line.split(" ");
      switch (parts[0]) {
      case ".class_map": {
        String from = parts[1];
        // In MCP2.x, deobfuscated classes are moved into "net/minecraft/src/"
        // after decompilation
        String to = "net/minecraft/src/" + parts[2];
        classes.add(new ClassEntry(from, to, side));
        break;
      }
      case ".method_map": {
        String fromClassMethod = parts[1];
        String fromClass =
            fromClassMethod.substring(0, fromClassMethod.lastIndexOf('/'));
        String fromMethod =
            fromClassMethod.substring(fromClassMethod.lastIndexOf('/') + 1);
        String fromDesc = parts[2];
        String toMethod = parts[3];
        methods.add(
            new MethodEntry(fromClass, fromMethod, fromDesc, toMethod, side));
        break;
      }
      case ".field_map": {
        String fromClassField = parts[1];
        String fromClass =
            fromClassField.substring(0, fromClassField.lastIndexOf('/'));
        String fromField =
            fromClassField.substring(fromClassField.lastIndexOf('/') + 1);
        String toField = parts[2];
        fields.add(new FieldEntry(fromClass, fromField, toField, side));
        break;
      }
      }
    }

    return new NotchSeargeMapping(classes, fields, methods);
  }

  public static NotchSeargeMapping readSRG(Reader reader) throws IOException {
    return readSRG(reader, Side.BOTH);
  }

  public static NotchSeargeMapping readSRG(Reader reader, Side defaultSide)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);
    List<ClassEntry> classes = new ArrayList<>();
    List<MethodEntry> methods = new ArrayList<>();
    List<FieldEntry> fields = new ArrayList<>();

    while (br.ready()) {
      String line = br.readLine();
      String[] parts = line.split(" ");
      switch (parts[0]) {
      case "PK:": {
        break;
      }
      case "CL:": {
        String from = parts[1];
        String to = parts[2];
        Side side = defaultSide;

        if (parts.length >= 4) {
          side = parseSRGSide(parts[3]);
        }

        classes.add(new ClassEntry(from, to, side));
        break;
      }
      case "MD:": {
        String fromClassMethod = parts[1];
        String fromClass =
            fromClassMethod.substring(0, fromClassMethod.lastIndexOf('/'));
        String fromMethod =
            fromClassMethod.substring(fromClassMethod.lastIndexOf('/') + 1);
        String fromDesc = parts[2];
        String toClassMethod = parts[3];
        String toMethod =
            toClassMethod.substring(toClassMethod.lastIndexOf('/') + 1);
        Side side = defaultSide;

        if (parts.length >= 6) {
          side = parseSRGSide(parts[5]);
        }

        methods.add(
            new MethodEntry(fromClass, fromMethod, fromDesc, toMethod, side));
        break;
      }
      case "FD:": {
        String fromClassField = parts[1];
        String fromClass =
            fromClassField.substring(0, fromClassField.lastIndexOf('/'));
        String fromField =
            fromClassField.substring(fromClassField.lastIndexOf('/') + 1);
        String toClassField = parts[2];
        String toField =
            toClassField.substring(toClassField.lastIndexOf('/') + 1);
        Side side = defaultSide;

        if (parts.length >= 4) {
          side = parseSRGSide(parts[3]);
        }

        fields.add(new FieldEntry(fromClass, fromField, toField, side));
        break;
      }
      }
    }

    return new NotchSeargeMapping(classes, fields, methods);
  }

  public static NotchSeargeMapping readCSRG(Reader reader) throws IOException {
    BufferedReader br = new BufferedReader(reader);
    List<ClassEntry> classes = new ArrayList<>();
    List<MethodEntry> methods = new ArrayList<>();
    List<FieldEntry> fields = new ArrayList<>();

    while (br.ready()) {
      String line = br.readLine();
      String[] parts = line.split(" ");

      if (parts.length == 2) {
        if (parts[0].endsWith("/")) {
          // package
        } else {
          String from = parts[0];
          String to = parts[1];
          classes.add(new ClassEntry(from, to, Side.BOTH));
        }
      } else if (parts.length == 3) {
        String fromClass = parts[0];
        String fromField = parts[1];
        String toField = parts[2];
        fields.add(new FieldEntry(fromClass, fromField, toField, Side.BOTH));
      } else if (parts.length == 4) {
        String fromClass = parts[0];
        String fromMethod = parts[1];
        String fromMethodDesc = parts[2];
        String toMethod = parts[3];
        methods.add(new MethodEntry(fromClass, fromMethod, fromMethodDesc,
                                    toMethod, Side.BOTH));
      }
    }

    return new NotchSeargeMapping(classes, fields, methods);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    NotchSeargeMapping that = (NotchSeargeMapping)o;
    return Objects.equals(classes, that.classes) &&
        Objects.equals(fields, that.fields) &&
        Objects.equals(methods, that.methods);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classes, fields, methods);
  }

  @Override
  public String toString() {
    return "NotchSeargeMapping{"
        + "classes=" + classes + ", fields=" + fields + ", methods=" + methods +
        '}';
  }

  public static class ClassEntry {
    public final String notchName;
    public final String seargeName;
    public final Side side;

    public ClassEntry(String notchName, String seargeName, Side side) {
      this.notchName = notchName;
      this.seargeName = seargeName;
      this.side = side;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      ClassEntry that = (ClassEntry)o;
      return Objects.equals(notchName, that.notchName) &&
          Objects.equals(seargeName, that.seargeName) && side == that.side;
    }

    @Override
    public int hashCode() {
      return Objects.hash(notchName, seargeName, side);
    }

    @Override
    public String toString() {
      return "ClassEntry{"
          + "notchName='" + notchName + '\'' + ", seargeName='" + seargeName +
          '\'' + ", side=" + side + '}';
    }
  }

  public static class FieldEntry {
    public final String notchClassName;
    public final String notchName;
    public final String seargeName;
    public final Side side;

    public FieldEntry(String notchClassName, String notchName,
                      String seargeName, Side side) {
      this.notchClassName = notchClassName;
      this.notchName = notchName;
      this.seargeName = seargeName;
      this.side = side;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      FieldEntry that = (FieldEntry)o;
      return Objects.equals(notchClassName, that.notchClassName) &&
          Objects.equals(notchName, that.notchName) &&
          Objects.equals(seargeName, that.seargeName) && side == that.side;
    }

    @Override
    public int hashCode() {
      return Objects.hash(notchClassName, notchName, seargeName, side);
    }

    @Override
    public String toString() {
      return "FieldEntry{"
          + "notchClassName='" + notchClassName + '\'' + ", notchName='" +
          notchName + '\'' + ", seargeName='" + seargeName + '\'' +
          ", side=" + side + '}';
    }
  }

  public static class MethodEntry {
    public final String notchClassName;
    public final String notchName;
    public final String notchDesc;
    public final String seargeName;
    public final Side side;

    public MethodEntry(String notchClassName, String notchName,
                       String notchDesc, String seargeName, Side side) {
      this.notchClassName = notchClassName;
      this.notchName = notchName;
      this.notchDesc = notchDesc;
      this.seargeName = seargeName;
      this.side = side;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      MethodEntry that = (MethodEntry)o;
      return Objects.equals(notchClassName, that.notchClassName) &&
          Objects.equals(notchName, that.notchName) &&
          Objects.equals(notchDesc, that.notchDesc) &&
          Objects.equals(seargeName, that.seargeName) && side == that.side;
    }

    @Override
    public int hashCode() {
      return Objects.hash(notchClassName, notchName, notchDesc, seargeName,
                          side);
    }

    @Override
    public String toString() {
      return "MethodEntry{"
          + "notchClassName='" + notchClassName + '\'' + ", notchName='" +
          notchName + '\'' + ", notchDesc='" + notchDesc + '\'' +
          ", seargeName='" + seargeName + '\'' + ", side=" + side + '}';
    }
  }
}
