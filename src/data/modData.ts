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
    description: 'The main biome of the Dark Caverns, made of Carfstone with stalactites and natural stone bridges.',
    details: 'This is where you first arrive when coming down through a Bedrock Gateway. Overworld ores generate here alongside native veins of Luminite and Platinum ore pockets.',
    stats: [
      { label: 'Stone', value: 'Carfstone' },
      { label: 'Ores', value: 'Luminite, Platinum, Iron, Diamond, Gold' },
      { label: 'Fauna', value: 'Camorocks, Luminite Foxes' }
    ]
  },
  {
    id: 'molten_depths',
    name: 'Molten Depths',
    category: 'biomes',
    texture: 'textures/block/molten_carfstone.png',
    description: 'An underground lava biome lined with molten carfstone and magma lakes.',
    details: 'Hellstone ore generates along the cliff walls here. Walking the shores without fire resistance is hazardous due to the ambient lava pools and roaming Scorchhounds.',
    stats: [
      { label: 'Stone', value: 'Molten Carfstone' },
      { label: 'Ores', value: 'Hellstone Ore, Hellstone Rock' },
      { label: 'Fauna', value: 'Scorchlings, Scorchhounds, Molteners' }
    ]
  },
  {
    id: 'glimmershroom_forest',
    name: 'Glimmershroom Forest',
    category: 'biomes',
    texture: 'textures/block/glimmershroom.png',
    description: 'A glowing fungal cave filled with tall glimmershrooms and glimmergrass.',
    details: 'This is the safest place underground. Hostile spawns are reduced by the natural light, and native Shroomie villagers build homes here out of mushrooms and twistwood.',
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
    description: 'A subterranean forest with shallow teal pools and twisted Twistwood trees.',
    details: 'Twistwood does not need sunlight to grow, making it your primary source of wood underground. Its saplings and logs can be farmed renewably.',
    stats: [
      { label: 'Flora', value: 'Twistwood Trees, Undersprouts' },
      { label: 'Wood', value: 'Twistwood Planks (Teal/Dark)' }
    ]
  },

  // Key Items
  {
    id: 'cavern_compass',
    name: 'Cavern Compass',
    category: 'items',
    texture: 'textures/item/cavern_compass.png',
    description: 'Points back to your active Bedrock Gateway landing site in the Dark Caverns.',
    details: 'Normal compasses spin out of control below bedrock because the Overworld magnetic pole does not reach down here. Crafting a Cavern Compass gives you a reliable way to navigate back to your exit portal. In the Overworld, it spins erratically. Right-clicking a Lodestone binds the needle to that block instead.',
    stats: [
      { label: 'Underground Target', value: 'Bedrock Gateway landing site' },
      { label: 'Overworld Target', value: 'Spins erratically' },
      { label: 'Lodestone Binding', value: 'Supported' }
    ],
    recipe: {
      slots: [
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Compass', texture: 'textures/item/compass.png' },
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
        { name: 'Platinum Piece', texture: 'textures/item/platinum_piece.png' },
        { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' }
      ],
      output: { name: 'Cavern Compass', texture: 'textures/item/cavern_compass.png', count: 1 }
    }
  },
  {
    id: 'key_to_the_caverns',
    name: 'Key to the Caverns',
    category: 'items',
    texture: 'textures/item/key_to_the_caverns.png',
    description: 'Used on Cracked Bedrock at Y <= -60 to open a gateway into the Dark Caverns.',
    details: 'Found in the loot chest at the top of the Forgotten Tower after defeating the Illagers guarding it. Right-clicking Cracked Bedrock with the key opens a permanent two-way gateway beam down into the caverns.',
    stats: [
      { label: 'Source', value: 'Forgotten Tower chest' },
      { label: 'Target', value: 'Cracked Bedrock (Y <= -60)' },
      { label: 'Use', value: 'Consumed on gateway creation' }
    ]
  },
  {
    id: 'throwable_luminite_torch',
    name: 'Throwable Luminite Torch',
    category: 'items',
    texture: 'textures/item/throwable_luminite_torch.png',
    description: 'A throwable variant of the Luminite Torch that places torches on impact.',
    details: 'Thrown like a snowball, placing a Luminite Torch onto whatever block face it hits, including distant walls and ceilings.',
    stats: [
      { label: 'Type', value: 'Throwable Luminite Torch' },
      { label: 'Placement', value: 'Places Luminite Torch on impact' },
      { label: 'Crafting Yield', value: '4 Throwable Luminite Torches' }
    ],
    recipe: {
      slots: [
        null, { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' }, null,
        null, { name: 'Stick', texture: 'textures/item/stick.png' }, null,
        null, null, null
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
    details: 'Traded by Shroomies in exchange for diamonds. Unlike regular ender pearls which teleport you, the Corrupted Pearl keeps you where you stand and moves the nearest nearby monster or animal to the landing point.',
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
    description: 'An impact explosive made from glimmershrooms and gunpowder.',
    details: 'Explodes instantly upon hitting a block or mob. Breaks stone cleanly for rapid excavation without destroying ore drops.',
    stats: [
      { label: 'Detonation', value: 'On impact' },
      { label: 'Crafting Yield', value: '2 Shroombombs' }
    ],
    recipe: {
      slots: [
        { name: 'Glimmershroom', texture: 'textures/block/glimmershroom.png' },
        { name: 'Gunpowder', texture: 'textures/item/gunpowder.png' },
        null, null, null, null, null, null, null
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
    details: 'Mined from Carfstone Luminite Ore in the Rocky Caverns. At a Smithing Table, Luminite Dust replaces Netherite upgrade templates for upgrading Diamond gear to Platinum, Hellstone, and Shroomstone.',
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
    details: 'Mined as raw pieces in Rocky Caverns. Used at a Smithing Table with Luminite Dust to upgrade Diamond gear to Platinum tier. Platinum stats sit between Diamond and Netherite.',
    stats: [
      { label: 'Recipe', value: '4 Platinum Pieces + 4 Iron Ingots' },
      { label: 'Gear Tier', value: 'Between Diamond and Netherite' }
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
    description: 'Smelted from Hellstone Rock mined in the Molten Depths.',
    details: 'Used at a Smithing Table with Luminite Dust to upgrade Platinum gear into Hellstone gear. Completely fireproof and immune to lava.',
    stats: [
      { label: 'Source', value: 'Molten Depths ore' },
      { label: 'Lava Safe', value: 'Yes (Floats in lava)' }
    ]
  },
  {
    id: 'shroomstone',
    name: 'Shroomstone',
    category: 'materials',
    texture: 'textures/item/shroomstone.png',
    description: 'Crafted from 4 Shroomstone Pieces traded by Shroomies.',
    details: 'Used at a Smithing Table with Luminite Dust to upgrade Platinum gear into Shroomstone gear. Bounces entities and prevents fall damage.',
    stats: [
      { label: 'Source', value: 'Shroomie villager trades' },
      { label: 'Recipe', value: '4 Shroomstone Pieces' }
    ]
  },
  {
    id: 'scorchsteel_ingot',
    name: 'Scorchsteel Ingot',
    category: 'materials',
    texture: 'textures/item/scorchsteel_ingot.png',
    description: 'Forged from Scorchling Tails and Iron Ingots.',
    details: 'Crafted with 1 Scorchling Tail and 4 Iron Ingots. Used to craft Scorchsteel Armor for camouflaging from monsters.',
    stats: [
      { label: 'Recipe', value: '1 Scorchling Tail + 4 Iron Ingots' }
    ]
  },

  // Gear
  {
    id: 'platinum_gear',
    name: 'Platinum Armor and Tools',
    category: 'gear',
    texture: 'textures/item/platinum_sword.png',
    description: 'Upgrades Diamond gear at the smithing table using Platinum Ingots.',
    details: 'Stats sit between Diamond and Netherite with 18 enchantability. Upgrades into Hellstone or Shroomstone gear.',
    stats: [
      { label: 'Tier', value: 'Between Diamond and Netherite' },
      { label: 'Enchantability', value: '18' },
      { label: 'Smithing Catalyst', value: 'Luminite Dust' }
    ],
    smithing: {
      template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
      base: { name: 'Diamond Sword', texture: 'textures/item/diamond.png' },
      addition: { name: 'Platinum Ingot', texture: 'textures/item/platinum_ingot.png' },
      output: { name: 'Platinum Sword', texture: 'textures/item/platinum_sword.png' }
    }
  },
  {
    id: 'hellstone_gear',
    name: 'Hellstone Armor and Tools',
    category: 'gear',
    texture: 'textures/item/hellstone_sword.png',
    description: 'Upgrades Platinum gear. Grants fire and lava immunity.',
    details: 'Each armor piece reduces fire and lava damage by 25%. Wearing the full set grants complete immunity to fire and lava. Fully charged attacks ignite targets for 8 seconds.',
    stats: [
      { label: 'Armor Bonus', value: '25% Fire/Lava damage reduction per piece' },
      { label: 'Full Set Bonus', value: '100% Fire and Lava immunity' },
      { label: 'Weapon Trait', value: 'Fully charged attacks ignite for 8s' }
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
    description: 'Upgrades Platinum gear. Grants Jump Boost II and negates fall damage.',
    details: 'Each armor piece reduces fall damage by 25%. Wearing the full set grants Jump Boost II and negates all fall damage. Fully charged strikes launch targets into the air.',
    stats: [
      { label: 'Armor Bonus', value: '25% Fall damage reduction per piece' },
      { label: 'Full Set Bonus', value: 'Jump Boost II + 100% Fall Negation' },
      { label: 'Weapon Trait', value: 'Fully charged attacks launch targets skyward' }
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
    description: 'Forged from Scorchsteel Ingots. Stand still to hide from monsters.',
    details: 'Wearing the full set and standing still for 1 second conceals you from hostile monsters. Moving, attacking, or taking damage breaks concealment.',
    stats: [
      { label: 'Full Set Bonus', value: 'Stillness Camouflage (invisible to hostile mob aggro)' },
      { label: 'Delay', value: '1.0 second standing still' },
      { label: 'Breaks On', value: 'Moving, attacking, or taking damage' }
    ]
  },
  {
    id: 'luminite_helmet',
    name: 'Luminite Helmet',
    category: 'gear',
    texture: 'textures/item/luminite_helmet.png',
    description: 'Lights the area around you while worn.',
    details: 'Crafted with Luminite Dust and iron. Lights up surrounding blocks as you walk.',
    stats: [
      { label: 'Light Source', value: 'Dynamic light while worn' },
      { label: 'Slot', value: 'Helmet' }
    ]
  },

  // Mobs
  {
    id: 'shroomie',
    name: 'Shroomie',
    category: 'mobs',
    texture: 'textures/item/shroomie_spawn_egg.png',
    description: 'Mushroom villagers living in the Glimmershroom Forest.',
    details: 'Peaceful inhabitants that build mushroom huts. They trade Corrupted Pearls, Shroomstone Pieces, and rare items in exchange for Diamonds and Luminite Dust.',
    stats: [
      { label: 'Behavior', value: 'Passive / Trader' },
      { label: 'Location', value: 'Glimmershroom Forest' },
      { label: 'Currency', value: 'Diamonds, Luminite' }
    ]
  },
  {
    id: 'camorock',
    name: 'Camorock',
    category: 'mobs',
    texture: 'textures/item/camorock_spawn_egg.png',
    description: 'A monster disguised as a normal Carfstone block.',
    details: 'Blends in seamlessly with the surrounding cave stone until you get close, at which point it reveals its eyes and attacks. Drops carfstone, iron, and occasional diamonds.',
    stats: [
      { label: 'Behavior', value: 'Hostile / Ambush' },
      { label: 'Location', value: 'Rocky Caverns' },
      { label: 'Drops', value: 'Carfstone, Raw Iron, Diamonds' }
    ]
  },
  {
    id: 'scorchling_hound',
    name: 'Scorchling & Scorchhound',
    category: 'mobs',
    texture: 'textures/item/scorchhound_spawn_egg.png',
    description: 'Predators of the Molten Depths.',
    details: 'Scorchlings are fast, agile critters that drop Scorchling Tails used for Scorchsteel. Scorchhounds are heavy pack beasts that drop Scorched Meat.',
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
    description: 'A glowing friendly creature found in Rocky Caverns.',
    details: 'Emits light as it moves around. Can be tempted and bred with Luminite Dust.',
    stats: [
      { label: 'Behavior', value: 'Passive' },
      { label: 'Tempt Item', value: 'Luminite Dust' }
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
        body: 'Mine down below Y = -60 at the bottom of the Overworld until you reach bedrock. Look for Cracked Bedrock in the floor.'
      },
      {
        title: '4. Open the gateway',
        body: 'Right-click Cracked Bedrock with the Key to create a permanent gateway beam into the caverns.',
        note: 'Jump into the gateway block above the landing platform to return to the Overworld.'
      }
    ]
  },
  {
    id: 'cavern-compass-navigation',
    title: 'The Cavern Compass and navigation',
    summary: 'Craft the Cavern Compass to navigate back to your gateway beneath bedrock.',
    steps: [
      {
        title: '1. The bedrock magnetic blindspot',
        body: 'Standard compasses spin randomly below bedrock because Overworld magnetic poles do not reach the caverns. Craft a Cavern Compass to find your way back to your gateway.'
      },
      {
        title: '2. Crafting the Cavern Compass',
        body: 'In a crafting table, surround a standard compass with 4 Platinum Pieces on the edges and 4 Luminite Dust in the corners.'
      },
      {
        title: '3. Reading the needle',
        body: 'In the caverns, the needle points directly to your Bedrock Gateway. Right-click a Lodestone to bind the compass to that block instead.'
      }
    ]
  },
  {
    id: 'smithing-and-gear-progression',
    title: 'Smithing and gear progression',
    summary: 'Upgrade Diamond gear to Platinum, Hellstone, and Shroomstone at the smithing table.',
    steps: [
      {
        title: '1. Luminite dust as upgrade catalyst',
        body: 'Dark Caverns gear upgrades use Luminite Dust in the template slot at a standard smithing table instead of Netherite upgrade templates.'
      },
      {
        title: '2. Platinum gear',
        body: 'Combine 4 Platinum Pieces and 4 Iron Ingots for a Platinum Ingot. At the smithing table, combine Luminite Dust, Diamond gear, and a Platinum Ingot. Stats sit between Diamond and Netherite with 18 enchantability.'
      },
      {
        title: '3. Hellstone gear',
        body: 'Smelt Hellstone Rock from the Molten Depths into Hellstone. At the smithing table, combine Luminite Dust, Platinum gear, and Hellstone. Full armor grants fire and lava immunity.'
      },
      {
        title: '4. Shroomstone gear',
        body: 'Combine 4 Shroomstone Pieces traded by Shroomies into Shroomstone. At the smithing table, combine Luminite Dust, Platinum gear, and Shroomstone. Full armor grants Jump Boost II and negates fall damage.'
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
        body: 'Eat Scorched Berries from shore bushes for 5 seconds of instant Fire Resistance near lava pools.'
      },
      {
        title: '2. Mining Hellstone',
        body: 'Hellstone veins generate on cliff faces above lava lakes. Mine them with an iron pickaxe or better. Mined Hellstone floats on lava and will not burn.'
      },
      {
        title: '3. Crafting Scorchsteel armor',
        body: 'Defeat Scorchlings for Scorchling Tails. Combine 1 tail with 4 iron ingots to forge Scorchsteel Ingots. Full armor conceals you from hostile monsters while standing still.'
      }
    ]
  },
  {
    id: 'shroomie-trading-and-settlements',
    title: 'Shroomie trading and settlements',
    summary: 'Find fungal villages and barter diamonds or Luminite Dust for rare items.',
    steps: [
      {
        title: '1. Locating the Glimmershroom Forest',
        body: 'Follow glowing cyan mushrooms through cave tunnels until stone gives way to glimmergrass. Shroomie houses generate naturally among giant glimmershrooms.'
      },
      {
        title: '2. Bartering with Shroomies',
        body: 'Right-click a Shroomie to open their barter window. Trade Diamonds or Luminite Dust for Corrupted Pearls, Shroomstone Pieces, and fungal blocks.'
      },
      {
        title: '3. Building an underground base',
        body: 'Low monster spawns and renewable Twistwood make the Glimmershroom Forest the safest biome for an underground base.'
      }
    ]
  }
];
