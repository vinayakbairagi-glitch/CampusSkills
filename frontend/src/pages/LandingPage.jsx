import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Sparkles,
  ArrowRight,
  ShieldCheck,
  Coins,
  Users,
  HelpCircle,
  FolderGit2,
  FileCheck2,
  Compass,
  Briefcase,
  CheckCircle2,
  Code2,
  Palette,
  Clock,
  Award,
} from 'lucide-react';

export default function LandingPage() {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 selection:bg-brand-500 selection:text-white">
      {/* Hero Section */}
      <section className="relative overflow-hidden pt-12 pb-20 lg:pt-20 lg:pb-32">
        <div className="absolute inset-0 -z-10 bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,rgba(34,197,94,0.15),rgba(255,255,255,0))] dark:bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,rgba(34,197,94,0.1),rgba(0,0,0,0))]"></div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-brand-50 dark:bg-brand-950/50 border border-brand-200 dark:border-brand-800 text-brand-700 dark:text-brand-300 text-xs font-bold mb-8 shadow-xs animate-in fade-in slide-in-from-bottom-2">
            <ShieldCheck className="w-4 h-4 text-brand-600" />
            <span>Verified College Peer-Learning Platform</span>
          </div>

          {/* Heading with Tagline */}
          <h1 className="text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight text-slate-900 dark:text-white max-w-4xl mx-auto leading-[1.1] mb-6">
            Campus<span className="text-brand-600 dark:text-brand-500">Skills</span>
            <span className="block text-2xl sm:text-3xl lg:text-4xl font-semibold text-slate-600 dark:text-slate-400 mt-3">
              "Learn. Teach. Practice. Grow Together."
            </span>
          </h1>

          <p className="text-base sm:text-lg text-slate-600 dark:text-slate-300 max-w-2xl mx-auto leading-relaxed mb-10">
            A credit-backed skill exchange where verified college peers tutor each other, solve engineering doubts, form capstone project teams, and prepare for placement tests.
          </p>

          {/* CTA Buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-16">
            {user ? (
              <Link
                to="/dashboard"
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-8 py-3.5 rounded-2xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-sm shadow-lg shadow-brand-600/25 transition-all hover:-translate-y-0.5"
              >
                Go to My Dashboard
                <ArrowRight className="w-4 h-4" />
              </Link>
            ) : (
              <>
                <Link
                  to="/register"
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-8 py-3.5 rounded-2xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-sm shadow-lg shadow-brand-600/25 transition-all hover:-translate-y-0.5"
                >
                  Get Started (3 Free Credits)
                  <ArrowRight className="w-4 h-4" />
                </Link>
                <Link
                  to="/login"
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-8 py-3.5 rounded-2xl bg-white dark:bg-slate-900 hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-800 dark:text-slate-200 border border-slate-200 dark:border-slate-800 font-bold text-sm shadow-xs transition-all"
                >
                  Sign In to Campus
                </Link>
              </>
            )}
          </div>

          {/* How SkillCredits Work - Visual Interactive Banner */}
          <div className="max-w-4xl mx-auto bg-white dark:bg-slate-900 rounded-3xl p-6 sm:p-8 border border-slate-200/80 dark:border-slate-800 shadow-xl text-left relative overflow-hidden">
            <div className="absolute top-0 right-0 w-64 h-64 bg-amber-500/10 rounded-full blur-3xl pointer-events-none"></div>

            <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-amber-600 dark:text-amber-400 mb-2">
              <Coins className="w-4 h-4" />
              The SkillCredits Engine
            </div>
            <h2 className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-white mb-4">
              How Peer Tutoring Exchanges Work
            </h2>

            {/* Example Scenario Card */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-center bg-slate-50 dark:bg-slate-800/50 p-5 rounded-2xl border border-slate-200/60 dark:border-slate-700/60">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-2xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 flex items-center justify-center font-bold text-lg">
                  <Code2 className="w-6 h-6" />
                </div>
                <div>
                  <h4 className="font-bold text-slate-900 dark:text-white text-sm">1. Ravi Teaches Java</h4>
                  <p className="text-xs text-slate-500 dark:text-slate-400">1 hr tutoring session (+1 Credit, +30 XP)</p>
                </div>
              </div>

              <div className="flex justify-center text-center py-2 md:py-0">
                <div className="px-3 py-1.5 rounded-full bg-amber-100 dark:bg-amber-950 text-amber-800 dark:text-amber-300 font-bold text-xs flex items-center gap-1.5 shadow-xs">
                  <Coins className="w-3.5 h-3.5 text-amber-500" />
                  <span>1 Credit Exchanged</span>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-2xl bg-rose-500/10 text-rose-600 dark:text-rose-400 flex items-center justify-center font-bold text-lg">
                  <Palette className="w-6 h-6" />
                </div>
                <div>
                  <h4 className="font-bold text-slate-900 dark:text-white text-sm">2. Learns Figma from Priya</h4>
                  <p className="text-xs text-slate-500 dark:text-slate-400">Spends his 1 earned credit to master UI design</p>
                </div>
              </div>
            </div>

            <p className="text-xs text-slate-500 dark:text-slate-400 mt-4 leading-relaxed">
              ⭐ <em>Example:</em> Ravi teaches Java for 1 hour, earns 1 credit, and spends it learning Figma from Priya. Both rate each other after completion, boosting their trust reliability scores!
            </p>
          </div>
        </div>
      </section>

      {/* Feature Grid */}
      <section className="py-20 bg-white dark:bg-slate-900/60 border-y border-slate-200 dark:border-slate-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <h2 className="text-xs font-bold uppercase tracking-wider text-brand-600 dark:text-brand-400 mb-2">
              Everything in One Verified Hub
            </h2>
            <h3 className="text-3xl font-extrabold text-slate-900 dark:text-white">
              Built Specifically for Engineering Colleges
            </h3>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 sm:gap-8">
            {/* 1 */}
            <div className="p-6 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 hover:border-brand-500/40 transition-all">
              <div className="w-10 h-10 rounded-xl bg-brand-500/10 text-brand-600 flex items-center justify-center mb-4">
                <Compass className="w-5 h-5" />
              </div>
              <h4 className="font-bold text-slate-900 dark:text-white text-base mb-2">Smart Matching Engine</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                70% bidirectional skill coverage + 20% average peer rating + 10% overlapping availability schedules. Instantly see who can teach you and what you can teach them.
              </p>
            </div>

            {/* 2 */}
            <div className="p-6 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 hover:border-brand-500/40 transition-all">
              <div className="w-10 h-10 rounded-xl bg-indigo-500/10 text-indigo-600 flex items-center justify-center mb-4">
                <Users className="w-5 h-5" />
              </div>
              <h4 className="font-bold text-slate-900 dark:text-white text-base mb-2">Collaborative Study Groups</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Filter by subject, branch & year. Join public groups instantly or apply to private clubs. Real-time STOMP WebSocket live chat, session attendance tracking, and resource vaults.
              </p>
            </div>

            {/* 3 */}
            <div className="p-6 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 hover:border-brand-500/40 transition-all">
              <div className="w-10 h-10 rounded-xl bg-amber-500/10 text-amber-600 flex items-center justify-center mb-4">
                <HelpCircle className="w-5 h-5" />
              </div>
              <h4 className="font-bold text-slate-900 dark:text-white text-base mb-2">Doubt-Solving Board</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Live search duplicate doubts before asking. Asker picks the Best Answer (+1 Credit & +25 XP). Seniors (Year 3+) and faculty verify correct solutions.
              </p>
            </div>

            {/* 4 */}
            <div className="p-6 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 hover:border-brand-500/40 transition-all">
              <div className="w-10 h-10 rounded-xl bg-rose-500/10 text-rose-600 flex items-center justify-center mb-4">
                <FolderGit2 className="w-5 h-5" />
              </div>
              <h4 className="font-bold text-slate-900 dark:text-white text-base mb-2">Project Team Finder</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                Post capstone or hackathon project ideas, recruit team roles, assign task deadlines, and monitor completion progress bars. Earn +40 XP on project delivery.
              </p>
            </div>

            {/* 5 */}
            <div className="p-6 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 hover:border-brand-500/40 transition-all">
              <div className="w-10 h-10 rounded-xl bg-sky-500/10 text-sky-600 flex items-center justify-center mb-4">
                <FileCheck2 className="w-5 h-5" />
              </div>
              <h4 className="font-bold text-slate-900 dark:text-white text-base mb-2">Exams, Quizzes & Adaptive Tests</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                6 question types (MCQ, True/False, Fill Blank, Numeric, Match, Short). Timed server-side auto-submit, topic-wise accuracy analytics, and printable passed certificates.
              </p>
            </div>

            {/* 6 */}
            <div className="p-6 rounded-2xl bg-slate-50 dark:bg-slate-800/40 border border-slate-200/80 dark:border-slate-800 hover:border-brand-500/40 transition-all">
              <div className="w-10 h-10 rounded-xl bg-purple-500/10 text-purple-600 flex items-center justify-center mb-4">
                <Briefcase className="w-5 h-5" />
              </div>
              <h4 className="font-bold text-slate-900 dark:text-white text-base mb-2">Placement Readiness Tracker</h4>
              <p className="text-xs text-slate-600 dark:text-slate-400 leading-relaxed">
                0-100% composite score combining DSA problem solve count, mock exam aptitude, senior mock interview ratings, resume scoring, and completed project portfolio.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-8 border-t border-slate-200 dark:border-slate-800 text-center text-xs text-slate-500 dark:text-slate-400">
        <p>© 2026 CampusSkills. Built with Spring Boot 3 + Java 17 & React + Tailwind CSS.</p>
      </footer>
    </div>
  );
}
