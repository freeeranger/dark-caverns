import React from 'react';
import { PixelIcon } from './PixelIcon';

interface NavbarProps {
  currentPath: '/' | '/guides/' | '/wiki/' | '/download/' | '/credits/';
}

export const Navbar: React.FC<NavbarProps> = ({ currentPath }) => {
  const isRoot = currentPath === '/';
  const prefix = isRoot ? './' : '../';

  const links = [
    { label: 'Overview', href: prefix },
    { label: 'Guides', href: `${prefix}guides/` },
    { label: 'Wiki', href: `${prefix}wiki/` }
  ];

  return (
    <header className="sticky top-0 z-50 border-b-2 border-black bg-[#1c1d22]">
      <div className="mx-auto flex h-14 max-w-6xl items-center gap-2 px-2 sm:h-16 sm:gap-4 sm:px-6">
        <a
          href={prefix}
          className="group flex shrink-0 items-center gap-3 text-left no-underline"
          aria-label="Dark Caverns home"
        >
          <div className="mc-slot h-8 w-8 shrink-0">
            <img
              src={`${prefix}favicon.png`}
              alt=""
              className="h-6 w-6 pixel-art"
            />
          </div>
          <div className="hidden font-pixel text-2xl leading-tight text-white mc-shadow group-hover:text-[#ffffa0] sm:block">
            Dark Caverns
          </div>
        </a>

        <nav className="grid min-w-0 flex-1 grid-cols-4 gap-1 sm:ml-auto sm:flex sm:flex-none sm:items-center sm:gap-2" aria-label="Main navigation">
          {links.map((link) => {
            const isActive =
              (link.label === 'Overview' && currentPath === '/') ||
              (link.label === 'Guides' && currentPath === '/guides/') ||
              (link.label === 'Wiki' && currentPath === '/wiki/');

            return (
              <a
                key={link.label}
                href={link.href}
                className={`mc-btn min-h-11 min-w-0 px-1 py-1.5 text-[11px] sm:px-3.5 sm:text-sm ${
                  isActive ? 'mc-btn-active' : ''
                }`}
                aria-current={isActive ? 'page' : undefined}
              >
                {link.label}
              </a>
            );
          })}

          <a
            href={`${prefix}download/`}
            className={`mc-btn mc-btn-luminite min-h-11 min-w-0 gap-1 px-1 py-1.5 text-[11px] sm:gap-2 sm:px-4 sm:text-sm ${
              currentPath === '/download/' ? 'mc-btn-active' : ''
            }`}
            aria-current={currentPath === '/download/' ? 'page' : undefined}
            aria-label="Download"
          >
            <PixelIcon name="download" className="h-3.5 w-3.5" />
            <span className="sm:hidden">Get</span>
            <span className="hidden sm:inline">Download</span>
          </a>
        </nav>
      </div>
    </header>
  );
};
