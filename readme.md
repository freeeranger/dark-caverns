# Dark Caverns

[![Website](https://img.shields.io/badge/Website-Dark%20Caverns-4b69bd?logo=githubpages)](https://freeeranger.github.io/dark-caverns/) [![CurseForge](https://img.shields.io/badge/CurseForge-Download-f16436?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/dark-caverns) [![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)](https://www.curseforge.com/minecraft/mc-mods/dark-caverns) [![NeoForge](https://img.shields.io/badge/Modloader-NeoForge-orange)](https://neoforged.net/)

[![Dark Caverns mod trailer](https://img.youtube.com/vi/Z3q_B4iXvOw/maxresdefault.jpg)](https://www.youtube.com/watch?v=Z3q_B4iXvOw)

Dark Caverns is a Minecraft NeoForge mod for 1.21.1. It adds a cave dimension beneath the Overworld bedrock. Its terrain contains large vaults, dense passages, shelves, and vertical rifts. It has four biomes. Custom mobs and structures appear throughout the dimension. Its materials are used for new equipment.

## How to get there

To enter Dark Caverns:

1. Buy a Forgotten Tower map from an expert-level cartographer villager.
2. Follow the map to a tower in a forest biome. Defeat the vindicators and evoker, then take the key.
3. Dig down to Overworld bedrock and find cracked bedrock.
4. Use the key on the cracked bedrock to open the gateway.

The gateway builds a lantern-lit entrance on a safe cave floor. The gateway above the entrance returns you to the Overworld. The mod saves the link between both gateways, so later visits use the same entrance without rebuilding it.

## Biomes

| Biome | Terrain and resources | Mobs and landmarks |
| :--- | :--- | :--- |
| Rocky Caverns | Carfstone caverns with stalagmites, stalactites, columns, Overworld ores, Luminite, and rare Platinum. Luminite also forms in clusters on exposed Carfstone stalactites. | Luminite golems, Luminite foxes, and camorocks |
| Molten Depths | Molten Carfstone, magma, lava flows, ash, and exposed Hellstone. | Scorchlings, scorchhounds, molteners, blazes, sacred torches, and territory markers |
| Glimmershroom Forest | Glimmergrass and giant glowing mushrooms. | Shroomies, shroomlings, and Shroomie houses |
| Tangled Hallow | Overgrown Carfstone, shallow teal lakes, and winding Twistwood trees. | Luminite foxes, camorocks, golems, and skeletons |

Biome transitions mix neighboring ground and plants instead of cutting between biomes along straight borders. A strip of rocky ground separates Glimmershroom Forest and Molten Depths at shared borders.

New chunks may include short ramps, cut-throughs, and small natural bridges between nearby walking areas. These connections avoid ores, trees, structures, and fluids. Large drops and vertical rifts remain hazardous.

Twistwood leaves drop saplings. The saplings grow without light on Overgrown Carfstone or ordinary planting soil, and bone meal works on them. Twistwood logs can be stripped or crafted into wood, planks, doors, and trapdoors. Twistwood planks work in vanilla wood recipes.

World generation changes only affect new chunks.

## Gear and upgrades

Platinum ore drops Platinum Pieces. Four Platinum Pieces and four iron ingots make one Platinum Ingot. Smelting Silk Touch ore yields one piece. Use a smithing table to upgrade diamond equipment with Platinum. Platinum equipment has stats between diamond and Netherite.

Luminite Dust is the smithing catalyst for Cavern equipment upgrades. A Netherite upgrade still requires a Netherite Upgrade Smithing Template.

- **Hellstone.** Four Hellstone Rocks and four diamonds make one Hellstone. Each armor piece reduces fire and lava damage by 25 percent. A full set prevents both types of damage. Fully charged tool attacks ignite their target.
- **Shroomstone.** Buy Shroomstone Pieces from Shroomies with diamonds. Four pieces and four emeralds make one Shroomstone. Each armor piece reduces fall damage by 25 percent. A full set grants Jump Boost II. Fully charged tool attacks launch enemies.
- **Scorchsteel armor.** Four Scorchling Tails and four iron ingots make one Scorchsteel Ingot. A full set conceals you from hostile mobs after you stand still for one second. Moving, attacking, or taking damage ends the effect.

## Other items and blocks

- **Luminite lights.** Luminite Torches and Lanterns emit light level 15. Throwable Luminite Torches place light on distant surfaces.
- **Corrupted Pearl.** A Corrupted Pearl teleports the nearest mob to the thrower to its landing point.
- **Shroombomb.** This throwable explosive uses Glimmershrooms and gunpowder. It damages enemies and breaks blocks.
- **Glimmershroom Block.** Entities bounce on it as they do on a slime block, but it does not stick to adjacent blocks.

## Links

- [Official website](https://freeeranger.github.io/dark-caverns/)
- [Official wiki](https://github.com/freeeranger/dark-caverns/wiki) for stats, recipes, and mob drops
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/dark-caverns) for downloads and changelogs
- [YouTube trailer](https://www.youtube.com/watch?v=Z3q_B4iXvOw)
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
