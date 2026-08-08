# Minecraft 26.2 port notes

This branch retains the original Architectury multi-loader layout. The `enabled_platforms` property controls which loader projects Gradle includes; for Minecraft 26.2 it is deliberately set to `fabric` because Forge has no corresponding target in this port.

## Build

Minecraft 26.2 requires Java 25. Run:

```text
./gradlew.bat :fabric:build --no-daemon
```

## Mapping limitation

As of this port, Minecraft 26.2 has no published official Mojang mappings. `libs/identity-26.2.jar` and the local `libs/maven` intermediary artifacts are identity Tiny v2 mappings generated from the merged 26.2 client jar. They only make the named Minecraft classes usable at compile time.

Therefore a successful build is not a runtime compatibility guarantee. The Fabric jar must still be client-launch smoke-tested, especially the renderer mixins.
