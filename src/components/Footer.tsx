import React from 'react';
import { PixelIcon } from './PixelIcon';

interface FooterProps {
  basePrefix?: string;
  currentPath?: string;
}

const EXTERNAL_LINKS = [
  { label: 'Discord', href: 'https://discord.gg/ZaBswMUQgx', hoverColor: 'hover:text-[#7289da]' },
  { label: 'CurseForge', href: 'https://www.curseforge.com/minecraft/mc-mods/dark-caverns', hoverColor: 'hover:text-[#ff9970]' },
  { label: 'Modrinth', href: 'https://modrinth.com/mod/dark-caverns', hoverColor: 'hover:text-[#55ffaf]' },
  { label: 'GitHub', href: 'https://github.com/freeeranger/dark-caverns', hoverColor: 'hover:text-white' }
];

export const Footer: React.FC<FooterProps> = ({ basePrefix, currentPath }) => {
  const isRoot = currentPath === '/';
  const prefix = basePrefix ?? (isRoot ? './' : '../');

  return (
    <footer className="mt-auto border-t-2 border-black bg-[#16171b] px-4 py-6 text-xs text-[#9ba2b5] sm:px-6">
      <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-3 sm:flex-row">
        <div>
          <span className="text-white font-pixel">Dark Caverns</span>
        </div>
        <nav className="flex flex-wrap items-center justify-center gap-x-6 sm:gap-x-7 gap-y-2 font-pixel text-xs" aria-label="Project links">
          <a
            href={`${prefix}credits/`}
            className="inline-flex min-h-11 items-center hover:text-white"
            aria-current={currentPath === '/credits/' ? 'page' : undefined}
          >
            Credits
          </a>
          {EXTERNAL_LINKS.map((link) => (
            <a
              key={link.label}
              href={link.href}
              target="_blank"
              rel="noreferrer"
              className={`inline-flex min-h-11 items-center gap-1 ${link.hoverColor}`}
              aria-label={`${link.label}, opens in a new tab`}
            >
              <span>{link.label}</span>
              <PixelIcon name="external-link" className="w-3 h-3 opacity-70" />
            </a>
          ))}
        </nav>
      </div>
    </footer>
  );
};
