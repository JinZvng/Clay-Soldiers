# Clay Soldiers — NeoForge 26.3

Port of Slainlite / matthewperiut's Clay Soldiers, retaining the original MIT license.

## Requirements

- Minecraft Java 26.3
- NeoForge 26.3.0.101 or a compatible later build for Minecraft 26.3
- GeckoLib 5.5.7 for NeoForge 26.3
- Architectury API 21.0.5 for NeoForge
- Java JDK 25 to build

Install all three mod JARs (Clay Soldiers, GeckoLib and Architectury) in the same instance's `mods` directory.
Use a new test world before opening existing saves; compatibility with old Forge saves is not established.

## Build in Codespaces

```bash
git switch codex/neoforge-26.3
git pull --ff-only origin codex/neoforge-26.3
bash ./gradlew clean build
```

The mod JAR is written to `build/libs/`. The GitHub Actions workflow also provides a `clay-neoforge-26.3` artifact after its checks pass.

The active Java sources are in `src/main/java`. The historical Fabric and NeoForge source trees are retained for reference and are not part of this build. Resources are converted from `common/src/main/resources` by `tools/prepare_resources_26_3.py` during the build.

## In-game validation

After installing the mod and dependencies, test soldier colors, combat between teams, horses and dispensers. Drop stick, flint, leather, wool and glowstone upgrades near soldiers; verify durability, rendering and persistence after saving and reopening the world. Check all three disruptors. A successful build or client-startup check alone does not validate those interactions.

---

## Original project notes

# Clay Soldiers Reborn

Porting Clay Soldiers from b1.7.3/1.7.10/1.12 to 1.21+

# Upgrades

| Upgrade Item | Description                                 | Requires | Durability |
|:------------:|---------------------------------------------|:--------:|:----------:|
|    Stick     | Increases Attack damage by 2                |   ---    |     20     |
|    Flint     | Increases Attack damage by 1                |  Stick   |    ---     |
|  Glowstone   | Makes the Soldier glow in the dark          |   ---    |    ---     |
|   Leather    | Gives a leather armor / 3 Armor points      |   ---    |     20     |
|     Wool     | Upgrades the leather Armor / 2 Armor points | Leather  |    ---     |
(More Upgrades coming)

# Mounts
- Various Horse
- (more coming)

# Contributions

Special Thanks to @TrustNoOneElse for Upgrade Interface and related code