import React from 'react';

interface SidebarNavGroupProps {
  title?: string;
  children: React.ReactNode;
}

export const SidebarNavGroup: React.FC<SidebarNavGroupProps> = ({ title, children }) => {
  return (
    <section>
      {title && (
        <h2 className="mb-1.5 px-1 font-pixel text-[10px] uppercase tracking-wide text-[#8e95a8]">
          {title}
        </h2>
      )}
      <ul className="space-y-1.5">{children}</ul>
    </section>
  );
};
