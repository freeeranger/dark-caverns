import React from 'react';
import { CraftingRecipe } from '../data/modData';
import { MinecraftTooltip } from './MinecraftTooltip';
import { PixelIcon } from './PixelIcon';
import { TextureImage } from './TextureImage';

interface CraftingGridProps {
  recipe: CraftingRecipe;
}

const GRID_POSITIONS = ['top-left', 'top', 'top-right', 'left', 'center', 'right', 'bottom-left', 'bottom', 'bottom-right'];

const ITEM_PLURALS: Record<string, string> = {
  Diamond: 'Diamonds',
  Emerald: 'Emeralds',
  Gunpowder: 'Gunpowder',
  'Hellstone Rock': 'Hellstone Rocks',
  'Iron Ingot': 'Iron Ingots',
  'Luminite Dust': 'Luminite Dust',
  'Luminite Torch': 'Luminite Torches',
  Mushroom: 'Mushrooms',
  'Platinum Piece': 'Platinum Pieces',
  'Scorchling Tail': 'Scorchling Tails',
  Shroombomb: 'Shroombombs',
  'Shroomstone Piece': 'Shroomstone Pieces',
  'Throwable Luminite Torch': 'Throwable Luminite Torches'
};

function countedItem(name: string, count: number): string {
  return `${count} ${count === 1 ? name : ITEM_PLURALS[name] ?? name}`;
}

function ingredientSummary(recipe: CraftingRecipe): string {
  const counts = new Map<string, number>();

  recipe.slots.forEach((slot) => {
    if (slot) {
      counts.set(slot.name, (counts.get(slot.name) ?? 0) + (slot.count ?? 1));
    }
  });

  const ingredients = Array.from(counts.entries()).map(([name, count]) => countedItem(name, count));

  if (ingredients.length < 2) {
    return ingredients[0] ?? '';
  }

  if (ingredients.length === 2) {
    return `${ingredients[0]} and ${ingredients[1]}`;
  }

  return `${ingredients.slice(0, -1).join(', ')}, and ${ingredients[ingredients.length - 1]}`;
}

export const CraftingGrid: React.FC<CraftingGridProps> = ({ recipe }) => {
  const outputCount = recipe.output.count ?? 1;
  const ingredients = `Use ${ingredientSummary(recipe)} to make ${countedItem(recipe.output.name, outputCount)}.`;
  const summary = recipe.shapeless
    ? `${ingredients} The ingredients can go in any crafting-grid slots.`
    : ingredients;

  return (
    <figure className="max-w-full">
      <div className="flex max-w-full flex-wrap items-center gap-4 py-2 select-none" aria-label={summary}>
        <div className="mc-crafting-grid">
          {GRID_POSITIONS.map((position, index) => {
            const slot = recipe.slots[index];
            const slotElement = (
              <div className="mc-slot">
                {slot?.texture && (
                  <TextureImage
                    texture={slot.texture}
                    alt={slot.name}
                    className="w-6 h-6 pixel-art"
                    width={24}
                    height={24}
                  />
                )}
              </div>
            );

            return slot ? (
              <MinecraftTooltip key={position} content={slot.name}>
                {slotElement}
              </MinecraftTooltip>
            ) : (
              <React.Fragment key={position}>{slotElement}</React.Fragment>
            );
          })}
        </div>

        <div className="text-[#828898] flex items-center justify-center" aria-hidden="true">
          <PixelIcon name="arrow-right" className="w-6 h-6" />
        </div>

        <MinecraftTooltip content={recipe.output.name}>
          <div className="mc-slot mc-slot-output relative">
            {recipe.output.texture && (
              <TextureImage
                texture={recipe.output.texture}
                alt={recipe.output.name}
                className="w-8 h-8 pixel-art"
                width={32}
                height={32}
              />
            )}
            {(recipe.output.count ?? 1) > 1 && (
              <span className="absolute bottom-1 right-1 font-pixel text-xs text-[#ffffa0] mc-shadow">
                {recipe.output.count}
              </span>
            )}
          </div>
        </MinecraftTooltip>
      </div>
    </figure>
  );
};
