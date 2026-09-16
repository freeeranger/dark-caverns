import React, { ImgHTMLAttributes, useState } from 'react';
import { getTextureUrl } from '../utils/assets';

interface TextureImageProps extends Omit<ImgHTMLAttributes<HTMLImageElement>, 'src'> {
  texture?: string;
}

export const TextureImage: React.FC<TextureImageProps> = ({
  texture,
  alt = '',
  className = '',
  loading = 'lazy',
  onError,
  ...props
}) => {
  const source = getTextureUrl(texture);
  const [failedSource, setFailedSource] = useState<string | null>(null);
  const hasFailed = !source || failedSource === source;

  if (hasFailed) {
    return (
      <span
        className={`texture-fallback ${className}`}
        role={alt ? 'img' : undefined}
        aria-label={alt || undefined}
        aria-hidden={alt ? undefined : true}
        title={alt ? `${alt} texture unavailable` : undefined}
      >
        {alt ? '?' : ''}
      </span>
    );
  }

  return (
    <img
      src={source}
      alt={alt}
      className={className}
      loading={loading}
      decoding="async"
      onError={(event) => {
        setFailedSource(source);
        onError?.(event);
      }}
      {...props}
    />
  );
};
