import type { GuideCategory, GuideChapter } from '../types/content';

const guideModules = import.meta.glob<GuideChapter>('../content/guides/*.json', {
  eager: true,
  import: 'default'
});

export const GUIDES: GuideChapter[] = Object.values(guideModules)
  .sort((a, b) => (a.order ?? 999) - (b.order ?? 999) || a.title.localeCompare(b.title));

export const GUIDE_CATEGORIES: readonly GuideCategory[] = [
  'Start here',
  'Explore',
  'Progression'
] as const;
