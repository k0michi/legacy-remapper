package com.koyomiji.legacy_remapper.fs;

import java.io.*;

public interface IReadOnlyFileSystem extends Closeable {
  boolean exists(String path);
  boolean isFile(String path);
  boolean isDirectory(String path);
  InputStream newInputStream(String path) throws IOException;

  default Reader newReader(String path) throws IOException {
    return new InputStreamReader(newInputStream(path));
  }

  CloseableIterator<String> walk(String start) throws Exception;

  @Override
  default void close() throws IOException {}
}
