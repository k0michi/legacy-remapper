# LegacyRemapper

An all-in-one, portable Minecraft deobfuscator/reobfuscator for Alpha 1.1.2 to 1.12.2, built on top of MCP ecosystem. Aiming to be compatible with every release of MCP.

## Usage example

You need to obtain mappings from either [MCP releases](https://minecraft.wiki/w/Tutorials/Programs_and_editors/Mod_Coder_Pack) or [MCPMappingsArchive](https://github.com/ModCoderPack/MCPMappingsArchive).

### Deobfuscation

Using MCP release:

```
$ java -jar LegacyRemapper.jar --from=notch --to=mcp --config=mcp940.zip/conf 1.12client.jar
```

Using MCPBot snapshot:

```
$ java -jar LegacyRemapper.jar --from=notch --to=mcp --config=mcp_snapshot-20180814-1.12.zip --config=mcp-1.12-srg.zip 1.12client.jar
```

### Reobfuscation

Feed the index generated in deobfuscation.

```
$ java -jar LegacyRemapper.jar --from=mcp --to=notch --config=mcp940.zip/conf --index-in=1.12client.index.json 1.12client.mcp.jar
```

## License

Apache License 2.0