package com.koyomiji.legacy_remapper;

import com.koyomiji.legacy_remapper.fs.IReadOnlyFileSystem;
import com.koyomiji.legacy_remapper.mapping.NotchSeargeMCPMapping;
import com.koyomiji.legacy_remapper.mapping.NotchSeargeMapping;
import com.koyomiji.legacy_remapper.mapping.SeargeExceptor;
import com.koyomiji.legacy_remapper.mapping.SeargeMCPMapping;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ConfigReader {
  private final IReadOnlyFileSystem fs;
  private NotchSeargeMCPMapping notchSeargeMCP;

  public ConfigReader(IReadOnlyFileSystem fileSystem) { this.fs = fileSystem; }

  private boolean hasLine(Reader reader, String line) throws IOException {
    BufferedReader br = new BufferedReader(reader);

    while (br.ready()) {
      if (br.readLine().equals(line)) {
        return true;
      }
    }

    return false;
  }

  public NotchSeargeMapping readNotchSeargeMapping(Side side)
      throws IOException {
    if (fs.isFile(MCPFilenames.joinedCSRG)) {
      try (Reader r = fs.newReader(MCPFilenames.joinedCSRG)) {
        return NotchSeargeMapping.readCSRG(r);
      }
    } else if (fs.isFile(MCPFilenames.joinedSRG)) {
      try (Reader r = fs.newReader(MCPFilenames.joinedSRG)) {
        return NotchSeargeMapping.readSRG(r);
      }
    } else if (fs.isFile(MCPFilenames.clientSRG) ||
               fs.isFile(MCPFilenames.serverSRG)) {
      List<NotchSeargeMapping> mappings = new ArrayList<>();

      if (side.includesClient() && fs.isFile(MCPFilenames.clientSRG)) {
        try (Reader r = fs.newReader(MCPFilenames.clientSRG)) {
          mappings.add(NotchSeargeMapping.readSRG(r, Side.CLIENT));
        }
      }

      if (side.includesServer() && fs.isFile(MCPFilenames.serverSRG)) {
        try (Reader r = fs.newReader(MCPFilenames.serverSRG)) {
          mappings.add(NotchSeargeMapping.readSRG(r, Side.SERVER));
        }
      }

      return NotchSeargeMapping.merge(mappings);
    } else if (fs.isFile(MCPFilenames.minecraftRGS) ||
               fs.isFile(MCPFilenames.minecraftServerRGS)) {
      boolean hasMCP = false;

      if (side.includesClient() && fs.isFile(MCPFilenames.minecraftRGS)) {
        try (Reader r = fs.newReader(MCPFilenames.minecraftRGS)) {
          hasMCP = hasMCP || hasLine(r, "### GENERATED MAPPINGS:");
        }
      }

      if (side.includesServer() && fs.isFile(MCPFilenames.minecraftServerRGS)) {
        try (Reader r = fs.newReader(MCPFilenames.minecraftServerRGS)) {
          hasMCP = hasMCP || hasLine(r, "### GENERATED MAPPINGS:");
        }
      }

      if (hasMCP) {
        List<NotchSeargeMCPMapping> mappings = new ArrayList<>();

        if (side.includesClient() && fs.isFile(MCPFilenames.minecraftRGS)) {
          try (Reader r = fs.newReader(MCPFilenames.minecraftRGS)) {
            mappings.add(NotchSeargeMCPMapping.readMCP1RGS(r, Side.CLIENT));
          }
        }

        if (side.includesServer() &&
            fs.isFile(MCPFilenames.minecraftServerRGS)) {
          try (Reader r = fs.newReader(MCPFilenames.minecraftServerRGS)) {
            mappings.add(NotchSeargeMCPMapping.readMCP1RGS(r, Side.SERVER));
          }
        }

        notchSeargeMCP = NotchSeargeMCPMapping.merge(mappings);
        return NotchSeargeMapping.from(notchSeargeMCP);
      } else {
        List<NotchSeargeMapping> mappings = new ArrayList<>();

        if (side.includesClient() && fs.isFile(MCPFilenames.minecraftRGS)) {
          try (Reader r = fs.newReader(MCPFilenames.minecraftRGS)) {
            mappings.add(NotchSeargeMapping.readMCP2RGS(r, Side.CLIENT));
          }
        }

        if (side.includesServer() &&
            fs.isFile(MCPFilenames.minecraftServerRGS)) {
          try (Reader r = fs.newReader(MCPFilenames.minecraftServerRGS)) {
            mappings.add(NotchSeargeMapping.readMCP2RGS(r, Side.SERVER));
          }
        }

        return NotchSeargeMapping.merge(mappings);
      }
    } else if (fs.isFile(MCPFilenames.classesCSV) ||
               fs.isFile(MCPFilenames.fieldsCSV) ||
               fs.isFile(MCPFilenames.methodsCSV)) {
      List<NotchSeargeMCPMapping.ClassEntry> classes = List.of();
      List<NotchSeargeMCPMapping.FieldEntry> fields = List.of();
      List<NotchSeargeMCPMapping.MethodEntry> methods = List.of();

      if (fs.isFile(MCPFilenames.classesCSV)) {
        try (Reader r = fs.newReader(MCPFilenames.classesCSV)) {
          classes = NotchSeargeMCPMapping.readMCP3ClassCSV(r);
        }
      }

      if (fs.isFile(MCPFilenames.fieldsCSV)) {
        try (Reader r = fs.newReader(MCPFilenames.fieldsCSV)) {
          fields = NotchSeargeMCPMapping.readMCP3FieldCSV(r);
        }
      }

      if (fs.isFile(MCPFilenames.methodsCSV)) {
        try (Reader r = fs.newReader(MCPFilenames.methodsCSV)) {
          methods = NotchSeargeMCPMapping.readMCP3MethodCSV(r);
        }
      }

      notchSeargeMCP = new NotchSeargeMCPMapping(classes, fields, methods);
      return NotchSeargeMapping.from(notchSeargeMCP);
    }

    throw new RuntimeException("No Searge mapping found");
  }

  public SeargeExceptor readSeargeExceptor(Side side) throws IOException {
    Map<SidedMethodIdentifier, SeargeExceptor.MethodEntry> methods =
        new HashMap<>();
    Map<String, SeargeExceptor.ClassEntry> classes = new HashMap<>();

    if (fs.isFile(MCPFilenames.exceptorJSON)) {
      try (Reader r = fs.newReader(MCPFilenames.exceptorJSON)) {
        classes = SeargeExceptor.readExceptorJSON(r);
      }
    }

    if (fs.isFile(MCPFilenames.joinedEXC)) {
      try (Reader r = fs.newReader(MCPFilenames.joinedEXC)) {
        methods = SeargeExceptor.readEXC(r);
      }

      return new SeargeExceptor(methods, classes);
    } else if (fs.isFile(MCPFilenames.clientEXC) ||
               fs.isFile(MCPFilenames.serverEXC)) {
      List<Map<SidedMethodIdentifier, SeargeExceptor.MethodEntry>>
          methodExceptors = new ArrayList<>();

      if (side.includesClient() && fs.isFile(MCPFilenames.clientEXC)) {
        try (Reader r = fs.newReader(MCPFilenames.clientEXC)) {
          methodExceptors.add(SeargeExceptor.readEXC(r, Side.CLIENT));
        }
      }

      if (side.includesServer() && fs.isFile(MCPFilenames.serverEXC)) {
        try (Reader r = fs.newReader(MCPFilenames.serverEXC)) {
          methodExceptors.add(SeargeExceptor.readEXC(r, Side.SERVER));
        }
      }

      methods = SeargeExceptor.merge(methodExceptors);
    }

    return new SeargeExceptor(methods, classes);
  }

  private int detectMembersCSVVersion(Reader reader) throws IOException {
    try (CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT)) {
      List<CSVRecord> records = parser.getRecords();

      if (records.size() >= 1 && records.get(0).toList().equals(List.of(
                                     "searge", "name", "side", "desc"))) {
        return 6;
      }

      if (records.size() >= 1 &&
          records.get(0).toList().equals(
              List.of("searge", "name", "notch", "sig", "notchsig", "classname",
                      "classnotch", "package", "side"))) {
        return 3;
      }

      if (records.size() >= 4 &&
          records.get(3).toList().equals(List.of(
              "class (for reference only)", "Reference",
              "class (for reference only)", "Reference", "Name", "Notes"))) {
        return 2;
      }

      if (records.size() >= 3 && records.get(2).toList().equals(List.of(
                                     "Class", "Field", "Name", "Class", "Field",
                                     "Name", "Name", "Notes"))) {
        return 2;
      }
    }

    throw new RuntimeException("Unsupported csv format");
  }

  public SeargeMCPMapping readSeargeMCPMapping() throws IOException {
    List<SeargeMCPMapping.MemberEntry> fields = List.of();
    List<SeargeMCPMapping.MemberEntry> methods = List.of();
    List<SeargeMCPMapping.ParamEntry> params = List.of();

    if (fs.isFile(MCPFilenames.fieldsCSV)) {
      int version;

      try (Reader r = fs.newReader(MCPFilenames.fieldsCSV)) {
        version = detectMembersCSVVersion(r);
      }

      try (Reader r = fs.newReader(MCPFilenames.fieldsCSV)) {
        if (version == 6) {
          fields = SeargeMCPMapping.readMCP6MemberCSV(r);
        } else {
          fields = SeargeMCPMapping.readMCP2FieldCSV(r);
        }
      }
    }

    if (fs.isFile(MCPFilenames.methodsCSV)) {
      int version;

      try (Reader r = fs.newReader(MCPFilenames.methodsCSV)) {
        version = detectMembersCSVVersion(r);
      }

      try (Reader r = fs.newReader(MCPFilenames.methodsCSV)) {
        if (version == 6) {
          methods = SeargeMCPMapping.readMCP6MemberCSV(r);
        } else {
          methods = SeargeMCPMapping.readMCP2MethodCSV(r);
        }
      }
    }

    if (fs.isFile(MCPFilenames.paramsCSV)) {
      try (Reader r = fs.newReader(MCPFilenames.paramsCSV)) {
        params = SeargeMCPMapping.readMCP6ParamCSV(r);
      }
    }

    return new SeargeMCPMapping(fields, methods, params);
  }

  public Config readConfig(Side side) throws IOException {
    NotchSeargeMapping ns = readNotchSeargeMapping(side);
    SeargeExceptor e = readSeargeExceptor(side);
    SeargeMCPMapping sm;

    if (notchSeargeMCP != null) {
      sm = SeargeMCPMapping.from(notchSeargeMCP);
      notchSeargeMCP = null;
    } else {
      sm = readSeargeMCPMapping();
    }

    return new Config(ns, e, sm);
  }
}
