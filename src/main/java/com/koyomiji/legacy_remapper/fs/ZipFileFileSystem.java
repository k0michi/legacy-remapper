package com.koyomiji.legacy_remapper.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;

public class ZipFileFileSystem implements IReadOnlyFileSystem {
  private final ZipFile file;
  private final String root;

  public ZipFileFileSystem(ZipFile file) { this(file, "."); }

  public ZipFileFileSystem(ZipFile file, String root) {
    this.file = file;
    this.root = root;
  }

  private String getCanonicalPath(String path) {
    return FilenameUtils.concat("/", path);
  }

  public boolean exists(String path) {
    return file.getEntry(FilenameUtils.concat(root, path)) != null;
  }

  public boolean isFile(String path) {
    ZipEntry e = file.getEntry(FilenameUtils.concat(root, path));

    if (e == null) {
      return false;
    }

    return !e.isDirectory();
  }

  public boolean isDirectory(String path) {
    String concat = FilenameUtils.concat(root, path);

    if (path.equals(concat)) {
      return true;
    }

    ZipEntry e = file.getEntry(concat);

    if (e == null) {
      return false;
    }

    return e.isDirectory();
  }

  public InputStream newInputStream(String path) throws IOException {
    ZipEntry e = file.getEntry(FilenameUtils.concat(root, path));

    if (e == null) {
      return null;
    }

    return file.getInputStream(e);
  }

  @Override
  public void close() throws IOException {
    file.close();
    IReadOnlyFileSystem.super.close();
  }

  public CloseableIterator<String> walk(String start) throws IOException {
    return new Walker(start);
  }

  private class Walker implements CloseableIterator<String> {
    private final Iterator<ZipEntry> iterator;
    private String stored;
    private final String start;

    public Walker(String start) {
      this.start = start;
      iterator = (Iterator<ZipEntry>)file.entries().asIterator();
      stored = "";
    }

    private void skip() {
      while (iterator.hasNext()) {
        if (FilenameUtils.directoryContains(
                getCanonicalPath(FilenameUtils.concat(root, start)),
                getCanonicalPath(stored = iterator.next().getName()))) {
          return;
        }
      }

      stored = null;
    }

    @Override
    public boolean hasNext() {
      return stored != null;
    }

    @Override
    public String next() {
      String entry = stored;
      stored = null;
      skip();
      return entry;
    }
  }
}
