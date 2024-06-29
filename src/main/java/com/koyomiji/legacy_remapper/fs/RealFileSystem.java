package com.koyomiji.legacy_remapper.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

public class RealFileSystem
    implements IReadOnlyFileSystem, IWriteOnlyFileSystem {
  private final Path root;

  public RealFileSystem(Path root) { this.root = root; }

  @Override
  public boolean exists(String path) {
    return Files.exists(root.resolve(path));
  }

  @Override
  public boolean isFile(String path) {
    return Files.isRegularFile(root.resolve(path));
  }

  @Override
  public boolean isDirectory(String path) {
    return Files.isDirectory(root.resolve(path));
  }

  @Override
  public InputStream newInputStream(String path) throws IOException {
    return Files.newInputStream(root.resolve(path));
  }

  public CloseableIterator<String> walk(String start) throws IOException {
    return new Walker(start);
  }

  @Override
  public void close() throws IOException {
    IReadOnlyFileSystem.super.close();
    IWriteOnlyFileSystem.super.close();
  }

  private class Walker implements CloseableIterator<String> {
    private final Stream<Path> stream;
    private final Iterator<Path> iterator;
    private final Path walkRoot;

    Walker(String start) throws IOException {
      walkRoot = root.resolve(start);
      stream = Files.walk(walkRoot);
      iterator = stream.iterator();
    }

    @Override
    public void close() throws Exception {
      stream.close();
      CloseableIterator.super.close();
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public String next() {
      return walkRoot.relativize(iterator.next()).toString();
    }
  }

  public OutputStream newOutputStream(String path) throws IOException {
    return Files.newOutputStream(Path.of(path));
  }

  public String createDirectory(String path) throws IOException {
    return Files.createDirectory(Path.of(path)).toString();
  }

  public String createDirectories(String path) throws IOException {
    return Files.createDirectories(Path.of(path)).toString();
  }
}
