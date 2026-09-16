import React from 'react';
import { SmithingRecipe } from '../data/modData';
import { MinecraftTooltip } from './MinecraftTooltip';
import { PixelIcon } from './PixelIcon';
import { TextureImage } from './TextureImage';

interface SmithingTableProps {
  recipe: SmithingRecipe;
  basePrefix?: string;
}

interface SmithingSlotProps {
  label: string;
  name: string;
  texture?: string;
  output?: boolean;
}

const SmithingSlot: React.FC<SmithingSlotProps> = ({ label, name, texture, output = false }) => (
  <div className="flex flex-col items-center gap-1">
    <MinecraftTooltip content={name}>
      <div className={`mc-slot ${output ? 'mc-slot-output' : ''}`}>
        {texture && (
          <TextureImage
            texture={texture}
            alt={name}
            className={`${output ? 'h-8 w-8' : 'h-6 w-6'} pixel-art`}
            width={output ? 32 : 24}
            height={output ? 32 : 24}
          />
        )}
      </div>
    </MinecraftTooltip>
    <span className={`text-[10px] font-mono ${output ? 'text-[#55ffaf]' : 'text-[#8e95a8]'}`}>
      {label}
    </span>
  </div>
);

export const SmithingTable: React.FC<SmithingTableProps> = ({ recipe }) => {
  const summary = `${recipe.template.name} + ${recipe.base.name} + ${recipe.addition.name} makes ${recipe.output.name}`;

  return (
    <figure className="max-w-full space-y-2">
      <div className="max-w-full overflow-x-auto pb-1">
        <div className="inline-flex min-w-max items-center gap-2 select-none py-2" aria-label={summary}>
          <SmithingSlot label="Template" name={recipe.template.name} texture={recipe.template.texture} />
          <PixelIcon name="plus" className="w-3.5 h-3.5 text-[#686f80]" aria-hidden="true" />
          <SmithingSlot label="Base" name={recipe.base.name} texture={recipe.base.texture} />
          <PixelIcon name="plus" className="w-3.5 h-3.5 text-[#686f80]" aria-hidden="true" />
          <SmithingSlot label="Material" name={recipe.addition.name} texture={recipe.addition.texture} />
          <div className="text-[#828898] flex items-center justify-center px-1" aria-hidden="true">
            <PixelIcon name="arrow-right" className="w-5 h-5" />
          </div>
          <SmithingSlot
            label="Result"
            name={recipe.output.name}
            texture={recipe.output.texture}
            output
          />
        </div>
      </div>
      <figcaption className="recipe-summary">{summary}</figcaption>
    </figure>
  );
};
