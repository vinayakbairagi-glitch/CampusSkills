import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../utils/api';
import { useAuth } from '../context/AuthContext';
import {
  FiBriefcase, FiCheckCircle, FiZap, FiPlus, FiStar,
  FiFileText, FiCode, FiAward, FiAlertCircle, FiTrendingUp
} from 'react-icons/fi';

export default function PlacementPage() {
  const { user, refreshUser } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [msg, setMsg] = useState('');

  // Daily Challenge Modal
  const [showChallengeModal, setShowChallengeModal] = useState(false);
  const [platform, setPlatform] = useState('LeetCode');
  const [problemTitle, setProblemTitle] = useState('');
  const [problemCount, setProblemCount] = useState(1);
  const [savingChallenge, setSavingChallenge] = useState(false);

  // Review Modal (For seniors/tutors/TPO)
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [targetStudentId, setTargetStudentId] = useState('');
  const [reviewResumeScore, setReviewResumeScore] = useState(80);
  const [reviewInterviewRating, setReviewInterviewRating] = useState(4.0);
  const [reviewFeedback, setReviewFeedback] = useState('');

  const fetchPlacement = async () => {
    try {
      const res = await api.get('/api/placement/me');
      setData(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load placement profile');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlacement();
  }, []);

  const handleLogChallenge = async (e) => {
    e.preventDefault();
    setError('');
    setMsg('');

    const count = Number(problemCount);
    const title = problemTitle.trim();
    if (!title) {
      setError('Enter the problem title or topic before saving.');
      return;
    }
    if (!Number.isInteger(count) || count < 1 || count > 20) {
      setError('Enter a whole number of problems between 1 and 20.');
      return;
    }

    setSavingChallenge(true);
    try {
      await api.post('/api/placement/challenge', {
        platform,
        problemTitle: title,
        problemCount: count
      });

      setMsg(`Great job! Logged ${count} problem(s). +${count * 5} XP earned!`);
      setShowChallengeModal(false);
      setProblemTitle('');
      setProblemCount(1);

      // Update the placement history and the XP shown in the shared navbar.
      await Promise.all([fetchPlacement(), refreshUser()]);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to log challenge');
    } finally {
      setSavingChallenge(false);
    }
  };

  const handleReviewStudent = async (e) => {
    e.preventDefault();
    try {
      await api.post('/api/placement/review', {
        studentId: Number(targetStudentId),
        resumeScore: Number(reviewResumeScore),
        mockInterviewRating: Number(reviewInterviewRating),
        feedback: reviewFeedback
      });
      setMsg('Student placement evaluation saved successfully!');
      setShowReviewModal(false);
      setTargetStudentId('');
      setReviewFeedback('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save review');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-24 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  const {
    placementReadiness = 0,
    dsaSolved = 0,
    dsaTarget = 150,
    dsaScore = 0,
    aptitudeScore = 0,
    mockInterviewRating = 0,
    interviewScore = 0,
    resumeScore = 0,
    completedProjectsCount = 0,
    projectScore = 0,
    streakDays = 0,
    recentChallenges = []
  } = data || {};

  const canReview = user && (user.role === 'TUTOR' || user.role === 'TPO' || user.role === 'SUPER_ADMIN' || (user.studyYear && user.studyYear >= 3));

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-12">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-blue-700 via-indigo-700 to-violet-800 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <span className="px-3 py-1 bg-white/20 backdrop-blur rounded-full text-xs font-semibold tracking-wide uppercase">
            Career & Placements
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold mt-2">Campus Placement Readiness Tracker</h1>
          <p className="text-indigo-100 text-sm mt-1 max-w-xl">
            Track and balance the 5 core pillars of campus recruitment: DSA problems, aptitude mock scores, interview performance, resume strength, and project portfolio.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <button
            onClick={() => setShowChallengeModal(true)}
            className="px-5 py-2.5 bg-white text-indigo-700 hover:bg-indigo-50 font-bold rounded-xl text-xs sm:text-sm shadow transition flex items-center"
          >
            <FiPlus className="mr-1.5" /> Log Daily Code Problem
          </button>
          {canReview && (
            <button
              onClick={() => setShowReviewModal(true)}
              className="px-4 py-2.5 bg-white/10 hover:bg-white/20 border border-white/20 text-white font-bold rounded-xl text-xs sm:text-sm backdrop-blur transition flex items-center"
            >
              <FiStar className="mr-1.5" /> Evaluate Student
            </button>
          )}
        </div>
      </div>

      {error && (
        <div className="p-4 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400 rounded-xl text-sm flex items-center">
          <FiAlertCircle className="mr-2" /> {error}
        </div>
      )}

      {msg && (
        <div className="p-4 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-400 rounded-xl text-sm flex items-center">
          <FiCheckCircle className="mr-2" /> {msg}
        </div>
      )}

      {/* Main Readiness Ring & 5 Pillars */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Ring Chart / Score Card */}
        <div className="bg-white dark:bg-slate-800 rounded-3xl p-6 sm:p-8 border border-slate-200 dark:border-slate-700 shadow-sm flex flex-col items-center justify-center text-center space-y-4">
          <h2 className="text-sm font-bold text-slate-400 uppercase tracking-wider">
            Overall Placement Readiness
          </h2>

          <div className="relative w-44 h-44 flex items-center justify-center">
            {/* SVG Circular Progress Ring */}
            <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
              <circle
                cx="50"
                cy="50"
                r="40"
                className="text-slate-100 dark:text-slate-700"
                strokeWidth="10"
                stroke="currentColor"
                fill="transparent"
              />
              <circle
                cx="50"
                cy="50"
                r="40"
                className="text-indigo-600 transition-all duration-1000 ease-out"
                strokeWidth="10"
                strokeDasharray={251.2}
                strokeDashoffset={251.2 - (251.2 * placementReadiness) / 100}
                strokeLinecap="round"
                stroke="currentColor"
                fill="transparent"
              />
            </svg>
            <div className="absolute flex flex-col items-center">
              <span className="text-4xl font-black text-slate-900 dark:text-white">
                {placementReadiness}
              </span>
              <span className="text-xs text-slate-400 font-bold uppercase">out of 100</span>
            </div>
          </div>

          <div className="flex items-center space-x-2 text-xs font-semibold px-3 py-1.5 rounded-full bg-amber-50 dark:bg-amber-900/30 text-amber-700 dark:text-amber-300">
            <FiZap className="text-amber-500" />
            <span>{streakDays} Day Problem Solving Streak</span>
          </div>
        </div>

        {/* 5 Pillars Breakdown (2 columns) */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-800 rounded-3xl p-6 sm:p-8 border border-slate-200 dark:border-slate-700 shadow-sm space-y-5">
          <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center justify-between">
            <span>5 Core Recruitment Pillars</span>
            <span className="text-xs font-normal text-slate-400">Equal 20% weightage each</span>
          </h2>

          <div className="space-y-4">
            {/* 1. DSA Progress */}
            <div className="space-y-1">
              <div className="flex justify-between text-xs font-semibold">
                <span className="text-slate-700 dark:text-slate-300 flex items-center">
                  <FiCode className="mr-1.5 text-indigo-600" /> 1. Data Structures & Algorithms
                </span>
                <span className="text-indigo-600 font-bold">{dsaSolved} / {dsaTarget} ({dsaScore}%)</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                <div className="bg-indigo-600 h-full rounded-full transition-all duration-500" style={{ width: `${dsaScore}%` }}></div>
              </div>
            </div>

            {/* 2. Aptitude Score */}
            <div className="space-y-1">
              <div className="flex justify-between text-xs font-semibold">
                <span className="text-slate-700 dark:text-slate-300 flex items-center">
                  <FiTrendingUp className="mr-1.5 text-blue-600" /> 2. Aptitude & Reasoning (Mock Tests)
                </span>
                <span className="text-blue-600 font-bold">{aptitudeScore}%</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                <div className="bg-blue-600 h-full rounded-full transition-all duration-500" style={{ width: `${aptitudeScore}%` }}></div>
              </div>
            </div>

            {/* 3. Mock Interview */}
            <div className="space-y-1">
              <div className="flex justify-between text-xs font-semibold">
                <span className="text-slate-700 dark:text-slate-300 flex items-center">
                  <FiStar className="mr-1.5 text-amber-500" /> 3. Mock Interview Rating
                </span>
                <span className="text-amber-600 font-bold">{mockInterviewRating} ★ × 20 ({interviewScore}%)</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                <div className="bg-amber-500 h-full rounded-full transition-all duration-500" style={{ width: `${interviewScore}%` }}></div>
              </div>
            </div>

            {/* 4. Resume Score */}
            <div className="space-y-1">
              <div className="flex justify-between text-xs font-semibold">
                <span className="text-slate-700 dark:text-slate-300 flex items-center">
                  <FiFileText className="mr-1.5 text-emerald-600" /> 4. Faculty & Peer Resume Review
                </span>
                <span className="text-emerald-600 font-bold">{resumeScore}%</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                <div className="bg-emerald-500 h-full rounded-full transition-all duration-500" style={{ width: `${resumeScore}%` }}></div>
              </div>
            </div>

            {/* 5. Projects Score */}
            <div className="space-y-1">
              <div className="flex justify-between text-xs font-semibold">
                <span className="text-slate-700 dark:text-slate-300 flex items-center">
                  <FiAward className="mr-1.5 text-purple-600" /> 5. Completed Projects Portfolio
                </span>
                <span className="text-purple-600 font-bold">{completedProjectsCount} completed ({projectScore}%)</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                <div className="bg-purple-600 h-full rounded-full transition-all duration-500" style={{ width: `${projectScore}%` }}></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Coding Activity & Quick Links */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Coding Challenge History */}
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-4">
          <h3 className="text-sm font-bold text-slate-900 dark:text-white flex items-center">
            <FiZap className="mr-2 text-amber-500" /> Daily Challenge Logs
          </h3>
          {recentChallenges.length === 0 ? (
            <p className="text-xs text-slate-400 py-6 text-center">No challenges logged recently. Solve a problem and click Log Daily Code!</p>
          ) : (
            <div className="space-y-2">
              {recentChallenges.map(c => (
                <div key={c.id} className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-700/40 text-xs">
                  <div>
                    <span className="font-semibold text-slate-800 dark:text-slate-200 block">{c.problemTitle || 'Daily Challenge'}</span>
                    <span className="text-slate-400">{c.challengeDate} • {c.platform || 'LeetCode'}</span>
                  </div>
                  <div className="text-right">
                    <span className="font-bold text-emerald-600 dark:text-emerald-400">+{c.xpEarned} XP</span>
                    <p className="text-slate-400">{c.problemCount} solved</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Resources & Placement Groups */}
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-4">
          <h3 className="text-sm font-bold text-slate-900 dark:text-white flex items-center">
            <FiBriefcase className="mr-2 text-indigo-600" /> Placement Prep Hub
          </h3>
          <div className="space-y-3">
            <Link
              to="/groups"
              className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-700 hover:border-indigo-500 transition flex items-center justify-between block"
            >
              <div>
                <p className="text-xs font-bold text-slate-800 dark:text-slate-200">Placement Prep Circles</p>
                <p className="text-[11px] text-slate-500">Join daily 7 PM DSA cohorts and mock rounds</p>
              </div>
              <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400">Browse →</span>
            </Link>

            <Link
              to="/tests"
              className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-700 hover:border-indigo-500 transition flex items-center justify-between block"
            >
              <div>
                <p className="text-xs font-bold text-slate-800 dark:text-slate-200">Official Campus Mock Exams</p>
                <p className="text-[11px] text-slate-500">Take official quantitative, aptitude and coding assessments</p>
              </div>
              <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400">Test Now →</span>
            </Link>

            <Link
              to="/swap"
              className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-700 hover:border-indigo-500 transition flex items-center justify-between block"
            >
              <div>
                <p className="text-xs font-bold text-slate-800 dark:text-slate-200">1-on-1 Mock Interview Mentors</p>
                <p className="text-[11px] text-slate-500">Book sessions with placed seniors and verified tutors</p>
              </div>
              <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400">Find Mentor →</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Log Daily Challenge Modal */}
      {showChallengeModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl max-w-md w-full p-6 shadow-xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white flex items-center">
              <FiZap className="mr-2 text-amber-500" /> Log Daily Code Problem
            </h3>
            <form onSubmit={handleLogChallenge} className="space-y-4 text-sm">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Platform</label>
                <select
                  value={platform}
                  onChange={e => setPlatform(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                >
                  <option value="LeetCode">LeetCode</option>
                  <option value="GeeksforGeeks">GeeksforGeeks</option>
                  <option value="CodeChef">CodeChef</option>
                  <option value="HackerRank">HackerRank</option>
                  <option value="Codeforces">Codeforces</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Problem Title or Topic</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. 3Sum, LRU Cache, Binary Tree Max Path"
                  value={problemTitle}
                  maxLength={180}
                  onChange={e => setProblemTitle(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Problems Solved</label>
                <input
                  type="number"
                  min="1"
                  max="20"
                  value={problemCount}
                  onChange={e => setProblemCount(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
                <p className="text-[11px] text-slate-400 mt-1">+5 XP per problem solved</p>
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowChallengeModal(false)}
                  disabled={savingChallenge}
                  className="px-4 py-2 text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-xl text-sm disabled:opacity-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={savingChallenge || !problemTitle.trim() || !Number.isInteger(Number(problemCount)) || Number(problemCount) < 1 || Number(problemCount) > 20}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {savingChallenge ? 'Saving...' : 'Save & Earn XP'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Review Student Modal */}
      {showReviewModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl max-w-md w-full p-6 shadow-xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white flex items-center">
              <FiStar className="mr-2 text-amber-500" /> Evaluate Peer / Student
            </h3>
            <form onSubmit={handleReviewStudent} className="space-y-4 text-sm">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Student User ID</label>
                <input
                  type="number"
                  required
                  placeholder="e.g. 3"
                  value={targetStudentId}
                  onChange={e => setTargetStudentId(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Resume (0-100)</label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={reviewResumeScore}
                    onChange={e => setReviewResumeScore(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Interview (1-5)</label>
                  <input
                    type="number"
                    min="1"
                    max="5"
                    step="0.5"
                    value={reviewInterviewRating}
                    onChange={e => setReviewInterviewRating(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  />
                </div>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Feedback</label>
                <textarea
                  rows="2"
                  value={reviewFeedback}
                  onChange={e => setReviewFeedback(e.target.value)}
                  placeholder="Detailed constructive feedback on resume & interview readiness..."
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowReviewModal(false)}
                  className="px-4 py-2 text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-xl text-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl text-sm"
                >
                  Submit Review
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
