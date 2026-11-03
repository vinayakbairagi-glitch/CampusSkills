import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Clock, ShieldAlert, RefreshCw, LogOut, CheckCircle2, Building, Mail, Hash } from 'lucide-react';

export default function PendingScreen() {
  const { user, logout, fetchCurrentUser } = useAuth();
  const [checking, setChecking] = useState(false);

  const handleRefresh = async () => {
    setChecking(true);
    await fetchCurrentUser();
    setTimeout(() => setChecking(false), 600);
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-950">
      <div className="max-w-md w-full bg-white dark:bg-slate-900 rounded-3xl shadow-xl border border-slate-200 dark:border-slate-800 p-8 text-center">
        {/* Animated Icon */}
        <div className="w-20 h-20 rounded-2xl bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 flex items-center justify-center mx-auto mb-6 text-amber-500 shadow-inner">
          <Clock className="w-10 h-10 animate-pulse" />
        </div>

        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300 mb-3">
          Verification Pending
        </span>

        <h1 className="text-2xl font-bold text-slate-900 dark:text-white mb-2">
          Waiting for Verification
        </h1>

        <p className="text-sm text-slate-600 dark:text-slate-400 mb-6 leading-relaxed">
          Welcome to <strong className="text-brand-600">CampusSkills</strong>! Your college account has been created and is currently awaiting approval from a faculty administrator or Training & Placement Officer (TPO).
        </p>

        {/* User Card */}
        <div className="bg-slate-50 dark:bg-slate-800/50 rounded-2xl p-4 text-left border border-slate-100 dark:border-slate-800 mb-6 text-xs space-y-2">
          <div className="flex items-center justify-between py-1 border-b border-slate-200/50 dark:border-slate-700/50">
            <span className="text-slate-500 flex items-center gap-1.5"><Mail className="w-3.5 h-3.5" /> College Email</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{user?.email}</span>
          </div>
          <div className="flex items-center justify-between py-1 border-b border-slate-200/50 dark:border-slate-700/50">
            <span className="text-slate-500 flex items-center gap-1.5"><Hash className="w-3.5 h-3.5" /> Roll Number</span>
            <span className="font-mono font-semibold text-slate-800 dark:text-slate-200">{user?.rollNumber}</span>
          </div>
          <div className="flex items-center justify-between py-1">
            <span className="text-slate-500 flex items-center gap-1.5"><Building className="w-3.5 h-3.5" /> Branch & Year</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{user?.branch || 'CSE'} - Year {user?.studyYear || 1}</span>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-3 justify-center mb-6">
          <button
            onClick={handleRefresh}
            disabled={checking}
            className="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-semibold text-sm shadow-sm transition-all"
          >
            <RefreshCw className={`w-4 h-4 ${checking ? 'animate-spin' : ''}`} />
            {checking ? 'Checking...' : 'Check Status'}
          </button>
          <button
            onClick={logout}
            className="flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 font-semibold text-sm transition-all"
          >
            <LogOut className="w-4 h-4" />
            Sign Out
          </button>
        </div>

        {/* Verification Help */}
        <div className="p-3 rounded-xl bg-indigo-50/70 dark:bg-indigo-950/30 border border-indigo-200/60 dark:border-indigo-800/40 text-left text-xs text-indigo-900 dark:text-indigo-300">
          <p className="font-bold mb-1 flex items-center gap-1">
            <ShieldAlert className="w-3.5 h-3.5 text-indigo-600" />
            Need help with verification?
          </p>
          <p className="text-[11px] leading-relaxed">
            Please contact your campus administrator or Training &amp; Placement Officer to review your account.
          </p>
        </div>
      </div>
    </div>
  );
}
