import React from 'react';
import ReactDOM from 'react-dom/client';
import { NuqsAdapter } from 'nuqs/adapters/react';
import { GuidesPage } from '../pages/GuidesPage';
import '../index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <NuqsAdapter>
      <GuidesPage />
    </NuqsAdapter>
  </React.StrictMode>
);
