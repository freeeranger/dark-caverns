const GITHUB_RAW_BASE =
  'https://raw.githubusercontent.com/freeeranger/dark-caverns/1.21.1-neoforge/src/main/resources/assets/dark_caverns/';
const MC_ASSETS_BASE =
  'https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.21.1/assets/minecraft/';
const GITHUB_BLOB_BASE =
  'https://github.com/freeeranger/dark-caverns/blob/1.21.1-neoforge/src/main/resources/assets/dark_caverns/';

const VANILLA_MAPPING: Record<string, string> = {
  'textures/item/diamond.png': 'textures/item/diamond.png',
  'textures/item/diamond_sword.png': 'textures/item/diamond_sword.png',
  'textures/item/emerald.png': 'textures/item/emerald.png',
  'textures/item/iron_ingot.png': 'textures/item/iron_ingot.png',
  'textures/item/gunpowder.png': 'textures/item/gunpowder.png',
  'textures/item/map.png': 'textures/item/map.png',
  'textures/item/slime_ball.png': 'textures/item/slime_ball.png',
};

/**
 * Returns the remote GitHub URL for an item/block texture.
 * Avoids copying texture assets into the website build.
 */
export function getTextureUrl(texturePath?: string): string {
  if (!texturePath) return '';
  if (texturePath.startsWith('http://') || texturePath.startsWith('https://')) {
    return texturePath;
  }
  // Strip leading ./ or /
  const clean = texturePath.replace(/^\.?\//, '');

  if (VANILLA_MAPPING[clean]) {
    return `${MC_ASSETS_BASE}${VANILLA_MAPPING[clean]}`;
  }

  return `${GITHUB_RAW_BASE}${clean}`;
}

/**
 * Returns the GitHub repository file URL for an item/block.
 */
export function getGithubSourceUrl(texturePath?: string): string {
  if (!texturePath) {
    return 'https://github.com/freeeranger/dark-caverns/tree/1.21.1-neoforge';
  }
  const clean = texturePath.replace(/^\.?\//, '');
  if (VANILLA_MAPPING[clean]) {
    return `https://github.com/InventivetalentDev/minecraft-assets/blob/1.21.1/assets/minecraft/${VANILLA_MAPPING[clean]}`;
  }
  return `${GITHUB_BLOB_BASE}${clean}`;
}
