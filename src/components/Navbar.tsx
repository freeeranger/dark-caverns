import React, { useEffect, useRef, useState } from 'react';
import { Button } from './Button';
import { PixelIcon } from './PixelIcon';

interface NavbarProps {
  currentPath: '/' | '/guides/' | '/wiki/' | '/download/' | '/credits/';
}

export const Navbar: React.FC<NavbarProps> = ({ currentPath }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const menuButtonRef = useRef<HTMLButtonElement>(null);
  const isRoot = currentPath === '/';
  const prefix = isRoot ? './' : '../';

  const links = [
    { label: 'Overview', href: prefix, path: '/' },
    { label: 'Guides', href: `${prefix}guides/`, path: '/guides/' },
    { label: 'Wiki', href: `${prefix}wiki/`, path: '/wiki/' }
  ];

  useEffect(() => {
    if (!isMenuOpen) {
      return;
    }

    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setIsMenuOpen(false);
        menuButtonRef.current?.focus();
      }
    };

    document.addEventListener('keydown', closeOnEscape);
    return () => document.removeEventListener('keydown', closeOnEscape);
  }, [isMenuOpen]);

  return (
    <header className="sticky top-0 z-50 border-b-2 border-black bg-[#1c1d22]">
      <div className="mx-auto flex h-14 max-w-6xl items-center gap-2 px-2 md:h-16 md:gap-4 md:px-6">
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
          <div className="pb-1 font-pixel text-base leading-tight text-white mc-shadow group-hover:text-[#ffffa0] md:text-2xl">
            Dark Caverns
          </div>
        </a>

        <Button
          ref={menuButtonRef}
          variant="icon"
          className="md:hidden ml-auto"
          isActive={isMenuOpen}
          aria-expanded={isMenuOpen}
          aria-controls="mobile-navigation"
          aria-label={isMenuOpen ? 'Close navigation menu' : 'Open navigation menu'}
          onClick={() => setIsMenuOpen((open) => !open)}
          icon={<PixelIcon name={isMenuOpen ? 'close' : 'menu'} className="h-5 w-5" />}
        />

        <nav className="ml-auto hidden items-center gap-2 md:flex" aria-label="Main navigation">
          {links.map((link) => {
            const isActive = currentPath === link.path;

            return (
              <Button
                key={link.label}
                href={link.href}
                isActive={isActive}
                className="px-3.5 py-1.5 text-sm"
                aria-current={isActive ? 'page' : undefined}
              >
                {link.label}
              </Button>
            );
          })}

          <Button
            href={`${prefix}download/`}
            variant="luminite"
            isActive={currentPath === '/download/'}
            className="px-4 py-1.5 text-sm"
            aria-current={currentPath === '/download/' ? 'page' : undefined}
            aria-label="Download"
            icon={<PixelIcon name="download" className="h-3.5 w-3.5" />}
          >
            Download
          </Button>
        </nav>
      </div>

      <nav
        id="mobile-navigation"
        className={`${isMenuOpen ? 'block' : 'hidden'} border-t-2 border-black bg-[#17181c] px-3 py-3 md:hidden`}
        aria-label="Mobile navigation"
      >
        <div className="mx-auto grid max-w-6xl gap-2">
          {links.map((link) => {
            const isActive = currentPath === link.path;

            return (
              <Button
                key={link.label}
                href={link.href}
                onClick={() => setIsMenuOpen(false)}
                isActive={isActive}
                className="justify-start w-full px-4 text-sm"
                aria-current={isActive ? 'page' : undefined}
              >
                {link.label}
              </Button>
            );
          })}
          <Button
            href={`${prefix}download/`}
            variant="luminite"
            onClick={() => setIsMenuOpen(false)}
            isActive={currentPath === '/download/'}
            className="justify-start w-full px-4 text-sm"
            aria-current={currentPath === '/download/' ? 'page' : undefined}
            icon={<PixelIcon name="download" className="h-4 w-4" />}
          >
            Download
          </Button>
        </div>
      </nav>
    </header>
  );
};
