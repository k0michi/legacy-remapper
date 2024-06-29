package com.koyomiji.legacy_remapper.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.commons.io.FilenameUtils;

public class JointReadOnlyFileSystem implements IReadOnlyFileSystem {
  private final List<IReadOnlyFileSystem> fileSystems;

  public JointReadOnlyFileSystem(List<IReadOnlyFileSystem> fileSystems) {
    this.fileSystems = fileSystems;
  }

  @Override
  public boolean exists(String path) {
    for (IReadOnlyFileSystem fs : fileSystems) {
      if (fs.exists(path)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isFile(String path) {
    for (IReadOnlyFileSystem fs : fileSystems) {
      if (fs.exists(path)) {
        return fs.isFile(path);
      }
    }

    return false;
  }

  @Override
  public boolean isDirectory(String path) {
    for (IReadOnlyFileSystem fs : fileSystems) {
      if (fs.exists(path)) {
        return fs.isDirectory(path);
      }
    }

    return false;
  }

  @Override
  public InputStream newInputStream(String path) throws IOException {
    for (IReadOnlyFileSystem fs : fileSystems) {
      InputStream is = fs.newInputStream(path);

      if (is != null) {
        return is;
      }
    }

    return null;
  }

  @Override
  public void close() throws IOException {
    for (IReadOnlyFileSystem fs : fileSystems) {
      fs.close();
    }

    IReadOnlyFileSystem.super.close();
  }

  public CloseableIterator<String> walk(String start) throws Exception {
    return new Walker(start);
  }

  private class Walker implements CloseableIterator<String> {
    private final List<CloseableIterator<String>> iterators;
    private final Iterator<CloseableIterator<String>> iteratorIterator;
    private CloseableIterator<String> iterator;
    private String stored;
    private final TreeSet<String> visited =
        new TreeSet<>(new Comparator<String>() {
          @Override
          public int compare(String o1, String o2) {
            return o1.replaceAll("/", "\0/")
                .compareTo(o2.replaceAll("/", "\0/"));
          }
        });

    public Walker(String start) throws Exception {
      iterators = new ArrayList<>();

      for (IReadOnlyFileSystem fs : fileSystems) {
        iterators.add(fs.walk(start));
      }

      iteratorIterator = iterators.iterator();

      if (iteratorIterator.hasNext()) {
        iterator = iteratorIterator.next();
        skip();
      }
    }

    private boolean canVisit(String path) {
      String normalized = FilenameUtils.normalize(path, true);
      String lower = visited.lower(normalized);

      // Avoid visiting files inside a directory, if there was a file with the
      // same name as the directory
      if (lower != null && path.startsWith(visited.lower(normalized) + "/")) {
        return false;
      }

      return !visited.contains(normalized);
    }

    private void skip() {
      while (iterator.hasNext()) {
        if (canVisit(stored = iterator.next())) {
          visited.add(stored);
          return;
        }
      }

      stored = null;

      if (iteratorIterator.hasNext()) {
        iterator = iteratorIterator.next();
        skip();
      }
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

    @Override
    public void close() throws Exception {
      for (CloseableIterator<String> it : iterators) {
        it.close();
      }

      CloseableIterator.super.close();
    }
  }
}
