import React from 'react';
import { PixelIcon } from './PixelIcon';

interface MasterDetailLayoutProps {
  sidebarTitle: string;
  sidebarHeaderRight?: React.ReactNode;
  sidebarAriaLabel: string;
  sidebarRef?: React.Ref<HTMLElement>;
  articleRef?: React.Ref<HTMLElement>;
  articleAriaLabel?: string;
  mobileDetailOpen: boolean;
  onBack: () => void;
  backLabel: string;
  sidebarContent: React.ReactNode;
  emptyState?: React.ReactNode;
  children: React.ReactNode;
}

export const MasterDetailLayout: React.FC<MasterDetailLayoutProps> = ({
  sidebarTitle,
  sidebarHeaderRight,
  sidebarAriaLabel,
  sidebarRef,
  articleRef,
  articleAriaLabel,
  mobileDetailOpen,
  onBack,
  backLabel,
  sidebarContent,
  emptyState,
  children
}) => {
  return (
    <main className="flex-1 min-w-0 max-w-6xl w-full mx-auto px-4 sm:px-6 py-8">
      <div className="grid min-w-0 md:grid-cols-12 gap-6 items-start">
        {/* Sidebar */}
        <aside
          ref={sidebarRef}
          tabIndex={-1}
          className={`${
            mobileDetailOpen ? 'hidden' : 'flex flex-col'
          } min-w-0 md:col-span-4 mc-box p-4 md:sticky md:top-20 md:flex md:flex-col md:max-h-[calc(100dvh-7rem)] focus:outline-none outline-none`}
          aria-label={sidebarAriaLabel}
        >
          <div className="mc-panel-header -mx-4 -mt-4 p-3 border-b-2 border-black flex items-center justify-between shrink-0">
            <span className="font-pixel text-xs sm:text-sm text-white">{sidebarTitle}</span>
            {sidebarHeaderRight}
          </div>
          <div className="flex flex-col min-h-0 flex-1 pt-3">
            {sidebarContent}
          </div>
        </aside>

        {/* Detail Article or Empty State */}
        {children ? (
          <article
            ref={articleRef}
            tabIndex={-1}
            className={`${
              mobileDetailOpen ? 'block' : 'hidden'
            } min-w-0 scroll-mt-32 md:col-span-8 mc-box p-6 sm:p-8 space-y-6 md:block md:scroll-mt-20 focus:outline-none outline-none`}
            aria-label={articleAriaLabel}
          >
            <button
              type="button"
              onClick={onBack}
              className="mc-btn min-h-11 gap-2 px-3 text-xs md:hidden"
            >
              <PixelIcon name="arrow-left" className="h-4 w-4" />
              <span>{backLabel}</span>
            </button>
            {children}
          </article>
        ) : (
          emptyState ?? null
        )}
      </div>
    </main>
  );
};
