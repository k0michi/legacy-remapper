package com.koyomiji.legacy_remapper;

public enum Side {
  CLIENT(0, 1),
  SERVER(1, 2),
  BOTH(2, 3),
  ;

  private final int id;
  private final int flags;

  Side(int id, int flags) {
    this.id = id;
    this.flags = flags;
  }

  public int getID() { return this.id; }

  public int getFlags() { return this.flags; }

  public static Side fromID(int id) {
    switch (id) {
    case 0:
      return Side.CLIENT;
    case 1:
      return Side.SERVER;
    case 2:
      return Side.BOTH;
    default:
      throw new IllegalArgumentException("Invalid side ID");
    }
  }

  public boolean includes(Side other) {
    return (getFlags() & other.getFlags()) == other.getFlags();
  }

  public boolean includesClient() { return includes(Side.CLIENT); }

  public boolean includesServer() { return includes(Side.SERVER); }

  public Side opposite() {
    if (this == Side.CLIENT) {
      return Side.SERVER;
    }

    if (this == Side.SERVER) {
      return Side.CLIENT;
    }

    throw new RuntimeException("opposite() not supported on Side.BOTH");
  }

  public static Side fromID(String id) { return fromID(Integer.parseInt(id)); }

  public static Side fromFlags(int flags) {
    switch (flags) {
    case 1:
      return Side.CLIENT;
    case 2:
      return Side.SERVER;
    case 3:
      return Side.BOTH;
    default:
      throw new IllegalArgumentException("Invalid side flags");
    }
  }
}
