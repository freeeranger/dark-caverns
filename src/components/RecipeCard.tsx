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
    <section className="guide-recipe mc-inset">
      {(title || badge) && (
        <div className="guide-recipe-heading">
          {title && <h3 className="font-pixel text-xs text-white">{title}</h3>}
          {badge && <span className="mc-tag">{badge}</span>}
        </div>
      )}
      {content}
    </section>
  );
};
