import React, { useState } from 'react';
import { Routes, Route, Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

// Components
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import PendingScreen from './components/PendingScreen';

// Pages
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import ResetPasswordPage from './pages/ResetPasswordPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import SkillSwapPage from './pages/SkillSwapPage';
import StudyGroupsPage from './pages/StudyGroupsPage';
import GroupDetailPage from './pages/GroupDetailPage';
import DoubtsPage from './pages/DoubtsPage';
import DoubtDetailPage from './pages/DoubtDetailPage';
import ProjectsPage from './pages/ProjectsPage';
import ProjectDetailPage from './pages/ProjectDetailPage';
import TestsPage from './pages/TestsPage';
import TakeTestPage from './pages/TakeTestPage';
import TestResultPage from './pages/TestResultPage';
import RecommendationsPage from './pages/RecommendationsPage';
import PlacementPage from './pages/PlacementPage';
import LeaderboardPage from './pages/LeaderboardPage';
import CertificatesPage from './pages/CertificatesPage';
import ProfilePage from './pages/ProfilePage';
import NotificationsPage from './pages/NotificationsPage';
import AdminPage from './pages/AdminPage';
import MyNotesPage from './pages/MyNotesPage';

function PageTransition({ children }) {
  const { pathname } = useLocation();

  return (
    <div key={pathname} className="page-transition">
      {children}
    </div>
  );
}

function ProtectedLayout() {
  const { user, loading } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-950">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Pending user gatekeeper: ONLY show PendingScreen until verified by admin/TPO
  if (user.status === 'PENDING') {
    return <PendingScreen />;
  }

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 flex flex-col">
      <Navbar onToggleSidebar={() => setSidebarOpen(prev => !prev)} />
      <div className="flex-1 flex overflow-hidden">
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
          <PageTransition>
            <Outlet />
          </PageTransition>
        </main>
      </div>
    </div>
  );
}

export default function App() {
  const { user, isTpo } = useAuth();
  const homePath = isTpo ? '/admin' : '/dashboard';

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={user ? <Navigate to={homePath} replace /> : <PageTransition><LandingPage /></PageTransition>} />
      <Route path="/login" element={user ? <Navigate to={homePath} replace /> : <PageTransition><LoginPage /></PageTransition>} />
      <Route path="/forgot-password" element={<PageTransition><ForgotPasswordPage /></PageTransition>} />
      <Route path="/reset-password" element={<PageTransition><ResetPasswordPage /></PageTransition>} />
      <Route path="/register" element={user ? <Navigate to={homePath} replace /> : <PageTransition><RegisterPage /></PageTransition>} />

      {/* Protected College App Routes */}
      <Route element={<ProtectedLayout />}>
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/swap" element={<SkillSwapPage />} />
        <Route path="/groups" element={<StudyGroupsPage />} />
        <Route path="/groups/:id" element={<GroupDetailPage />} />
        <Route path="/notes" element={<MyNotesPage />} />
        <Route path="/doubts" element={<DoubtsPage />} />
        <Route path="/doubts/:id" element={<DoubtDetailPage />} />
        <Route path="/projects" element={<ProjectsPage />} />
        <Route path="/projects/:id" element={<ProjectDetailPage />} />
        <Route path="/tests" element={<TestsPage />} />
        <Route path="/tests/:id/take" element={<TakeTestPage />} />
        <Route path="/tests/:id/result" element={<TestResultPage />} />
        <Route path="/recommendations" element={<RecommendationsPage />} />
        <Route path="/placement" element={<PlacementPage />} />
        <Route path="/leaderboard" element={<LeaderboardPage />} />
        <Route path="/certificates" element={<CertificatesPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/notifications" element={<NotificationsPage />} />

        {/* Admin and TPO portal */}
        <Route
          path="/admin"
          element={isTpo ? <AdminPage /> : <Navigate to="/dashboard" replace />}
        />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
