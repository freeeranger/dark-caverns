import React from 'react';

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
    <header className="border-b-2 border-black bg-[#1c1d22] sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-3 sm:px-6 py-2 sm:py-0 sm:h-16 flex flex-wrap sm:flex-nowrap items-center justify-between gap-2 sm:gap-4">
        {/* Brand */}
        <a
          href={prefix}
          className="flex w-full sm:w-auto items-center gap-2 sm:gap-3 text-left group no-underline shrink-0"
          aria-label="Dark Caverns Home"
        >
          <div className="w-7 h-7 sm:w-8 sm:h-8 mc-slot shrink-0">
            <img
              src={`${prefix}favicon.png`}
              alt="Dark Caverns Icon"
              className="w-5 h-5 sm:w-6 sm:h-6 pixel-art"
            />
          </div>
          <div>
            <div className="font-pixel text-base sm:text-2xl text-white mc-shadow leading-tight group-hover:text-[#ffffa0]">
              Dark Caverns
            </div>
          </div>
        </a>

        {/* Navigation links */}
        <nav className="grid w-full min-w-0 grid-cols-4 gap-1 sm:flex sm:w-auto sm:items-center sm:gap-2" aria-label="Main Navigation">
          {links.map((link) => {
            const isActive =
              (link.label === 'Overview' && currentPath === '/') ||
              (link.label === 'Guides' && currentPath === '/guides/') ||
              (link.label === 'Wiki' && currentPath === '/wiki/');

            return (
              <a
                key={link.label}
                href={link.href}
                className={`mc-btn min-w-0 text-xs sm:text-sm py-1.5 px-1 sm:px-3.5 ${
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
            className={`mc-btn mc-btn-luminite min-w-0 text-xs sm:text-sm py-1.5 px-1 sm:px-4 ${
              currentPath === '/download/' ? 'mc-btn-active' : ''
            }`}
            aria-current={currentPath === '/download/' ? 'page' : undefined}
          >
            Download
          </a>
        </nav>
      </div>
    </header>
  );
};
