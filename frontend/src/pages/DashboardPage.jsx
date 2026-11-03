import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../utils/api';
import Avatar from '../components/Avatar';
import BadgePill from '../components/BadgePill';
import Toast from '../components/Toast';
import FirstTimeGuide from '../components/FirstTimeGuide';
import {
  Coins,
  Flame,
  Award,
  Calendar,
  Users,
  Compass,
  Briefcase,
  ArrowRight,
  Clock,
  Video,
  Check,
  X,
  Sparkles,
  TrendingUp,
  AlertTriangle,
  GraduationCap,
} from 'lucide-react';

const getCollegeDomain = (member) =>
  (member?.collegeDomain || member?.email?.split('@').pop() || '').toLowerCase();

export default function DashboardPage() {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [campusMatches, setCampusMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);

  const fetchDashboard = async () => {
    try {
      const data = await apiRequest('/dashboard');
      setDashboard(data);
    } catch (err) {
      console.error('Failed to load dashboard:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
    apiRequest('/matches')
      .then((data) => setCampusMatches(data || []))
      .catch((err) => console.error('Failed to load campus peers:', err));
  }, []);

  const handleActionRequest = async (requestId, action) => {
    try {
      await apiRequest(`/swap-requests/${requestId}`, {
        method: 'PUT',
        body: JSON.stringify({ action }),
      });
      setToast({ message: `Request ${action.toLowerCase()}ed!`, type: 'success' });
      fetchDashboard();
    } catch (err) {
      setToast({ message: err.message || 'Action failed', type: 'error' });
    }
  };

  if (loading) {
    return (
      <div className="p-8 flex items-center justify-center min-h-[60vh]">
        <div className="w-8 h-8 rounded-full border-2 border-brand-500 border-t-transparent animate-spin"></div>
      </div>
    );
  }

  const userData = dashboard?.user || user;
  const collegeDomain = getCollegeDomain(userData);
  const localCampusMatches = campusMatches.filter((match) =>
    collegeDomain && getCollegeDomain(match.user) === collegeDomain);
  const crossCampusCount = campusMatches.length - localCampusMatches.length;
  const xpProgress = dashboard?.xpProgressPercent || 0;
  const targetXp = dashboard?.nextLevelTarget || 100;
  const placementReadiness = dashboard?.placementReadiness || 0;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}

      {/* Greeting Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 p-6 rounded-3xl bg-gradient-to-r from-brand-600 to-emerald-600 text-white shadow-lg shadow-brand-500/15">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs font-bold uppercase tracking-wider bg-white/20 px-2.5 py-0.5 rounded-full">
              {userData.role} • Year {userData.studyYear || 2}
            </span>
            <span className="text-xs text-white/80 flex items-center gap-1">
              <Flame className="w-3.5 h-3.5 fill-amber-300 text-amber-300" />
              {userData.dailyStreak || 1} Day Streak
            </span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Welcome back, {userData.fullName}! 👋
          </h1>
          <p className="text-xs sm:text-sm text-white/90 mt-1 max-w-xl">
            "Learn. Teach. Practice. Grow Together." Keep trading skills and leveling up your college career.
          </p>
        </div>

        {/* Quick Stats in Banner */}
        <div className="flex items-center gap-3 shrink-0">
          <FirstTimeGuide userId={userData.id} />
          <div className="bg-white/10 backdrop-blur-md px-4 py-3 rounded-2xl border border-white/20 text-center">
            <div className="flex items-center justify-center gap-1 text-amber-300 font-extrabold text-lg">
              <Coins className="w-5 h-5 fill-amber-300" />
              <span>{userData.skillCredits || 0}</span>
            </div>
            <p className="text-[10px] text-white/80 font-bold uppercase tracking-wider">SkillCredits</p>
          </div>

          <div className="bg-white/10 backdrop-blur-md px-4 py-3 rounded-2xl border border-white/20 text-center">
            <div className="flex items-center justify-center gap-1 text-white font-extrabold text-lg">
              <TrendingUp className="w-5 h-5 text-emerald-300" />
              <span>{placementReadiness}%</span>
            </div>
            <p className="text-[10px] text-white/80 font-bold uppercase tracking-wider">Placement</p>
          </div>
        </div>
      </div>

      <section className="p-5 sm:p-6 rounded-3xl bg-white dark:bg-slate-900 border border-indigo-200 dark:border-indigo-900 shadow-xs">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4">
          <div className="flex items-start gap-3">
            <div className="w-10 h-10 rounded-xl bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 flex items-center justify-center shrink-0">
              <GraduationCap className="w-5 h-5" />
            </div>
            <div>
              <h2 className="font-bold text-slate-900 dark:text-white">Your campus community</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                {collegeDomain
                  ? `Showing verified peers from ${collegeDomain} first.`
                  : 'Your campus is identified by your college email domain.'}
              </p>
            </div>
          </div>
          <Link
            to="/swap"
            className="text-xs font-bold px-3.5 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white flex items-center gap-1.5 self-start sm:self-auto"
          >
            Find campus peers
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {localCampusMatches.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {localCampusMatches.slice(0, 3).map((match) => (
              <div key={match.user.id} className="flex items-center gap-3 p-3 rounded-2xl bg-indigo-50/70 dark:bg-indigo-950/30 border border-indigo-100 dark:border-indigo-900">
                <Avatar src={match.user.photoUrl} name={match.user.fullName} size="md" />
                <div className="min-w-0">
                  <p className="text-xs font-bold text-slate-900 dark:text-white truncate">{match.user.fullName}</p>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{match.user.branch}</p>
                  <p className="text-[10px] font-semibold text-indigo-700 dark:text-indigo-300">{getCollegeDomain(match.user)}</p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-xs text-slate-500 dark:text-slate-400 py-2">
            No verified peers from your college yet. Students from other colleges are still available in Skill Swap.
          </p>
        )}
        {crossCampusCount > 0 && (
          <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-3">
            {crossCampusCount} verified {crossCampusCount === 1 ? 'peer is' : 'peers are'} also available from other colleges.
          </p>
        )}
      </section>

      {/* Top 3 Metric Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Card 1: Level & XP Bar */}
        <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Gamification Level</span>
            <span className="text-xs font-bold px-2 py-0.5 rounded-full bg-indigo-50 dark:bg-indigo-950 text-indigo-600 dark:text-indigo-400 border border-indigo-200 dark:border-indigo-800">
              {userData.level || 'Beginner'}
            </span>
          </div>
          <div className="flex items-baseline gap-2 mb-2">
            <span className="text-2xl font-black text-slate-900 dark:text-white">{userData.xp || 0}</span>
            <span className="text-xs text-slate-400">/ {targetXp} XP to next level</span>
          </div>
          <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-2.5 overflow-hidden">
            <div
              className="bg-indigo-500 h-2.5 rounded-full transition-all duration-500"
              style={{ width: `${Math.min(100, Math.max(5, xpProgress))}%` }}
            ></div>
          </div>
        </div>

        {/* Card 2: Trust Reliability Score */}
        <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Reliability Score</span>
            <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400">
              ★ {userData.averageRating || '5.0'} Rating
            </span>
          </div>
          <div className="flex items-baseline gap-2 mb-2">
            <span className="text-2xl font-black text-slate-900 dark:text-white">
              {userData.reliabilityScore || 100}%
            </span>
            <span className="text-xs text-slate-400">Trust rating</span>
          </div>
          <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-2.5 overflow-hidden">
            <div
              className="bg-emerald-500 h-2.5 rounded-full transition-all duration-500"
              style={{ width: `${Math.min(100, userData.reliabilityScore || 100)}%` }}
            ></div>
          </div>
        </div>

        {/* Card 3: Badges Earned */}
        <div className="p-5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">College Badges</span>
              <span className="text-xs text-slate-400 font-semibold">{userData.badges?.length || 0} Earned</span>
            </div>
            <div className="flex flex-wrap gap-1.5 mt-2 max-h-16 overflow-y-auto">
              {userData.badges && userData.badges.length > 0 ? (
                userData.badges.map((b) => <BadgePill key={b} badge={b} />)
              ) : (
                <span className="text-xs text-slate-400">Complete tutoring & quizzes to earn badges!</span>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Weak Topics & Smart Recommendation Box */}
      {dashboard?.recommendationSummary && (
        <div className="p-4 sm:p-5 rounded-2xl bg-amber-50/70 dark:bg-amber-950/20 border border-amber-200/80 dark:border-amber-800/40 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-start gap-3">
            <div className="w-9 h-9 rounded-xl bg-amber-500/20 text-amber-600 dark:text-amber-400 flex items-center justify-center shrink-0 mt-0.5">
              <Compass className="w-5 h-5" />
            </div>
            <div>
              <h4 className="text-xs font-bold uppercase tracking-wider text-amber-800 dark:text-amber-300">
                Personalized Learning Recommendations
              </h4>
              <p className="text-xs sm:text-sm text-slate-700 dark:text-slate-300 mt-0.5 font-medium leading-relaxed">
                {dashboard.recommendationSummary}
              </p>
            </div>
          </div>
          <Link
            to="/recommendations"
            className="shrink-0 text-xs font-bold px-3.5 py-2 rounded-xl bg-amber-600 hover:bg-amber-700 text-white shadow-xs transition-colors flex items-center gap-1.5"
          >
            Explore Plan
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      )}

      {/* Main Grid: Upcoming Sessions & Swap Requests */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left Column: Upcoming Tutoring & Group Sessions */}
        <div className="space-y-6">
          {/* Upcoming 1-to-1 Tutoring */}
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base flex items-center gap-2">
                <Calendar className="w-4 h-4 text-brand-600" />
                Upcoming Tutoring Sessions
              </h3>
              <Link to="/swap" className="text-xs font-semibold text-brand-600 hover:underline">
                View all
              </Link>
            </div>

            {dashboard?.upcomingTutoring && dashboard.upcomingTutoring.length > 0 ? (
              <div className="space-y-3">
                {dashboard.upcomingTutoring.map((s) => (
                  <div
                    key={s.id}
                    className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 flex items-center justify-between gap-3"
                  >
                    <div>
                      <h4 className="text-xs font-bold text-slate-900 dark:text-white">
                        {s.skillName} Tutoring
                      </h4>
                      <p className="text-xs text-slate-500 dark:text-slate-400 flex items-center gap-2 mt-1">
                        <span className="flex items-center gap-1">
                          <Clock className="w-3 h-3" />
                          {s.durationMinutes} mins
                        </span>
                        <span>•</span>
                        <span>{s.mode}</span>
                      </p>
                    </div>

                    {s.meetingLink ? (
                      <a
                        href={s.meetingLink}
                        target="_blank"
                        rel="noreferrer"
                        className="px-3 py-1.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs flex items-center gap-1.5 shadow-xs transition-colors shrink-0"
                      >
                        <Video className="w-3.5 h-3.5" />
                        Join Room
                      </a>
                    ) : (
                      <span className="text-xs font-semibold text-slate-400">Offline Location</span>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-6 text-center text-slate-400 text-xs">
                No upcoming tutoring sessions scheduled.{' '}
                <Link to="/swap" className="text-brand-600 dark:text-brand-400 font-bold hover:underline">
                  Find a peer tutor
                </Link>
              </div>
            )}
          </div>

          {/* Upcoming Group Study Sessions */}
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base flex items-center gap-2">
                <Users className="w-4 h-4 text-indigo-600" />
                Study Group Live Sessions
              </h3>
              <Link to="/groups" className="text-xs font-semibold text-brand-600 hover:underline">
                Explore Groups
              </Link>
            </div>

            {dashboard?.upcomingGroupSessions && dashboard.upcomingGroupSessions.length > 0 ? (
              <div className="space-y-3">
                {dashboard.upcomingGroupSessions.map((gs) => (
                  <div
                    key={gs.id}
                    className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 flex items-center justify-between gap-3"
                  >
                    <div>
                      <h4 className="text-xs font-bold text-slate-900 dark:text-white truncate max-w-xs">
                        {gs.title}
                      </h4>
                      <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                        {gs.durationMinutes} min • {gs.mode}
                      </p>
                    </div>

                    <Link
                      to={`/groups/${gs.groupId}`}
                      className="px-3 py-1.5 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs flex items-center gap-1.5 shadow-xs transition-colors shrink-0"
                    >
                      <Users className="w-3.5 h-3.5" />
                      View Group
                    </Link>
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-6 text-center text-slate-400 text-xs">
                You haven't joined any groups with live sessions scheduled.{' '}
                <Link to="/groups" className="text-brand-600 dark:text-brand-400 font-bold hover:underline">
                  Browse groups
                </Link>
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Pending Requests & Placement Ring */}
        <div className="space-y-6">
          {/* Pending Skill Swap Requests */}
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-amber-500" />
                Pending Swap Requests
              </h3>
              {dashboard?.pendingRequestsCount > 0 && (
                <span className="px-2 py-0.5 rounded-full text-xs font-bold bg-amber-100 dark:bg-amber-950 text-amber-700 dark:text-amber-300">
                  {dashboard.pendingRequestsCount} Pending
                </span>
              )}
            </div>

            {dashboard?.pendingRequests && dashboard.pendingRequests.length > 0 ? (
              <div className="space-y-3">
                {dashboard.pendingRequests.map((req) => (
                  <div
                    key={req.id}
                    className="p-4 rounded-2xl bg-amber-50/50 dark:bg-amber-950/20 border border-amber-200/60 dark:border-amber-800/40"
                  >
                    <div className="flex items-start justify-between gap-2 mb-2">
                      <div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-amber-700 dark:text-amber-400">
                          Tutoring Request
                        </span>
                        <h4 className="font-bold text-slate-900 dark:text-white text-sm">
                          Skill: {req.skillName}
                        </h4>
                      </div>
                      <div className="flex items-center gap-1.5">
                        <button
                          onClick={() => handleActionRequest(req.id, 'ACCEPT')}
                          className="p-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white shadow-xs transition-colors"
                          title="Accept Request"
                        >
                          <Check className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleActionRequest(req.id, 'REJECT')}
                          className="p-1.5 rounded-lg bg-rose-600 hover:bg-rose-700 text-white shadow-xs transition-colors"
                          title="Decline Request"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                    {req.message && (
                      <p className="text-xs text-slate-600 dark:text-slate-300 italic bg-white/60 dark:bg-slate-800/60 p-2 rounded-xl">
                        "{req.message}"
                      </p>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-6 text-center text-slate-400 text-xs">
                No incoming swap requests right now.
              </div>
            )}
          </div>

          {/* Placement Readiness Breakdown Mini-Widget */}
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base flex items-center gap-2">
                <Briefcase className="w-4 h-4 text-purple-600" />
                Placement Readiness Overview
              </h3>
              <Link to="/placement" className="text-xs font-semibold text-brand-600 hover:underline">
                Full Details →
              </Link>
            </div>

            <div className="flex items-center gap-6">
              {/* Ring / Percentage Display */}
              <div className="relative w-24 h-24 flex items-center justify-center shrink-0">
                <svg className="w-24 h-24 transform -rotate-90" viewBox="0 0 36 36">
                  <path
                    className="text-slate-100 dark:text-slate-800"
                    strokeWidth="3.5"
                    stroke="currentColor"
                    fill="none"
                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                  <path
                    className="text-brand-500"
                    strokeDasharray={`${placementReadiness}, 100`}
                    strokeWidth="3.5"
                    strokeLinecap="round"
                    stroke="currentColor"
                    fill="none"
                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                </svg>
                <div className="absolute flex flex-col items-center justify-center text-center">
                  <span className="text-xl font-black text-slate-900 dark:text-white leading-none">
                    {placementReadiness}%
                  </span>
                  <span className="text-[9px] text-slate-400 font-bold uppercase tracking-wider">Ready</span>
                </div>
              </div>

              {/* 5 pillars checklist */}
              <div className="text-xs space-y-1.5 flex-1">
                <div className="flex justify-between py-0.5 border-b border-slate-100 dark:border-slate-800">
                  <span className="text-slate-500">DSA Progress</span>
                  <span className="font-semibold text-slate-800 dark:text-slate-200">
                    {dashboard?.placementSummary?.dsaSolved || 0}/150 Solved
                  </span>
                </div>
                <div className="flex justify-between py-0.5 border-b border-slate-100 dark:border-slate-800">
                  <span className="text-slate-500">Aptitude Score</span>
                  <span className="font-semibold text-slate-800 dark:text-slate-200">
                    {dashboard?.placementSummary?.aptitudeScore || 70}%
                  </span>
                </div>
                <div className="flex justify-between py-0.5">
                  <span className="text-slate-500">Mock Interview</span>
                  <span className="font-semibold text-slate-800 dark:text-slate-200">
                    ★ {dashboard?.placementSummary?.mockInterviewRating || 4.0}/5
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
