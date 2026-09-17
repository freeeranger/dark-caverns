import React from 'react';
import { TextureImage } from './TextureImage';

interface SidebarNavButtonProps {
  label: string;
  isSelected: boolean;
  onClick: () => void;
  texture?: string;
}

export const SidebarNavButton: React.FC<SidebarNavButtonProps> = ({
  label,
  isSelected,
  onClick,
  texture
}) => {
  return (
    <button
      type="button"
      aria-current={isSelected ? 'page' : undefined}
      onClick={onClick}
      className={`w-full min-h-11 text-left px-3 py-2 text-xs flex items-center gap-2.5 border transition-none cursor-pointer font-pixel focus:outline-none focus-visible:outline-2 focus-visible:outline-[#55ffaf] ${
        isSelected
          ? 'bg-[#22252d] border-[#55ffaf] text-[#ffffa0]'
          : 'bg-[#15161a] border-[#202228] text-[#b0b6c6] hover:bg-[#1b1d23] hover:text-white'
      }`}
    >
      {texture && (
        <TextureImage
          texture={texture}
          alt=""
          className="w-5 h-5 pixel-art shrink-0"
          width={20}
          height={20}
        />
      )}
      <span className="truncate">{label}</span>
    </button>
  );
};
