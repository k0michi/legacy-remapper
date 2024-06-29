package com.koyomiji.legacy_remapper.mappings;

import com.koyomiji.legacy_remapper.Side;
import com.koyomiji.legacy_remapper.util.IteratorUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SeargeMCPMapping {
  public final List<MemberEntry> fields;
  public final List<MemberEntry> methods;
  public final List<ParamEntry> params;

  public SeargeMCPMapping(List<MemberEntry> fields, List<MemberEntry> methods,
                          List<ParamEntry> params) {
    this.fields = fields;
    this.methods = methods;
    this.params = params;
  }

  public static SeargeMCPMapping from(NotchSeargeMCPMapping mapping) {
    List<MemberEntry> fields = new ArrayList<>();
    List<MemberEntry> methods = new ArrayList<>();
    List<ParamEntry> params = new ArrayList<>();

    for (NotchSeargeMCPMapping.FieldEntry e : mapping.fields) {
      fields.add(new MemberEntry(e.seargeName, e.mcpName, e.side, ""));
    }

    for (NotchSeargeMCPMapping.MethodEntry e : mapping.methods) {
      methods.add(new MemberEntry(e.seargeName, e.mcpName, e.side, ""));
    }

    for (NotchSeargeMCPMapping.ParamEntry e : mapping.params) {
      params.add(new ParamEntry(e.seargeName, e.mcpName, e.side));
    }

    return new SeargeMCPMapping(fields, methods, params);
  }

  public static List<MemberEntry> readMCP2FieldCSV(Reader reader)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);

    List<MemberEntry> entries = new ArrayList<>();

    try (CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> it = parser.iterator();
      IteratorUtils.skip(it, 3);

      while (it.hasNext()) {
        CSVRecord record = it.next();

        String clientSeargeName = record.get(2);
        String serverSeargeName = record.get(5);

        if (clientSeargeName.isEmpty() && serverSeargeName.isEmpty()) {
          continue;
        }

        String mcpName = record.get(6);
        String notes = "";

        if (record.size() >= 8) {
          notes = record.get(7);

          if (notes.equals("*")) {
            notes = "";
          }
        }

        if (!clientSeargeName.equals("*")) {
          entries.add(
              new MemberEntry(clientSeargeName, mcpName, Side.CLIENT, notes));
        }

        if (!serverSeargeName.equals("*")) {
          entries.add(
              new MemberEntry(serverSeargeName, mcpName, Side.SERVER, notes));
        }
      }
    }

    return entries;
  }

  public static List<MemberEntry> readMCP2MethodCSV(Reader reader)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);

    List<MemberEntry> entries = new ArrayList<>();

    try (CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> it = parser.iterator();
      IteratorUtils.skip(it, 4);

      while (it.hasNext()) {
        CSVRecord record = it.next();

        String clientSeargeName = record.get(1);
        String serverSeargeName = record.get(3);

        if (clientSeargeName.isEmpty() && serverSeargeName.isEmpty()) {
          continue;
        }

        String mcpName = record.get(4);
        String notes = "";

        if (record.size() >= 6) {
          notes = record.get(5);

          if (notes.equals("*")) {
            notes = "";
          }
        }

        if (!clientSeargeName.equals("*")) {
          entries.add(
              new MemberEntry(clientSeargeName, mcpName, Side.CLIENT, notes));
        }

        if (!serverSeargeName.equals("*")) {
          entries.add(
              new MemberEntry(serverSeargeName, mcpName, Side.SERVER, notes));
        }
      }
    }

    return entries;
  }

  public static List<MemberEntry> readMCP6MemberCSV(Reader reader)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);

    List<MemberEntry> entries = new ArrayList<>();

    try (CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> it = parser.iterator();
      IteratorUtils.skip(it, 1);

      while (it.hasNext()) {
        CSVRecord record = it.next();

        String searge = record.get(0);
        String name = record.get(1);
        Side side = Side.fromID(record.get(2));
        String desc = record.get(3);
        entries.add(new MemberEntry(searge, name, side, desc));
      }
    }

    return entries;
  }

  public static List<ParamEntry> readMCP6ParamCSV(Reader reader)
      throws IOException {
    BufferedReader br = new BufferedReader(reader);

    List<ParamEntry> entries = new ArrayList<>();

    try (CSVParser parser = CSVParser.parse(br, CSVFormat.DEFAULT)) {
      Iterator<CSVRecord> it = parser.iterator();
      IteratorUtils.skip(it, 1);

      while (it.hasNext()) {
        CSVRecord record = it.next();

        String searge = record.get(0);
        String name = record.get(1);
        Side side = Side.fromID(record.get(2));
        entries.add(new ParamEntry(searge, name, side));
      }
    }

    return entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    SeargeMCPMapping that = (SeargeMCPMapping)o;
    return Objects.equals(fields, that.fields) &&
        Objects.equals(methods, that.methods) &&
        Objects.equals(params, that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fields, methods, params);
  }

  @Override
  public String toString() {
    return "SeargeMCPMapping{"
        + "fields=" + fields + ", methods=" + methods + ", params=" + params +
        '}';
  }

  public static class MemberEntry {
    public final String seargeName;
    public final String mcpName;
    public final Side side;
    public final String description;

    public MemberEntry(String seargeName, String mcpName, Side side,
                       String description) {
      this.seargeName = seargeName;
      this.mcpName = mcpName;
      this.side = side;
      this.description = description;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      MemberEntry that = (MemberEntry)o;
      return Objects.equals(seargeName, that.seargeName) &&
          Objects.equals(mcpName, that.mcpName) && side == that.side &&
          Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
      return Objects.hash(seargeName, mcpName, side, description);
    }

    @Override
    public String toString() {
      return "MemberEntry{"
          + "seargeName='" + seargeName + '\'' + ", mcpName='" + mcpName +
          '\'' + ", side=" + side + ", description='" + description + '\'' +
          '}';
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
      ParamEntry that = (ParamEntry)o;
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
