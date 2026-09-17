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

export type WikiCategory = 'biomes' | 'blocks' | 'materials' | 'gear' | 'items' | 'mobs';

export interface WikiEntry {
  id: string;
  name: string;
  category: WikiCategory;
  texture: string;
  description: string;
  details: string;
  stats?: { label: string; value: string }[];
  recipe?: CraftingRecipe;
  smithing?: SmithingRecipe;
  aliases?: string[];
  registryId?: string | null;
}

export interface GuideStep {
  title: string;
  body: string;
  note?: string;
  recipeEntryIds?: string[];
}

export type GuideCategory = 'Start here' | 'Explore' | 'Progression';

export interface GuideChapter {
  id: string;
  title: string;
  summary: string;
  category: GuideCategory;
  steps: GuideStep[];
}
