import React from 'react';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';

interface Contributor {
  name: string;
  contribution: string;
}

const CONTRIBUTORS: Contributor[] = [
  {
    name: 'ShroomTheShroom',
    contribution: 'Created several textures, mobs, and music tracks.'
  },
  {
    name: 'Samuel Arden',
    contribution: 'Composed 2 music tracks for the mod.'
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
            <p className="text-xs sm:text-sm text-[#8e95a8] mt-1">
              Contributors and acknowledgments for Dark Caverns
            </p>
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
        </div>
      </main>

      <Footer currentPath="/credits/" />
    </div>
  );
};
