import type { GuideCategory, GuideChapter } from '../types/content';

const guideModules = import.meta.glob<GuideChapter>('../content/guides/*.json', {
  eager: true,
  import: 'default'
});

export const GUIDES: GuideChapter[] = Object.entries(guideModules)
  .sort(([pathA], [pathB]) => pathA.localeCompare(pathB))
  .map(([, guide]) => guide);

export const GUIDE_CATEGORIES: readonly GuideCategory[] = [
  'Start here',
  'Explore',
  'Progression'
] as const;
