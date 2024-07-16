package com.koyomiji.legacy_remapper.mapping;

import com.koyomiji.legacy_remapper.FieldIdentifier;
import com.koyomiji.legacy_remapper.MethodIdentifier;
import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.util.IteratorUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class NotchSeargeMCPMapping {
  public final List<ClassEntry> classes;
  public final List<FieldEntry> fields;
  public final List<MethodEntry> methods;
  public final List<ParamEntry> params;

  public NotchSeargeMCPMapping(List<ClassEntry> classes,
                               List<FieldEntry> fields,
                               List<MethodEntry> methods) {
    this(classes, fields, methods, List.of());
  }

  public NotchSeargeMCPMapping(List<ClassEntry> classes,
                               List<FieldEntry> fields,
                               List<MethodEntry> methods,
                               List<ParamEntry> params) {
    this.classes = classes;
    this.fields = fields;
    this.methods = methods;
    this.params = params;
  }

  public static NotchSeargeMCPMapping
  merge(Iterable<NotchSeargeMCPMapping> mappings) {
    List<ClassEntry> classes = new ArrayList<>();
    List<FieldEntry> fields = new ArrayList<>();
    List<MethodEntry> methods = new ArrayList<>();
    List<ParamEntry> params = new ArrayList<>();

    for (NotchSeargeMCPMapping m : mappings) {
      classes.addAll(m.classes);
      fields.addAll(m.fields);
      methods.addAll(m.methods);
      params.addAll(m.params);
    }

    return new NotchSeargeMCPMapping(classes, fields, methods, params);
  }

  public static NotchSeargeMCPMapping readMCP1RGS(Reader reader, Side side)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);
    List<ClassEntry> classes = new ArrayList<>();
    List<FieldEntry> fields = new ArrayList<>();
    List<MethodEntry> methods = new ArrayList<>();
    Map<FieldIdentifier, String> notchMCPFieldMap = new HashMap<>();
    Map<MethodIdentifier, String> notchMCPMethodMap = new HashMap<>();
    boolean generated = false;

    while (br.ready()) {
      String line = br.readLine();

      if (line.equals("### GENERATED MAPPINGS:")) {
        generated = true;
      }

      if (line.startsWith("#")) {
        continue;
      }

      String[] parts = line.split(" ");
      switch (parts[0]) {
      case ".class_map": {
        String from = parts[1];
        // In MCP1.x, deobfuscated classes are in the default package
        // But we move them into "net/minecraft/src/" for consistency
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

        if (generated) {
          String mcp = notchMCPMethodMap.getOrDefault(
              new MethodIdentifier(fromClass, fromMethod, fromDesc), toMethod);
          methods.add(new MethodEntry(fromClass, fromMethod, fromDesc, toMethod,
                                      mcp, side));
        } else {
          notchMCPMethodMap.put(
              new MethodIdentifier(fromClass, fromMethod, fromDesc), toMethod);
        }

        break;
      }
      case ".field_map": {
        String fromClassField = parts[1];
        String fromClass =
            fromClassField.substring(0, fromClassField.lastIndexOf('/'));
        String fromField =
            fromClassField.substring(fromClassField.lastIndexOf('/') + 1);
        String toField = parts[2];

        if (generated) {
          String mcp = notchMCPFieldMap.getOrDefault(
              new FieldIdentifier(fromClass, fromField), toField);
          fields.add(new FieldEntry(fromClass, fromField, toField, mcp, side));
        } else {
          notchMCPFieldMap.put(new FieldIdentifier(fromClass, fromField),
                               toField);
        }

        break;
      }
      }
    }

    return new NotchSeargeMCPMapping(classes, fields, methods);
  }

  public static List<ClassEntry> readMCP3ClassCSV(Reader reader)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);

    List<ClassEntry> entries = new ArrayList<>();

    try (CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> it = parser.iterator();
      IteratorUtils.skip(it, 1);

      while (it.hasNext()) {
        CSVRecord record = it.next();

        String seargeName = record.get(0);
        String notchName = record.get(1);
        String packageName = record.get(3);
        Side side = Side.fromID(record.get(4));

        if (seargeName.equals(notchName)) {
          // This happens for Minecraft and MinecraftServer classes
          notchName = String.join("/", packageName, notchName);
        }

        entries.add(new ClassEntry(
            notchName, String.join("/", packageName, seargeName), side));
      }
    }

    return entries;
  }

  public static List<FieldEntry> readMCP3FieldCSV(Reader reader)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);

    List<FieldEntry> entries = new ArrayList<>();

    try (CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> it = parser.iterator();
      IteratorUtils.skip(it, 1);

      while (it.hasNext()) {
        CSVRecord record = it.next();

        String seargeName = record.get(0);
        String mcpName = record.get(1);
        String notchName = record.get(2);
        String seargeClassName = record.get(5);
        String notchClassName = record.get(6);
        String packageName = record.get(7);

        if (seargeClassName.equals(notchClassName)) {
          // This happens for Minecraft and MinecraftServer classes
          notchClassName = String.join("/", packageName, notchClassName);
        }

        Side side = Side.fromID(record.get(8));
        entries.add(new FieldEntry(notchClassName, notchName, seargeName,
                                   mcpName, side));
      }
    }

    return entries;
  }

  public static List<MethodEntry> readMCP3MethodCSV(Reader reader)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);

    List<MethodEntry> entries = new ArrayList<>();

    try (CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> it = parser.iterator();
      IteratorUtils.skip(it, 1);

      while (it.hasNext()) {
        CSVRecord record = it.next();

        String seargeName = record.get(0);
        String mcpName = record.get(1);
        String notchName = record.get(2);
        String notchDesc = record.get(4);
        String seargeClassName = record.get(5);
        String notchClassName = record.get(6);
        String packageName = record.get(7);

        if (seargeClassName.equals(notchClassName)) {
          // This happens for Minecraft and MinecraftServer classes
          notchClassName = String.join("/", packageName, notchClassName);
        }

        Side side = Side.fromID(record.get(8));
        entries.add(new MethodEntry(notchClassName, notchName, notchDesc,
                                    seargeName, mcpName, side));
      }
    }

    return entries;
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
    public final String mcpName;
    public final Side side;

    public FieldEntry(String notchClassName, String notchName,
                      String seargeName, String mcpName, Side side) {
      this.notchClassName = notchClassName;
      this.notchName = notchName;
      this.seargeName = seargeName;
      this.mcpName = mcpName;
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
          Objects.equals(seargeName, that.seargeName) &&
          Objects.equals(mcpName, that.mcpName) && side == that.side;
    }

    @Override
    public int hashCode() {
      return Objects.hash(notchClassName, notchName, seargeName, mcpName, side);
    }

    @Override
    public String toString() {
      return "FieldEntry{"
          + "notchClassName='" + notchClassName + '\'' + ", notchName='" +
          notchName + '\'' + ", seargeName='" + seargeName + '\'' +
          ", mcpName='" + mcpName + '\'' + ", side=" + side + '}';
    }
  }

  public static class MethodEntry {
    public final String notchClassName;
    public final String notchName;
    public final String notchDesc;
    public final String seargeName;
    public final String mcpName;
    public final Side side;

    public MethodEntry(String notchClassName, String notchName,
                       String notchDesc, String seargeName, String mcpName,
                       Side side) {
      this.notchClassName = notchClassName;
      this.notchName = notchName;
      this.notchDesc = notchDesc;
      this.seargeName = seargeName;
      this.mcpName = mcpName;
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
          Objects.equals(seargeName, that.seargeName) &&
          Objects.equals(mcpName, that.mcpName) && side == that.side;
    }

    @Override
    public int hashCode() {
      return Objects.hash(notchClassName, notchName, notchDesc, seargeName,
                          mcpName, side);
    }

    @Override
    public String toString() {
      return "MethodEntry{"
          + "notchClassName='" + notchClassName + '\'' + ", notchName='" +
          notchName + '\'' + ", notchDesc='" + notchDesc + '\'' +
          ", seargeName='" + seargeName + '\'' + ", mcpName='" + mcpName +
          '\'' + ", side=" + side + '}';
    }
  }

  public static class ParamEntry {
    public final String seargeName;
    public final String mcpName;
    public final Side side;

    public ParamEntry(String seargeName, String mcpName, Side side) {
      this.seargeName = seargeName;
      this.mcpName = mcpName;
      this.side = side;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      SeargeMCPMapping.ParamEntry that = (SeargeMCPMapping.ParamEntry)o;
      return Objects.equals(seargeName, that.seargeName) &&
          Objects.equals(mcpName, that.mcpName) && side == that.side;
    }

    @Override
    public int hashCode() {
      return Objects.hash(seargeName, mcpName, side);
    }

    @Override
    public String toString() {
      return "ParamEntry{"
          + "seargeName='" + seargeName + '\'' + ", mcpName='" + mcpName +
          '\'' + ", side=" + side + '}';
    }
  }
}
