import React, { useState, useEffect, useMemo } from 'react';
import { useQueryState, parseAsString, parseAsStringLiteral } from 'nuqs';
import Fuse from 'fuse.js';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { WIKI_ENTRIES, WikiEntry } from '../data/modData';
import { CraftingGrid } from '../components/CraftingGrid';
import { SmithingTable } from '../components/SmithingTable';
import { PixelIcon } from '../components/PixelIcon';
import { getTextureUrl, getGithubSourceUrl } from '../utils/assets';

const CATEGORIES = ['all', 'biomes', 'materials', 'gear', 'items', 'mobs'] as const;
type CopyStatus = 'copied' | 'error';

interface CopyFeedback {
  registryId: string;
  status: CopyStatus;
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
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [copyFeedback, setCopyFeedback] = useState<CopyFeedback | null>(null);

  // Fallback support for hash navigation
  useEffect(() => {
    const hash = window.location.hash.replace('#', '');
    if (hash && WIKI_ENTRIES.some((item) => item.id === hash)) {
      setActiveEntryId(hash);
    }
  }, [setActiveEntryId]);

  // Fuzzy search index powered by Fuse.js
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

  const filteredEntries = useMemo(() => {
    let pool = WIKI_ENTRIES;

    if (searchQuery.trim().length > 0) {
      const results = fuse.search(searchQuery.trim());
      pool = results.map((res) => res.item);
    }

    if (activeCategory !== 'all') {
      pool = pool.filter((item) => item.category === activeCategory);
    }

    return pool;
  }, [searchQuery, activeCategory, fuse]);

  const selectedItem = useMemo(() => {
    return WIKI_ENTRIES.find((item) => item.id === activeEntryId) || WIKI_ENTRIES[0];
  }, [activeEntryId]);

  const registryId = `dark_caverns:${selectedItem.id}`;
  const copyStatus = copyFeedback?.registryId === registryId ? copyFeedback.status : null;

  useEffect(() => {
    if (!copyFeedback) {
      return;
    }

    const timeout = window.setTimeout(() => setCopyFeedback(null), 2000);
    return () => window.clearTimeout(timeout);
  }, [copyFeedback]);

  const selectItem = (item: WikiEntry) => {
    setActiveEntryId(item.id);
  };

  const copyRegistryId = async () => {
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
          {/* Sidebar */}
          <aside
            className="min-w-0 md:col-span-4 mc-box p-4 space-y-4 md:sticky md:top-20"
            aria-label="Wiki navigation"
          >
            <div className="mc-panel-header -mx-4 -mt-4 p-3 border-b-2 border-black flex items-center justify-between">
              <span className="font-pixel text-xs sm:text-sm text-white">Wiki database</span>
              <span className="text-[11px] font-mono text-[#55ffaf]">{filteredEntries.length} items</span>
            </div>

            {/* Search Input */}
            <div className="relative">
              <PixelIcon
                name="search"
                className="w-3.5 h-3.5 text-[#8e95a8] absolute left-2.5 top-1/2 -translate-y-1/2 pointer-events-none"
              />
              <input
                type="text"
                placeholder="Search wiki..."
                aria-label="Search wiki entries"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full mc-inset pl-8 pr-7 py-2 text-xs text-white placeholder-[#8e95a8] focus:outline-none"
              />
              {searchQuery && (
                <button
                  type="button"
                  onClick={() => setSearchQuery('')}
                  className="absolute right-2 top-1/2 -translate-y-1/2 text-[#8e95a8] hover:text-white p-0.5"
                  title="Clear search"
                  aria-label="Clear search"
                >
                  <PixelIcon name="close" className="w-3.5 h-3.5" />
                </button>
              )}
            </div>

            {/* Category Filter */}
            <div className="grid grid-cols-3 gap-1.5" role="group" aria-label="Filter by category">
              {CATEGORIES.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setActiveCategory(cat)}
                  aria-pressed={activeCategory === cat}
                  className={`mc-btn min-w-0 text-xs py-1.5 px-1 capitalize min-h-[32px] ${
                    activeCategory === cat ? 'mc-btn-active' : ''
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>

            {/* Entry List */}
            <div
              className="max-h-[380px] overflow-y-auto space-y-1 pr-1"
              role="listbox"
              aria-label="Wiki entries"
            >
              {filteredEntries.map((entry) => {
                const isSelected = selectedItem.id === entry.id;
                return (
                  <button
                    key={entry.id}
                    role="option"
                    aria-selected={isSelected}
                    onClick={() => selectItem(entry)}
                    className={`w-full min-h-[40px] text-left px-2.5 py-1.5 text-xs flex items-center gap-2.5 transition-none cursor-pointer ${
                      isSelected
                        ? 'bg-[#22252d] text-[#ffffa0] border border-[#55ffaf]'
                        : 'bg-[#15161a] text-[#b0b6c6] border border-[#202228] hover:bg-[#1b1d23]'
                    }`}
                  >
                    <img
                      src={getTextureUrl(entry.texture)}
                      alt=""
                      className="w-5 h-5 pixel-art shrink-0"
                      aria-hidden="true"
                    />
                    <span className="font-pixel truncate">{entry.name}</span>
                  </button>
                );
              })}
            </div>
          </aside>

          {/* Active Article Content */}
          <article
            className="min-w-0 md:col-span-8 mc-box p-6 sm:p-8 space-y-6"
            aria-label={selectedItem.name}
          >
            {/* Header */}
            <div className="flex min-w-0 flex-col items-start justify-between gap-4 border-b border-[#232630] pb-5 sm:flex-row">
              <div className="flex min-w-0 items-center gap-4">
                <a
                  href={getGithubSourceUrl(selectedItem.texture)}
                  target="_blank"
                  rel="noreferrer"
                  className="mc-slot mc-slot-output shrink-0 group hover:border-[#55ffaf] transition-colors"
                  title="View texture on GitHub"
                >
                  <img
                    src={getTextureUrl(selectedItem.texture)}
                    alt={selectedItem.name}
                    className="w-8 h-8 pixel-art group-hover:scale-105 transition-transform"
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
                      title="View file on GitHub"
                    >
                      <span>GitHub source</span>
                      <PixelIcon name="external-link" className="w-3 h-3" />
                    </a>
                  </div>
                </div>
              </div>

              <span className="mc-tag uppercase">
                {selectedItem.category}
              </span>
            </div>

            {/* Description & Details */}
            <div className="space-y-3 text-sm leading-relaxed text-[#c6cbe0]">
              <p className="text-base text-white">
                {selectedItem.description}
              </p>
              <p className="text-xs sm:text-sm text-[#a0a7ba]">
                {selectedItem.details}
              </p>
            </div>

            {/* Properties */}
            {selectedItem.stats && selectedItem.stats.length > 0 && (
              <div className="space-y-2.5 pt-3 border-t border-[#232630]">
                <h2 className="font-pixel text-xs text-white">
                  Properties
                </h2>
                <div className="border border-[#232630] overflow-hidden">
                  <table className="w-full text-xs text-left border-collapse">
                    <tbody>
                      {selectedItem.stats.map((st, idx) => (
                        <tr
                          key={st.label}
                          className={`border-b border-[#1f222a] last:border-b-0 ${
                            idx % 2 === 0 ? 'bg-[#141519]' : 'bg-[#181920]'
                          }`}
                        >
                          <th
                            scope="row"
                            className="py-2 px-3 text-[#8e95a8] font-mono text-[11px] font-normal w-1/3 sm:w-1/4 border-r border-[#1f222a] align-top"
                          >
                            {st.label}
                          </th>
                          <td className="py-2 px-3 text-[#e0e3ec] align-top">
                            {st.value}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Crafting Recipe */}
            {selectedItem.recipe && (
              <div className="space-y-2 pt-2 border-t border-[#232630]">
                <div className="font-pixel text-xs text-white">
                  Crafting recipe
                </div>
                <CraftingGrid recipe={selectedItem.recipe} />
              </div>
            )}

            {/* Smithing Recipe */}
            {selectedItem.smithing && (
              <div className="space-y-2 pt-2 border-t border-[#232630]">
                <div className="font-pixel text-xs text-white">
                  Smithing table recipe
                </div>
                <SmithingTable recipe={selectedItem.smithing} />
              </div>
            )}
          </article>
        </div>
      </main>

      <Footer currentPath="/wiki/" />
    </div>
  );
};
