import React, { useState, useEffect } from 'react';
import { ArticleHeader } from './ArticleHeader';
import { RecipeCard } from './RecipeCard';
import { Button } from './Button';
import { TextureImage } from './TextureImage';
import { WikiText } from './WikiText';
import type { WikiEntry } from '../data/modData';
import { getGithubSourceUrl } from '../utils/assets';

type CopyStatus = 'copied' | 'error';

interface WikiDetailArticleProps {
  item: WikiEntry;
}

export const WikiDetailArticle: React.FC<WikiDetailArticleProps> = ({ item }) => {
  const [copyStatus, setCopyStatus] = useState<CopyStatus | null>(null);

  const registryId = item.registryId === null
    ? ''
    : item.registryId ?? `dark_caverns:${item.id}`;

  const textureLabel = item.name;

  useEffect(() => {
    if (!copyStatus) {
      return;
    }

    const timeout = window.setTimeout(() => setCopyStatus(null), 2000);
    return () => window.clearTimeout(timeout);
  }, [copyStatus]);

  const copyRegistryId = async () => {
    if (!registryId) {
      return;
    }

    try {
      await navigator.clipboard.writeText(registryId);
      setCopyStatus('copied');
    } catch {
      setCopyStatus('error');
    }
  };

  return (
    <>
      <ArticleHeader
        title={item.name}
        badge={<span className="mc-tag uppercase">{item.category}</span>}
        icon={
          <a
            href={getGithubSourceUrl(item.texture)}
            target="_blank"
            rel="noreferrer"
            className="mc-slot shrink-0 group hover:border-[#55ffaf] transition-colors"
            aria-label={`View the ${textureLabel} texture on GitHub in a new tab`}
          >
            <TextureImage
              texture={item.texture}
              alt={textureLabel}
              className="w-8 h-8 pixel-art group-hover:scale-105 transition-transform object-cover object-top"
              width={32}
              height={32}
            />
          </a>
        }
        subtitle={
          registryId ? (
            <div className="flex items-center h-4 mt-2 gap-2">
              <code className="h-full select-text cursor-text min-w-0 break-all text-xs text-[#8e95a8] font-pixel">
                {registryId}
              </code>
              <Button
                variant="ghost"
                size="none"
                onClick={copyRegistryId}
                className={`items-end gap-1 px-1.5 py-1 text-[11px] font-pixel ${
                  copyStatus === 'copied'
                    ? 'text-[#55ffaf]'
                    : copyStatus === 'error'
                      ? 'text-[#ff9970]'
                      : 'text-[#8e95a8] hover:text-white'
                }`}
                aria-label={`Copy registry ID ${registryId}`}
                title={copyStatus === 'error' ? 'Copy failed. Select the ID instead.' : 'Copy registry ID'}
                icon={copyStatus === 'copied' ? 'check' : 'copy'}
              >
                <span aria-live="polite">
                  {copyStatus === 'copied'
                    ? 'Copied'
                    : copyStatus === 'error'
                      ? 'Select ID'
                      : 'Copy ID'}
                </span>
              </Button>
            </div>
          ) : undefined
        }
      />

      <div className="space-y-3 text-sm leading-relaxed text-[#c6cbe0]">
        <p className="text-base text-white">
          <WikiText text={item.description} hrefPrefix="?entry=" excludeEntryId={item.id} />
        </p>
        <p className="text-xs sm:text-sm text-[#a0a7ba]">
          <WikiText text={item.details} hrefPrefix="?entry=" excludeEntryId={item.id} />
        </p>
      </div>

      {item.stats && item.stats.length > 0 && (
        <div className="space-y-2.5 pt-3 border-t border-[#232630]">
          <h2 className="font-pixel text-xs text-white">Properties</h2>
          <div className="border border-[#232630] overflow-hidden">
            <table className="w-full text-xs text-left border-collapse">
              <tbody>
                {item.stats.map((stat, index) => (
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
                      <WikiText text={stat.value} hrefPrefix="?entry=" excludeEntryId={item.id} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {item.recipe && (
        <RecipeCard
          title="Crafting recipe"
          crafting={item.recipe}
          variant="section"
        />
      )}

      {item.smithing && (
        <RecipeCard
          title="Smithing table recipe"
          smithing={item.smithing}
          variant="section"
        />
      )}
    </>
  );
};
