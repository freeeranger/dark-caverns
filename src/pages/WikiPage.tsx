import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useQueryState, parseAsString, parseAsStringLiteral } from 'nuqs';
import Fuse from 'fuse.js';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { WIKI_ENTRIES, WikiEntry } from '../data/modData';
import { CraftingGrid } from '../components/CraftingGrid';
import { SmithingTable } from '../components/SmithingTable';
import { PixelIcon } from '../components/PixelIcon';
import { TextureImage } from '../components/TextureImage';
import { getGithubSourceUrl } from '../utils/assets';

const CATEGORIES = ['all', 'biomes', 'materials', 'gear', 'items', 'mobs'] as const;
type WikiCategory = typeof CATEGORIES[number];
type CopyStatus = 'copied' | 'error';

interface CopyFeedback {
  registryId: string;
  status: CopyStatus;
}

function filterWikiEntries(
  query: string,
  category: WikiCategory,
  fuse: Fuse<WikiEntry>
): WikiEntry[] {
  let pool = WIKI_ENTRIES;

  if (query.trim().length > 0) {
    pool = fuse.search(query.trim()).map((result) => result.item);
  }

  if (category !== 'all') {
    pool = pool.filter((item) => item.category === category);
  }

  return pool;
}

export const WikiPage: React.FC = () => {
  const [activeCategory, setActiveCategory] = useQueryState(
    'category',
    parseAsStringLiteral(CATEGORIES).withDefault('all')
  );
  const [activeEntryId, setActiveEntryId] = useQueryState(
    'entry',
    parseAsString.withDefault(WIKI_ENTRIES[0].id)
  );
  const [searchQuery, setSearchQuery] = useState('');
  const [copyFeedback, setCopyFeedback] = useState<CopyFeedback | null>(null);
  const [mobileDetailOpen, setMobileDetailOpen] = useState(() => {
    if (typeof window === 'undefined') {
      return false;
    }

    return new URLSearchParams(window.location.search).has('entry') || window.location.hash.length > 1;
  });
  const navigationRef = useRef<HTMLElement>(null);
  const articleRef = useRef<HTMLElement>(null);

  useEffect(() => {
    const hash = window.location.hash.replace('#', '');
    if (hash && WIKI_ENTRIES.some((item) => item.id === hash)) {
      setActiveEntryId(hash);
    }
  }, [setActiveEntryId]);

  const fuse = useMemo(() => {
    return new Fuse(WIKI_ENTRIES, {
      keys: [
        { name: 'name', weight: 0.6 },
        { name: 'id', weight: 0.25 },
        { name: 'description', weight: 0.15 }
      ],
      threshold: 0.35,
      ignoreLocation: true,
      minMatchCharLength: 2
    });
  }, []);

  const filteredEntries = useMemo(
    () => filterWikiEntries(searchQuery, activeCategory, fuse),
    [searchQuery, activeCategory, fuse]
  );

  const selectedItem = useMemo(() => {
    return filteredEntries.find((item) => item.id === activeEntryId) || filteredEntries[0] || null;
  }, [activeEntryId, filteredEntries]);

  const registryId = selectedItem ? `dark_caverns:${selectedItem.id}` : '';
  const copyStatus = copyFeedback?.registryId === registryId ? copyFeedback.status : null;

  useEffect(() => {
    if (!copyFeedback) {
      return;
    }

    const timeout = window.setTimeout(() => setCopyFeedback(null), 2000);
    return () => window.clearTimeout(timeout);
  }, [copyFeedback]);

  useEffect(() => {
    if (!mobileDetailOpen || !selectedItem || !window.matchMedia('(max-width: 767px)').matches) {
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
  }, [mobileDetailOpen, selectedItem]);

  const preserveVisibleSelection = (entries: WikiEntry[]) => {
    if (entries.length > 0 && !entries.some((entry) => entry.id === activeEntryId)) {
      setActiveEntryId(entries[0].id);
    }

    if (entries.length === 0) {
      setMobileDetailOpen(false);
    }
  };

  const changeSearchQuery = (value: string) => {
    setSearchQuery(value);
    preserveVisibleSelection(filterWikiEntries(value, activeCategory, fuse));
  };

  const changeCategory = (category: WikiCategory) => {
    setActiveCategory(category);
    preserveVisibleSelection(filterWikiEntries(searchQuery, category, fuse));
  };

  const clearSearch = () => {
    setSearchQuery('');
    preserveVisibleSelection(filterWikiEntries('', activeCategory, fuse));
  };

  const showEverything = () => {
    setSearchQuery('');
    setActiveCategory('all');
    preserveVisibleSelection(WIKI_ENTRIES);
  };

  const selectItem = (item: WikiEntry) => {
    setActiveEntryId(item.id);
    setMobileDetailOpen(true);
  };

  const showNavigation = () => {
    setMobileDetailOpen(false);
    window.requestAnimationFrame(() => navigationRef.current?.focus());
  };

  const copyRegistryId = async () => {
    if (!registryId) {
      return;
    }

    try {
      await navigator.clipboard.writeText(registryId);
      setCopyFeedback({ registryId, status: 'copied' });
    } catch {
      setCopyFeedback({ registryId, status: 'error' });
    }
  };

  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/wiki/" />

      <main className="flex-1 min-w-0 max-w-6xl w-full mx-auto px-4 sm:px-6 py-8">
        <div className="grid min-w-0 md:grid-cols-12 gap-6 items-start">
          <aside
            ref={navigationRef}
            tabIndex={-1}
            className={`${mobileDetailOpen ? 'hidden' : 'block'} min-w-0 md:col-span-4 mc-box p-4 space-y-4 md:sticky md:top-20 md:block`}
            aria-label="Wiki navigation"
          >
            <div className="mc-panel-header -mx-4 -mt-4 p-3 border-b-2 border-black flex items-center justify-between">
              <span className="font-pixel text-xs sm:text-sm text-white">Wiki database</span>
              <span className="text-[11px] font-mono text-[#55ffaf]">
                {filteredEntries.length} {filteredEntries.length === 1 ? 'item' : 'items'}
              </span>
            </div>

            <div className="relative">
              <PixelIcon
                name="search"
                className="w-3.5 h-3.5 text-[#8e95a8] absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none"
              />
              <input
                type="search"
                placeholder="Search wiki..."
                aria-label="Search wiki entries"
                value={searchQuery}
                onChange={(event) => changeSearchQuery(event.target.value)}
                className="w-full min-h-11 mc-inset pl-9 pr-12 py-2 text-base sm:text-sm text-white placeholder-[#8e95a8]"
              />
              {searchQuery && (
                <button
                  type="button"
                  onClick={clearSearch}
                  className="absolute right-0 top-0 inline-flex h-11 w-11 items-center justify-center text-[#8e95a8] hover:text-white"
                  title="Clear search"
                  aria-label="Clear wiki search"
                >
                  <PixelIcon name="close" className="w-3.5 h-3.5" />
                </button>
              )}
            </div>

            <label className="sr-only" htmlFor="wiki-category">
              Filter by category
            </label>
            <select
              id="wiki-category"
              value={activeCategory}
              onChange={(event) => changeCategory(event.target.value as WikiCategory)}
              className="mc-inset min-h-11 w-full px-3 text-base text-white md:hidden"
            >
              {CATEGORIES.map((category) => (
                <option key={category} value={category} className="bg-[#101114] capitalize">
                  {category === 'all' ? 'All categories' : category}
                </option>
              ))}
            </select>

            <div className="hidden grid-cols-3 gap-1.5 md:grid" role="group" aria-label="Filter by category">
              {CATEGORIES.map((category) => (
                <button
                  key={category}
                  type="button"
                  onClick={() => changeCategory(category)}
                  aria-pressed={activeCategory === category}
                  className={`mc-btn min-w-0 min-h-11 text-xs py-1.5 px-1 capitalize ${
                    activeCategory === category ? 'mc-btn-active' : ''
                  }`}
                >
                  {category}
                </button>
              ))}
            </div>

            {filteredEntries.length > 0 ? (
              <nav aria-label="Wiki entries">
                <ul className="max-h-[380px] overflow-y-auto space-y-1 pr-1">
                  {filteredEntries.map((entry) => {
                    const isSelected = selectedItem?.id === entry.id;
                    return (
                      <li key={entry.id}>
                        <button
                          type="button"
                          aria-current={isSelected ? 'page' : undefined}
                          onClick={() => selectItem(entry)}
                          className={`w-full min-h-11 text-left px-2.5 py-2 text-xs flex items-center gap-2.5 transition-none cursor-pointer ${
                            isSelected
                              ? 'bg-[#22252d] text-[#ffffa0] border border-[#55ffaf]'
                              : 'bg-[#15161a] text-[#b0b6c6] border border-[#202228] hover:bg-[#1b1d23]'
                          }`}
                        >
                          <TextureImage
                            texture={entry.texture}
                            alt=""
                            className="w-5 h-5 pixel-art shrink-0"
                            width={20}
                            height={20}
                          />
                          <span className="font-pixel truncate">{entry.name}</span>
                        </button>
                      </li>
                    );
                  })}
                </ul>
              </nav>
            ) : (
              <div className="wiki-empty" role="status">
                <div className="space-y-1">
                  <p className="font-pixel text-sm text-white">No matching entries</p>
                  <p className="text-xs text-[#a0a7ba]">
                    No wiki entry matches this search and category.
                  </p>
                </div>
                <div className="flex flex-col gap-2 sm:flex-row md:flex-col">
                  {searchQuery && (
                    <button type="button" onClick={clearSearch} className="mc-btn min-h-11 px-3 text-xs">
                      Clear search
                    </button>
                  )}
                  <button type="button" onClick={showEverything} className="mc-btn min-h-11 px-3 text-xs">
                    Show everything
                  </button>
                </div>
              </div>
            )}
          </aside>

          {selectedItem ? (
            <article
              ref={articleRef}
              tabIndex={-1}
              className={`${mobileDetailOpen ? 'block' : 'hidden'} min-w-0 scroll-mt-32 md:col-span-8 mc-box p-6 sm:p-8 space-y-6 md:block md:scroll-mt-20`}
              aria-label={selectedItem.name}
            >
              <button
                type="button"
                onClick={showNavigation}
                className="mc-btn min-h-11 gap-2 px-3 text-xs md:hidden"
              >
                <PixelIcon name="arrow-left" className="h-4 w-4" />
                <span>Back to wiki</span>
              </button>

              <div className="flex min-w-0 flex-col items-start justify-between gap-4 border-b border-[#232630] pb-5 sm:flex-row">
                <div className="flex min-w-0 items-center gap-4">
                  <a
                    href={getGithubSourceUrl(selectedItem.texture)}
                    target="_blank"
                    rel="noreferrer"
                    className="mc-slot mc-slot-output shrink-0 group hover:border-[#55ffaf] transition-colors"
                    aria-label={`View the ${selectedItem.name} texture on GitHub in a new tab`}
                  >
                    <TextureImage
                      texture={selectedItem.texture}
                      alt={selectedItem.name}
                      className="w-8 h-8 pixel-art group-hover:scale-105 transition-transform"
                      width={32}
                      height={32}
                    />
                  </a>
                  <div className="min-w-0">
                    <h1 className="font-pixel text-2xl sm:text-3xl text-white mc-shadow font-bold break-words">
                      {selectedItem.name}
                    </h1>
                    <div className="flex min-w-0 flex-wrap items-center gap-x-2 gap-y-1 mt-0.5">
                      <code className="registry-id min-w-0 break-all text-xs font-mono text-[#8e95a8]">
                        {registryId}
                      </code>
                      <button
                        type="button"
                        onClick={copyRegistryId}
                        className={`inline-flex min-h-11 items-center gap-1 px-1.5 text-[11px] font-pixel sm:min-h-8 ${
                          copyStatus === 'copied'
                            ? 'text-[#55ffaf]'
                            : copyStatus === 'error'
                              ? 'text-[#ff9970]'
                              : 'text-[#8e95a8] hover:text-white'
                        }`}
                        aria-label={`Copy registry ID ${registryId}`}
                        title={copyStatus === 'error' ? 'Copy failed. Select the ID instead.' : 'Copy registry ID'}
                      >
                        <PixelIcon
                          name={copyStatus === 'copied' ? 'check' : 'copy'}
                          className="w-3 h-3"
                        />
                        <span aria-live="polite">
                          {copyStatus === 'copied'
                            ? 'Copied'
                            : copyStatus === 'error'
                              ? 'Select ID'
                              : 'Copy ID'}
                        </span>
                      </button>
                      <a
                        href={getGithubSourceUrl(selectedItem.texture)}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex min-h-11 items-center gap-1 text-[11px] font-pixel text-[#55ffaf] hover:text-[#ffffa0] sm:min-h-8"
                        aria-label="View texture source on GitHub in a new tab"
                      >
                        <span>GitHub source</span>
                        <PixelIcon name="external-link" className="w-3 h-3" />
                      </a>
                    </div>
                  </div>
                </div>

                <span className="mc-tag uppercase">{selectedItem.category}</span>
              </div>

              <div className="space-y-3 text-sm leading-relaxed text-[#c6cbe0]">
                <p className="text-base text-white">{selectedItem.description}</p>
                <p className="text-xs sm:text-sm text-[#a0a7ba]">{selectedItem.details}</p>
              </div>

              {selectedItem.stats && selectedItem.stats.length > 0 && (
                <div className="space-y-2.5 pt-3 border-t border-[#232630]">
                  <h2 className="font-pixel text-xs text-white">Properties</h2>
                  <div className="border border-[#232630] overflow-hidden">
                    <table className="w-full text-xs text-left border-collapse">
                      <tbody>
                        {selectedItem.stats.map((stat, index) => (
                          <tr
                            key={stat.label}
                            className={`border-b border-[#1f222a] last:border-b-0 ${
                              index % 2 === 0 ? 'bg-[#141519]' : 'bg-[#181920]'
                            }`}
                          >
                            <th
                              scope="row"
                              className="py-2 px-3 text-[#8e95a8] font-mono text-[11px] font-normal w-1/3 sm:w-1/4 border-r border-[#1f222a] align-top"
                            >
                              {stat.label}
                            </th>
                            <td className="py-2 px-3 text-[#e0e3ec] align-top">{stat.value}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {selectedItem.recipe && (
                <div className="space-y-2 pt-2 border-t border-[#232630]">
                  <h2 className="font-pixel text-xs text-white">Crafting recipe</h2>
                  <CraftingGrid recipe={selectedItem.recipe} />
                </div>
              )}

              {selectedItem.smithing && (
                <div className="space-y-2 pt-2 border-t border-[#232630]">
                  <h2 className="font-pixel text-xs text-white">Smithing table recipe</h2>
                  <SmithingTable recipe={selectedItem.smithing} />
                </div>
              )}
            </article>
          ) : (
            <section className="hidden min-w-0 md:col-span-8 mc-box p-8 md:block" aria-live="polite">
              <h1 className="font-pixel text-2xl text-white mc-shadow">No matching wiki entry</h1>
              <p className="mt-3 text-sm text-[#a0a7ba]">
                Clear the search or show every category to return to the reference.
              </p>
              <button type="button" onClick={showEverything} className="mc-btn mt-5 min-h-11 px-4 text-sm">
                Show everything
              </button>
            </section>
          )}
        </div>
      </main>

      <Footer currentPath="/wiki/" />
    </div>
  );
};
