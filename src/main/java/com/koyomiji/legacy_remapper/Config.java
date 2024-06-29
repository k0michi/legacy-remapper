package com.koyomiji.legacy_remapper;

import com.koyomiji.legacy_remapper.mapping.NotchSeargeMapping;
import com.koyomiji.legacy_remapper.mapping.SeargeExceptor;
import com.koyomiji.legacy_remapper.mapping.SeargeMCPMapping;
import java.util.Objects;

public class Config {
  public NotchSeargeMapping notchSeargeMapping;
  public SeargeExceptor seargeExceptor;
  public SeargeMCPMapping seargeMCPMapping;

  public Config(NotchSeargeMapping notchSeargeMapping,
                SeargeExceptor seargeExceptor,
                SeargeMCPMapping seargeMCPMapping) {
    this.notchSeargeMapping = notchSeargeMapping;
    this.seargeExceptor = seargeExceptor;
    this.seargeMCPMapping = seargeMCPMapping;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Config config = (Config)o;
    return Objects.equals(notchSeargeMapping, config.notchSeargeMapping) &&
        Objects.equals(seargeExceptor, config.seargeExceptor) &&
        Objects.equals(seargeMCPMapping, config.seargeMCPMapping);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notchSeargeMapping, seargeExceptor, seargeMCPMapping);
  }

  @Override
  public String toString() {
    return "Config{"
        + "notchSeargeMapping=" + notchSeargeMapping +
        ", seargeExceptor=" + seargeExceptor +
        ", seargeMCPMapping=" + seargeMCPMapping + '}';
  }
}
