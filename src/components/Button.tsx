import React from 'react';

export type ButtonVariant = 'default' | 'luminite' | 'curseforge' | 'icon';
export type ButtonSize = 'sm' | 'md' | 'lg' | 'none';

interface BaseButtonProps {
  variant?: ButtonVariant;
  size?: ButtonSize;
  isActive?: boolean;
  icon?: React.ReactNode;
  iconPosition?: 'left' | 'right';
  className?: string;
  children?: React.ReactNode;
}

export type ButtonAsButton = BaseButtonProps &
  Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, keyof BaseButtonProps> & {
    href?: undefined;
  };

export type ButtonAsLink = BaseButtonProps &
  Omit<React.AnchorHTMLAttributes<HTMLAnchorElement>, keyof BaseButtonProps> & {
    href: string;
    external?: boolean;
  };

export type ButtonProps = ButtonAsButton | ButtonAsLink;

export interface ButtonComponent {
  (props: ButtonAsLink & { ref?: React.Ref<HTMLAnchorElement> }): React.ReactElement | null;
  (props: ButtonAsButton & { ref?: React.Ref<HTMLButtonElement> }): React.ReactElement | null;
  displayName?: string;
}

export const Button: ButtonComponent = React.forwardRef<
  HTMLButtonElement | HTMLAnchorElement,
  ButtonProps
>((props, ref) => {
  const {
    variant = 'default',
    size = 'md',
    isActive = false,
    icon,
    iconPosition = 'left',
    className = '',
    children,
    ...rest
  } = props;

  const variantClass =
    variant === 'luminite'
      ? 'mc-btn-luminite'
      : variant === 'curseforge'
      ? 'mc-btn-curseforge'
      : variant === 'icon'
      ? 'mc-btn-icon'
      : '';

  const sizeClass =
    variant === 'icon' || size === 'none'
      ? ''
      : size === 'sm'
      ? 'px-3 text-xs'
      : size === 'lg'
      ? 'px-4 py-3 text-sm sm:text-base'
      : 'px-4 text-xs sm:text-sm';

  const activeClass = isActive ? 'mc-btn-active' : '';

  const combinedClasses = [
    'mc-btn min-h-11 gap-2',
    variantClass,
    sizeClass,
    activeClass,
    className
  ]
    .filter(Boolean)
    .join(' ');

  const content =
    variant === 'icon' ? (
      children ?? icon
    ) : (
      <>
        {icon && iconPosition === 'left' && icon}
        {children && <span>{children}</span>}
        {icon && iconPosition === 'right' && icon}
      </>
    );

  if ('href' in rest && typeof rest.href === 'string') {
    const { href, external, ...anchorProps } = rest as ButtonAsLink;
    const isExternal =
      external ??
      (href.startsWith('http://') ||
        href.startsWith('https://') ||
        href.startsWith('//'));

    return (
      <a
        ref={ref as React.Ref<HTMLAnchorElement>}
        href={href}
        className={combinedClasses}
        {...(isExternal ? { target: '_blank', rel: 'noreferrer' } : {})}
        {...anchorProps}
      >
        {content}
      </a>
    );
  }

  const buttonProps = rest as ButtonAsButton;
  return (
    <button
      ref={ref as React.Ref<HTMLButtonElement>}
      type={buttonProps.type ?? 'button'}
      className={combinedClasses}
      {...buttonProps}
    >
      {content}
    </button>
  );
}) as unknown as ButtonComponent;

Button.displayName = 'Button';
