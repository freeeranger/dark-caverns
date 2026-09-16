export interface RecipeSlot {
  name: string;
  texture?: string;
  count?: number;
}

export interface CraftingRecipe {
  slots: (RecipeSlot | null)[]; // 9 slots for 3x3 grid
  output: RecipeSlot;
}

export interface SmithingRecipe {
  template: RecipeSlot;
  base: RecipeSlot;
  addition: RecipeSlot;
  output: RecipeSlot;
}

export interface WikiEntry {
  id: string;
  name: string;
  category: 'biomes' | 'materials' | 'gear' | 'items' | 'mobs';
  texture: string;
  description: string;
  details: string;
  stats?: { label: string; value: string }[];
  recipe?: CraftingRecipe;
  smithing?: SmithingRecipe;
}

export interface GuideStep {
  title: string;
  body: string;
  note?: string;
}

export interface GuideChapter {
  id: string;
  title: string;
  summary: string;
  steps: GuideStep[];
}

export const WIKI_ENTRIES: WikiEntry[] = [
  // Biomes
  {
    id: 'rocky_caverns',
    name: 'Rocky Caverns',
    category: 'biomes',
    texture: 'textures/block/carfstone.png',
    description: 'A Carfstone cave biome with large chambers, layered passages, stone bridges, stalactites, and stalagmites.',
    details: 'Luminite Ore is common here and can appear on exposed Carfstone formations. Platinum and the six Overworld ores also generate throughout the biome.',
    stats: [
      { label: 'Stone', value: 'Carfstone' },
      { label: 'Ores', value: 'Luminite, Platinum, Coal, Iron, Gold, Redstone, Lapis, Diamond' },
      { label: 'Fauna', value: 'Camorocks, Luminite Foxes, Luminite Golems' }
    ]
  },
  {
    id: 'molten_depths',
    name: 'Molten Depths',
    category: 'biomes',
    texture: 'textures/block/molten_carfstone.png',
    description: 'A hot cave biome made of Molten Carfstone, ash patches, magma, and lava springs.',
    details: 'Hellstone Ore only replaces Molten Carfstone. Scorched Berry bushes provide short Fire Resistance, while Scorchlings and Scorchhounds make open ground dangerous.',
    stats: [
      { label: 'Stone', value: 'Molten Carfstone, Ashy Molten Carfstone' },
      { label: 'Native Ore', value: 'Hellstone Ore' },
      { label: 'Plants', value: 'Scorched Berries, Charred Grass, Ashy Charred Grass' },
      { label: 'Fauna', value: 'Scorchlings, Scorchhounds, Molteners' }
    ]
  },
  {
    id: 'glimmershroom_forest',
    name: 'Glimmershroom Forest',
    category: 'biomes',
    texture: 'textures/block/glimmershroom.png',
    description: 'A glowing fungal cave filled with tall glimmershrooms and glimmergrass.',
    details: 'The biome has no native monster spawns. Shroomies trade useful blocks and progression materials, while neutral Shroomlings attack only after they are provoked.',
    stats: [
      { label: 'Flora', value: 'Glimmershrooms, Glimmergrass' },
      { label: 'Settlements', value: 'Shroomie Houses' },
      { label: 'Fauna', value: 'Shroomies, Shroomlings' }
    ]
  },
  {
    id: 'tangled_hallow',
    name: 'Tangled Hallow',
    category: 'biomes',
    texture: 'textures/block/twistwood_log.png',
    description: 'A dense subterranean forest with overgrown Carfstone, twisted Twistwood trees, and teal surface lakes.',
    details: 'Water Sproutlets grow across the lakes. Twistwood saplings grow without sunlight, so logs and planks can be farmed inside the Dark Caverns.',
    stats: [
      { label: 'Flora', value: 'Twistwood Trees, Undersprouts, Water Sproutlets' },
      { label: 'Ground', value: 'Overgrown Carfstone' },
      { label: 'Fauna', value: 'Camorocks, Luminite Foxes, Luminite Golems' }
    ]
  },

  // Key Items
  {
    id: 'key_to_the_caverns',
    name: 'Key to the Caverns',
    category: 'items',
    texture: 'textures/item/key_to_the_caverns.png',
    description: 'Used on Cracked Bedrock in the Overworld floor to open a gateway into the Dark Caverns.',
    details: 'The key waits in the Forgotten Tower. Using it replaces one Cracked Bedrock block with a permanent gateway. The linked return gateway forms in the Dark Caverns ceiling above a lit ladder shaft that opens onto safe cave ground.',
    stats: [
      { label: 'Source', value: 'Forgotten Tower chest' },
      { label: 'Target', value: 'Cracked Bedrock' },
      { label: 'Use', value: 'Consumed on gateway creation' }
    ]
  },
  {
    id: 'throwable_luminite_torch',
    name: 'Throwable Luminite Torch',
    category: 'items',
    texture: 'textures/item/throwable_luminite_torch.png',
    description: 'A throwable variant of the Luminite Torch that places torches on impact.',
    details: 'Thrown like a snowball. It places a Luminite Torch on the top or side of a valid block, and drops itself if the torch cannot be placed.',
    stats: [
      { label: 'Type', value: 'Throwable Luminite Torch' },
      { label: 'Placement', value: 'Places Luminite Torch on impact' },
      { label: 'Crafting Yield', value: '4 Throwable Luminite Torches' }
    ],
    recipe: {
      slots: [
        null, { name: 'Luminite Torch', texture: 'textures/block/luminite_torch.png' }, null,
        { name: 'Luminite Torch', texture: 'textures/block/luminite_torch.png' },
        { name: 'Slimeball', texture: 'textures/item/slime_ball.png' },
        { name: 'Luminite Torch', texture: 'textures/block/luminite_torch.png' },
        null, { name: 'Luminite Torch', texture: 'textures/block/luminite_torch.png' }, null
      ],
      output: { name: 'Throwable Luminite Torch', texture: 'textures/item/throwable_luminite_torch.png', count: 4 }
    }
  },
  {
    id: 'corrupted_pearl',
    name: 'Corrupted Pearl',
    category: 'items',
    texture: 'textures/item/corrupted_pearl.png',
    description: 'Teleports the nearest non-player creature near you to wherever the pearl lands.',
    details: 'A rare Shroomie trade that costs 4 Ender Pearls. The Corrupted Pearl keeps you in place and moves the nearest non-player creature within 5 blocks of you to the landing point.',
    stats: [
      { label: 'Source', value: 'Shroomie trade' },
      { label: 'Stack Size', value: '16' },
      { label: 'Effect', value: 'Teleports nearest mob' }
    ]
  },
  {
    id: 'shroombomb',
    name: 'Shroombomb',
    category: 'items',
    texture: 'textures/item/shroombomb.png',
    description: 'An impact explosive made from mushrooms and gunpowder.',
    details: 'Explodes as soon as it hits a block or creature. Its default explosion power is 2.5, compared with 4.0 for TNT, and the server config can change it.',
    stats: [
      { label: 'Detonation', value: 'On impact' },
      { label: 'Crafting Yield', value: '2 Shroombombs' }
    ],
    recipe: {
      slots: [
        { name: 'Mushroom', texture: 'textures/block/glimmershroom.png' },
        { name: 'Mushroom', texture: 'textures/block/glimmershroom.png' },
        { name: 'Gunpowder', texture: 'textures/item/gunpowder.png' },
        { name: 'Gunpowder', texture: 'textures/item/gunpowder.png' },
        { name: 'Gunpowder', texture: 'textures/item/gunpowder.png' },
        null, null, null, null
      ],
      output: { name: 'Shroombomb', texture: 'textures/item/shroombomb.png', count: 2 }
    }
  },

  // Materials
  {
    id: 'luminite_dust',
    name: 'Luminite Dust',
    category: 'materials',
    texture: 'textures/item/luminite_dust.png',
    description: 'The smithing template catalyst for all Cavern gear upgrades.',
    details: 'Carfstone Luminite Ore generates throughout the Dark Caverns and on exposed Carfstone formations. Put Luminite Dust in the template slot of a Smithing Table for Platinum, Hellstone, Shroomstone, and Scorchsteel upgrades.',
    stats: [
      { label: 'Smithing Role', value: 'Upgrade Template Catalyst' },
      { label: 'Ore Source', value: 'Carfstone Luminite Ore' }
    ]
  },
  {
    id: 'platinum_ingot',
    name: 'Platinum Ingot',
    category: 'materials',
    texture: 'textures/item/platinum_ingot.png',
    description: 'Forged from 4 Platinum Pieces and 4 Iron Ingots.',
    details: 'Carfstone Platinum Ore generates below Y = 80 throughout the Dark Caverns and drops Platinum Pieces. Use a Platinum Ingot with Luminite Dust at a Smithing Table to upgrade Diamond gear.',
    stats: [
      { label: 'Recipe', value: '4 Platinum Pieces + 4 Iron Ingots' },
      { label: 'Ore Height', value: 'Below Y = 80' }
    ],
    recipe: {
      slots: [
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        null
      ],
      output: { name: 'Platinum Ingot', texture: 'textures/item/platinum_ingot.png', count: 1 }
    }
  },
  {
    id: 'hellstone',
    name: 'Hellstone',
    category: 'materials',
    texture: 'textures/item/hellstone.png',
    description: 'Crafted from 4 Hellstone Rocks and 4 Diamonds.',
    details: 'Hellstone Rock drops from Hellstone Ore in the Molten Depths. Hellstone does not burn in fire or lava and upgrades Platinum equipment into Hellstone gear at a Smithing Table.',
    stats: [
      { label: 'Recipe', value: '4 Hellstone Rocks + 4 Diamonds' },
      { label: 'Item Fire Resistance', value: 'Does not burn' }
    ],
    recipe: {
      slots: [
        { name: 'Hellstone Rock', texture: 'textures/item/hellstone_rock.png' },
        { name: 'Hellstone Rock', texture: 'textures/item/hellstone_rock.png' },
        { name: 'Hellstone Rock', texture: 'textures/item/hellstone_rock.png' },
        { name: 'Hellstone Rock', texture: 'textures/item/hellstone_rock.png' },
        { name: 'Diamond', texture: 'textures/item/diamond.png' },
        { name: 'Diamond', texture: 'textures/item/diamond.png' },
        { name: 'Diamond', texture: 'textures/item/diamond.png' },
        { name: 'Diamond', texture: 'textures/item/diamond.png' },
        null
      ],
      output: { name: 'Hellstone', texture: 'textures/item/hellstone.png', count: 1 }
    }
  },
  {
    id: 'shroomstone',
    name: 'Shroomstone',
    category: 'materials',
    texture: 'textures/item/shroomstone.png',
    description: 'Crafted from 4 Shroomstone Pieces and 4 Emeralds.',
    details: 'Shroomies trade 2 Shroomstone Pieces for 1 Diamond. Finished Shroomstone upgrades Platinum equipment into Shroomstone gear at a Smithing Table.',
    stats: [
      { label: 'Piece Source', value: 'Shroomie trade' },
      { label: 'Recipe', value: '4 Shroomstone Pieces + 4 Emeralds' }
    ],
    recipe: {
      slots: [
        { name: 'Shroomstone Piece', texture: 'textures/item/shroomstone_piece.png' },
        { name: 'Shroomstone Piece', texture: 'textures/item/shroomstone_piece.png' },
        { name: 'Shroomstone Piece', texture: 'textures/item/shroomstone_piece.png' },
        { name: 'Shroomstone Piece', texture: 'textures/item/shroomstone_piece.png' },
        { name: 'Emerald', texture: 'textures/item/emerald.png' },
        { name: 'Emerald', texture: 'textures/item/emerald.png' },
        { name: 'Emerald', texture: 'textures/item/emerald.png' },
        { name: 'Emerald', texture: 'textures/item/emerald.png' },
        null
      ],
      output: { name: 'Shroomstone', texture: 'textures/item/shroomstone.png', count: 1 }
    }
  },
  {
    id: 'scorchsteel_ingot',
    name: 'Scorchsteel Ingot',
    category: 'materials',
    texture: 'textures/item/scorchsteel_ingot.png',
    description: 'Crafted from 4 Scorchling Tails and 4 Iron Ingots.',
    details: 'Use it with Luminite Dust at a Smithing Table to upgrade Platinum armor into Scorchsteel armor.',
    stats: [
      { label: 'Recipe', value: '4 Scorchling Tails + 4 Iron Ingots' }
    ],
    recipe: {
      slots: [
        { name: 'Scorchling Tail', texture: 'textures/item/scorchling_tail.png' },
        { name: 'Scorchling Tail', texture: 'textures/item/scorchling_tail.png' },
        { name: 'Scorchling Tail', texture: 'textures/item/scorchling_tail.png' },
        { name: 'Scorchling Tail', texture: 'textures/item/scorchling_tail.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        { name: 'Iron Ingot', texture: 'textures/item/iron_ingot.png' },
        null
      ],
      output: { name: 'Scorchsteel Ingot', texture: 'textures/item/scorchsteel_ingot.png', count: 1 }
    }
  },

  // Gear
  {
    id: 'platinum_gear',
    name: 'Platinum Armor and Tools',
    category: 'gear',
    texture: 'textures/item/platinum_sword.png',
    description: 'Upgrades Diamond gear at the smithing table using Platinum Ingots.',
    details: 'Platinum tools mine faster than Netherite tools, while their durability and damage sit between Diamond and Netherite. Platinum armor has 2.5 toughness and the whole tier has 20 enchantability. Platinum gear upgrades into Hellstone or Shroomstone gear, and Platinum armor also upgrades into Scorchsteel.',
    stats: [
      { label: 'Tool Durability', value: '1,843' },
      { label: 'Mining Speed', value: '10' },
      { label: 'Enchantability', value: '20' },
      { label: 'Smithing Catalyst', value: 'Luminite Dust' }
    ],
    smithing: {
      template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
      base: { name: 'Diamond Sword', texture: 'textures/item/diamond_sword.png' },
      addition: { name: 'Platinum Ingot', texture: 'textures/item/platinum_ingot.png' },
      output: { name: 'Platinum Sword', texture: 'textures/item/platinum_sword.png' }
    }
  },
  {
    id: 'hellstone_gear',
    name: 'Hellstone Armor and Tools',
    category: 'gear',
    texture: 'textures/item/hellstone_sword.png',
    description: 'Upgrades Platinum gear. A full armor set blocks fire and lava damage.',
    details: 'Each armor piece reduces fire and lava damage by 25%. Wearing the full set grants complete immunity to fire and lava. Fully charged attacks ignite targets for 8 seconds.',
    stats: [
      { label: 'Armor Bonus', value: '25% Fire/Lava damage reduction per piece' },
      { label: 'Full Set Bonus', value: '100% Fire and Lava immunity' },
      { label: 'Tool Trait', value: 'Fully charged attacks ignite for 8 seconds' }
    ],
    smithing: {
      template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
      base: { name: 'Platinum Sword', texture: 'textures/item/platinum_sword.png' },
      addition: { name: 'Hellstone', texture: 'textures/item/hellstone.png' },
      output: { name: 'Hellstone Sword', texture: 'textures/item/hellstone_sword.png' }
    }
  },
  {
    id: 'shroomstone_gear',
    name: 'Shroomstone Armor and Tools',
    category: 'gear',
    texture: 'textures/item/shroomstone_sword.png',
    description: 'Upgrades Platinum gear. A full armor set grants Jump Boost II and blocks fall damage.',
    details: 'Each armor piece reduces fall damage by 25%. Wearing the full set grants Jump Boost II and negates all fall damage. Fully charged strikes launch targets into the air.',
    stats: [
      { label: 'Armor Bonus', value: '25% Fall damage reduction per piece' },
      { label: 'Full Set Bonus', value: 'Jump Boost II + 100% Fall Negation' },
      { label: 'Tool Trait', value: 'Fully charged attacks launch targets upward' }
    ],
    smithing: {
      template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
      base: { name: 'Platinum Sword', texture: 'textures/item/platinum_sword.png' },
      addition: { name: 'Shroomstone', texture: 'textures/item/shroomstone.png' },
      output: { name: 'Shroomstone Sword', texture: 'textures/item/shroomstone_sword.png' }
    }
  },
  {
    id: 'scorchsteel_armor',
    name: 'Scorchsteel Armor',
    category: 'gear',
    texture: 'textures/item/scorchsteel_chestplate.png',
    description: 'Upgrades Platinum armor. Stand still to hide from monsters.',
    details: 'At a Smithing Table, combine Luminite Dust, a piece of Platinum armor, and a Scorchsteel Ingot. Wearing the full set and standing still for 1 second applies invisibility and clears hostile monster targets. Moving, attacking, or taking damage ends concealment.',
    stats: [
      { label: 'Full Set Bonus', value: 'Hostile monsters lose their target' },
      { label: 'Delay', value: '1.0 second standing still' },
      { label: 'Breaks On', value: 'Moving, attacking, or taking damage' }
    ],
    smithing: {
      template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
      base: { name: 'Platinum Chestplate', texture: 'textures/item/platinum_chestplate.png' },
      addition: { name: 'Scorchsteel Ingot', texture: 'textures/item/scorchsteel_ingot.png' },
      output: { name: 'Scorchsteel Chestplate', texture: 'textures/item/scorchsteel_chestplate.png' }
    }
  },
  {
    id: 'luminite_helmet',
    name: 'Luminite Helmet',
    category: 'gear',
    texture: 'textures/item/luminite_helmet.png',
    description: 'Lights the area around you while worn.',
    details: 'Crafted from 5 Luminite Dust or bought from a Shroomie for 10 Iron Ingots. It creates client-side dynamic light while worn. Players can disable the effect in the client config.',
    stats: [
      { label: 'Light Source', value: 'Dynamic light while worn' },
      { label: 'Recipe', value: '5 Luminite Dust' },
      { label: 'Trade', value: '10 Iron Ingots' }
    ],
    recipe: {
      slots: [
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        null,
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        null, null, null
      ],
      output: { name: 'Luminite Helmet', texture: 'textures/item/luminite_helmet.png', count: 1 }
    }
  },

  // Mobs
  {
    id: 'shroomie',
    name: 'Shroomie',
    category: 'mobs',
    texture: 'textures/item/shroomie_spawn_egg.png',
    description: 'Peaceful traders found in the Glimmershroom Forest.',
    details: 'Each Shroomie offers five common trades, one guaranteed Shroomstone Piece trade, and one rare trade. Their prices use Luminite Dust, Iron Ingots, Gold Ingots, Diamonds, Ender Pearls, or Emeralds, depending on the offer.',
    stats: [
      { label: 'Behavior', value: 'Passive / Trader' },
      { label: 'Location', value: 'Glimmershroom Forest' },
      { label: 'Progression Trade', value: '1 Diamond for 2 Shroomstone Pieces' },
      { label: 'Pearl Trade', value: '4 Ender Pearls for 1 Corrupted Pearl' }
    ]
  },
  {
    id: 'camorock',
    name: 'Camorock',
    category: 'mobs',
    texture: 'textures/item/camorock_spawn_egg.png',
    description: 'A timid Carfstone creature that flees from nearby players.',
    details: 'Camorocks wander the Rocky Caverns and Tangled Hallow. Their weighted loot includes Carfstone, Raw Iron, Gold Nuggets, Luminite Dust, and a rare Diamond.',
    stats: [
      { label: 'Behavior', value: 'Passive / Avoids players' },
      { label: 'Location', value: 'Rocky Caverns, Tangled Hallow' },
      { label: 'Rare Drop', value: 'Diamond' }
    ]
  },
  {
    id: 'scorchling_hound',
    name: 'Scorchling & Scorchhound',
    category: 'mobs',
    texture: 'textures/item/scorchhound_spawn_egg.png',
    description: 'Predators of the Molten Depths.',
    details: 'Scorchlings leap at players and drop the tails needed for Scorchsteel. Scorchhounds have 40 health, throw players with melee attacks, and drop Scorched Meat.',
    stats: [
      { label: 'Behavior', value: 'Hostile' },
      { label: 'Location', value: 'Molten Depths' },
      { label: 'Key Drops', value: 'Scorchling Tails, Scorched Meat' }
    ]
  },
  {
    id: 'luminite_fox',
    name: 'Luminite Fox',
    category: 'mobs',
    texture: 'textures/item/luminite_fox_spawn_egg.png',
    description: 'A passive creature found in Rocky Caverns and Tangled Hallow.',
    details: 'Luminite Dust attracts Luminite Foxes. A fox killed by a player has a 25% chance to drop one Luminite Dust.',
    stats: [
      { label: 'Behavior', value: 'Passive' },
      { label: 'Location', value: 'Rocky Caverns, Tangled Hallow' },
      { label: 'Tempt Item', value: 'Luminite Dust' }
    ]
  },
  {
    id: 'luminite_golem',
    name: 'Luminite Golem',
    category: 'mobs',
    texture: 'textures/item/luminite_golem_spawn_egg.png',
    description: 'A slow, armored monster found in Rocky Caverns and Tangled Hallow.',
    details: 'Luminite Golems have 40 health, 10 armor, strong knockback resistance, and a heavy attack that launches its target. They drop 3 to 6 Luminite Dust and 1 to 3 Carfstone.',
    stats: [
      { label: 'Behavior', value: 'Hostile' },
      { label: 'Location', value: 'Rocky Caverns, Tangled Hallow' },
      { label: 'Drops', value: 'Luminite Dust, Carfstone' }
    ]
  },
  {
    id: 'shroomling',
    name: 'Shroomling',
    category: 'mobs',
    texture: 'textures/item/shroomling_spawn_egg.png',
    description: 'A neutral creature native to the Glimmershroom Forest.',
    details: 'Shroomlings attack after they are provoked and alert nearby Shroomlings. They drop 1 to 3 Glimmershrooms before Looting bonuses.',
    stats: [
      { label: 'Behavior', value: 'Neutral' },
      { label: 'Location', value: 'Glimmershroom Forest' },
      { label: 'Drops', value: 'Glimmershrooms' }
    ]
  },
  {
    id: 'moltener',
    name: 'Moltener',
    category: 'mobs',
    texture: 'textures/item/moltener_spawn_egg.png',
    description: 'A passive creature found in the Molten Depths.',
    details: 'Scorched Berries attract Molteners. They can drop Fire Charges or Gold Nuggets.',
    stats: [
      { label: 'Behavior', value: 'Passive' },
      { label: 'Location', value: 'Molten Depths' },
      { label: 'Tempt Item', value: 'Scorched Berries' }
    ]
  }
];

export const GUIDES: GuideChapter[] = [
  {
    id: 'getting-to-the-caverns',
    title: 'Getting to the caverns',
    summary: 'Find the Forgotten Tower, claim the key, and open a Bedrock Gateway.',
    steps: [
      {
        title: '1. Buy the Forgotten Tower map',
        body: 'Trade with an Expert Cartographer in the Overworld for an explorer map to a nearby Illager tower.'
      },
      {
        title: '2. Defeat the Illagers and claim the key',
        body: 'Follow the map to the tower, defeat the Illagers, and loot the Key to the Caverns from the top chest.'
      },
      {
        title: '3. Dig down to bedrock',
        body: 'Mine down to the Overworld bedrock floor and look for Cracked Bedrock. It generates in small veins throughout the bottom bedrock layer.'
      },
      {
        title: '4. Open the gateway',
        body: 'Use the Key on Cracked Bedrock to replace that block with a permanent gateway, then step onto it.',
        note: 'The linked return gateway sits in the Dark Caverns ceiling. Follow the lights down the enclosed ladder shaft, then jump into the gateway overhead when you want to return.'
      }
    ]
  },
  {
    id: 'exploring-the-tangled-hallow',
    title: 'Exploring the Tangled Hallow',
    summary: 'Find surface lakes, harvest Twistwood, and prepare for the biome\'s hostile spawns.',
    steps: [
      {
        title: '1. Find the forest floor',
        body: 'Look for Overgrown Carfstone covered with Undersprouts and dense Twistwood trees. The biome also has its own music and teal water color.'
      },
      {
        title: '2. Search the surface lakes',
        body: 'The lakes cut into open cavern floors rather than hiding underground. Water Sproutlets grow on their surfaces and can be collected as the biome\'s alternative to Lily Pads.'
      },
      {
        title: '3. Farm Twistwood underground',
        body: 'Twistwood saplings grow without sunlight. Replant saplings to produce renewable logs, planks, doors, and trapdoors inside the dimension.'
      },
      {
        title: '4. Watch for golems',
        body: 'Camorocks and Luminite Foxes are passive, but Luminite Golems and Skeletons also spawn here. Keep the forest floor lit while you gather wood or build.'
      }
    ]
  },
  {
    id: 'smithing-and-gear-progression',
    title: 'Smithing and gear progression',
    summary: 'Use Luminite Dust to upgrade Diamond gear and choose a later specialization.',
    steps: [
      {
        title: '1. Luminite dust as upgrade catalyst',
        body: 'Dark Caverns gear upgrades use Luminite Dust in the template slot at a standard smithing table instead of Netherite upgrade templates.'
      },
      {
        title: '2. Platinum gear',
        body: 'Combine 4 Platinum Pieces and 4 Iron Ingots for a Platinum Ingot. At the Smithing Table, combine Luminite Dust, Diamond gear, and a Platinum Ingot. Platinum tools have 1,843 durability, 10 mining speed, and 20 enchantability.'
      },
      {
        title: '3. Hellstone gear',
        body: 'Mine Hellstone Ore for Hellstone Rock, then combine 4 rocks with 4 Diamonds to make Hellstone. At the Smithing Table, combine Luminite Dust, Platinum gear, and Hellstone. A full armor set blocks all fire and lava damage.'
      },
      {
        title: '4. Shroomstone gear',
        body: 'Trade with Shroomies for Shroomstone Pieces, then combine 4 pieces with 4 Emeralds to make Shroomstone. At the Smithing Table, combine Luminite Dust, Platinum gear, and Shroomstone. A full armor set grants Jump Boost II and blocks all fall damage.'
      },
      {
        title: '5. Scorchsteel armor',
        body: 'Combine 4 Scorchling Tails with 4 Iron Ingots for a Scorchsteel Ingot. At the Smithing Table, combine Luminite Dust, Platinum armor, and the ingot. A full set conceals you from hostile monsters after you stand still for 1 second.'
      }
    ]
  },
  {
    id: 'surviving-the-molten-depths',
    title: 'Surviving the Molten Depths',
    summary: 'Harvest Hellstone, gather Scorched Berries, and hunt Scorchlings.',
    steps: [
      {
        title: '1. Scorched berries for quick fire resistance',
        body: 'Harvest Scorched Berries from bushes on Molten Carfstone. Eating one grants 5 seconds of Fire Resistance.'
      },
      {
        title: '2. Mining Hellstone',
        body: 'Hellstone Ore generates inside Molten Carfstone. Mine it with a Diamond Pickaxe or better to collect Hellstone Rock. The dropped rock does not burn in fire or lava.'
      },
      {
        title: '3. Crafting Scorchsteel armor',
        body: 'Defeat Scorchlings for Scorchling Tails. Combine 4 tails with 4 Iron Ingots for a Scorchsteel Ingot, then use Luminite Dust to upgrade Platinum armor at a Smithing Table.'
      }
    ]
  },
  {
    id: 'shroomie-trading-and-settlements',
    title: 'Shroomie trading and settlements',
    summary: 'Find Shroomie Houses and check each trader for common, progression, and rare offers.',
    steps: [
      {
        title: '1. Locating the Glimmershroom Forest',
        body: 'Follow glowing cyan mushrooms through cave tunnels until stone gives way to glimmergrass. Shroomie houses generate naturally among giant glimmershrooms.'
      },
      {
        title: '2. Bartering with Shroomies',
        body: 'Each Shroomie has five common offers, a guaranteed trade of 1 Diamond for 2 Shroomstone Pieces, and one rare offer. The Corrupted Pearl rare trade costs 4 Ender Pearls.'
      },
      {
        title: '3. Building an underground base',
        body: 'The Glimmershroom Forest has no native monster spawns, though nearby biomes and structures can still bring danger. Shroomlings remain neutral until provoked.'
      }
    ]
  }
];
