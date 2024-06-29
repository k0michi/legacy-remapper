package com.koyomiji.legacy_remapper;

public class UsageException extends RuntimeException {
  public UsageException() {}

  public UsageException(String message) { super(message); }

  public UsageException(String message, Throwable cause) {
    super(message, cause);
  }

  public UsageException(Throwable cause) { super(cause); }
}
