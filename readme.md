# Dark Caverns

[![Website](https://img.shields.io/badge/Website-Dark%20Caverns-4b69bd?logo=githubpages)](https://freeeranger.github.io/dark-caverns/) [![Download](https://img.shields.io/badge/Download-Mod-55ffaf)](https://freeeranger.github.io/dark-caverns/download/) [![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)](https://freeeranger.github.io/dark-caverns/download/) [![NeoForge](https://img.shields.io/badge/Modloader-NeoForge-orange)](https://neoforged.net/)

[![Dark Caverns mod trailer](https://img.youtube.com/vi/Z3q_B4iXvOw/maxresdefault.jpg)](https://www.youtube.com/watch?v=Z3q_B4iXvOw)

Dark Caverns is a Minecraft NeoForge mod for 1.21.1. It adds a 256-block-tall cave dimension beneath the Overworld bedrock. Large vaults connect through smaller passages and shelves. Vertical rifts cross several elevations. The dimension has four biomes: Rocky Caverns, Molten Depths, Glimmershroom Forest, and Tangled Hallow.

Players enter by finding a Forgotten Tower, taking its key, and opening a gateway through Cracked Bedrock. The gateway creates a linked entrance on a cave floor and provides a route back to the Overworld.

Players can make Platinum tools and armor, then upgrade Platinum equipment with Hellstone or Shroomstone. Scorchsteel armor conceals stationary players from hostile mobs. Luminite powers the Cavern Compass and several light sources.

## Installation

Dark Caverns requires Minecraft 1.21.1, NeoForge, and GeckoLib. The [download page](https://freeeranger.github.io/dark-caverns/download/) links to the current Modrinth and CurseForge releases.

## Links

- [Official website](https://freeeranger.github.io/dark-caverns/) for guides, recipes, equipment stats, and mob drops
- [Downloads](https://freeeranger.github.io/dark-caverns/download/)
- [Modrinth](https://modrinth.com/mod/dark-caverns)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/dark-caverns)
- [Issue tracker](https://github.com/freeeranger/dark-caverns/issues) for bug reports and feedback

## Development

Building Dark Caverns requires Java 21. The Gradle wrapper downloads the other build dependencies.

```shell
./gradlew build
```

Run the in-game test suite with:

```shell
./gradlew runGameTestServer
```

Check or apply the repository formatting rules with:

```shell
./gradlew spotlessCheck
./gradlew spotlessApply
```
