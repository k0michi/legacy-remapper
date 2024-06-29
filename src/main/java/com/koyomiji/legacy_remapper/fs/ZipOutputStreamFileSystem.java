package com.koyomiji.legacy_remapper.fs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipOutputStreamFileSystem implements IWriteOnlyFileSystem {
  private final ZipOutputStream stream;
  private final Set<String> added = new HashSet<>();
  private CustomOutputStream lastStream;

  public ZipOutputStreamFileSystem(ZipOutputStream stream) {
    this.stream = stream;
  }

  /*
   * NOTE: Do not open multiple streams at once.
   */
  @Override
  public OutputStream newOutputStream(String path) throws IOException {
    if (lastStream != null && !lastStream.isClosed()) {
      throw new IOException("Previous stream must be closed");
    }

    ZipEntry entry = new ZipEntry(path);
    stream.putNextEntry(entry);
    added.add(path);
    return lastStream = new CustomOutputStream();
  }

  @Override
  public String createDirectory(String path) throws IOException {
    if (lastStream != null && !lastStream.isClosed()) {
      throw new IOException("Previous stream must be closed");
    }

    if (!path.endsWith("/")) {
      path = path + "/";
    }

    if (added.contains(path)) {
      return path;
    }

    ZipEntry entry = new ZipEntry(path);
    stream.putNextEntry(entry);
    added.add(path);
    stream.closeEntry();
    return path;
  }

  @Override
  public String createDirectories(String path) throws IOException {
    if (!path.endsWith("/")) {
      path = path + "/";
    }

    int i = path.indexOf('/');

    for (; i != -1; i = path.indexOf('/', i + 1)) {
      createDirectory(path.substring(0, i + 1));
    }

    return path;
  }

  @Override
  public void close() throws IOException {
    stream.close();
    IWriteOnlyFileSystem.super.close();
  }

  private class CustomOutputStream extends OutputStream {
    private boolean closed = false;

    @Override
    public void write(int b) throws IOException {
      stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
      stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      stream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
      stream.flush();
    }

    @Override
    public void close() throws IOException {
      stream.closeEntry();
      closed = true;
    }

    public boolean isClosed() { return closed; }
  }
}
