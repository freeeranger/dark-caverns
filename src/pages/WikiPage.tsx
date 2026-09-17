import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useQueryState, parseAsString, parseAsStringLiteral } from 'nuqs';
import Fuse from 'fuse.js';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { Button } from '../components/Button';
import { MasterDetailLayout } from '../components/MasterDetailLayout';
import { SidebarNavGroup } from '../components/SidebarNavGroup';
import { SidebarNavButton } from '../components/SidebarNavButton';
import { WikiDetailArticle } from '../components/WikiDetailArticle';
import { PixelIcon } from '../components/PixelIcon';
import { resolveWikiEntryId, WIKI_ENTRIES, WikiEntry } from '../data/modData';

const CATEGORIES = ['all', 'biomes', 'blocks', 'materials', 'gear', 'items', 'mobs'] as const;
const ENTRY_CATEGORIES = CATEGORIES.filter((category) => category !== 'all');
type WikiCategory = typeof CATEGORIES[number];

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
  const [mobileDetailOpen, setMobileDetailOpen] = useState(() => {
    if (typeof window === 'undefined') {
      return false;
    }

    return new URLSearchParams(window.location.search).has('entry') || window.location.hash.length > 1;
  });

  const navigationRef = useRef<HTMLElement>(null);
  const articleRef = useRef<HTMLElement>(null);
  const searchInputRef = useRef<HTMLInputElement>(null);

  // Keyboard shortcut: Press '/' to focus search, 'Escape' to blur
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      const activeEl = document.activeElement;
      const isTyping = activeEl instanceof HTMLInputElement ||
        activeEl instanceof HTMLTextAreaElement ||
        activeEl?.getAttribute('contenteditable') === 'true';

      if (event.key === '/' && !isTyping) {
        event.preventDefault();
        searchInputRef.current?.focus();
        searchInputRef.current?.select();
      } else if (event.key === 'Escape' && activeEl === searchInputRef.current) {
        searchInputRef.current?.blur();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

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
        { name: 'aliases', weight: 0.1 }
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

  const isFirstRender = useRef(true);

  useEffect(() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }
    if (!selectedItem) {
      return;
    }

    const frame = window.requestAnimationFrame(() => {
      if (mobileDetailOpen && window.matchMedia('(max-width: 767px)').matches) {
        articleRef.current?.focus({ preventScroll: true });
      }
      window.scrollTo({
        top: 0,
        behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth'
      });
    });

    return () => window.cancelAnimationFrame(frame);
  }, [selectedItem?.id, mobileDetailOpen]);

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
    window.scrollTo({
      top: 0,
      behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth'
    });
  };

  const showNavigation = () => {
    setMobileDetailOpen(false);
    window.scrollTo({
      top: 0,
      behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth'
    });
    if (window.matchMedia('(max-width: 767px)').matches) {
      window.requestAnimationFrame(() => navigationRef.current?.focus({ preventScroll: true }));
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
    <div className="flex flex-col min-h-0 flex-1 space-y-3">
      <div className="relative shrink-0">
        <PixelIcon
          name="search"
          className="w-3.5 h-3.5 text-[#8e95a8] absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none"
        />
        <input
          ref={searchInputRef}
          type="search"
          autoComplete="off"
          spellCheck={false}
          placeholder="Search the wiki (/)"
          aria-label="Search wiki entries (press / to focus)"
          value={searchQuery}
          onChange={(event) => changeSearchQuery(event.target.value)}
          className="w-full min-h-11 mc-inset pl-9 pr-12 py-2 text-base sm:text-sm text-white placeholder-[#8e95a8] [&::-webkit-search-cancel-button]:hidden [&::-webkit-search-decoration]:hidden"
        />
        {searchQuery ? (
          <button
            type="button"
            onClick={clearSearch}
            className="absolute right-0 top-0 inline-flex h-11 w-11 items-center justify-center text-[#8e95a8] hover:text-white"
            title="Clear search"
            aria-label="Clear wiki search"
          >
            <PixelIcon name="close" className="w-3.5 h-3.5" />
          </button>
        ) : (
          <kbd className="hidden sm:inline-flex absolute right-3 top-1/2 -translate-y-1/2 items-center justify-center w-5 h-5 font-pixel text-[11px] text-[#6b7280] bg-[#1a1c22] border border-[#2a2c34] pointer-events-none" aria-hidden="true">
            /
          </kbd>
        )}
      </div>

      <label className="sr-only" htmlFor="wiki-category">
        Filter by category
      </label>
      <select
        id="wiki-category"
        value={activeCategory}
        onChange={(event) => changeCategory(event.target.value as WikiCategory)}
        className="mc-inset min-h-11 w-full px-3 text-base text-white md:hidden shrink-0"
      >
        {CATEGORIES.map((category) => (
          <option key={category} value={category} className="bg-[#101114] capitalize">
            {category === 'all' ? 'All categories' : category}
          </option>
        ))}
      </select>

      <div className="hidden grid-cols-4 gap-1 md:grid shrink-0" role="group" aria-label="Filter entries by category">
        {CATEGORIES.map((category) => (
          <Button
            key={category}
            size="none"
            isActive={activeCategory === category}
            onClick={() => changeCategory(category)}
            className="min-w-0 text-xs py-1.5 px-1 capitalize"
            aria-pressed={activeCategory === category}
          >
            {category}
          </Button>
        ))}
      </div>

      {filteredEntries.length > 0 ? (
        <nav aria-label="Wiki entries" className="flex-1 min-h-0 overflow-y-auto overscroll-contain pr-1.5">
          <div className="space-y-4 pt-1 pb-1">
            {entryGroups.map((group) => (
              <SidebarNavGroup
                key={group.category ?? 'results'}
                title={group.category ?? undefined}
              >
                {group.entries.map((item) => (
                  <li key={item.id}>
                    <SidebarNavButton
                      label={item.name}
                      isSelected={item.id === selectedItem?.id}
                      onClick={() => selectItem(item)}
                      texture={item.texture}
                    />
                  </li>
                ))}
              </SidebarNavGroup>
            ))}
          </div>
        </nav>
      ) : (
        <div className="flex-1 min-h-0 overflow-y-auto overscroll-contain flex flex-col gap-4 p-4 bg-[#141519] border border-[#2a2c34]" role="status">
          <div className="space-y-1">
            <p className="font-pixel text-sm text-white">No matching entries</p>
            <p className="text-xs text-[#a0a7ba]">
              No wiki entry matches this search and category.
            </p>
          </div>
          <div className="flex flex-col gap-2 sm:flex-row md:flex-col">
            {searchQuery && (
              <Button onClick={clearSearch} size="sm">
                Clear search
              </Button>
            )}
            <Button onClick={showEverything} size="sm">
              Show all entries
            </Button>
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
      <Button onClick={showEverything} className="mt-5" size="md">
        Show all entries
      </Button>
    </section>
  );

  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/wiki/" />

      <MasterDetailLayout
        sidebarTitle="Wiki"
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
        {selectedItem && <WikiDetailArticle item={selectedItem} />}
      </MasterDetailLayout>

      <Footer currentPath="/wiki/" />
    </div>
  );
};
