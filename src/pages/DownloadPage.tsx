import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { PixelIcon } from '../components/PixelIcon';

export const DownloadPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/download/" />

      <main className="flex-1 flex items-center justify-center px-4 sm:px-6 py-12">
        <div className="mc-box max-w-md w-full p-6 sm:p-8 space-y-6 text-center">
          <div>
            <h1 className="font-pixel text-3xl sm:text-4xl text-white mc-shadow font-bold">
              Download Dark Caverns
            </h1>
          </div>

          <div className="grid sm:grid-cols-2 gap-3 pt-1">
            <a
              href="https://modrinth.com/mod/dark-caverns"
              target="_blank"
              rel="noreferrer"
              className="mc-btn mc-btn-luminite text-sm sm:text-base py-3 px-4 text-center flex items-center justify-center gap-2"
            >
              <PixelIcon name="download" className="w-4 h-4" />
              <span>Modrinth</span>
            </a>
            <a
              href="https://www.curseforge.com/minecraft/mc-mods/dark-caverns"
              target="_blank"
              rel="noreferrer"
              className="mc-btn mc-btn-curseforge text-sm sm:text-base py-3 px-4 text-center flex items-center justify-center gap-2"
            >
              <PixelIcon name="download" className="w-4 h-4" />
              <span>CurseForge</span>
            </a>
          </div>

          <div className="pt-3 border-t border-[#232630] text-xs text-[#8e95a8]">
            For Minecraft 1.21.1 with NeoForge. Requires{' '}
            <a
              href="https://modrinth.com/mod/geckolib"
              target="_blank"
              rel="noreferrer"
              className="font-pixel text-[#55ffaf] hover:text-[#ffffa0] inline-flex items-center gap-1"
            >
              <span>GeckoLib</span>
              <PixelIcon name="external-link" className="w-3 h-3" />
            </a>
          </div>
        </div>
      </main>

      <Footer currentPath="/download/" />
    </div>
  );
};
