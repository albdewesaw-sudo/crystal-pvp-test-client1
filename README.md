# Crystal PvP Test Client (Fabric)

A passive client-side diagnostic mod scaffold for Minecraft Java 1.21.11. It records local observations when the player presses use while holding an End Crystal and aiming at a block, and when the player attacks while targeting an End Crystal.

## Important limits

- This logs **input attempts**, not server-confirmed placements, hits, damage, or kills.
- Client-side logs cannot prove another player's cheating and should not be used as sole ban evidence.
- No automated combat, packet manipulation, or anti-cheat bypass is included.
- The project has not been compiled or runtime-tested in this environment. Dependency versions/mappings may need adjustment before a build succeeds.

## Build

Requires JDK 21 and internet access to download Gradle/Fabric dependencies. Open this folder in a Gradle-capable environment and run:

```sh
gradle build
```

The mod jar, if the build succeeds, will be in `build/libs/`.

## Install on Android / Mojo Launcher

1. Build the jar in a Gradle-capable environment first.
2. Ensure the launcher instance uses Minecraft Java 1.21.11 with Fabric Loader and Fabric API.
3. Copy the built `crystal-pvp-test-client-1.0.0.jar` into that instance's `mods` folder.
4. Launch the game and test only in a world/server where you have permission.

## Log location

`config/crystal-test-client/events.log` inside the game directory.
