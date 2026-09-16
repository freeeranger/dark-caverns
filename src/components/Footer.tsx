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
    <footer className="border-t-2 border-black bg-[#16171b] px-4 sm:px-6 py-6 text-xs text-[#707688] mt-auto">
      <div className="max-w-6xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4">
        <div>
          <span className="text-white font-pixel">Dark Caverns</span>
        </div>
        <div className="flex items-center gap-5 font-pixel text-xs">
          <a
            href={`${prefix}credits/`}
            className="hover:text-white transition-colors"
          >
            Credits
          </a>
          <a
            href="https://modrinth.com/mod/dark-caverns"
            target="_blank"
            rel="noreferrer"
            className="hover:text-[#55ffaf] transition-colors inline-flex items-center gap-1"
          >
            <span>Modrinth</span>
            <PixelIcon name="external-link" className="w-3 h-3 opacity-70" />
          </a>
          <a
            href="https://www.curseforge.com/minecraft/mc-mods/dark-caverns"
            target="_blank"
            rel="noreferrer"
            className="hover:text-[#ff9970] transition-colors inline-flex items-center gap-1"
          >
            <span>CurseForge</span>
            <PixelIcon name="external-link" className="w-3 h-3 opacity-70" />
          </a>
          <a
            href="https://github.com/freeeranger/dark-caverns"
            target="_blank"
            rel="noreferrer"
            className="hover:text-white transition-colors inline-flex items-center gap-1"
          >
            <span>GitHub</span>
            <PixelIcon name="external-link" className="w-3 h-3 opacity-70" />
          </a>
        </div>
      </div>
    </footer>
  );
};
