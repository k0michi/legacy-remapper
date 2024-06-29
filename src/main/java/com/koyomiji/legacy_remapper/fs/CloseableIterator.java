package com.koyomiji.legacy_remapper.fs;

import java.util.Iterator;

public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
  @Override
  default void close() throws Exception {}
}
