[![Dark Caverns Mod Trailer](https://img.youtube.com/vi/Z3q_B4iXvOw/maxresdefault.jpg)](https://www.youtube.com/watch?v=Z3q_B4iXvOw)

[![CurseForge](https://img.shields.io/badge/CurseForge-Dark%20Caverns-f16436?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/dark-caverns) [![Wiki](https://img.shields.io/badge/Documentation-Official%20Wiki-blue?logo=github)](https://github.com/freeeranger/dark-caverns/wiki) [![YouTube Trailer](https://img.shields.io/badge/YouTube-Mod%20Trailer-red?logo=youtube)](https://www.youtube.com/watch?v=Z3q_B4iXvOw) [![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)](https://www.curseforge.com/minecraft/mc-mods/dark-caverns) [![NeoForge](https://img.shields.io/badge/Modloader-NeoForge-orange)](https://neoforged.net/)

Dark Caverns is a Minecraft NeoForge mod for 1.21.1 that adds a cave dimension below bedrock. The dimension has huge open caverns at the top and deep cave systems underneath, with three biomes, custom mobs, and upgradeable gear tiers.

---

## How to get there

Getting to the dark caverns takes a few steps:

1. Buy a Forgotten Tower map from an expert-level cartographer villager.
2. Follow the map to the tower in a forest biome, clear out the vindicators and evoker inside, and grab the key.
3. Dig down to bedrock in the overworld until you find cracked bedrock.
4. Right-click the cracked bedrock with the key to open the gateway.

The gateway creates a small landing platform on the Caverns side, so the first trip no longer drops you into the open cavern.

---

## Biomes

| Biome | Description | Mobs and features |
| :--- | :--- | :--- |
| Rocky Caverns | Stone caves made of carfstone and stalagmites. Generates overworld ores, luminite, and rare platinum. | Luminite golems, luminite foxes, camorocks, territory markers |
| Molten Depths | Volcanic biome with molten carfstone and magma. Generates hellstone on the surface. | Scorchlings, scorchhounds, molteners, blazes, sacred torches |
| Glimmershroom Forest | Glowing mushroom biome covered in glimmergrass. The safest place to build a base. | Shroomies, shroomlings, shroomie houses, giant mushrooms |

---

## Gear and upgrades

Mine platinum ore for platinum pieces, then combine four pieces with four iron ingots to make platinum ingots. Silk Touch ore can still be smelted into a piece. Platinum upgrades diamond gear in a smithing table and sits between diamond and netherite in stats. Cavern gear upgrades use luminite dust as the smithing catalyst; only the Netherite branch requires a Netherite Upgrade Smithing Template.

You can upgrade platinum gear further at a smithing table:

- **Hellstone.** Crafted with hellstone rock and diamonds. Each armor piece gives 25% fire and lava resistance, reaching 100% with the full set.
- **Shroomstone.** Bought from shroomies with diamonds. Each piece adds Jump Boost and 25% fall damage reduction. Weapons launch enemies into the air on hit.
- **Scorchsteel armor.** Crafted from scorchling tails and iron. Standing still for one second grants invisibility, and mobs will stop targeting you until you move.

---

## Interesting items and blocks

- **Luminite lights.** Torches, lanterns, and throwable torches that emit light level 15. Throwing torches makes it easier to scout dark caverns without walking in blindly.
- **Corrupted pearl.** Thrown pearls that teleport the closest mob near you to where the pearl lands. Useful for moving villagers and livestock into pens.
- **Shroombomb.** Throwable explosive crafted from glimmershrooms and gunpowder for combat and quick mining.
- **Glimmershroom block.** Bounces entities like a slime block, but without sticking to adjacent blocks.

---

## Links

- [Official wiki](https://github.com/freeeranger/dark-caverns/wiki), full stats, recipes, and mob drops
- [CurseForge page](https://www.curseforge.com/minecraft/mc-mods/dark-caverns), downloads and changelogs
- [YouTube mod trailer](https://www.youtube.com/watch?v=Z3q_B4iXvOw), gameplay preview
- [Issue tracker](https://github.com/freeeranger/dark-caverns/issues), bug reports and feedback

---

## Development

Building Dark Caverns requires a Java 21 JDK. The Gradle wrapper downloads the remaining build dependencies.

```shell
./gradlew build
```

Run the automated in-game regression suite with:

```shell
./gradlew runGameTestServer
```

Check or apply the repository's formatting rules with:

```shell
./gradlew spotlessCheck
./gradlew spotlessApply
```
