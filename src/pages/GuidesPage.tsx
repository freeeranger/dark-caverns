import React, { useEffect } from 'react';
import { useQueryState, parseAsString } from 'nuqs';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { GUIDES } from '../data/modData';
import { SmithingTable } from '../components/SmithingTable';

export const GuidesPage: React.FC = () => {
  const [guideParam, setGuideParam] = useQueryState(
    'guide',
    parseAsString.withDefault(GUIDES[0].id)
  );

  // Hash fallback for backwards compatibility
  useEffect(() => {
    const hashGuide = window.location.hash.replace('#', '');
    if (hashGuide && GUIDES.some((g) => g.id === hashGuide)) {
      setGuideParam(hashGuide);
    }
  }, [setGuideParam]);

  const activeGuide = GUIDES.find((g) => g.id === guideParam) || GUIDES[0];

  const selectGuide = (id: string) => {
    setGuideParam(id);
  };

  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/guides/" />

      <main className="flex-1 min-w-0 max-w-6xl w-full mx-auto px-4 sm:px-6 py-8">
        <div className="grid min-w-0 md:grid-cols-12 gap-6 items-start">
          {/* Sidebar */}
          <aside
            className="min-w-0 md:col-span-4 mc-box p-4 space-y-3 md:sticky md:top-20"
            aria-label="Guide chapters"
          >
            <div className="mc-panel-header -mx-4 -mt-4 p-3 border-b-2 border-black">
              <span className="font-pixel text-xs sm:text-sm text-white">Guides and walkthroughs</span>
            </div>

            <div className="space-y-1.5 pt-1" role="tablist" aria-orientation="vertical">
              {GUIDES.map((guide, idx) => {
                const isSelected = guideParam === guide.id;
                return (
                  <button
                    key={guide.id}
                    role="tab"
                    aria-selected={isSelected}
                    onClick={() => selectGuide(guide.id)}
                    className={`w-full min-h-[40px] text-left px-3 py-2 border transition-none cursor-pointer font-pixel text-xs ${
                      isSelected
                        ? 'bg-[#22252d] border-[#55ffaf] text-[#ffffa0]'
                        : 'bg-[#15161a] border-[#22242a] text-[#b8bdcb] hover:bg-[#1c1e24] hover:text-white'
                    }`}
                  >
                    <span>{idx + 1}. {guide.title}</span>
                  </button>
                );
              })}
            </div>
          </aside>

          {/* Active Guide Content */}
          <article
            className="min-w-0 md:col-span-8 mc-box p-6 sm:p-8 space-y-6"
            role="tabpanel"
            aria-label={activeGuide.title}
          >
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
                <div key={idx} className="space-y-2 border-b border-[#232630] pb-5 last:border-b-0">
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
