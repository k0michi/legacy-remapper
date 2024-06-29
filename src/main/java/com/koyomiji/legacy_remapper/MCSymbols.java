package com.koyomiji.legacy_remapper;

import java.util.List;

public class MCSymbols {
  public static final List<String> clientMains =
      List.of("net/minecraft/client/Minecraft", // - MC1.5.2
              "net/minecraft/client/main/Main"  // MC1.6.1 -
      );

  public static final List<String> serverMains =
      List.of("net/minecraft/server/MinecraftServer");

  public static final String rootPackage = "net/minecraft/";
}
