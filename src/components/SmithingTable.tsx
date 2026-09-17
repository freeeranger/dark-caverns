import React from 'react';
import { SmithingRecipe } from '../data/modData';
import { MinecraftTooltip } from './MinecraftTooltip';
import { PixelIcon } from './PixelIcon';
import { TextureImage } from './TextureImage';

interface SmithingTableProps {
  recipe: SmithingRecipe;
}

interface SmithingSlotProps {
  name: string;
  texture?: string;
  output?: boolean;
}

const SmithingSlot: React.FC<SmithingSlotProps> = ({ name, texture }) => (
  <MinecraftTooltip content={name}>
    <div className={`mc-slot`}>
      {texture && (
        <TextureImage
          texture={texture}
          alt={name}
          className="size-6 pixel-art"
          width={24}
          height={24}
        />
      )}
    </div>
  </MinecraftTooltip>
);

export const SmithingTable: React.FC<SmithingTableProps> = ({ recipe }) => {
  const summary = `Use ${recipe.template.name} with ${recipe.base.name} and ${recipe.addition.name} to make ${recipe.output.name}.`;

  return (
    <figure className="max-w-full">
      <div className="max-w-full overflow-x-auto">
        <div className="inline-flex min-w-max items-center gap-3 select-none pt-2" aria-label={summary}>
          <SmithingSlot name={recipe.template.name} texture={recipe.template.texture} />
          <PixelIcon name="plus" className="w-3.5 h-3.5 text-[#686f80]" aria-hidden="true" />
          <SmithingSlot name={recipe.base.name} texture={recipe.base.texture} />
          <PixelIcon name="plus" className="w-3.5 h-3.5 text-[#686f80]" aria-hidden="true" />
          <SmithingSlot name={recipe.addition.name} texture={recipe.addition.texture} />
          <div className="text-[#828898] flex items-center justify-center px-1" aria-hidden="true">
            <PixelIcon name="arrow-right" className="w-6 h-6" />
          </div>
          <SmithingSlot
            name={recipe.output.name}
            texture={recipe.output.texture}
          />
        </div>
      </div>
    </figure>
  );
};
