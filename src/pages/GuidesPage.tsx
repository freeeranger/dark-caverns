import React, { useEffect, useRef, useState } from 'react';
import { useQueryState, parseAsString } from 'nuqs';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { CraftingGrid } from '../components/CraftingGrid';
import { SmithingTable } from '../components/SmithingTable';
import { PixelIcon } from '../components/PixelIcon';
import { WikiText } from '../components/WikiText';
import { GUIDES, WIKI_ENTRIES, WikiEntry } from '../data/modData';

const WIKI_ENTRY_BY_ID = new Map(WIKI_ENTRIES.map((entry) => [entry.id, entry]));
const WIKI_ENTRY_ID_BY_NAME = new Map(WIKI_ENTRIES.map((entry) => [entry.name.toLocaleLowerCase(), entry.id]));
const GUIDE_CATEGORIES = ['Start here', 'Explore', 'Progression'] as const;

function wikiHrefForName(name: string, fallbackId: string): string {
  const id = WIKI_ENTRY_ID_BY_NAME.get(name.toLocaleLowerCase()) ?? fallbackId;
  return `../wiki/?entry=${encodeURIComponent(id)}`;
}

function getRecipeEntries(ids: string[] | undefined): WikiEntry[] {
  return (ids ?? [])
    .map((id) => WIKI_ENTRY_BY_ID.get(id))
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

  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/guides/" />

      <main className="flex-1 min-w-0 max-w-6xl w-full mx-auto px-4 sm:px-6 py-8">
        <div className="grid min-w-0 md:grid-cols-12 gap-6 items-start">
          {/* Sidebar */}
          <aside
            ref={chapterNavRef}
            tabIndex={-1}
            className={`${mobileDetailOpen ? 'hidden' : 'block'} min-w-0 md:col-span-4 mc-box p-4 space-y-3 md:sticky md:top-20 md:block`}
            aria-label="Guide chapters"
          >
            <div className="mc-panel-header -mx-4 -mt-4 p-3 border-b-2 border-black">
              <span className="font-pixel text-xs sm:text-sm text-white">Guides</span>
            </div>

            <nav aria-label="Choose a guide">
              <div className="space-y-4 pt-1">
                {GUIDE_CATEGORIES.map((category) => (
                  <section key={category}>
                    <h2 className="mb-1.5 px-1 font-pixel text-[10px] uppercase tracking-wide text-[#8e95a8]">
                      {category}
                    </h2>
                    <ul className="space-y-1.5">
                      {GUIDES.filter((guide) => guide.category === category).map((guide) => {
                        const isSelected = activeGuide.id === guide.id;
                        return (
                          <li key={guide.id}>
                            <button
                              type="button"
                              aria-current={isSelected ? 'page' : undefined}
                              onClick={() => selectGuide(guide.id)}
                              className={`w-full min-h-11 text-left px-3 py-2 border transition-none cursor-pointer font-pixel text-xs ${
                                isSelected
                                  ? 'bg-[#22252d] border-[#55ffaf] text-[#ffffa0]'
                                  : 'bg-[#15161a] border-[#22242a] text-[#b8bdcb] hover:bg-[#1c1e24] hover:text-white'
                              }`}
                            >
                              {guide.title}
                            </button>
                          </li>
                        );
                      })}
                    </ul>
                  </section>
                ))}
              </div>
            </nav>
          </aside>

          {/* Active Guide Content */}
          <article
            ref={articleRef}
            tabIndex={-1}
            className={`${mobileDetailOpen ? 'block' : 'hidden'} min-w-0 scroll-mt-32 md:col-span-8 mc-box p-6 sm:p-8 space-y-6 md:block md:scroll-mt-20`}
            aria-label={activeGuide.title}
          >
            <button
              type="button"
              onClick={showChapterList}
              className="mc-btn min-h-11 gap-2 px-3 text-xs md:hidden"
            >
              <PixelIcon name="arrow-left" className="h-4 w-4" />
              <span>Back to guides</span>
            </button>
            <div className="border-b border-[#232630] pb-4">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <h1 className="font-pixel text-2xl sm:text-3xl text-white mc-shadow font-bold break-words">
                  <WikiText text={activeGuide.title} hrefPrefix="../wiki/?entry=" />
                </h1>
                <span className="mc-tag shrink-0">
                  {activeGuide.steps.length} {activeGuide.steps.length === 1 ? 'step' : 'steps'}
                </span>
              </div>
              <p className="text-xs sm:text-sm text-[#a0a7ba] mt-1.5 leading-relaxed">
                <WikiText text={activeGuide.summary} hrefPrefix="../wiki/?entry=" />
              </p>
            </div>

            <ol className="guide-steps text-sm leading-relaxed text-[#c6cbe0]">
              {activeGuide.steps.map((step, idx) => {
                const recipeEntries = getRecipeEntries(step.recipeEntryIds);

                return (
                  <li key={step.title} className="guide-step">
                    <div className="guide-step-heading">
                      <span className="guide-step-number" aria-hidden="true">{idx + 1}</span>
                      <h2 className="font-pixel text-sm sm:text-base text-white">
                        <WikiText text={step.title} hrefPrefix="../wiki/?entry=" />
                      </h2>
                    </div>
                    <p className="guide-step-body text-xs sm:text-sm text-[#a0a7ba] leading-relaxed">
                      <WikiText text={step.body} hrefPrefix="../wiki/?entry=" />
                    </p>

                    {recipeEntries.length > 0 && (
                      <div className="guide-recipes" aria-label={`${step.title} recipes`}>
                        {recipeEntries.flatMap((entry) => [
                          entry.recipe ? (
                            <section key={`${entry.id}-crafting`} className="guide-recipe mc-inset">
                              <div className="guide-recipe-heading">
                                <h3 className="font-pixel text-xs">
                                  <a className="text-white hover:text-[#ffffa0]" href={wikiHrefForName(entry.recipe.output.name, entry.id)}>
                                    {entry.recipe.output.name}
                                  </a>
                                </h3>
                                <span className="mc-tag">Crafting recipe</span>
                              </div>
                              <CraftingGrid recipe={entry.recipe} />
                            </section>
                          ) : null,
                          entry.smithing ? (
                            <section key={`${entry.id}-smithing`} className="guide-recipe mc-inset">
                              <div className="guide-recipe-heading">
                                <h3 className="font-pixel text-xs">
                                  <a className="text-white hover:text-[#ffffa0]" href={wikiHrefForName(entry.smithing.output.name, entry.id)}>
                                    {entry.smithing.output.name}
                                  </a>
                                </h3>
                                <span className="mc-tag">Smithing table</span>
                              </div>
                              <SmithingTable recipe={entry.smithing} />
                            </section>
                          ) : null
                        ])}
                      </div>
                    )}

                    {step.note && (
                      <aside className="guide-note">
                        <span className="font-pixel text-[10px] text-[#55ffaf]">Good to know</span>
                        <p className="text-xs text-[#ffd276]"><WikiText text={step.note} hrefPrefix="../wiki/?entry=" /></p>
                      </aside>
                    )}
                  </li>
                );
              })}
            </ol>
          </article>
        </div>
      </main>

      <Footer currentPath="/guides/" />
    </div>
  );
};
