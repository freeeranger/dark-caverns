import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { PixelIcon } from '../components/PixelIcon';

interface Contributor {
  name: string;
  contribution: string;
}

const CONTRIBUTORS: Contributor[] = [
  {
    name: 'ShroomTheShroom',
    contribution: 'ShroomTheShroom made several textures and mobs, and composed music for the mod.'
  },
  {
    name: 'Samuel Arden',
    contribution: 'Samuel Arden composed two music tracks for the mod.'
  }
];

export const CreditsPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/credits/" />

      <main className="flex-1 max-w-3xl w-full mx-auto px-4 sm:px-6 py-12">
        <div className="mc-box p-6 sm:p-8 space-y-6">
          <div className="border-b border-[#232630] pb-4">
            <h1 className="font-pixel text-2xl sm:text-3xl text-white mc-shadow font-bold">
              Credits
            </h1>
          </div>

          <div className="space-y-6">
            {CONTRIBUTORS.map((contributor) => (
              <div
                key={contributor.name}
                className="border-b border-[#232630] pb-5 last:border-b-0 space-y-1.5"
              >
                <h2 className="font-pixel text-base sm:text-lg text-white">
                  {contributor.name}
                </h2>
                <p className="text-xs sm:text-sm text-[#a0a7ba] leading-relaxed">
                  {contributor.contribution}
                </p>
              </div>
            ))}
          </div>

          <div className="flex flex-col gap-3 border-t border-[#232630] pt-5 sm:flex-row sm:items-center sm:justify-between">
            <p className="text-xs text-[#9ba2b5]">Dark Caverns runs on Minecraft 1.21.1 with NeoForge and GeckoLib.</p>
            <a
              href="https://github.com/freeeranger/dark-caverns"
              target="_blank"
              rel="noreferrer"
              className="mc-btn min-h-11 gap-2 px-4 text-xs"
              aria-label="View the Dark Caverns source on GitHub, opens in a new tab"
            >
              <span>Project source</span>
              <PixelIcon name="external-link" className="h-3 w-3" />
            </a>
          </div>
        </div>
      </main>

      <Footer currentPath="/credits/" />
    </div>
  );
};
