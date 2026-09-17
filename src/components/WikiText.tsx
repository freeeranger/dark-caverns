import React, { useMemo } from 'react';
import { WIKI_ENTRIES } from '../data/modData';

interface WikiTextProps {
  text: string;
  hrefPrefix: string;
  excludeEntryId?: string;
}

interface LinkTarget {
  id: string;
  label: string;
}

function pluralize(value: string): string {
  if (/[^aeiou]y$/i.test(value)) return `${value.slice(0, -1)}ies`;
  if (/(s|sh|ch|x|z)$/i.test(value)) return `${value}es`;
  return `${value}s`;
}

function escapeRegex(value: string): string {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

const LINK_TARGETS = (() => {
  const byLabel = new Map<string, LinkTarget>();

  for (const entry of WIKI_ENTRIES) {
    const relatedItemLabels = (entry.relatedItems ?? []).flatMap((item) => [item.name, pluralize(item.name)]);
    const labels = [entry.name, pluralize(entry.name), ...(entry.aliases ?? []), ...relatedItemLabels];
    for (const label of labels) {
      const key = label.toLocaleLowerCase();
      if (!byLabel.has(key)) byLabel.set(key, { id: entry.id, label });
    }
  }

  return [...byLabel.values()].sort((a, b) => b.label.length - a.label.length);
})();

const TARGET_BY_LABEL = new Map(
  LINK_TARGETS.map((target) => [target.label.toLocaleLowerCase(), target])
);

const LINK_PATTERN = new RegExp(`\\b(${LINK_TARGETS.map((target) => escapeRegex(target.label)).join('|')})\\b`, 'giu');

export const WikiText: React.FC<WikiTextProps> = ({ text, hrefPrefix, excludeEntryId }) => {
  const parts = useMemo(() => {
    let offset = 0;
    return text.split(LINK_PATTERN).map((part) => {
      const segment = { key: `${offset}:${part}`, text: part };
      offset += part.length;
      return segment;
    });
  }, [text]);

  return (
    <>
      {parts.map((part) => {
        const target = TARGET_BY_LABEL.get(part.text.toLocaleLowerCase());
        if (!target || target.id === excludeEntryId) return <React.Fragment key={part.key}>{part.text}</React.Fragment>;

        return (
          <a
            key={part.key}
            href={`${hrefPrefix}${encodeURIComponent(target.id)}`}
            className="text-[#7dd3fc] underline decoration-[#365b72] underline-offset-2 hover:text-[#ffffa0] hover:decoration-[#8f8f55]"
          >
            {part.text}
          </a>
        );
      })}
    </>
  );
};
