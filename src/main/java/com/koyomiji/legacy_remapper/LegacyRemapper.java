package com.koyomiji.legacy_remapper;

import com.koyomiji.legacy_remapper.adapter.ClassIndexVisitor;
import com.koyomiji.legacy_remapper.adapter.ClassInfoExtractor;
import com.koyomiji.legacy_remapper.fs.*;
import com.koyomiji.legacy_remapper.tuple.Pair;
import com.koyomiji.legacy_remapper.util.ClassUtils;
import com.koyomiji.legacy_remapper.util.PathUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class LegacyRemapper {
  static CommandLineParser parser = new DefaultParser();
  static HelpFormatter formatter = new HelpFormatter();
  static Options options = new Options();

  static Path jarPath;
  static JarFile jarFile;
  static ZipFileFileSystem jarFileSystem;
  static MappingType mappingFrom;
  static MappingType mappingTo;
  static List<Path> configPaths = new ArrayList<>();
  static IReadOnlyFileSystem configFileSystem;
  static Path indexInPath;
  static Path outPath;
  static Path indexOutPath;

  static Side side;
  static Config config;
  static PipelineBuilder pipelineBuilder = new PipelineBuilder();
  static ClassIndex indexIn;
  static ClassIndex indexOut;
  static boolean verbose;

  static void printHelp() {
    formatter.printHelp("LegacyRemapper [JAR_FILE]", options);
  }

  private static Path pathOf(String string) {
    if (string != null) {
      return Path.of(string);
    }

    return null;
  }

  private static ArrayList<Path> pathsOf(String[] strings) {
    ArrayList<Path> paths = new ArrayList<>();

    for (String string : strings) {
      paths.add(Path.of(string));
    }

    return paths;
  }

  // path/to/archive.zip/conf -> (path/to/archive.zip, conf)
  private static Optional<Pair<Path, String>> splitArchivePath(Path path) {
    path = path.toAbsolutePath();
    Path zipPath = path;

    while (!Files.exists(zipPath)) {
      zipPath = zipPath.getParent();
    }

    if (!Files.isRegularFile(zipPath)) {
      return Optional.empty();
    }

    Path insidePath = zipPath.relativize(path);
    return Optional.of(new Pair<>(
        zipPath, FilenameUtils.separatorsToUnix(insidePath.toString())));
  }

  private static void parseCommandLine(String[] args) {
    options.addOption(
        Option.builder("h").longOpt("help").desc("Show help").build());
    options.addOption(
        Option.builder("c")
            .longOpt("config")
            .argName("path")
            .hasArg()
            .desc("Path of MCP Config archive or directory to use")
            .build());
    options.addOption(
        Option.builder("f")
            .longOpt("from")
            .argName("mapping")
            .required()
            .hasArg()
            .desc("Current mapping. Possible values: notch, searge, mcp")
            .build());
    options.addOption(
        Option.builder("t")
            .longOpt("to")
            .argName("mapping")
            .required()
            .hasArg()
            .desc("Target of remapping. Possible values: notch, searge, mcp")
            .build());
    options.addOption(Option.builder("i")
                          .longOpt("index-in")
                          .argName("path")
                          .hasArg()
                          .desc("Path of class index to use")
                          .build());
    options.addOption(Option.builder("o")
                          .longOpt("out")
                          .argName("path")
                          .hasArg()
                          .desc("Destination path of remapped jar")
                          .build());
    options.addOption(Option.builder("j")
                          .longOpt("index-out")
                          .argName("path")
                          .hasArg()
                          .desc("Destination path of class index")
                          .build());
    options.addOption(Option.builder("v")
                          .longOpt("verbose")
                          .desc("Enable verbose logging")
                          .build());
    CommandLine line;

    try {
      line = parser.parse(options, args);
    } catch (ParseException exp) {
      throw new UsageException();
    }

    if (line.hasOption("help")) {
      throw new UsageException();
    }

    String[] jarPaths = line.getArgs();

    if (jarPaths.length != 1) {
      throw new UsageException();
    }

    jarPath = Path.of(jarPaths[0]);

    try {
      jarFile = new JarFile(jarPath.toFile());
    } catch (IOException e) {
      throw new ExplainedException(
          String.format("Failed to open %s\n", jarPath));
    }

    jarFileSystem = new ZipFileFileSystem(jarFile);

    try {
      mappingFrom =
          MappingType.valueOf(line.getOptionValue("from").toUpperCase());
      mappingTo = MappingType.valueOf(line.getOptionValue("to").toUpperCase());
    } catch (RuntimeException e) {
      throw new UsageException();
    }

    if (mappingFrom.equals(mappingTo)) {
      throw new UsageException();
    }

    configPaths = pathsOf(line.getOptionValues("config"));
    indexInPath = pathOf(line.getOptionValue("index-in"));
    indexOutPath = pathOf(line.getOptionValue("index-out"));
    verbose = line.hasOption("verbose");

    String outPathString = line.getOptionValue("out");

    if (outPathString != null) {
      outPath = Path.of(outPathString);
    } else {
      outPath = jarPath.resolveSibling(PathUtils.appendBeforeExtension(
          jarPath.getFileName().toString(),
          "." + mappingTo.name().toLowerCase()));
    }

    if (mappingFrom == MappingType.NOTCH) {
      pipelineBuilder.setSeargeClassIndexOut(indexOut = new ClassIndex());

      if (indexOutPath == null) {
        indexOutPath = outPath.resolveSibling(PathUtils.changeExtension(
            jarPath.getFileName().toString(), ".index.json"));
      }
    }
  }

  private static void prepareFileSystem() {
    List<IReadOnlyFileSystem> fileSystems = new ArrayList<>();

    for (Path p : configPaths) {
      if (Files.exists(p)) {
        if (Files.isDirectory(p)) {
          fileSystems.add(new RealFileSystem(p));
        } else {
          ZipFile zipFile = null;

          try {
            zipFile = new ZipFile(p.toFile());
          } catch (IOException ignored) {
          }

          if (zipFile != null) {
            fileSystems.add(new ZipFileFileSystem(zipFile));
          } else {
            fileSystems.add(new SingleFileFileSystem(p));
          }
        }
      } else {
        Optional<Pair<Path, String>> split = splitArchivePath(p);

        if (split.isEmpty()) {
          throw new ExplainedException(String.format("Could not find %s\n", p));
        }

        ZipFile zipFile = null;

        try {
          zipFile = new ZipFile(split.get().first.toFile());
        } catch (IOException e) {
          throw new ExplainedException(String.format("Failed to open %s\n", p));
        }

        fileSystems.add(new ZipFileFileSystem(zipFile, split.get().second));
      }
    }

    configFileSystem = new JointReadOnlyFileSystem(fileSystems);
  }

  private static void closeFileSystem() throws IOException {
    configFileSystem.close();
  }

  public static void main(String[] args) throws Exception {
    try {
      parseCommandLine(args);
      prepareFileSystem();

      MCSideDetector detector = new MCSideDetector(jarFile);
      side = detector.detect();
      ConfigReader configReader = new ConfigReader(configFileSystem);
      config = configReader.readConfig(side);

      pipelineBuilder.setSide(side)
          .mapFrom(mappingFrom)
          .mapTo(mappingTo)
          .setFromConfig(config);

      if (mappingFrom == MappingType.NOTCH ||
          (mappingFrom == MappingType.SEARGE &&
           mappingTo == MappingType.NOTCH)) {
        ClassIndex indexIn = new ClassIndex();

        try (CloseableIterator<String> it = jarFileSystem.walk("")) {
          while (it.hasNext()) {
            String e = it.next();

            if (ClassUtils.isClassFile(e)) {
              ClassIndexVisitor civ = new ClassIndexVisitor(indexIn);

              try (InputStream is = jarFileSystem.newInputStream(e)) {
                ClassReader reader = new ClassReader(is);
                reader.accept(civ, 0);
              }
            }
          }
        }

        if (mappingFrom == MappingType.NOTCH) {
          pipelineBuilder.setNotchClassIndexIn(indexIn);
        } else {
          pipelineBuilder.setSeargeClassIndexIn(indexIn);
        }
      }

      if (mappingFrom == MappingType.MCP) {
        try (Reader r = Files.newBufferedReader(indexInPath)) {
          pipelineBuilder.setSeargeClassIndexIn(ClassIndex.readJSON(r));
        } catch (IOException e) {
          throw new ExplainedException(
              "Class index is required when remapping from MCP");
        }
      }

      Manifest newManifest = new Manifest(jarFile.getManifest());
      newManifest.getEntries().clear();

      try (
          IWriteOnlyFileSystem wofs = new ZipOutputStreamFileSystem(
              new JarOutputStream(Files.newOutputStream(outPath), newManifest));
          CloseableIterator<String> it = jarFileSystem.walk("")) {
        while (it.hasNext()) {
          String e = it.next();

          if (jarFileSystem.isFile(e)) {
            if (e.startsWith("META-INF/")) {
              continue;
            }

            if (ClassUtils.isClassFile(e)) {
              ClassWriter w = new ClassWriter(0);
              ClassInfoExtractor cne = new ClassInfoExtractor(w);
              Pipeline pipeline = pipelineBuilder.build(cne);

              if (verbose) {
                System.out.println(String.format("Remapping %s", e));
              }

              try (InputStream is = jarFileSystem.newInputStream(e)) {
                ClassReader reader = new ClassReader(is);
                reader.accept(pipeline, 0);
              }

              byte[] newBytes = w.toByteArray();
              String mappedName = cne.getClassName();
              String outPath = ClassUtils.classNameToPath(mappedName);
              String parent = PathUtils.getParentPath(outPath);

              if (parent != null) {
                wofs.createDirectories(parent);
              }

              try (OutputStream os = wofs.newOutputStream(outPath)) {
                os.write(newBytes);
              }
            } else {
              String parent = PathUtils.getParentPath(e);

              if (parent != null) {
                wofs.createDirectories(parent);
              }

              if (verbose) {
                System.out.println(String.format("Copying %s", e));
              }

              try (InputStream is = jarFileSystem.newInputStream(e);
                   OutputStream os = wofs.newOutputStream(e)) {
                os.write(is.readAllBytes());
              }
            }
          }
        }
      }

      if (indexOut != null) {
        Files.writeString(indexOutPath, indexOut.toJSON());
      }

      closeFileSystem();
    } catch (ExplainedException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    } catch (UsageException e) {
      printHelp();
      System.exit(1);
    }
  }
}
