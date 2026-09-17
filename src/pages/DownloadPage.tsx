import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { Button } from '../components/Button';
import { PixelIcon } from '../components/PixelIcon';
import { LINKS } from '../utils/constants';

export const DownloadPage: React.FC = () => {
  return (
      <div className="min-h-screen bg-[#121316] text-[#d6dae5] font-sans flex flex-col antialiased">
          <Navbar currentPath="/download/" />

          <main className="mx-auto w-full max-w-2xl flex-1 px-4 py-12 sm:px-6 sm:py-20 h-full flex justify-center items-center">
              <section className="flex flex-col h-full" aria-labelledby="download-heading">
                  <header className="mb-8">
                      <h1
                          id="download-heading"
                          className="font-pixel text-4xl font-bold text-white mc-shadow sm:text-6xl text-center mb-8"
                      >
                          Download Dark Caverns!
                      </h1>
                  </header>

                  <div className="grid gap-3 sm:grid-cols-2">
                      <Button
                          href={LINKS.curseforge}
                          variant="curseforge"
                          size="lg"
                          className="text-center"
                          aria-label="Download Dark Caverns from CurseForge, opens in a new tab"
                          icon="download"
                      >
                          CurseForge
                      </Button>
                      <Button
                          href={LINKS.modrinth}
                          variant="modrinth"
                          size="lg"
                          className="text-center"
                          aria-label="Download Dark Caverns from Modrinth, opens in a new tab"
                          icon="download"
                      >
                          Modrinth
                      </Button>
                  </div>

                  <p className="text-sm text-center text-[#aeb5c5]">
                      Dark Caverns is available for 1.21.1 NeoForge and requires{" "}
                      <a
                          href={LINKS.geckolib}
                          target="_blank"
                          rel="noreferrer"
                          className="ml-1 inline-flex min-h-11 items-center gap-1 font-pixel text-[#55ffaf] hover:text-[#ffffa0]"
                          aria-label="GeckoLib on Modrinth, opens in a new tab"
                      >
                          <span>GeckoLib</span>
                          <PixelIcon name="external-link" className="h-3 w-3" />
                      </a>
                      .
                  </p>
              </section>
          </main>

          <Footer currentPath="/download/" />
      </div>
  );
};
