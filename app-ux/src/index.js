import React, { Suspense } from 'react';
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
import SourceDetails from './pages/Sources/SourceDetails';
import Pipelines from './pages/Pipelines';
import NewDestination from './pages/Destinations/NewDestination';
import Loading from './components/Loading';
import DestinationDetails from './pages/Destinations/DestinationDetails';
import NewPipeline from './pages/Pipelines/NewPipeline';
import PipelineDetails from './pages/Pipelines/PipelineDetails';
import NewTransformation from './pages/Transformations/NewTransformation';
import TransformationDetails from './pages/Transformations/TransformationDetails';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter basename="/app">
      <Suspense fallback={<div><Loading /></div>}>
        <Routes>
          <Route path="/" element={<App />}>
            <Route path="home" element={<Home />} />
            <Route path="sources">
              <Route path="" element={<Sources />} />
              <Route path="new" element={<NewSource />} />
              <Route path=":id" element={<SourceDetails />} />
              <Route path=":id/:tab" element={<SourceDetails />} />
            </Route>
            <Route path="destinations">
              <Route path="" element={<Destinations />} />
              <Route path="new" element={<NewDestination />} />
              <Route path=":id" element={<DestinationDetails />} />
              <Route path=":id/:tab" element={<DestinationDetails />} />
            </Route>
            <Route path="pipelines" >
              <Route path="" element={<Pipelines />} />
              <Route path="new" element={<NewPipeline />} />
              <Route path=":id" element={<PipelineDetails />} />
            </Route>
            <Route path="transformations" >
              <Route path="" element={<Transformations />} />
              <Route path="new" element={<NewTransformation />} />
              <Route path=":id" element={<TransformationDetails />} />
              <Route path=":id/:tab" element={<TransformationDetails />} />
            </Route>
            <Route exact path="" element={<Navigate to="/home" replace />} />
          </Route>
          <Route path="/" element={<Public />}>
          </Route>
          <Route path="*" element={<Navigate to="/home" replace />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
