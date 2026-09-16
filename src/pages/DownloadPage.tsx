import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { PixelIcon } from '../components/PixelIcon';

export const DownloadPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
      <Navbar currentPath="/download/" />

      <main className="mx-auto w-full max-w-3xl flex-1 px-4 py-8 sm:px-6 sm:py-12">
        <div className="space-y-8">
          <header className="space-y-3">
            <h1 className="font-pixel text-3xl font-bold text-white mc-shadow sm:text-4xl">
              Download Dark Caverns
            </h1>
            <p className="max-w-2xl text-sm text-[#aeb5c5] sm:text-base">
              Choose the project page you prefer. Both links open in a new tab.
            </p>
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

          <section className="mc-box overflow-hidden" aria-labelledby="requirements-heading">
            <div className="mc-panel-header p-4">
              <h2 id="requirements-heading" className="font-pixel text-base text-white">Requirements</h2>
            </div>
            <dl className="grid gap-px bg-[#232630] sm:grid-cols-3">
              <div className="bg-[#17181c] p-4">
                <dt className="font-pixel text-xs text-[#8e95a8]">Minecraft</dt>
                <dd className="mt-1 text-sm text-white">1.21.1</dd>
              </div>
              <div className="bg-[#17181c] p-4">
                <dt className="font-pixel text-xs text-[#8e95a8]">Mod loader</dt>
                <dd className="mt-1 text-sm text-white">NeoForge</dd>
              </div>
              <div className="bg-[#17181c] p-4">
                <dt className="font-pixel text-xs text-[#8e95a8]">Dependency</dt>
                <dd className="mt-1 text-sm">
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
                </dd>
              </div>
            </dl>
          </section>

          <section className="mc-box p-5 sm:p-6" aria-labelledby="install-heading">
            <h2 id="install-heading" className="font-pixel text-lg text-white">Install</h2>
            <ol className="mt-5 space-y-4">
              <li className="flex gap-4">
                <span className="font-mono text-sm text-[#55ffaf]">01</span>
                <p className="text-sm text-[#c5cad7]">Install NeoForge for Minecraft 1.21.1.</p>
              </li>
              <li className="flex gap-4">
                <span className="font-mono text-sm text-[#55ffaf]">02</span>
                <p className="text-sm text-[#c5cad7]">Download Dark Caverns and GeckoLib from your chosen provider.</p>
              </li>
              <li className="flex gap-4">
                <span className="font-mono text-sm text-[#55ffaf]">03</span>
                <p className="text-sm text-[#c5cad7]">Put both .jar files in the mods folder, then launch the same NeoForge profile.</p>
              </li>
            </ol>
          </section>
        </div>
      </main>

      <Footer currentPath="/download/" />
    </div>
  );
};
