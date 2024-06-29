package com.koyomiji.legacy_remapper;

import com.koyomiji.legacy_remapper.adapter.*;
import com.koyomiji.legacy_remapper.mapping.NotchSeargeMapping;
import com.koyomiji.legacy_remapper.mapping.SeargeExceptor;
import com.koyomiji.legacy_remapper.mapping.SeargeMCPMapping;
import com.koyomiji.legacy_remapper.remapper.*;
import org.objectweb.asm.ClassVisitor;

public class PipelineBuilder {
  private MappingType mappingFrom;
  private MappingType mappingTo;
  private ClassIndex notchClassIndexIn;
  private ClassIndex seargeClassIndexIn;
  private ClassIndex mcpClassIndexIn;
  private ClassIndex officialClassIndexIn;
  private ClassIndex notchClassIndexOut;
  private ClassIndex seargeClassIndexOut;
  private ClassIndex mcpClassIndexOut;
  private ClassIndex officialClassIndexOut;
  private NotchSeargeRemapper notchSeargeRemapper;
  private SeargeMCPRemapper seargeMCPRemapper;
  private SeargeExceptorAccessor seargeExceptorAccessor;
  private SeargeExceptorGlobal seargeExceptorGlobal;
  private MCPSeargeRemapper mcpSeargeRemapper;
  private SeargeNotchRemapper seargeNotchRemapper;
  private Side side;
  private NotchSeargeMapping notchSeargeMapping;
  private SeargeMCPMapping seargeMCPMapping;
  private SeargeExceptor seargeExceptor;

  public PipelineBuilder() {}

  public PipelineBuilder mapFrom(MappingType mappingFrom) {
    this.mappingFrom = mappingFrom;
    return this;
  }

  public PipelineBuilder mapTo(MappingType mappingTo) {
    this.mappingTo = mappingTo;
    return this;
  }

  public PipelineBuilder setNotchClassIndexIn(ClassIndex notchClassIndexIn) {
    this.notchClassIndexIn = notchClassIndexIn;
    return this;
  }

  public PipelineBuilder setSeargeClassIndexIn(ClassIndex seargeClassIndexIn) {
    this.seargeClassIndexIn = seargeClassIndexIn;
    return this;
  }

  public PipelineBuilder setMcpClassIndexIn(ClassIndex mcpClassIndexIn) {
    this.mcpClassIndexIn = mcpClassIndexIn;
    return this;
  }

  public PipelineBuilder
  setOfficialClassIndexIn(ClassIndex officialClassIndexIn) {
    this.officialClassIndexIn = officialClassIndexIn;
    return this;
  }

  public PipelineBuilder setNotchClassIndexOut(ClassIndex notchClassIndexOut) {
    this.notchClassIndexOut = notchClassIndexOut;
    return this;
  }

  public PipelineBuilder
  setSeargeClassIndexOut(ClassIndex seargeClassIndexOut) {
    this.seargeClassIndexOut = seargeClassIndexOut;
    return this;
  }

  public PipelineBuilder setMcpClassIndexOut(ClassIndex mcpClassIndexOut) {
    this.mcpClassIndexOut = mcpClassIndexOut;
    return this;
  }

  public PipelineBuilder
  setOfficialClassIndexOut(ClassIndex officialClassIndexOut) {
    this.officialClassIndexOut = officialClassIndexOut;
    return this;
  }

  public PipelineBuilder setSide(Side side) {
    this.side = side;
    return this;
  }

  public PipelineBuilder setNotchSeargeRemapper(NotchSeargeRemapper remapper) {
    this.notchSeargeRemapper = remapper;
    return this;
  }

  public PipelineBuilder setNotchSeargeMapping(NotchSeargeMapping mapping) {
    this.notchSeargeMapping = mapping;
    return this;
  }

  public PipelineBuilder setSeargeMCPRemapper(SeargeMCPRemapper remapper) {
    this.seargeMCPRemapper = remapper;
    return this;
  }

  public PipelineBuilder setSeargeMCPMapping(SeargeMCPMapping mapping) {
    this.seargeMCPMapping = mapping;
    return this;
  }

  public PipelineBuilder
  setSeargeExceptorAccessor(SeargeExceptorAccessor accessor) {
    this.seargeExceptorAccessor = accessor;
    return this;
  }

  public PipelineBuilder setSeargeExceptor(SeargeExceptor exceptor) {
    this.seargeExceptor = exceptor;
    return this;
  }

  public PipelineBuilder
  setSeargeExceptorGlobal(SeargeExceptorGlobal exceptorGlobal) {
    this.seargeExceptorGlobal = exceptorGlobal;
    return this;
  }

  public PipelineBuilder setMcpSeargeRemapper(MCPSeargeRemapper remapper) {
    this.mcpSeargeRemapper = remapper;
    return this;
  }

  public PipelineBuilder setSeargeNotchRemapper(SeargeNotchRemapper remapper) {
    this.seargeNotchRemapper = remapper;
    return this;
  }

  public PipelineBuilder setFromConfig(Config config) {
    if (config.notchSeargeMapping != null) {
      this.notchSeargeMapping = config.notchSeargeMapping;
    }

    if (config.seargeExceptor != null) {
      this.seargeExceptor = config.seargeExceptor;
    }

    if (config.seargeMCPMapping != null) {
      this.seargeMCPMapping = config.seargeMCPMapping;
    }

    return this;
  }

  private SeargeExceptorAccessor getSeargeExceptorAccessor() {
    if (seargeExceptorAccessor == null && seargeExceptor != null &&
        side != null) {
      seargeExceptorAccessor = new SeargeExceptorAccessor(seargeExceptor, side);
    }

    return seargeExceptorAccessor;
  }

  private SeargeExceptorGlobal getSeargeExceptorGlobal() {
    if (seargeExceptorGlobal == null) {
      seargeExceptorGlobal = new SeargeExceptorGlobal();
    }

    return seargeExceptorGlobal;
  }

  private NotchSeargeRemapper getNotchSeargeRemapper() {
    if (notchSeargeRemapper == null && notchSeargeMapping != null &&
        notchClassIndexIn != null && side != null) {
      notchSeargeRemapper =
          new NotchSeargeRemapper(notchSeargeMapping, notchClassIndexIn, side);
    }

    return notchSeargeRemapper;
  }

  private SeargeMCPRemapper getSeargeMCPRemapper() {
    if (seargeMCPRemapper == null && seargeMCPMapping != null && side != null) {
      seargeMCPRemapper = new SeargeMCPRemapper(seargeMCPMapping, side);
    }

    return seargeMCPRemapper;
  }

  private MCPSeargeRemapper getMCPSeargeRemapper() {
    if (mcpSeargeRemapper == null && seargeMCPMapping != null &&
        seargeClassIndexIn != null && side != null) {
      mcpSeargeRemapper =
          new MCPSeargeRemapper(seargeMCPMapping, seargeClassIndexIn, side);
    }

    return mcpSeargeRemapper;
  }

  private SeargeNotchRemapper getSeargeNotchRemapper() {
    if (seargeNotchRemapper == null && notchSeargeMapping != null &&
        side != null) {
      seargeNotchRemapper = new SeargeNotchRemapper(notchSeargeMapping, side);
    }

    return seargeNotchRemapper;
  }

  private ClassVisitor buildNotchSearge(ClassVisitor next) {
    if (seargeClassIndexOut != null) {
      next = new ClassIndexVisitor(next, seargeClassIndexOut);
    }

    if (getSeargeExceptorAccessor() != null) {
      next = new SeargeExceptorApplier(next, getSeargeExceptorAccessor(),
                                       getSeargeExceptorGlobal());
    }

    return new NotchSeargeClassRemaper(next, getNotchSeargeRemapper());
  }

  private ClassVisitor buildMCPSearge(ClassVisitor next) {
    return new MCPSeargeClassRemapper(next, getMCPSeargeRemapper());
  }

  private ClassVisitor buildSeargeMCP(ClassVisitor next) {
    return new SeargeMCPClassRemapper(next, getSeargeMCPRemapper());
  }

  private ClassVisitor buildSeargeNotch(ClassVisitor next) {
    return new SeargeNotchClassRemapper(next, getSeargeNotchRemapper());
  }

  public Pipeline build(ClassVisitor next) {
    if (side == null || mappingTo == null || mappingFrom == null) {
      throw new IllegalStateException("Incomplete pipeline builder");
    }

    if (mappingTo == MappingType.MCP) {
      next = buildSeargeMCP(next);

      if (mappingFrom == MappingType.NOTCH) {
        next = buildNotchSearge(next);
        return new Pipeline(next);
      } else if (mappingFrom == MappingType.SEARGE) {
        return new Pipeline(next);
      }
    } else if (mappingTo == MappingType.SEARGE) {
      if (mappingFrom == MappingType.NOTCH) {
        next = buildNotchSearge(next);
        return new Pipeline(next);
      } else if (mappingFrom == MappingType.MCP) {
        next = buildMCPSearge(next);
        return new Pipeline(next);
      }
    } else if (mappingTo == MappingType.NOTCH) {
      next = buildMCPSearge(next);

      if (mappingFrom == MappingType.MCP) {
        next = buildSeargeNotch(next);
        return new Pipeline(next);
      } else if (mappingFrom == MappingType.SEARGE) {
        return new Pipeline(next);
      }
    }

    throw new RuntimeException("Unsupported remapping");
  }
}
