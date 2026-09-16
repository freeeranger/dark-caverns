import React, { useState } from 'react';
import {
  useFloating,
  autoUpdate,
  offset,
  flip,
  shift,
  useHover,
  useFocus,
  useClick,
  useDismiss,
  useRole,
  useInteractions,
  FloatingPortal,
  Placement
} from '@floating-ui/react';

interface MinecraftTooltipProps {
  content: React.ReactNode;
  placement?: Placement;
  children: React.ReactNode;
}

export const MinecraftTooltip: React.FC<MinecraftTooltipProps> = ({
  content,
  placement = 'top',
  children
}) => {
  const [isOpen, setIsOpen] = useState(false);

  const { refs, floatingStyles, context } = useFloating({
    open: isOpen,
    onOpenChange: setIsOpen,
    placement,
    whileElementsMounted: autoUpdate,
    middleware: [
      offset(6),
      flip({ fallbackAxisSideDirection: 'start' }),
      shift({ padding: 6 })
    ]
  });

  const hover = useHover(context, { delay: { open: 40, close: 0 } });
  const focus = useFocus(context);
  const click = useClick(context);
  const dismiss = useDismiss(context);
  const role = useRole(context, { role: 'tooltip' });

  const { getReferenceProps, getFloatingProps } = useInteractions([
    hover,
    focus,
    click,
    dismiss,
    role
  ]);

  return (
    <>
      <span
        ref={refs.setReference}
        {...getReferenceProps()}
        tabIndex={0}
        className="inline-flex items-center justify-center"
      >
        {children}
      </span>
      {isOpen && (
        <FloatingPortal>
          <div
            ref={refs.setFloating}
            style={floatingStyles}
            {...getFloatingProps()}
            className="z-[9999] pointer-events-none select-none mc-tooltip px-2 py-1 max-w-xs text-center"
          >
            <div className="font-pixel text-[11px] text-[#e8ecf4] mc-shadow-subtle leading-none">
              {content}
            </div>
          </div>
        </FloatingPortal>
      )}
    </>
  );
};
