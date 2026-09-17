import type { WikiEntry } from '../types/content';
import { BLOCK_WIKI_ENTRIES, ITEM_WIKI_ENTRIES } from './wikiCatalog';

const wikiModules = import.meta.glob<WikiEntry>('../content/wiki/**/*.json', {
  eager: true,
  import: 'default'
});

const curatedWikiEntries = Object.values(wikiModules);

const wikiEntriesById = new Map<string, WikiEntry>();

for (const entry of [...curatedWikiEntries, ...BLOCK_WIKI_ENTRIES, ...ITEM_WIKI_ENTRIES]) {
  if (!wikiEntriesById.has(entry.id)) {
    wikiEntriesById.set(entry.id, entry);
  }
}

export const WIKI_ENTRIES: WikiEntry[] = [...wikiEntriesById.values()];

export const WIKI_ENTRY_REDIRECTS = new Map<string, string>([
  ['scorchling_hound', 'scorchling']
]);

export function resolveWikiEntryId(id: string): string {
  return WIKI_ENTRY_REDIRECTS.get(id) ?? id;
}

export const WIKI_ENTRY_BY_ID = new Map(WIKI_ENTRIES.map((entry) => [entry.id, entry]));
