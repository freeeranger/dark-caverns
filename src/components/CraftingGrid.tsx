import React from 'react';
import { CraftingRecipe } from '../data/modData';
import { getTextureUrl } from '../utils/assets';
import { MinecraftTooltip } from './MinecraftTooltip';
import { PixelIcon } from './PixelIcon';

interface CraftingGridProps {
  recipe: CraftingRecipe;
  basePrefix?: string;
}

export const CraftingGrid: React.FC<CraftingGridProps> = ({ recipe }) => {
  return (
    <div className="flex items-center gap-4 py-2 select-none">
      {/* 3x3 Table */}
      <div className="mc-crafting-grid">
        {recipe.slots.map((slot, index) => {
          const slotElement = (
            <div key={index} className="mc-slot">
              {slot?.texture && (
                <img
                  src={getTextureUrl(slot.texture)}
                  alt={slot.name}
                  className="w-6 h-6 pixel-art"
                />
              )}
            </div>
          );

          if (slot) {
            return (
              <MinecraftTooltip key={index} content={slot.name}>
                {slotElement}
              </MinecraftTooltip>
            );
          }

          return slotElement;
        })}
      </div>

      {/* Crafting Arrow */}
      <div className="text-[#828898] flex items-center justify-center">
        <PixelIcon name="arrow-right" className="w-6 h-6" />
      </div>

      {/* Output Slot */}
      <MinecraftTooltip content={recipe.output.name}>
        <div className="mc-slot mc-slot-output relative">
          {recipe.output.texture && (
            <img
              src={getTextureUrl(recipe.output.texture)}
              alt={recipe.output.name}
              className="w-8 h-8 pixel-art"
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
  );
};
