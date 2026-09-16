import React from 'react';
import { SmithingRecipe } from '../data/modData';
import { getTextureUrl } from '../utils/assets';
import { MinecraftTooltip } from './MinecraftTooltip';
import { PixelIcon } from './PixelIcon';

interface SmithingTableProps {
  recipe: SmithingRecipe;
  basePrefix?: string;
}

export const SmithingTable: React.FC<SmithingTableProps> = ({ recipe }) => {
  return (
    <div className="inline-flex items-center gap-2 select-none py-2">
      {/* Template Slot */}
      <div className="flex flex-col items-center gap-1">
        <MinecraftTooltip content={recipe.template.name}>
          <div className="mc-slot">
            {recipe.template.texture && (
              <img
                src={getTextureUrl(recipe.template.texture)}
                alt={recipe.template.name}
                className="w-6 h-6 pixel-art"
              />
            )}
          </div>
        </MinecraftTooltip>
        <span className="text-[10px] font-mono text-[#787f90]">Template</span>
      </div>

      <PixelIcon name="plus" className="w-3.5 h-3.5 text-[#686f80]" />

      {/* Base Slot */}
      <div className="flex flex-col items-center gap-1">
        <MinecraftTooltip content={recipe.base.name}>
          <div className="mc-slot">
            {recipe.base.texture && (
              <img
                src={getTextureUrl(recipe.base.texture)}
                alt={recipe.base.name}
                className="w-6 h-6 pixel-art"
              />
            )}
          </div>
        </MinecraftTooltip>
        <span className="text-[10px] font-mono text-[#787f90]">Base</span>
      </div>

      <PixelIcon name="plus" className="w-3.5 h-3.5 text-[#686f80]" />

      {/* Addition Slot */}
      <div className="flex flex-col items-center gap-1">
        <MinecraftTooltip content={recipe.addition.name}>
          <div className="mc-slot">
            {recipe.addition.texture && (
              <img
                src={getTextureUrl(recipe.addition.texture)}
                alt={recipe.addition.name}
                className="w-6 h-6 pixel-art"
              />
            )}
          </div>
        </MinecraftTooltip>
        <span className="text-[10px] font-mono text-[#787f90]">Ingot</span>
      </div>

      <div className="text-[#828898] flex items-center justify-center px-1">
        <PixelIcon name="arrow-right" className="w-5 h-5" />
      </div>

      {/* Output Slot */}
      <div className="flex flex-col items-center gap-1">
        <MinecraftTooltip content={recipe.output.name}>
          <div className="mc-slot mc-slot-output">
            {recipe.output.texture && (
              <img
                src={getTextureUrl(recipe.output.texture)}
                alt={recipe.output.name}
                className="w-8 h-8 pixel-art"
              />
            )}
          </div>
        </MinecraftTooltip>
        <span className="text-[10px] font-mono text-[#55ffaf]">Result</span>
      </div>
    </div>
  );
};
