import React from 'react';
import { PixelIcon } from './PixelIcon';

interface FooterProps {
  basePrefix?: string;
  currentPath?: string;
}

export const Footer: React.FC<FooterProps> = ({ basePrefix, currentPath }) => {
  const isRoot = currentPath === '/';
  const prefix = basePrefix ?? (isRoot ? './' : '../');

  return (
    <footer className="mt-auto border-t-2 border-black bg-[#16171b] px-4 py-6 text-xs text-[#9ba2b5] sm:px-6">
      <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-3 sm:flex-row">
        <div>
          <span className="text-white font-pixel">Dark Caverns</span>
        </div>
        <nav className="flex flex-wrap items-center justify-center gap-x-4 gap-y-1 font-pixel text-xs" aria-label="Project links">
          <a
            href={`${prefix}credits/`}
            className="inline-flex min-h-11 items-center hover:text-white"
            aria-current={currentPath === '/credits/' ? 'page' : undefined}
          >
            Credits
          </a>
          <a
            href="https://modrinth.com/mod/dark-caverns"
            target="_blank"
            rel="noreferrer"
            className="inline-flex min-h-11 items-center gap-1 hover:text-[#55ffaf]"
            aria-label="Modrinth, opens in a new tab"
          >
            <span>Modrinth</span>
            <PixelIcon name="external-link" className="w-3 h-3 opacity-70" />
          </a>
          <a
            href="https://www.curseforge.com/minecraft/mc-mods/dark-caverns"
            target="_blank"
            rel="noreferrer"
            className="inline-flex min-h-11 items-center gap-1 hover:text-[#ff9970]"
            aria-label="CurseForge, opens in a new tab"
          >
            <span>CurseForge</span>
            <PixelIcon name="external-link" className="w-3 h-3 opacity-70" />
          </a>
          <a
            href="https://github.com/freeeranger/dark-caverns"
            target="_blank"
            rel="noreferrer"
            className="inline-flex min-h-11 items-center gap-1 hover:text-white"
            aria-label="GitHub, opens in a new tab"
          >
            <span>GitHub</span>
            <PixelIcon name="external-link" className="w-3 h-3 opacity-70" />
          </a>
        </nav>
      </div>
    </footer>
  );
};
