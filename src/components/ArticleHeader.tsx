import React from 'react';

interface ArticleHeaderProps {
  title: string;
  badge?: React.ReactNode;
  icon?: React.ReactNode;
  subtitle?: React.ReactNode;
  summary?: React.ReactNode;
}

export const ArticleHeader: React.FC<ArticleHeaderProps> = ({
  title,
  badge,
  icon,
  subtitle,
  summary
}) => {
  return (
    <div className="border-b border-[#232630] pb-5">
      <div className="flex min-w-0 flex-col items-start justify-between gap-4 sm:flex-row">
        <div className="flex min-w-0 items-center gap-4">
          {icon}
          <div className="min-w-0">
            <h1 className="font-pixel text-2xl sm:text-3xl text-white mc-shadow font-bold wrap-break-word break-words">
              {title}
            </h1>
            {subtitle}
          </div>
        </div>
        {badge && <div className="shrink-0">{badge}</div>}
      </div>
      {summary && (
        <div className="text-xs sm:text-sm text-[#a0a7ba] mt-2 leading-relaxed">
          {summary}
        </div>
      )}
    </div>
  );
};
