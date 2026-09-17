import React, { useEffect, useRef, useState } from 'react';
import { useQueryState, parseAsString } from 'nuqs';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { WikiText } from '../components/WikiText';
import { MasterDetailLayout } from '../components/MasterDetailLayout';
import { SidebarNavGroup } from '../components/SidebarNavGroup';
import { SidebarNavButton } from '../components/SidebarNavButton';
import { ArticleHeader } from '../components/ArticleHeader';
import { RecipeCard } from '../components/RecipeCard';
import { GUIDES, GUIDE_CATEGORIES, resolveWikiEntryId, WIKI_ENTRY_BY_ID, WikiEntry } from '../data/modData';

function getRecipeEntries(ids: string[] | undefined): WikiEntry[] {
  return (ids ?? [])
    .map((id) => WIKI_ENTRY_BY_ID.get(resolveWikiEntryId(id)))
    .filter((entry): entry is WikiEntry => Boolean(entry));
}

export const GuidesPage: React.FC = () => {
  const [guideParam, setGuideParam] = useQueryState(
    'guide',
    parseAsString.withDefault(GUIDES[0].id)
  );
  const [mobileDetailOpen, setMobileDetailOpen] = useState(() => {
    if (typeof window === 'undefined') {
      return false;
    }

    return new URLSearchParams(window.location.search).has('guide') || window.location.hash.length > 1;
  });
  const chapterNavRef = useRef<HTMLElement>(null);
  const articleRef = useRef<HTMLElement>(null);

  // Hash fallback for backwards compatibility
  useEffect(() => {
    const hashGuide = window.location.hash.replace('#', '');
    if (hashGuide && GUIDES.some((g) => g.id === hashGuide)) {
      setGuideParam(hashGuide);
    }
  }, [setGuideParam]);

  const activeGuide = GUIDES.find((g) => g.id === guideParam) || GUIDES[0];

  useEffect(() => {
    if (!mobileDetailOpen || !window.matchMedia('(max-width: 767px)').matches) {
      return;
    }

    const frame = window.requestAnimationFrame(() => {
      articleRef.current?.focus({ preventScroll: true });
      articleRef.current?.scrollIntoView({
        behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth',
        block: 'start'
      });
    });

    return () => window.cancelAnimationFrame(frame);
  }, [activeGuide.id, mobileDetailOpen]);

  const selectGuide = (id: string) => {
    setGuideParam(id);
    setMobileDetailOpen(true);
  };

  const showChapterList = () => {
    setMobileDetailOpen(false);
    window.requestAnimationFrame(() => chapterNavRef.current?.focus());
  };

  const sidebarContent = (
    <nav aria-label="Choose a guide">
      <div className="space-y-4 pt-1">
        {GUIDE_CATEGORIES.map((category) => (
          <SidebarNavGroup key={category} title={category}>
            {GUIDES.filter((guide) => guide.category === category).map((guide) => (
              <li key={guide.id}>
                <SidebarNavButton
                  label={guide.title}
                  isSelected={activeGuide.id === guide.id}
                  onClick={() => selectGuide(guide.id)}
                />
              </li>
            ))}
          </SidebarNavGroup>
        ))}
      </div>
    </nav>
  );

  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/guides/" />

      <MasterDetailLayout
        sidebarTitle="Guides"
        sidebarAriaLabel="Guide chapters"
        sidebarRef={chapterNavRef}
        articleRef={articleRef}
        articleAriaLabel={activeGuide.title}
        mobileDetailOpen={mobileDetailOpen}
        onBack={showChapterList}
        backLabel="Back to guides"
        sidebarContent={sidebarContent}
      >
        <ArticleHeader
          title={activeGuide.title}
          badge={
            <span className="mc-tag">
              {activeGuide.steps.length} {activeGuide.steps.length === 1 ? 'step' : 'steps'}
            </span>
          }
          summary={<WikiText text={activeGuide.summary} hrefPrefix="../wiki/?entry=" />}
        />

        <ol className="grid gap-6 text-sm leading-relaxed text-[#c6cbe0]">
          {activeGuide.steps.map((step, idx) => {
            const recipeEntries = getRecipeEntries(step.recipeEntryIds);

            return (
              <li key={step.title} className="min-w-0 pb-6 border-b border-[#232630] last:border-b-0 last:pb-0">
                <div className="grid grid-cols-[2rem_minmax(0,1fr)] items-center gap-3">
                  <span className="inline-flex w-8 h-8 items-center justify-center bg-[#0c433f] border border-[#00ddc0] text-[#e2fff8] font-pixel text-xs mc-shadow-subtle" aria-hidden="true">
                    {idx + 1}
                  </span>
                  <h2 className="font-pixel text-sm sm:text-base text-white">
                    {step.title}
                  </h2>
                </div>
                <p className="max-w-[72ch] mt-3 text-xs sm:text-sm text-[#a0a7ba] leading-relaxed">
                  <WikiText text={step.body} hrefPrefix="../wiki/?entry=" />
                </p>

                {recipeEntries.length > 0 && (
                  <div className="grid gap-3 mt-4" aria-label={`${step.title} recipes`}>
                    {recipeEntries.flatMap((entry) => [
                      entry.recipe ? (
                        <RecipeCard
                          key={`${entry.id}-crafting`}
                          title={entry.recipe.output.name}
                          badge="Crafting recipe"
                          crafting={entry.recipe}
                        />
                      ) : null,
                      entry.smithing ? (
                        <RecipeCard
                          key={`${entry.id}-smithing`}
                          title={entry.smithing.output.name}
                          badge="Smithing table"
                          smithing={entry.smithing}
                        />
                      ) : null
                    ])}
                  </div>
                )}

                {step.note && (
                  <aside className="grid gap-1.5 max-w-[72ch] mt-4 p-3 bg-[#151b1b] border border-[#28655f]">
                    <span className="font-pixel text-[10px] text-[#55ffaf]">Good to know</span>
                    <p className="text-xs text-[#ffd276]"><WikiText text={step.note} hrefPrefix="../wiki/?entry=" /></p>
                  </aside>
                )}
              </li>
            );
          })}
        </ol>
      </MasterDetailLayout>

      <Footer currentPath="/guides/" />
    </div>
  );
};
