import React, { useState } from 'react';
import { PixelIcon } from './PixelIcon';

export const TrailerEmbed: React.FC = () => {
  const [isLoaded, setIsLoaded] = useState(false);

  if (isLoaded) {
    return (
      <iframe
        className="block h-full w-full"
        src="https://www.youtube-nocookie.com/embed/Z3q_B4iXvOw?rel=0&autoplay=1"
        title="Dark Caverns official trailer"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowFullScreen
      />
    );
  }

  return (
    <button
      type="button"
      onClick={() => setIsLoaded(true)}
      className="trailer-loader h-full w-full"
      aria-label="Load and play the Dark Caverns official trailer"
    >
      <span className="mc-slot mc-slot-output">
        <PixelIcon name="play" className="h-6 w-6 text-[#55ffaf]" />
      </span>
      <span className="font-pixel text-base text-white sm:text-xl">Play the official trailer</span>
      <span className="max-w-md text-xs leading-relaxed text-[#a0a7ba] sm:text-sm">
        The YouTube player loads only after you choose to play it.
      </span>
    </button>
  );
};
