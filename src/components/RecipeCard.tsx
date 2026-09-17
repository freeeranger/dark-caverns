import React from 'react';
import type { CraftingRecipe, SmithingRecipe } from '../types/content';
import { CraftingGrid } from './CraftingGrid';
import { SmithingTable } from './SmithingTable';

interface RecipeCardProps {
  title?: string;
  badge?: string;
  crafting?: CraftingRecipe;
  smithing?: SmithingRecipe;
  variant?: 'inset' | 'section';
}

export const RecipeCard: React.FC<RecipeCardProps> = ({
  title,
  badge,
  crafting,
  smithing,
  variant = 'inset'
}) => {
  const content = (
    <>
      {crafting && <CraftingGrid recipe={crafting} />}
      {smithing && <SmithingTable recipe={smithing} />}
    </>
  );

  if (variant === 'section') {
    return (
      <div className="space-y-2 pt-2 border-t border-[#232630]">
        {title && <h2 className="font-pixel text-xs text-white">{title}</h2>}
        {content}
      </div>
    );
  }

  return (
    <section className="min-w-0 p-4 mc-inset">
      {(title || badge) && (
        <div className="flex items-center justify-between gap-3 pb-2 border-b border-[#232630]">
          {title && <h3 className="font-pixel text-xs text-white">{title}</h3>}
          {badge && <span className="mc-tag">{badge}</span>}
        </div>
      )}
      {content}
    </section>
  );
};
