import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Public from './Public';
import Sources from './pages/Sources';
import Home from './pages/Home';
import Destinations from './pages/Destinations';
import Transformations from './pages/Transformations';
import NewSource from './pages/Sources/NewSource';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/app" element={<App />}>
          <Route path="home" element={<Home />} />
          <Route path="sources">
            <Route path="" element={<Sources />} />
            <Route path="new" element={<NewSource />} />
          </Route>
          <Route path="destinations" element={<Destinations />} />
          <Route path="transformations" element={<Transformations />} />
          <Route exact path="" element={<Navigate to="/app/home" replace />} />
        </Route>
        <Route path="/" element={<Public />}>
        </Route>
        <Route path="*" element={<Navigate to="/app/home" replace />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
