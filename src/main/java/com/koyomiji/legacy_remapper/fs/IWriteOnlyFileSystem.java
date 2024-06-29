package com.koyomiji.legacy_remapper.fs;

import java.io.*;

public interface IWriteOnlyFileSystem extends Closeable {
  OutputStream newOutputStream(String path) throws IOException;

  default Writer newWriter(String path) throws IOException {
    return new OutputStreamWriter(newOutputStream(path));
  }

  String createDirectory(String path) throws IOException;
  String createDirectories(String path) throws IOException;

  @Override
  default void close() throws IOException {}
}
