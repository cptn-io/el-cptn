import React, { Suspense } from 'react';
import ReactDOM from 'react-dom/client';
import './index.scss';
import App from './App';
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
import SignIn from './pages/Public/SignIn';
import Users from './pages/Users';
import NewUser from './pages/Users/NewUser';
import UserDetails from './pages/Users/UserDetails';
import Apps from './pages/Apps';
import UnknownError from './pages/Public/UnknownError';
import Settings from './pages/Settings';

const root = ReactDOM.createRoot(document.getElementById('root'));

const AppRoutes = () => {
  return <BrowserRouter basename="/app">
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
            <Route path=":id/:tab" element={<PipelineDetails />} />
          </Route>
          <Route path="transformations" >
            <Route path="" element={<Transformations />} />
            <Route path="new" element={<NewTransformation />} />
            <Route path=":id" element={<TransformationDetails />} />
            <Route path=":id/:tab" element={<TransformationDetails />} />
          </Route>
          <Route path="users" >
            <Route path="" element={<Users />} />
            <Route path="new" element={<NewUser />} />
            <Route path=":id" element={<UserDetails />} />
          </Route>
          <Route path="apps">
            <Route path="" element={<Apps />} />
          </Route>
          <Route path="settings">
            <Route path="" element={<Settings />} />
            <Route path=":tab" element={<Settings />} />
          </Route>
          <Route exact path="" element={<Navigate to="/home" replace />} />
        </Route>
        <Route path="*" element={<Navigate to="/home" replace />} />
      </Routes>
    </Suspense>
  </BrowserRouter>
}

const PublicRoutes = () => {
  return <BrowserRouter basename=''>
    <Suspense fallback={<div><Loading /></div>}>
      <Routes>
        <Route path="/" element={<Public />}>
          <Route path="signin" element={<SignIn />}> </Route>
          <Route path="unknownError" element={<UnknownError />}> </Route>
          <Route exact path="" element={<Navigate to="/signin" replace />} />
        </Route>
      </Routes>
    </Suspense>
  </BrowserRouter>
}

root.render(
  <>
    <AppRoutes />
    <PublicRoutes />
  </>
);
