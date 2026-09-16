import React from 'react';

export type PixelIconName =
  | 'search'
  | 'external-link'
  | 'arrow-right'
  | 'arrow-left'
  | 'plus'
  | 'download'
  | 'copy'
  | 'check'
  | 'close'
  | 'chevron-right';

interface PixelIconProps extends React.SVGProps<SVGSVGElement> {
  name: PixelIconName;
  className?: string;
}

export const PixelIcon: React.FC<PixelIconProps> = ({ name, className = 'w-4 h-4', ...props }) => {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="currentColor"
      aria-hidden={props['aria-hidden'] ?? true}
      className={`shrink-0 ${className}`}
      {...props}
    >
      {name === 'search' && (
        <path d="M22 22h-2v-2h2v2Zm-2-2h-2v-2h2v2Zm-6-2H6v-2h8v2Zm4 0h-2v-2h2v2ZM6 16H4v-2h2v2Zm10 0h-2v-2h2v2ZM4 14H2V6h2v8Zm14 0h-2V6h2v8ZM6 6H4V4h2v2Zm10 0h-2V4h2v2Zm-2-2H6V2h8v2Z" />
      )}
      {name === 'external-link' && (
        <>
          <path d="M11 5H5v2h6V5ZM5 7H3v12h2V7Zm12 12H5v2h12v-2Zm2-6h-2v6h2v-6Zm-8 0H9v2h2v-2Zm2-2h-2v2h2v-2Zm2-2h-2v2h2V9Zm2-2h-2v2h2V7Zm2-2h-2v2h2V5Zm2-2h-2v8h2V3Z" />
          <path d="M21 3h-8v2h8V3Z" />
        </>
      )}
      {name === 'arrow-right' && (
        <>
          <path d="M4 11v2h16v-2zm12 2v2h2v-2zm-2 2v2h2v-2zm-2 2v2h2v-2zm4-6V9h2v2z" />
          <path d="M14 15V7h2v8zm-2 2V5h2v12z" />
        </>
      )}
      {name === 'arrow-left' && (
        <>
          <path d="M20 11v2H4v-2zm-12 2v2H6v-2zm2 2v2H8v-2zm2 2v2h-2v-2zM8 11V9H6v2z" />
          <path d="M10 15V7H8v8zm2 2V5h-2v12z" />
        </>
      )}
      {name === 'plus' && (
        <path d="M13 11h7v2h-7v7h-2v-7H4v-2h7V4h2v7Z" />
      )}
      {name === 'download' && (
        <>
          <path d="M21 15v4h-2v-4zm-2 4v2H5v-2zM5 15v4H3v-4zm8-12v14h-2V3z" />
          <path d="M7 11v2h10v-2zm2 2v2h2v-2zm4 0v2h2v-2z" />
          <path d="M15 11v2h2v-2z" />
        </>
      )}
      {name === 'copy' && (
        <path d="M7 3h14v2H7V3Zm12 2h2v12h-2V5Zm-8 10h8v2h-8v-2ZM5 7h10v2H5V7ZM3 9h2v12H3V9Zm2 10h10v2H5v-2Zm8-8h2v8h-2v-8Z" />
      )}
      {name === 'check' && (
        <path d="M20 5h2v4h-2V5Zm-2 6h2V9h-2v2Zm-2 2h2v-2h-2v2Zm-2 2h2v-2h-2v2Zm-2 2h2v-2h-2v2Zm-2 0h2v2h-2v-2Zm-2-2h2v2H8v-2Zm-2-2h2v2H6v-2Z" />
      )}
      {name === 'close' && (
        <path d="M7 19H5V17H7V19ZM19 19H17V17H19V19ZM9 15V17H7V15H9ZM17 17H15V15H17V17ZM11 15H9V13H11V15ZM15 15H13V13H15V15ZM13 13H11V11H13V13ZM11 11H9V9H11V11ZM15 11H13V9H15V11ZM9 9H7V7H9V9ZM17 9H15V7H17V9ZM7 7H5V5H7V7ZM19 7H17V5H19V7Z" />
      )}
      {name === 'chevron-right' && (
        <path d="M16 13v-2h-2v2h2Zm-2-2V9h-2v2h2Zm0 4v-2h-2v2h2Zm-2-6V7h-2v2h2Zm0 8v-2h-2v2h2ZM10 7V5H8v2h2Zm0 12v-2H8v2h2Z" />
      )}
    </svg>
  );
};
