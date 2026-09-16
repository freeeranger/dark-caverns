import { resolve } from 'path';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  base: './',
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'index.html'),
        guides: resolve(__dirname, 'guides/index.html'),
        wiki: resolve(__dirname, 'wiki/index.html'),
        download: resolve(__dirname, 'download/index.html'),
        credits: resolve(__dirname, 'credits/index.html'),
        notFound: resolve(__dirname, '404.html'),
      },
    },
  },
});
