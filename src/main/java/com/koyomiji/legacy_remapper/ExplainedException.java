package com.koyomiji.legacy_remapper;

public class ExplainedException extends RuntimeException {
  public ExplainedException(String message) { super(message); }

  public ExplainedException(String message, Throwable cause) {
    super(message, cause);
  }
}
