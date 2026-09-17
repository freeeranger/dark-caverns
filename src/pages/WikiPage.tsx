import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useQueryState, parseAsString, parseAsStringLiteral } from 'nuqs';
import Fuse from 'fuse.js';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { MasterDetailLayout } from '../components/MasterDetailLayout';
import { SidebarNavGroup } from '../components/SidebarNavGroup';
import { SidebarNavButton } from '../components/SidebarNavButton';
import { ArticleHeader } from '../components/ArticleHeader';
import { RecipeCard } from '../components/RecipeCard';
import { PixelIcon } from '../components/PixelIcon';
import { TextureImage } from '../components/TextureImage';
import { WikiText } from '../components/WikiText';
import { resolveWikiEntryId, WIKI_ENTRIES, WikiEntry } from '../data/modData';
import { getGithubSourceUrl } from '../utils/assets';

const CATEGORIES = ['all', 'biomes', 'blocks', 'materials', 'gear', 'items', 'mobs'] as const;
const ENTRY_CATEGORIES = CATEGORIES.filter((category) => category !== 'all');
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
  const hasQuery = query.trim().length > 0;

  if (hasQuery) {
    pool = fuse.search(query.trim()).map((result) => result.item);
  }

  if (category !== 'all') {
    pool = pool.filter((item) => item.category === category);
  }

  return hasQuery ? pool : [...pool].sort((a, b) => a.name.localeCompare(b.name));
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
    const hash = resolveWikiEntryId(window.location.hash.replace('#', ''));
    if (hash && WIKI_ENTRIES.some((item) => item.id === hash)) {
      setActiveEntryId(hash);
    }
  }, [setActiveEntryId]);

  const fuse = useMemo(() => {
    return new Fuse(WIKI_ENTRIES, {
      keys: [
        { name: 'name', weight: 0.6 },
        { name: 'id', weight: 0.25 },
        { name: 'description', weight: 0.15 },
        { name: 'details', weight: 0.1 },
        { name: 'stats.value', weight: 0.1 },
        { name: 'aliases', weight: 0.1 },
        { name: 'relatedItems.name', weight: 0.2 },
        { name: 'relatedItems.id', weight: 0.15 }
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

  const resolvedActiveEntryId = resolveWikiEntryId(activeEntryId);

  const selectedItem = useMemo(() => {
    return filteredEntries.find((item) => item.id === resolvedActiveEntryId) || filteredEntries[0] || null;
  }, [resolvedActiveEntryId, filteredEntries]);

  useEffect(() => {
    if (resolvedActiveEntryId !== activeEntryId) {
      setActiveEntryId(resolvedActiveEntryId);
      setActiveCategory('all');
    }
  }, [activeEntryId, resolvedActiveEntryId, setActiveCategory, setActiveEntryId]);

  const registryId = selectedItem?.registryId === null
    ? ''
    : selectedItem?.registryId ?? (selectedItem ? `dark_caverns:${selectedItem.id}` : '');
  const textureLabel = selectedItem?.relatedItems?.find((item) => item.texture === selectedItem.texture)?.name
    ?? selectedItem?.name
    ?? '';
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
    if (entries.length > 0 && !entries.some((entry) => entry.id === resolvedActiveEntryId)) {
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

  const entryGroups = useMemo(() => {
    if (activeCategory === 'all' && !searchQuery.trim()) {
      return ENTRY_CATEGORIES.map((category) => ({
        category,
        entries: filteredEntries.filter((entry) => entry.category === category)
      })).filter((group) => group.entries.length > 0);
    }
    return [{ category: null, entries: filteredEntries }];
  }, [activeCategory, searchQuery, filteredEntries]);

  const sidebarContent = (
    <div className="space-y-4">
      <div className="relative">
        <PixelIcon
          name="search"
          className="w-3.5 h-3.5 text-[#8e95a8] absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none"
        />
        <input
          type="search"
          placeholder="Search the wiki"
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

      <div className="hidden grid-cols-2 gap-1.5 md:grid" role="group" aria-label="Filter by category">
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
          <div className="max-h-[55vh] space-y-3 overflow-y-auto pr-1">
            {entryGroups.map((group) => (
              <SidebarNavGroup key={group.category ?? 'results'} title={group.category ?? undefined}>
                {group.entries.map((entry) => (
                  <li key={entry.id}>
                    <SidebarNavButton
                      label={entry.name}
                      texture={entry.texture}
                      isSelected={selectedItem?.id === entry.id}
                      onClick={() => selectItem(entry)}
                    />
                  </li>
                ))}
              </SidebarNavGroup>
            ))}
          </div>
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
              Show all entries
            </button>
          </div>
        </div>
      )}
    </div>
  );

  const emptyState = (
    <section className="hidden min-w-0 md:col-span-8 mc-box p-8 md:block" aria-live="polite">
      <h1 className="font-pixel text-2xl text-white mc-shadow">No matching wiki entry</h1>
      <p className="mt-3 text-sm text-[#a0a7ba]">
        Clear the search or show all entries.
      </p>
      <button type="button" onClick={showEverything} className="mc-btn mt-5 min-h-11 px-4 text-sm">
        Show all entries
      </button>
    </section>
  );

  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/wiki/" />

      <MasterDetailLayout
        sidebarTitle="Wiki"
        sidebarHeaderRight={
          <span className="text-[11px] font-mono text-[#55ffaf]">
            {filteredEntries.length} {filteredEntries.length === 1 ? 'entry' : 'entries'}
          </span>
        }
        sidebarAriaLabel="Wiki navigation"
        sidebarRef={navigationRef}
        articleRef={articleRef}
        articleAriaLabel={selectedItem?.name}
        mobileDetailOpen={mobileDetailOpen}
        onBack={showNavigation}
        backLabel="Back to wiki"
        sidebarContent={sidebarContent}
        emptyState={emptyState}
      >
        {selectedItem && (
          <>
            <ArticleHeader
              title={selectedItem.name}
              badge={<span className="mc-tag uppercase">{selectedItem.category}</span>}
              icon={
                <a
                  href={getGithubSourceUrl(selectedItem.texture)}
                  target="_blank"
                  rel="noreferrer"
                  className="mc-slot mc-slot-output shrink-0 group hover:border-[#55ffaf] transition-colors"
                  aria-label={`View the ${textureLabel} texture on GitHub in a new tab`}
                >
                  <TextureImage
                    texture={selectedItem.texture}
                    alt={textureLabel}
                    className="w-8 h-8 pixel-art group-hover:scale-105 transition-transform"
                    width={32}
                    height={32}
                  />
                </a>
              }
              subtitle={
                <div className="flex min-w-0 flex-wrap items-center gap-x-2 gap-y-1 mt-0.5">
                  {registryId && (
                    <div className="flex items-center h-4 mt-2 gap-2">
                      <code className="h-full registry-id min-w-0 break-all text-xs text-[#8e95a8] font-pixel">
                        {registryId}
                      </code>
                      <button
                        type="button"
                        onClick={copyRegistryId}
                        className={`cursor-pointer inline-flex items-end gap-1 px-1.5 py-1 text-[11px] font-pixel ${
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
                    </div>
                  )}

                  {selectedItem.relatedItems?.map((relatedItem) => (
                    <span key={relatedItem.id} className="inline-flex min-w-0 basis-full flex-wrap items-center gap-x-1.5 text-[11px]">
                      <span className="font-pixel text-[#8e95a8]">{relatedItem.name}</span>
                      <code className="break-all font-mono text-[#b8bdcb]">dark_caverns:{relatedItem.id}</code>
                    </span>
                  ))}
                </div>
              }
            />

            <div className="space-y-3 text-sm leading-relaxed text-[#c6cbe0]">
              <p className="text-base text-white">
                <WikiText text={selectedItem.description} hrefPrefix="?entry=" excludeEntryId={selectedItem.id} />
              </p>
              <p className="text-xs sm:text-sm text-[#a0a7ba]">
                <WikiText text={selectedItem.details} hrefPrefix="?entry=" excludeEntryId={selectedItem.id} />
              </p>
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
                          <td className="py-2 px-3 text-[#e0e3ec] align-top">
                            <WikiText text={stat.value} hrefPrefix="?entry=" excludeEntryId={selectedItem.id} />
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {selectedItem.recipe && (
              <RecipeCard
                title="Crafting recipe"
                crafting={selectedItem.recipe}
                variant="section"
              />
            )}

            {selectedItem.smithing && (
              <RecipeCard
                title="Smithing table recipe"
                smithing={selectedItem.smithing}
                variant="section"
              />
            )}
          </>
        )}
      </MasterDetailLayout>

      <Footer currentPath="/wiki/" />
    </div>
  );
};
