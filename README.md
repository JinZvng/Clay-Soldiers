# Clay Soldiers — NeoForge 26.3

Port of Slainlite / matthewperiut's Clay Soldiers, retaining the original MIT license.

## Requirements

- Minecraft Java 26.3
- NeoForge 26.3.0.101 or a compatible later build for Minecraft 26.3
- GeckoLib 5.5.7 for NeoForge 26.3
- Java JDK 25 to build

Install both mod JARs (Clay Soldiers and GeckoLib) in the same instance's `mods` directory.
Use a new test world before opening existing saves; compatibility with old Forge saves is not established.

## Build in Codespaces

```bash
git switch codex/neoforge-26.3
git pull --ff-only origin codex/neoforge-26.3
bash ./gradlew clean build
```

The mod JAR is written to `build/libs/`. The GitHub Actions workflow also provides a `clay-neoforge-26.3` artifact after compilation succeeds. A separate virtual-display step checks client startup; review that result before treating the artifact as startup-validated.

The active Java sources are in `src/main/java`. The historical Fabric and NeoForge source trees are retained for reference and are not part of this build. Resources are converted from `common/src/main/resources` by `tools/prepare_resources_26_3.py` during the build.

## In-game validation

CI also creates a temporary flat world, renders a mounted soldier and horse, exercises client death callbacks, and verifies server-side mount removal and a single upgrade drop. This automation is opt-in through the development-only `clayGameplaySmoke` Gradle property.

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
## Toy commands (1.2.0)
Craft the Clay Whistle with copper around an iron nugget (top, left and right). Sneak-right-click a soldier to select its color. Use opens the Follow / Attack / Stay / Release menu; clicking a button sends that order to the selected color within 32 blocks. Attack aims at the nearest visible mob along the crosshair (not players or the selected color). A brief glow marks the target. Orders persist; attack changes to stay after reload.
Soldiers can mount a free allay within 1.5 blocks. Follow and attack orders pilot the allay; the allay survives its rider's death. No teleporting or chunk loading is used.
Drop feathers (fall immunity), bows (arrows every 35 ticks), shields (frontal block every 40 ticks), slime balls (brief slowness), copper ingots (30% damage reduction), or gunpowder (small death explosion without block damage). Bow conflicts with stick, copper with leather; feather/slime/gunpowder occupy one special slot. Existing upgrades remain supported. Bow, shield and copper badge appear on the soldier.
