import React, { useEffect, useRef, useState } from 'react';
import { useQueryState, parseAsString } from 'nuqs';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { GUIDES } from '../data/modData';
import { SmithingTable } from '../components/SmithingTable';
import { PixelIcon } from '../components/PixelIcon';

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
              <ol className="space-y-1.5 pt-1">
                {GUIDES.map((guide, idx) => {
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
                        <span>{idx + 1}. {guide.title}</span>
                      </button>
                    </li>
                  );
                })}
              </ol>
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
              <h1 className="font-pixel text-2xl sm:text-3xl text-white mc-shadow font-bold break-words">
                {activeGuide.title}
              </h1>
              <p className="text-xs sm:text-sm text-[#a0a7ba] mt-1.5 leading-relaxed">
                {activeGuide.summary}
              </p>
            </div>

            {/* Steps */}
            <div className="space-y-6 text-sm leading-relaxed text-[#c6cbe0]">
              {activeGuide.steps.map((step, idx) => (
                <div key={step.title} className="space-y-2 border-b border-[#232630] pb-5 last:border-b-0">
                  <h2 className="font-pixel text-sm sm:text-base text-white">
                    {step.title}
                  </h2>
                  <p className="text-xs sm:text-sm text-[#a0a7ba] leading-relaxed">
                    {step.body}
                  </p>

                  {/* Inline visual for Smithing guide */}
                  {activeGuide.id === 'smithing-and-gear-progression' && idx === 2 && (
                    <div className="pt-2 space-y-1">
                      <div className="font-pixel text-xs text-white pb-1">Smithing table recipe</div>
                      <SmithingTable
                        recipe={{
                          template: { name: 'Luminite Dust', texture: 'textures/item/luminite_dust.png' },
                          base: { name: 'Platinum Sword', texture: 'textures/item/platinum_sword.png' },
                          addition: { name: 'Hellstone', texture: 'textures/item/hellstone.png' },
                          output: { name: 'Hellstone Sword', texture: 'textures/item/hellstone_sword.png' }
                        }}
                      />
                    </div>
                  )}

                  {step.note && (
                    <p className="text-xs text-[#ffd276] border-l-2 border-[#55ffaf] pl-3 py-1 mt-2">
                      {step.note}
                    </p>
                  )}
                </div>
              ))}
            </div>
          </article>
        </div>
      </main>

      <Footer currentPath="/guides/" />
    </div>
  );
};
