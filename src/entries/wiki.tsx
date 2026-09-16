import React from 'react';
import ReactDOM from 'react-dom/client';
import { NuqsAdapter } from 'nuqs/adapters/react';
import { WikiPage } from '../pages/WikiPage';
import '../index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <NuqsAdapter>
      <WikiPage />
    </NuqsAdapter>
  </React.StrictMode>
);
