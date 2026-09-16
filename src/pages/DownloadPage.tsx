import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { PixelIcon } from '../components/PixelIcon';

export const DownloadPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/download/" />

      <main className="mx-auto w-full max-w-2xl flex-1 px-4 py-12 sm:px-6 sm:py-20">
        <section className="space-y-6" aria-labelledby="download-heading">
          <header>
            <h1 id="download-heading" className="font-pixel text-3xl font-bold text-white mc-shadow sm:text-4xl">
              Download Dark Caverns
            </h1>
          </header>

          <div className="grid gap-3 sm:grid-cols-2">
            <a
              href="https://modrinth.com/mod/dark-caverns"
              target="_blank"
              rel="noreferrer"
              className="mc-btn mc-btn-luminite min-h-11 gap-2 px-4 py-3 text-center text-sm sm:text-base"
              aria-label="Download Dark Caverns from Modrinth, opens in a new tab"
            >
              <PixelIcon name="download" className="w-4 h-4" />
              <span>Modrinth</span>
            </a>
            <a
              href="https://www.curseforge.com/minecraft/mc-mods/dark-caverns"
              target="_blank"
              rel="noreferrer"
              className="mc-btn mc-btn-curseforge min-h-11 gap-2 px-4 py-3 text-center text-sm sm:text-base"
              aria-label="Download Dark Caverns from CurseForge, opens in a new tab"
            >
              <PixelIcon name="download" className="w-4 h-4" />
              <span>CurseForge</span>
            </a>
          </div>

          <p className="border-t border-[#232630] pt-5 text-sm text-[#aeb5c5]">
            Minecraft 1.21.1 with NeoForge. Requires{' '}
            <a
              href="https://modrinth.com/mod/geckolib"
              target="_blank"
              rel="noreferrer"
              className="inline-flex min-h-11 items-center gap-1 font-pixel text-[#55ffaf] hover:text-[#ffffa0]"
              aria-label="GeckoLib on Modrinth, opens in a new tab"
            >
              <span>GeckoLib</span>
              <PixelIcon name="external-link" className="h-3 w-3" />
            </a>
          </p>
        </section>
      </main>

      <Footer currentPath="/download/" />
    </div>
  );
};
