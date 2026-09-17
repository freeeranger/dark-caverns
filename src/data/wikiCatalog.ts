import type { WikiEntry } from './modData';

type BlockDefinition = readonly [id: string, name: string, texture: string, description: string];
type ItemDefinition = readonly [id: string, name: string, category: 'materials' | 'items', description: string, details: string];

function blockDetails(id: string, name: string): string {
  if (id.endsWith('_ore')) return `${name} is the Dark Caverns stone variant of this ore and generates as part of the dimension's underground resource layers.`;
  if (/(slab|stairs|wall)$/.test(id)) return `It extends its matching full block into a shape for floors, roofs, trim, and detailed builds.`;
  if (/(platinum|hellstone|luminite|shroomstone)_block$/.test(id)) return `Use it for compact material storage or as a solid decorative block, then convert it back when the material is needed.`;
  if (id.startsWith('gateway_')) return 'Gateway blocks are created by the dimension-entry system rather than ordinary crafting.';
  if (id.includes('twistwood')) return 'It belongs to the complete Twistwood building set harvested in the Tangled Hallow.';
  if (['glimmergrass', 'glimmergrass_block', 'glimmershroom', 'glimmershroom_block'].includes(id)) return 'It contributes to the cool blue-green vegetation and ambient light of the Glimmershroom Forest.';
  if (['ashy_charred_grass', 'charred_grass', 'scorched_berry_bush'].includes(id)) return 'It is part of the scorched vegetation found across the Molten Depths.';
  if (['undersprouts', 'water_sproutlets', 'overgrown_carfstone'].includes(id)) return 'It is part of the dense ground cover that distinguishes the Tangled Hallow.';
  if (id.includes('molten_carfstone')) return 'It belongs to the warm-toned Molten Carfstone building family found in the Molten Depths.';
  if (id.includes('carfstone')) return 'It belongs to the Carfstone building family native to Rocky Caverns.';
  if (id.startsWith('luminite_')) return 'It provides permanent placed light for routes, rooms, and landmarks underground.';
  return `${name} is a placeable Dark Caverns block.`;
}

const block = ([id, name, texture, description]: BlockDefinition): WikiEntry => ({
  id,
  name,
  category: 'blocks',
  texture: `textures/block/${texture}.png`,
  description,
  details: blockDetails(id, name),
  aliases: name.startsWith('Carfstone ') && name.endsWith(' Ore')
    ? [name.replace('Carfstone ', '')]
    : undefined,
  stats: [{ label: 'Registry type', value: 'Block' }]
});

const item = ([id, name, category, description, details]: ItemDefinition): WikiEntry => ({
  id,
  name,
  category,
  texture: `textures/item/${id}.png`,
  description,
  details,
  stats: [{ label: 'Registry type', value: 'Item' }]
});

const BLOCK_DEFINITIONS: BlockDefinition[] = [
  ['ashy_charred_grass', 'Ashy Charred Grass', 'ashy_charred_grass', 'A pale ground plant found among the ash of the Molten Depths.'],
  ['ashy_molten_carfstone', 'Ashy Molten Carfstone', 'ashy_molten_carfstone_top', 'Molten Carfstone covered by a layer of ash.'],
  ['carfstone', 'Carfstone', 'carfstone', 'The main stone of Rocky Caverns and the base material for its building set.'],
  ['carfstone_brick_slab', 'Carfstone Brick Slab', 'carfstone_bricks', 'A half-height building block made from Carfstone Bricks.'],
  ['carfstone_brick_stairs', 'Carfstone Brick Stairs', 'carfstone_bricks', 'A stair-shaped building block made from Carfstone Bricks.'],
  ['carfstone_brick_wall', 'Carfstone Brick Wall', 'carfstone_bricks', 'A narrow barrier made from Carfstone Bricks.'],
  ['carfstone_bricks', 'Carfstone Bricks', 'carfstone_bricks', 'Cut Carfstone masonry for cavern builds.'],
  ['carfstone_coal_ore', 'Carfstone Coal Ore', 'carfstone_coal_ore', 'Coal Ore embedded in Carfstone.'],
  ['carfstone_diamond_ore', 'Carfstone Diamond Ore', 'carfstone_diamond_ore', 'Diamond Ore embedded in Carfstone.'],
  ['carfstone_gold_ore', 'Carfstone Gold Ore', 'carfstone_gold_ore', 'Gold Ore embedded in Carfstone.'],
  ['carfstone_iron_ore', 'Carfstone Iron Ore', 'carfstone_iron_ore', 'Iron Ore embedded in Carfstone.'],
  ['carfstone_lapis_ore', 'Carfstone Lapis Ore', 'carfstone_lapis_ore', 'Lapis Lazuli Ore embedded in Carfstone.'],
  ['carfstone_luminite_ore', 'Carfstone Luminite Ore', 'carfstone_luminite_ore', 'The primary source of Luminite Dust in the Dark Caverns.'],
  ['carfstone_platinum_ore', 'Carfstone Platinum Ore', 'carfstone_platinum_ore', 'A Dark Caverns ore that yields Platinum Pieces.'],
  ['carfstone_redstone_ore', 'Carfstone Redstone Ore', 'carfstone_redstone_ore', 'Redstone Ore embedded in Carfstone.'],
  ['carfstone_slab', 'Carfstone Slab', 'carfstone', 'A half-height building block made from Carfstone.'],
  ['carfstone_stairs', 'Carfstone Stairs', 'carfstone', 'A stair-shaped building block made from Carfstone.'],
  ['carfstone_wall', 'Carfstone Wall', 'carfstone', 'A narrow barrier made from Carfstone.'],
  ['charred_grass', 'Charred Grass', 'charred_grass', 'Dark ground cover native to the Molten Depths.'],
  ['chiseled_carfstone_bricks', 'Chiseled Carfstone Bricks', 'chiseled_carfstone_bricks', 'A decorative carved form of Carfstone Bricks.'],
  ['chiseled_molten_carfstone_bricks', 'Chiseled Molten Carfstone Bricks', 'chiseled_molten_carfstone_bricks', 'A decorative carved form of Molten Carfstone Bricks.'],
  ['cracked_bedrock', 'Cracked Bedrock', 'cracked_bedrock', 'A rare block in the Overworld floor that accepts the Key to the Caverns.'],
  ['gateway_to_the_caverns', 'Gateway to the Caverns', 'gateway_to_the_caverns', 'The permanent gateway opened in Cracked Bedrock by the Key to the Caverns.'],
  ['gateway_to_the_overworld', 'Gateway to the Overworld', 'gateway_to_the_caverns', 'The return gateway generated in the Dark Caverns ceiling.'],
  ['glimmergrass', 'Glimmergrass', 'glimmergrass', 'Blue-green ground cover found in the Glimmershroom Forest.'],
  ['glimmergrass_block', 'Glimmergrass Block', 'glimmergrass_block_top', 'The glowing forest floor of the Glimmershroom Forest.'],
  ['glimmershroom', 'Glimmershroom', 'glimmershroom', 'A luminous fungus that grows throughout the Glimmershroom Forest.'],
  ['glimmershroom_block', 'Glimmershroom Block', 'glimmershroom_block', 'The luminous cap block of a giant Glimmershroom.'],
  ['hellstone_block', 'Block of Hellstone', 'hellstone_block', 'A compact storage and building block made from Hellstone.'],
  ['hellstone_ore', 'Hellstone Ore', 'hellstone_ore', 'An ore in the Molten Depths that drops Hellstone Rock.'],
  ['luminite_block', 'Block of Luminite', 'luminite_block', 'A bright storage and building block made from Luminite Dust.'],
  ['luminite_lantern', 'Luminite Lantern', 'luminite_lantern', 'A hanging or standing light made with Luminite.'],
  ['luminite_torch', 'Luminite Torch', 'luminite_torch', 'A bright torch used to mark routes through the Dark Caverns.'],
  ['luminite_wall_torch', 'Wall-mounted Luminite Torch', 'luminite_torch', 'The wall-mounted block form of a Luminite Torch.'],
  ['molten_carfstone', 'Molten Carfstone', 'molten_carfstone', 'The main stone of the Molten Depths and the base material for its building set.'],
  ['molten_carfstone_brick_slab', 'Molten Carfstone Brick Slab', 'molten_carfstone_bricks', 'A half-height building block made from Molten Carfstone Bricks.'],
  ['molten_carfstone_brick_stairs', 'Molten Carfstone Brick Stairs', 'molten_carfstone_bricks', 'A stair-shaped building block made from Molten Carfstone Bricks.'],
  ['molten_carfstone_brick_wall', 'Molten Carfstone Brick Wall', 'molten_carfstone_bricks', 'A narrow barrier made from Molten Carfstone Bricks.'],
  ['molten_carfstone_bricks', 'Molten Carfstone Bricks', 'molten_carfstone_bricks', 'Cut Molten Carfstone masonry for warm-toned cavern builds.'],
  ['molten_carfstone_slab', 'Molten Carfstone Slab', 'molten_carfstone', 'A half-height building block made from Molten Carfstone.'],
  ['molten_carfstone_stairs', 'Molten Carfstone Stairs', 'molten_carfstone', 'A stair-shaped building block made from Molten Carfstone.'],
  ['molten_carfstone_wall', 'Molten Carfstone Wall', 'molten_carfstone', 'A narrow barrier made from Molten Carfstone.'],
  ['overgrown_carfstone', 'Overgrown Carfstone', 'overgrown_carfstone_top', 'Carfstone covered by the plant growth of the Tangled Hallow.'],
  ['platinum_block', 'Block of Platinum', 'platinum_block', 'A compact storage and building block made from Platinum Ingots.'],
  ['scorched_berry_bush', 'Scorched Berry Bush', 'scorched_berry_bush_stage3', 'A Molten Depths plant harvested for Scorched Berries.'],
  ['shroomstone_block', 'Block of Shroomstone', 'shroomstone_block', 'A compact storage and building block made from Shroomstone.'],
  ['smooth_carfstone', 'Smooth Carfstone', 'smooth_carfstone', 'A polished form of Carfstone.'],
  ['smooth_carfstone_slab', 'Smooth Carfstone Slab', 'smooth_carfstone', 'A half-height building block made from Smooth Carfstone.'],
  ['smooth_carfstone_stairs', 'Smooth Carfstone Stairs', 'smooth_carfstone', 'A stair-shaped building block made from Smooth Carfstone.'],
  ['smooth_carfstone_wall', 'Smooth Carfstone Wall', 'smooth_carfstone', 'A narrow barrier made from Smooth Carfstone.'],
  ['smooth_molten_carfstone', 'Smooth Molten Carfstone', 'smooth_molten_carfstone', 'A polished form of Molten Carfstone.'],
  ['smooth_molten_carfstone_slab', 'Smooth Molten Carfstone Slab', 'smooth_molten_carfstone', 'A half-height building block made from Smooth Molten Carfstone.'],
  ['smooth_molten_carfstone_stairs', 'Smooth Molten Carfstone Stairs', 'smooth_molten_carfstone', 'A stair-shaped building block made from Smooth Molten Carfstone.'],
  ['smooth_molten_carfstone_wall', 'Smooth Molten Carfstone Wall', 'smooth_molten_carfstone', 'A narrow barrier made from Smooth Molten Carfstone.'],
  ['stripped_twistwood_log', 'Stripped Twistwood Log', 'stripped_twistwood_log', 'A Twistwood Log with its bark removed.'],
  ['stripped_twistwood_wood', 'Stripped Twistwood Wood', 'stripped_twistwood_log', 'A bark-free Twistwood block with wood grain on every side.'],
  ['twistwood_door', 'Twistwood Door', 'twistwood_door_bottom', 'A door crafted from Twistwood Planks.'],
  ['twistwood_leaves', 'Twistwood Leaves', 'twistwood_leaves', 'The dense teal foliage of Twistwood trees.'],
  ['twistwood_log', 'Twistwood Log', 'twistwood_log', 'A log harvested from Twistwood trees in the Tangled Hallow.'],
  ['twistwood_planks', 'Twistwood Planks', 'twistwood_planks', 'Building planks processed from Twistwood Logs.'],
  ['twistwood_sapling', 'Twistwood Sapling', 'twistwood_sapling', 'A sapling that can grow into Twistwood without sunlight.'],
  ['twistwood_trapdoor', 'Twistwood Trapdoor', 'twistwood_trapdoor', 'A trapdoor crafted from Twistwood Planks.'],
  ['twistwood_wood', 'Twistwood Wood', 'twistwood_log', 'A Twistwood block with bark on every side.'],
  ['undersprouts', 'Undersprouts', 'undersprouts', 'Low vegetation that grows across the Tangled Hallow floor.'],
  ['water_sproutlets', 'Water Sproutlets', 'water_sproutlets', 'Small plants that grow on Tangled Hallow lakes.']
];

const ITEM_DEFINITIONS: ItemDefinition[] = [
  ['scorched_berries', 'Scorched Berries', 'items', 'Berries harvested from Scorched Berry Bushes.', 'Eating them grants a short period of Fire Resistance. They also attract Molteners.'],
  ['luminite_dust', 'Luminite Dust', 'materials', 'A luminous material mined from Carfstone Luminite Ore.', 'It is the shared template item for Dark Caverns smithing upgrades and a component in several utility recipes.'],
  ['platinum_piece', 'Platinum Piece', 'materials', 'A fragment dropped by Carfstone Platinum Ore.', 'Combine Platinum Pieces into Platinum Ingots for the first Dark Caverns equipment tier.'],
  ['shroomstone_piece', 'Shroomstone Piece', 'materials', 'A material obtained by trading with Shroomies.', 'Combine Shroomstone Pieces into Shroomstone for equipment upgrades.'],
  ['corrupted_pearl', 'Corrupted Pearl', 'items', 'A throwable pearl that moves a nearby creature to its landing point.', 'Shroomies can offer this unusual utility item as a rare trade.'],
  ['shroombomb', 'Shroombomb', 'items', 'A throwable explosive that detonates on impact.', 'Its explosion is smaller than TNT and can be adjusted in the server configuration.'],
  ['throwable_luminite_torch', 'Throwable Luminite Torch', 'items', 'A Luminite Torch that can be placed at range.', 'It places a torch on a valid surface where it lands or drops as an item if placement fails.'],
  ['platinum_ingot', 'Platinum Ingot', 'materials', 'The upgrade material for Platinum equipment.', 'Use it with Luminite Dust at a Smithing Table to upgrade Diamond equipment.'],
  ['scorched_meat', 'Scorched Meat', 'items', 'A food item dropped by Scorchhounds.', 'It is a creature drop from the Molten Depths.'],
  ['hellstone', 'Hellstone', 'materials', 'The upgrade material for Hellstone equipment.', 'It is made from the drops of Hellstone Ore and does not burn in fire or lava.'],
  ['hellstone_rock', 'Hellstone Rock', 'materials', 'A fire-resistant material dropped by Hellstone Ore.', 'It is refined into Hellstone for equipment upgrades.'],
  ['key_to_the_caverns', 'Key to the Caverns', 'items', 'The key that opens Cracked Bedrock into a gateway.', 'Find it in the top chest of a Forgotten Tower. It is consumed when the gateway is created.'],
  ['scorchling_tail', 'Scorchling Tail', 'materials', 'A crafting material dropped by Scorchlings.', 'It is refined into Scorchsteel Ingots for Scorchsteel armor.'],
  ['scorchsteel_ingot', 'Scorchsteel Ingot', 'materials', 'The upgrade material for Scorchsteel armor.', 'Use it with Luminite Dust to upgrade Platinum armor at a Smithing Table.']
];

const CONSOLIDATED_MATERIAL_ITEM_IDS = new Set([
  'luminite_dust',
  'platinum_piece',
  'platinum_ingot',
  'hellstone_rock',
  'shroomstone_piece',
  'scorchling_tail',
  'scorchsteel_ingot'
]);

type Tier = 'platinum' | 'hellstone' | 'shroomstone';
type Tool = 'sword' | 'axe' | 'pickaxe' | 'shovel' | 'hoe';
type ArmorPiece = 'helmet' | 'chestplate' | 'leggings' | 'boots';

const titleCase = (value: string) => value.charAt(0).toUpperCase() + value.slice(1);

const TOOL_STATS: Record<Tier, readonly [durability: string, enchantability: string, trait: string]> = {
  platinum: ['1,843', '20', 'Fast mining and a high enchantability value.'],
  hellstone: ['1,912', '15', 'Fire-resistant; fully charged attacks ignite targets for 8 seconds.'],
  shroomstone: ['1,912', '20', 'Fully charged attacks launch targets upward.']
};

const toolEntry = (tier: Tier, tool: Tool): WikiEntry => {
  const tierName = titleCase(tier);
  const toolName = titleCase(tool);
  const [durability, enchantability, trait] = TOOL_STATS[tier];
  const baseTier = tier === 'platinum' ? 'diamond' : 'platinum';
  const addition = tier === 'platinum' ? 'platinum_ingot' : tier;
  return {
    id: `${tier}_${tool}`,
    name: `${tierName} ${toolName}`,
    category: 'gear',
    texture: `textures/item/${tier}_${tool}.png`,
    description: `The ${toolName.toLowerCase()} in the ${tierName} equipment tier.`,
    details: trait,
    stats: [
      { label: 'Durability', value: durability },
      { label: 'Mining speed', value: '10' },
      { label: 'Enchantability', value: enchantability },
      { label: 'Registry type', value: 'Item' }
    ],
    smithing: {
      template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
      base: { name: `${titleCase(baseTier)} ${toolName}`, texture: `textures/item/${baseTier}_${tool}.png` },
      addition: { name: tier === 'platinum' ? 'Platinum Ingot' : tierName, texture: `textures/item/${addition}.png` },
      output: { name: `${tierName} ${toolName}`, texture: `textures/item/${tier}_${tool}.png` }
    }
  };
};

const ARMOR_EFFECTS: Record<'platinum' | 'hellstone' | 'shroomstone' | 'scorchsteel', string> = {
  platinum: 'A durable armor tier with 2.5 toughness and 20 enchantability.',
  hellstone: 'Each equipped piece reduces fire and lava damage by 25%; the full set prevents it.',
  shroomstone: 'Each equipped piece reduces fall damage by 25%; the full set also grants Jump Boost II.',
  scorchsteel: 'The full set conceals you from hostile creatures after you stand still for 1 second.'
};

const armorEntry = (tier: keyof typeof ARMOR_EFFECTS, piece: ArmorPiece): WikiEntry => {
  const tierName = titleCase(tier);
  const pieceName = titleCase(piece);
  const defense = { helmet: '3', chestplate: '8', leggings: '6', boots: '3' }[piece];
  const baseTier = tier === 'platinum' ? 'diamond' : 'platinum';
  const addition = tier === 'platinum' ? 'platinum_ingot' : tier === 'scorchsteel' ? 'scorchsteel_ingot' : tier;
  return {
    id: `${tier}_${piece}`,
    name: `${tierName} ${pieceName}`,
    category: 'gear',
    texture: `textures/item/${tier}_${piece}.png`,
    description: `The ${pieceName.toLowerCase()} in the ${tierName} armor set.`,
    details: ARMOR_EFFECTS[tier],
    stats: [
      { label: 'Armor', value: defense },
      { label: 'Toughness', value: '2.5' },
      { label: 'Enchantability', value: '20' },
      { label: 'Registry type', value: 'Item' }
    ],
    smithing: {
      template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
      base: { name: `${titleCase(baseTier)} ${pieceName}`, texture: `textures/item/${baseTier}_${piece}.png` },
      addition: {
        name: tier === 'platinum' ? 'Platinum Ingot' : tier === 'scorchsteel' ? 'Scorchsteel Ingot' : tierName,
        texture: `textures/item/${addition}.png`
      },
      output: { name: `${tierName} ${pieceName}`, texture: `textures/item/${tier}_${piece}.png` }
    }
  };
};

export const MOD_BLOCK_IDS = BLOCK_DEFINITIONS.map(([id]) => id);
export const MOD_ITEM_IDS = [
  ...ITEM_DEFINITIONS.map(([id]) => id),
  ...(['platinum', 'hellstone', 'shroomstone'] as const).flatMap((tier) =>
    (['sword', 'axe', 'pickaxe', 'shovel', 'hoe'] as const).map((tool) => `${tier}_${tool}`)
  ),
  ...(['platinum', 'hellstone', 'shroomstone', 'scorchsteel'] as const).flatMap((tier) =>
    (['helmet', 'chestplate', 'leggings', 'boots'] as const).map((piece) => `${tier}_${piece}`)
  ),
  ...['scorchling', 'luminite_golem', 'shroomling', 'shroomie', 'luminite_fox', 'camorock', 'scorchhound', 'moltener'].map((id) => `${id}_spawn_egg`),
  'luminite_helmet',
  'shroomstone'
];

export const BLOCK_WIKI_ENTRIES = BLOCK_DEFINITIONS.map(block);

export const ITEM_WIKI_ENTRIES: WikiEntry[] = [
  ...ITEM_DEFINITIONS.filter(([id]) => !CONSOLIDATED_MATERIAL_ITEM_IDS.has(id)).map(item),
  ...(['platinum', 'hellstone', 'shroomstone'] as const).flatMap((tier) =>
    (['sword', 'axe', 'pickaxe', 'shovel', 'hoe'] as const).map((tool) => toolEntry(tier, tool))
  ),
  ...(['platinum', 'hellstone', 'shroomstone', 'scorchsteel'] as const).flatMap((tier) =>
    (['helmet', 'chestplate', 'leggings', 'boots'] as const).map((piece) => armorEntry(tier, piece))
  ),
  {
    id: 'luminite_helmet',
    name: 'Luminite Helmet',
    category: 'gear',
    texture: 'textures/item/luminite_helmet.png',
    description: 'A helmet that creates dynamic light around its wearer.',
    details: 'The lighting effect is client-side and can be disabled in the client configuration.',
    stats: [
      { label: 'Armor', value: '2' },
      { label: 'Enchantability', value: '15' },
      { label: 'Registry type', value: 'Item' }
    ]
  }
];
