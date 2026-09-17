import { BLOCK_WIKI_ENTRIES, ITEM_WIKI_ENTRIES } from './wikiCatalog';

export interface RecipeSlot {
  name: string;
  texture?: string;
  count?: number;
}

export interface CraftingRecipe {
  slots: (RecipeSlot | null)[]; // 9 slots for 3x3 grid
  output: RecipeSlot;
  shapeless?: boolean;
}

export interface SmithingRecipe {
  template: RecipeSlot;
  base: RecipeSlot;
  addition: RecipeSlot;
  output: RecipeSlot;
}

export interface RelatedWikiItem {
  id: string;
  name: string;
  texture: string;
}

export interface WikiEntry {
  id: string;
  name: string;
  category: 'biomes' | 'blocks' | 'materials' | 'gear' | 'items' | 'mobs';
  texture: string;
  description: string;
  details: string;
  stats?: { label: string; value: string }[];
  recipe?: CraftingRecipe;
  smithing?: SmithingRecipe;
  aliases?: string[];
  registryId?: string | null;
  relatedItems?: RelatedWikiItem[];
}

export interface GuideStep {
  title: string;
  body: string;
  note?: string;
  recipeEntryIds?: string[];
}

export interface GuideChapter {
  id: string;
  title: string;
  summary: string;
  category: 'Start here' | 'Explore' | 'Progression';
  steps: GuideStep[];
}

function relatedItem(id: string, name: string): RelatedWikiItem {
  return {
    id,
    name,
    texture: `textures/item/${id}.png`
  };
}

function spawnEgg(mobId: string, mobName: string): RelatedWikiItem {
  return relatedItem(`${mobId}_spawn_egg`, `${mobName} Spawn Egg`);
}

const CURATED_WIKI_ENTRIES: WikiEntry[] = [
  // Biomes
  {
    id: 'rocky_caverns',
    name: 'Rocky Caverns',
    category: 'biomes',
    texture: 'textures/block/carfstone.png',
    description: 'Rocky Caverns has Carfstone chambers, layered passages, stone bridges, stalactites, and stalagmites.',
    details: 'Luminite Ore appears throughout the biome and on exposed Carfstone formations. Platinum and the six Overworld ores also generate here.',
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
    description: 'Molten Depths has Molten Carfstone, ash patches, magma, and lava springs.',
    details: 'Hellstone Ore replaces only Molten Carfstone. Scorched Berries grant short Fire Resistance. Scorchlings and Scorchhounds attack on open ground.',
    stats: [
      { label: 'Stone', value: 'Molten Carfstone, Ashy Molten Carfstone' },
      { label: 'Native ore', value: 'Hellstone Ore' },
      { label: 'Plants', value: 'Scorched Berries, Charred Grass, Ashy Charred Grass' },
      { label: 'Fauna', value: 'Scorchlings, Scorchhounds, Molteners' }
    ]
  },
  {
    id: 'glimmershroom_forest',
    name: 'Glimmershroom Forest',
    category: 'biomes',
    texture: 'textures/block/glimmershroom.png',
    description: 'Tall glimmershrooms and glimmergrass light this fungal cave biome.',
    details: 'No native monsters spawn here. Shroomies trade blocks and gear materials. Shroomlings remain neutral until provoked.',
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
    description: 'Tangled Hallow is a subterranean forest with Overgrown Carfstone, twisted Twistwood trees, and teal surface lakes.',
    details: 'Water Sproutlets grow across the lakes. Twistwood Saplings grow without sunlight, so you can farm Twistwood Logs and Twistwood Planks inside the Dark Caverns.',
    stats: [
      { label: 'Flora', value: 'Twistwood Trees, Undersprouts, Water Sproutlets' },
      { label: 'Ground', value: 'Overgrown Carfstone' },
      { label: 'Fauna', value: 'Camorocks, Luminite Foxes, Luminite Golems' }
    ]
  },

  // Key Items
  {
    id: 'cavern_compass',
    name: 'Cavern Compass',
    category: 'items',
    texture: 'textures/item/cavern_compass.png',
    description: 'The Cavern Compass points back to your gateway while you explore the Dark Caverns.',
    details: 'A normal Compass spins in the Dark Caverns. The Cavern Compass points to your linked gateway instead. Use it on a Lodestone if you want the needle to track that block.',
    stats: [
      { label: 'Cavern target', value: 'Your linked gateway' },
      { label: 'Lodestone binding', value: 'Supported' }
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
    description: 'Use the key on Cracked Bedrock in the Overworld floor to open a gateway into the Dark Caverns.',
    details: 'The Forgotten Tower\'s top chest contains the key. The key replaces one Cracked Bedrock block with a permanent gateway. A linked return gateway appears in the Dark Caverns ceiling above a lit ladder shaft that reaches safe cave ground.',
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
    description: 'Throw this Luminite Torch to place it where it lands.',
    details: 'It travels like a snowball and places a Luminite Torch on the top or side of a valid block. If placement fails, the item drops instead.',
    stats: [
      { label: 'Type', value: 'Throwable Luminite Torch' },
      { label: 'Placement', value: 'Places Luminite Torch on impact' },
      { label: 'Crafting yield', value: '4 Throwable Luminite Torches' }
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
    description: 'Throw the pearl to move a nearby non-player creature to its landing point.',
    details: 'Shroomies can offer it as a rare trade for 4 Ender Pearls. It moves the nearest non-player creature within 5 blocks of you, but it does not move you.',
    stats: [
      { label: 'Source', value: 'Shroomie trade' },
      { label: 'Stack size', value: '16' },
      { label: 'Effect', value: 'Teleports nearest mob' }
    ]
  },
  {
    id: 'shroombomb',
    name: 'Shroombomb',
    category: 'items',
    texture: 'textures/item/shroombomb.png',
    description: 'A Shroombomb explodes when it hits a block or creature.',
    details: 'Its default explosion power is 2.5. TNT has an explosion power of 4.0. Server owners can change the Shroombomb value in the config.',
    stats: [
      { label: 'Detonation', value: 'On impact' },
      { label: 'Crafting yield', value: '2 Shroombombs' }
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
      output: { name: 'Shroombomb', texture: 'textures/item/shroombomb.png', count: 2 },
      shapeless: true
    }
  },

  // Materials
  {
    id: 'luminite',
    name: 'Luminite',
    category: 'materials',
    registryId: null,
    texture: 'textures/item/luminite_dust.png',
    relatedItems: [relatedItem('luminite_dust', 'Luminite Dust')],
    description: 'Luminite is the light-producing mineral used throughout Dark Caverns equipment and utilities.',
    details: 'Carfstone Luminite Ore generates throughout the dimension and on exposed Carfstone formations. It drops Luminite Dust, the template item for Platinum, Hellstone, Shroomstone, and Scorchsteel upgrades.',
    stats: [
      { label: 'Smithing role', value: 'Template item' },
      { label: 'Ore source', value: 'Carfstone Luminite Ore' }
    ]
  },
  {
    id: 'platinum',
    name: 'Platinum',
    category: 'materials',
    registryId: null,
    texture: 'textures/item/platinum_ingot.png',
    relatedItems: [
      relatedItem('platinum_piece', 'Platinum Piece'),
      relatedItem('platinum_ingot', 'Platinum Ingot')
    ],
    description: 'Platinum is the first material tier in Dark Caverns equipment progression.',
    details: 'Carfstone Platinum Ore generates below Y = 80 and drops Platinum Pieces. Craft the pieces into Platinum Ingots, then use an ingot with Luminite Dust to upgrade Diamond equipment.',
    stats: [{ label: 'Ore height', value: 'Below Y = 80' }],
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
      output: { name: 'Platinum Ingot', texture: 'textures/item/platinum_ingot.png', count: 1 },
      shapeless: true
    }
  },
  {
    id: 'hellstone',
    name: 'Hellstone',
    category: 'materials',
    texture: 'textures/item/hellstone.png',
    relatedItems: [relatedItem('hellstone_rock', 'Hellstone Rock')],
    description: 'Hellstone is the upgrade material for fire-resistant equipment.',
    details: 'Hellstone Rock drops from Hellstone Ore in the Molten Depths and does not burn in fire or lava. Use Hellstone at a Smithing Table to upgrade Platinum equipment into Hellstone gear.',
    stats: [{ label: 'Item fire resistance', value: 'Does not burn' }],
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
      output: { name: 'Hellstone', texture: 'textures/item/hellstone.png', count: 1 },
      shapeless: true
    }
  },
  {
    id: 'shroomstone',
    name: 'Shroomstone',
    category: 'materials',
    texture: 'textures/item/shroomstone.png',
    relatedItems: [relatedItem('shroomstone_piece', 'Shroomstone Piece')],
    description: 'Shroomstone is the upgrade material for movement-focused equipment.',
    details: 'Shroomies trade 2 Shroomstone Pieces for 1 Diamond. Use Shroomstone at a Smithing Table to upgrade Platinum equipment into Shroomstone gear.',
    stats: [{ label: 'Piece source', value: 'Shroomie trade' }],
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
      output: { name: 'Shroomstone', texture: 'textures/item/shroomstone.png', count: 1 },
      shapeless: true
    }
  },
  {
    id: 'scorchsteel',
    name: 'Scorchsteel',
    category: 'materials',
    registryId: null,
    texture: 'textures/item/scorchsteel_ingot.png',
    relatedItems: [
      relatedItem('scorchling_tail', 'Scorchling Tail'),
      relatedItem('scorchsteel_ingot', 'Scorchsteel Ingot')
    ],
    description: 'Scorchsteel is the material used to make concealment armor.',
    details: 'Scorchlings drop Scorchling Tails. Craft the tails into Scorchsteel Ingots, then use an ingot with Luminite Dust to upgrade Platinum armor.',
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
      output: { name: 'Scorchsteel Ingot', texture: 'textures/item/scorchsteel_ingot.png', count: 1 },
      shapeless: true
    }
  },

  // Gear
  {
    id: 'platinum_gear',
    name: 'Platinum Armor and Tools',
    category: 'gear',
    registryId: null,
    aliases: ['Platinum gear', 'Platinum armor', 'Platinum tools', 'Platinum equipment'],
    texture: 'textures/item/platinum_sword.png',
    description: 'Use Platinum Ingots to upgrade Diamond gear at a Smithing Table.',
    details: 'Platinum tools mine faster than Netherite tools. Their durability and damage fall between Diamond and Netherite. Platinum armor has 2.5 toughness, and every Platinum item has 20 enchantability. Upgrade Platinum gear to Hellstone or Shroomstone. You can also upgrade Platinum armor to Scorchsteel.',
    stats: [
      { label: 'Tool durability', value: '1,843' },
      { label: 'Mining speed', value: '10' },
      { label: 'Enchantability', value: '20' },
      { label: 'Smithing item', value: 'Luminite Dust' }
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
    registryId: null,
    aliases: ['Hellstone gear', 'Hellstone armor', 'Hellstone tools', 'Hellstone equipment'],
    texture: 'textures/item/hellstone_sword.png',
    description: 'Use Hellstone to upgrade Platinum gear. A full armor set blocks fire and lava damage.',
    details: 'Each armor piece cuts fire and lava damage by 25%. The full set blocks all fire and lava damage. Fully charged attacks set targets on fire for 8 seconds.',
    stats: [
      { label: 'Armor bonus', value: '25% less fire and lava damage per piece' },
      { label: 'Full set bonus', value: 'Blocks all fire and lava damage' },
      { label: 'Tool trait', value: 'Fully charged attacks set targets on fire for 8 seconds' }
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
    registryId: null,
    aliases: ['Shroomstone gear', 'Shroomstone armor', 'Shroomstone tools', 'Shroomstone equipment'],
    texture: 'textures/item/shroomstone_sword.png',
    description: 'Use Shroomstone to upgrade Platinum gear. A full armor set grants Jump Boost II and blocks fall damage.',
    details: 'Each armor piece cuts fall damage by 25%. The full set grants Jump Boost II and blocks all fall damage. Fully charged attacks launch targets into the air.',
    stats: [
      { label: 'Armor bonus', value: '25% less fall damage per piece' },
      { label: 'Full set bonus', value: 'Jump Boost II and no fall damage' },
      { label: 'Tool trait', value: 'Fully charged attacks launch targets upward' }
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
    registryId: null,
    texture: 'textures/item/scorchsteel_chestplate.png',
    description: 'Use a Scorchsteel Ingot to upgrade Platinum armor. Stand still to hide from monsters.',
    details: 'After you stand still for 1 second, the full set makes you invisible and causes hostile monsters to lose their target. Moving, attacking, or taking damage ends the effect.',
    stats: [
      { label: 'Full set bonus', value: 'Hostile monsters lose their target' },
      { label: 'Delay', value: '1.0 second standing still' },
      { label: 'Breaks on', value: 'Moving, attacking, or taking damage' }
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
    description: 'The Luminite Helmet lights the area around you while worn.',
    details: 'The helmet creates client-side dynamic light while worn. You can disable the effect in the client config. Shroomies also sell it for 10 Iron Ingots.',
    stats: [
      { label: 'Light source', value: 'Dynamic light while worn' },
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
    relatedItems: [spawnEgg('shroomie', 'Shroomie')],
    description: 'Shroomies are peaceful traders in the Glimmershroom Forest.',
    details: 'Each Shroomie offers five common trades, one guaranteed Shroomstone Piece trade, and one rare trade. Their prices use Luminite Dust, Iron Ingots, Gold Ingots, Diamonds, Ender Pearls, or Emeralds, depending on the offer.',
    stats: [
      { label: 'Behavior', value: 'Passive trader' },
      { label: 'Location', value: 'Glimmershroom Forest' },
      { label: 'Shroomstone trade', value: '1 Diamond for 2 Shroomstone Pieces' },
      { label: 'Pearl trade', value: '4 Ender Pearls for 1 Corrupted Pearl' }
    ]
  },
  {
    id: 'camorock',
    name: 'Camorock',
    category: 'mobs',
    texture: 'textures/item/camorock_spawn_egg.png',
    relatedItems: [spawnEgg('camorock', 'Camorock')],
    description: 'Camorocks are timid Carfstone creatures that flee nearby players.',
    details: 'Camorocks wander the Rocky Caverns and Tangled Hallow. They can drop Carfstone, Raw Iron, Gold Nuggets, Luminite Dust, or a rare Diamond.',
    stats: [
      { label: 'Behavior', value: 'Passive, avoids players' },
      { label: 'Location', value: 'Rocky Caverns, Tangled Hallow' },
      { label: 'Rare drop', value: 'Diamond' }
    ]
  },
  {
    id: 'scorchling',
    name: 'Scorchling',
    category: 'mobs',
    texture: 'textures/item/scorchling_spawn_egg.png',
    relatedItems: [spawnEgg('scorchling', 'Scorchling')],
    description: 'Scorchlings are hostile creatures that hunt players in the Molten Depths.',
    details: 'They leap at players and drop the Scorchling Tails used to make Scorchsteel Ingots.',
    stats: [
      { label: 'Behavior', value: 'Hostile' },
      { label: 'Location', value: 'Molten Depths' },
      { label: 'Drop', value: 'Scorchling Tail' }
    ]
  },
  {
    id: 'scorchhound',
    name: 'Scorchhound',
    category: 'mobs',
    texture: 'textures/item/scorchhound_spawn_egg.png',
    relatedItems: [spawnEgg('scorchhound', 'Scorchhound')],
    description: 'Scorchhounds are powerful hostile creatures in the Molten Depths.',
    details: 'They have 40 health, and their melee attacks throw players through the air. Scorchhounds drop Scorched Meat.',
    stats: [
      { label: 'Behavior', value: 'Hostile' },
      { label: 'Location', value: 'Molten Depths' },
      { label: 'Health', value: '40' },
      { label: 'Drop', value: 'Scorched Meat' }
    ]
  },
  {
    id: 'luminite_fox',
    name: 'Luminite Fox',
    category: 'mobs',
    texture: 'textures/item/luminite_fox_spawn_egg.png',
    relatedItems: [spawnEgg('luminite_fox', 'Luminite Fox')],
    description: 'Luminite Foxes are passive creatures in Rocky Caverns and Tangled Hallow.',
    details: 'Luminite Dust attracts Luminite Foxes. When a player kills one, it has a 25% chance to drop one Luminite Dust.',
    stats: [
      { label: 'Behavior', value: 'Passive' },
      { label: 'Location', value: 'Rocky Caverns, Tangled Hallow' },
      { label: 'Tempt item', value: 'Luminite Dust' }
    ]
  },
  {
    id: 'luminite_golem',
    name: 'Luminite Golem',
    category: 'mobs',
    texture: 'textures/item/luminite_golem_spawn_egg.png',
    relatedItems: [spawnEgg('luminite_golem', 'Luminite Golem')],
    description: 'The Luminite Golem is a slow, armored monster in Rocky Caverns and Tangled Hallow.',
    details: 'It has 40 health, 10 armor, strong knockback resistance, and a heavy attack that launches its target. It drops 3 to 6 Luminite Dust and 1 to 3 Carfstone.',
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
    relatedItems: [spawnEgg('shroomling', 'Shroomling')],
    description: 'Shroomlings are neutral creatures in the Glimmershroom Forest.',
    details: 'Shroomlings attack players who provoke them and alert nearby Shroomlings. They drop 1 to 3 Glimmershrooms before Looting bonuses.',
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
    relatedItems: [spawnEgg('moltener', 'Moltener')],
    description: 'Molteners are passive creatures in the Molten Depths.',
    details: 'Scorched Berries attract Molteners. They can drop Fire Charges or Gold Nuggets.',
    stats: [
      { label: 'Behavior', value: 'Passive' },
      { label: 'Location', value: 'Molten Depths' },
      { label: 'Tempt item', value: 'Scorched Berries' }
    ]
  }
];

const wikiEntriesById = new Map<string, WikiEntry>();

for (const entry of [...CURATED_WIKI_ENTRIES, ...BLOCK_WIKI_ENTRIES, ...ITEM_WIKI_ENTRIES]) {
  if (!wikiEntriesById.has(entry.id)) {
    wikiEntriesById.set(entry.id, entry);
  }
}

export const WIKI_ENTRIES = [...wikiEntriesById.values()];

export const WIKI_ENTRY_REDIRECTS = new Map<string, string>([
  ...WIKI_ENTRIES.flatMap((entry) =>
    (entry.relatedItems ?? []).map((relatedItem): [string, string] => [relatedItem.id, entry.id])
  ),
  ['scorchling_hound', 'scorchling']
]);

export function resolveWikiEntryId(id: string): string {
  return WIKI_ENTRY_REDIRECTS.get(id) ?? id;
}

export const GUIDES: GuideChapter[] = [
  {
    id: 'getting-to-the-caverns',
    title: 'Getting to the caverns',
    summary: 'Buy the Forgotten Tower map, take the key from the tower, and use it on Cracked Bedrock.',
    category: 'Start here',
    steps: [
      {
        title: 'Buy the Forgotten Tower map',
        body: 'Trade with an Expert Cartographer in the Overworld for an explorer map to a nearby Illager tower.'
      },
      {
        title: 'Defeat the Illagers and claim the key',
        body: 'Follow the map to the tower, defeat the Illagers, and loot the Key to the Caverns from the top chest.'
      },
      {
        title: 'Dig down to bedrock',
        body: 'Mine down to the Overworld bedrock floor and look for Cracked Bedrock. Small veins appear throughout the bottom bedrock layer.'
      },
      {
        title: 'Open the gateway',
        body: 'Use the Key on Cracked Bedrock to replace that block with a permanent gateway, then step onto it.',
        note: 'The return gateway is in the Dark Caverns ceiling. Follow the lights down the enclosed ladder shaft. To return, jump into the gateway overhead.'
      }
    ]
  },
  {
    id: 'cavern-compass-navigation',
    title: 'Cavern Compass navigation',
    summary: 'Craft a Cavern Compass so you can find your linked gateway while exploring.',
    category: 'Start here',
    steps: [
      {
        title: 'Gather the materials',
        body: 'Bring a Compass into the Dark Caverns, then mine Luminite Dust and Platinum Pieces.'
      },
      {
        title: 'Craft the Cavern Compass',
        body: 'Craft the Cavern Compass at a Crafting Table using the recipe shown below.',
        recipeEntryIds: ['cavern_compass']
      },
      {
        title: 'Follow it back to your gateway',
        body: 'The needle points to your linked gateway in the Dark Caverns. A normal Compass spins here, so keep the Cavern Compass with you on long trips.',
        note: 'Use the Cavern Compass on a Lodestone to make the needle track that block instead.'
      }
    ]
  },
  {
    id: 'exploring-the-tangled-hallow',
    title: 'Exploring the Tangled Hallow',
    summary: 'Find the lakes and harvest Twistwood. Bring light for hostile mobs.',
    category: 'Explore',
    steps: [
      {
        title: 'Find the forest floor',
        body: 'Overgrown Carfstone, Undersprouts, and dense Twistwood trees mark the biome. Its water is teal, and it has its own music.'
      },
      {
        title: 'Search the surface lakes',
        body: 'Surface lakes cut into open cavern floors. Water Sproutlets grow on the water and replace Lily Pads in this biome.'
      },
      {
        title: 'Farm Twistwood underground',
        body: 'Twistwood Saplings grow without sunlight. Replant them to farm Twistwood Logs and Twistwood Planks inside the dimension. You can also craft Twistwood Doors and Twistwood Trapdoors.'
      },
      {
        title: 'Watch for golems',
        body: 'Camorocks and Luminite Foxes are passive, but Luminite Golems and Skeletons also spawn here. Keep the forest floor lit while you gather wood or build.'
      }
    ]
  },
  {
    id: 'smithing-and-gear-progression',
    title: 'Smithing and gear progression',
    summary: 'Use Luminite Dust to upgrade Diamond gear to Platinum. Then upgrade Platinum with Hellstone, Shroomstone, or Scorchsteel.',
    category: 'Progression',
    steps: [
      {
        title: 'Use Luminite Dust as the template',
        body: 'Put Luminite Dust in the template slot of a standard Smithing Table. Dark Caverns gear does not use Netherite Upgrade Smithing Templates.'
      },
      {
        title: 'Platinum gear',
        body: 'Craft a Platinum Ingot, then use it with Luminite Dust to upgrade Diamond gear at a Smithing Table. Platinum tools have 1,843 durability, 10 mining speed, and 20 enchantability.',
        recipeEntryIds: ['platinum', 'platinum_gear']
      },
      {
        title: 'Hellstone gear',
        body: 'Mine Hellstone Ore for Hellstone Rock and craft Hellstone. Use it with Luminite Dust to upgrade Platinum gear at a Smithing Table. A full armor set blocks all fire and lava damage.',
        recipeEntryIds: ['hellstone', 'hellstone_gear']
      },
      {
        title: 'Shroomstone gear',
        body: 'Trade with Shroomies for Shroomstone Pieces and craft Shroomstone. Use it with Luminite Dust to upgrade Platinum gear at a Smithing Table. A full armor set grants Jump Boost II and blocks all fall damage.',
        recipeEntryIds: ['shroomstone', 'shroomstone_gear']
      },
      {
        title: 'Scorchsteel armor',
        body: 'Craft a Scorchsteel Ingot, then use it with Luminite Dust to upgrade Platinum armor at a Smithing Table. The full set conceals you from hostile monsters after you stand still for 1 second.',
        recipeEntryIds: ['scorchsteel', 'scorchsteel_armor']
      }
    ]
  },
  {
    id: 'surviving-the-molten-depths',
    title: 'Surviving the Molten Depths',
    summary: 'Mine Hellstone, collect Scorched Berries, and hunt Scorchlings for armor materials.',
    category: 'Explore',
    steps: [
      {
        title: 'Eat Scorched Berries for Fire Resistance',
        body: 'Harvest Scorched Berries from bushes on Molten Carfstone. Eating one grants 5 seconds of Fire Resistance.'
      },
      {
        title: 'Mine Hellstone',
        body: 'Hellstone Ore generates inside Molten Carfstone. Mine it with a Diamond Pickaxe or better to collect Hellstone Rock. Hellstone Rock does not burn in fire or lava.'
      },
      {
        title: 'Craft Scorchsteel armor',
        body: 'Defeat Scorchlings for Scorchling Tails and craft a Scorchsteel Ingot. Then use it with Luminite Dust to upgrade Platinum armor at a Smithing Table.',
        recipeEntryIds: ['scorchsteel', 'scorchsteel_armor']
      }
    ]
  },
  {
    id: 'shroomie-trading-and-settlements',
    title: 'Shroomie trading and settlements',
    summary: 'Find Shroomie Houses and trade for Shroomstone Pieces or a Corrupted Pearl.',
    category: 'Explore',
    steps: [
      {
        title: 'Find the Glimmershroom Forest',
        body: 'Follow glowing cyan mushrooms through the caves until you reach glimmergrass. Shroomie Houses generate among giant glimmershrooms.'
      },
      {
        title: 'Trade with Shroomies',
        body: 'Each Shroomie has five common offers and one rare offer. All Shroomies trade 2 Shroomstone Pieces for 1 Diamond. The Corrupted Pearl trade costs 4 Ender Pearls.'
      },
      {
        title: 'Build an underground base',
        body: 'No native monsters spawn in the Glimmershroom Forest. Monsters from nearby biomes and structures can enter it. Shroomlings remain neutral until provoked.'
      }
    ]
  }
];
