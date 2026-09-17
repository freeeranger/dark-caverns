import React from 'react';

export const TrailerEmbed: React.FC = () => {
  return (
    <iframe
      className="block h-full w-full"
      src="https://www.youtube-nocookie.com/embed/Z3q_B4iXvOw?rel=0"
      title="Dark Caverns official trailer"
      loading="lazy"
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowFullScreen
    />
  );
};
