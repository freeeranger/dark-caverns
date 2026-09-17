import React, { useState } from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { PixelIcon } from '../components/PixelIcon';

interface Contributor {
  name: string;
  contribution: string;
  avatarUrl?: string;
}

const CONTRIBUTORS: Contributor[] = [
  {
    name: "Freeranger",
    contribution: "Owner and lead developer",
    avatarUrl: "https://avatars.githubusercontent.com/u/33828853?v=4"
  },
  {
    name: 'ShroomTheShroom',
    contribution: 'Texture artist, music, and mod design',
    avatarUrl: 'https://cdn.discordapp.com/avatars/707946930713526364/6672d344332f5aa8faa3bd857e79a9a9.png?size=1024'
  },
  {
    name: 'Samuel Arden',
    contribution: 'Composed two music tracks'
  }
];

interface ContributorAvatarProps {
  name: string;
  avatarUrl?: string;
}

const ContributorAvatar: React.FC<ContributorAvatarProps> = ({ avatarUrl }) => {
  const [hasError, setHasError] = useState(false);

  return (
    <div
      className="w-12 h-12 sm:w-14 sm:h-14 shrink-0 bg-[#16171c] border-2 border-black flex items-center justify-center relative select-none overflow-hidden"
      style={{
        boxShadow: 'inset 1px 1px 0px #2d303a, inset -1px -1px 0px #0c0d0f'
      }}
      aria-hidden="true"
    >
      {avatarUrl && !hasError ? (
        <img
          src={avatarUrl}
          alt=""
          loading="lazy"
          onError={() => setHasError(true)}
          className="w-full h-full object-cover block max-w-full max-h-full"
        />
      ) : (
        <PixelIcon
          name="user"
          className="w-6 h-6 sm:w-7 sm:h-7 text-[#6b7284]"
        />
      )}
    </div>
  );
};

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
                className="border-b border-[#232630] pb-5 last:border-b-0 flex items-center gap-4 sm:gap-5"
              >
                <ContributorAvatar
                  name={contributor.name}
                  avatarUrl={contributor.avatarUrl}
                />
                <div className="min-w-0 flex-1 space-y-1">
                  <h2 className="font-pixel text-base sm:text-lg text-white">
                    {contributor.name}
                  </h2>
                  <p className="text-xs sm:text-sm text-[#a0a7ba] leading-relaxed">
                    {contributor.contribution}
                  </p>
                </div>
              </div>
            ))}
          </div>

          <div className="flex flex-col gap-3 border-t border-[#232630] pt-5 sm:flex-row sm:items-center sm:justify-between">
            <p className="text-xs text-[#9ba2b5]">Want to help out yourself? Join our discord server and let us know!</p>
            <a
              href="https://discord.gg/ZaBswMUQgx"
              target="_blank"
              rel="noreferrer"
              className="mc-btn min-h-11 gap-2 px-4 text-xs"
              aria-label="Join our Discord server, opens in a new tab"
            >
              <PixelIcon name="discord" className="h-4 w-4" />
              <span>Join Discord</span>
            </a>
          </div>
        </div>
      </main>

      <Footer currentPath="/credits/" />
    </div>
  );
};
