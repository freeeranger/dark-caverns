import { resolve } from 'path';
import { existsSync } from 'fs';
import { defineConfig, type Plugin } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

function mpa404Plugin(): Plugin {
  return {
    name: 'mpa-404-fallback',
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        if (req.method !== 'GET' && req.method !== 'HEAD') return next();
        const rawUrl = req.url ? req.url.split('?')[0] : '';
        if (
          rawUrl.startsWith('/@') ||
          rawUrl.startsWith('/src') ||
          rawUrl.startsWith('/node_modules') ||
          rawUrl.includes('.')
        ) {
          return next();
        }
        const trimmed = rawUrl.replace(/^\/+|\/+$/g, '');
        const directPath = resolve(__dirname, trimmed, 'index.html');
        if (trimmed === '' || existsSync(directPath)) {
          return next();
        }

        const originalWriteHead = res.writeHead;
        res.writeHead = function (statusCode, ...args: any[]) {
          return originalWriteHead.call(this, 404, ...args);
        };
        req.url = '/404.html';
        next();
      });
    },
    configurePreviewServer(server) {
      server.middlewares.use((req, res, next) => {
        if (req.method !== 'GET' && req.method !== 'HEAD') return next();
        const rawUrl = req.url ? req.url.split('?')[0] : '';
        if (rawUrl.includes('.')) return next();
        const trimmed = rawUrl.replace(/^\/+|\/+$/g, '');
        const directPath = resolve(__dirname, 'dist', trimmed, 'index.html');
        if (trimmed === '' || existsSync(directPath)) return next();

        const originalWriteHead = res.writeHead;
        res.writeHead = function (statusCode, ...args: any[]) {
          return originalWriteHead.call(this, 404, ...args);
        };
        req.url = '/404.html';
        next();
      });
    },
  };
}

export default defineConfig({
  plugins: [react(), tailwindcss(), mpa404Plugin()],
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

