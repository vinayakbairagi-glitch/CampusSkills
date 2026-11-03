import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Repeat,
  Users,
  HelpCircle,
  FolderGit2,
  FileCheck2,
  Compass,
  Briefcase,
  Trophy,
  ShieldCheck,
  User,
  X,
  Sparkles,
  NotebookPen,
} from 'lucide-react';

export default function Sidebar({ isOpen, onClose }) {
  const { user, isTpo, isAdmin } = useAuth();

  const navItems = [
    { label: isTpo ? 'Student Dashboard' : 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { label: 'Skill Swap', path: '/swap', icon: Repeat, badge: 'Credits' },
    { label: 'Study Groups', path: '/groups', icon: Users },
    { label: 'My Notes', path: '/notes', icon: NotebookPen },
    { label: 'Doubt Board', path: '/doubts', icon: HelpCircle },
    { label: 'Project Finder', path: '/projects', icon: FolderGit2 },
    { label: 'Tests & Quizzes', path: '/tests', icon: FileCheck2 },
    { label: 'Recommendations', path: '/recommendations', icon: Compass, badge: 'Smart' },
    { label: 'Placement Prep', path: '/placement', icon: Briefcase },
    { label: 'Leaderboard', path: '/leaderboard', icon: Trophy },
    { label: 'My Profile', path: '/profile', icon: User },
  ];

  if (isTpo) {
    navItems.push({
      label: isAdmin ? 'Admin Console' : 'Admin & TPO',
      path: '/admin',
      icon: ShieldCheck,
      badge: 'Portal',
    });
  }

  const sidebarContent = (
    <div className="flex flex-col h-full bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 transition-colors">
      {/* Mobile Top Header */}
      <div className="flex items-center justify-between p-4 lg:hidden border-b border-slate-100 dark:border-slate-800">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center text-white">
            <Sparkles className="w-4 h-4" />
          </div>
          <span className="font-bold text-slate-900 dark:text-white">CampusSkills</span>
        </div>
        <button
          onClick={onClose}
          className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800"
        >
          <X className="w-5 h-5" />
        </button>
      </div>

      {/* Nav List */}
      <div className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        <div className="px-3 pb-2 text-[11px] font-bold text-slate-400 uppercase tracking-wider">
          Learning & Career
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-semibold transition-all group ${
                  isActive
                    ? 'bg-brand-50 text-brand-700 dark:bg-brand-950/50 dark:text-brand-300 shadow-xs'
                    : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100 hover:bg-slate-50 dark:hover:bg-slate-800/60'
                }`
              }
            >
              <div className="flex items-center gap-3">
                <Icon className="w-4 h-4 text-slate-400 group-hover:text-brand-600 dark:group-hover:text-brand-400 transition-colors" />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 group-hover:bg-brand-100 dark:group-hover:bg-brand-900/50 group-hover:text-brand-700 dark:group-hover:text-brand-300">
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}
      </div>

      {/* Footer Banner */}
      <div className="p-3 m-3 rounded-2xl bg-gradient-to-br from-brand-500/10 via-emerald-500/5 to-transparent border border-brand-500/20 text-xs">
        <p className="font-bold text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
          <Sparkles className="w-3.5 h-3.5 text-brand-600" />
          SkillCredits System
        </p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-1 leading-snug">
          Teach 1 hr, earn 1 credit. Spend credits to learn any skill from peers!
        </p>
      </div>
    </div>
  );

  return (
    <>
      {/* Desktop Sidebar (Permanent) */}
      <aside className="hidden lg:block w-64 shrink-0 h-[calc(100vh-4rem)] sticky top-16">
        {sidebarContent}
      </aside>

      {/* Mobile Drawer (Overlay) */}
      {isOpen && (
        <div className="fixed inset-0 z-40 lg:hidden">
          <div
            className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs animate-in fade-in duration-200"
            onClick={onClose}
          />
          <div className="fixed inset-y-0 left-0 w-72 max-w-full z-50 animate-in slide-in-from-left duration-200">
            {sidebarContent}
          </div>
        </div>
      )}
    </>
  );
}
