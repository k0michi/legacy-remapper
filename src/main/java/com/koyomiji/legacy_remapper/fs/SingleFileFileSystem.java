package com.koyomiji.legacy_remapper.fs;

import com.koyomiji.legacy_remapper.util.NIOPathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class SingleFileFileSystem implements IReadOnlyFileSystem {
  private final Path filePath;

  public SingleFileFileSystem(Path filePath) {
    this.filePath = filePath.toAbsolutePath();
  }

  @Override
  public boolean exists(String path) {
    return filePath.getParent().resolve(path).equals(filePath);
  }

  @Override
  public boolean isFile(String path) {
    return filePath.getParent().resolve(path).equals(filePath) &&
        Files.isRegularFile(filePath);
  }

  @Override
  public boolean isDirectory(String path) {
    return filePath.getParent().resolve(path).equals(filePath) &&
        Files.isDirectory(filePath);
  }

  @Override
  public InputStream newInputStream(String path) throws IOException {
    if (!filePath.getParent().resolve(path).equals(filePath)) {
      return null;
    }

    return Files.newInputStream(filePath);
  }

  public CloseableIterator<String> walk(String start) throws IOException {
    return new CloseableIterator<String>() {
      boolean has = NIOPathUtils.of(start).normalize().equals(NIOPathUtils.of(""));

      @Override
      public boolean hasNext() {
        return has;
      }

      @Override
      public String next() {
        if (!has) {
          throw new NoSuchElementException();
        }

        has = false;
        return filePath.getParent().relativize(filePath).toString();
      }
    };
  }
}
