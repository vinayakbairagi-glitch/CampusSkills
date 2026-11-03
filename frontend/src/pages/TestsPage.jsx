import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../utils/api';
import { useAuth } from '../context/AuthContext';
import {
  FiCheckCircle, FiClock, FiAward, FiSearch, FiPlus,
  FiBarChart2, FiAlertCircle, FiBookOpen, FiUserCheck, FiTarget
} from 'react-icons/fi';

export default function TestsPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tests, setTests] = useState([]);
  const [banks, setBanks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterType, setFilterType] = useState('ALL');
  const [search, setSearch] = useState('');
  const [error, setError] = useState('');

  // Leaderboard Modal
  const [showLeaderboard, setShowLeaderboard] = useState(false);
  const [leaderboardTest, setLeaderboardTest] = useState(null);
  const [leaderboardData, setLeaderboardData] = useState([]);

  // Create Test Modal
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [title, setTitle] = useState('');
  const [desc, setDesc] = useState('');
  const [subject, setSubject] = useState('');
  const [topic, setTopic] = useState('');
  const [testType, setTestType] = useState('PRACTICE');
  const [duration, setDuration] = useState(30);
  const [maxAttempts, setMaxAttempts] = useState(1);
  const [passingMarks, setPassingMarks] = useState(40);
  const [isOfficial, setIsOfficial] = useState(false);
  const [bankId, setBankId] = useState('');

  const fetchTests = async () => {
    try {
      const res = await api.get('/api/tests');
      setTests(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load tests');
    } finally {
      setLoading(false);
    }
  };

  const fetchBanks = async () => {
    try {
      const res = await api.get('/api/question-banks');
      setBanks(res.data);
    } catch (err) {
      // non-fatal
    }
  };

  useEffect(() => {
    fetchTests();
    fetchBanks();
  }, []);

  const handleStartTest = async (testId) => {
    const confirmed = window.confirm(
      'Starting this assessment enables local browser monitoring: the page records when it is hidden. No camera or external AI is used, and focus changes do not automatically affect your score. Continue?'
    );
    if (!confirmed) return;

    try {
      const res = await api.post(`/api/tests/${testId}/start`);
      // Navigate to test screen with attemptId
      navigate(`/tests/${testId}/take`, { state: { attemptData: res.data } });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to start test');
    }
  };

  const handleOpenLeaderboard = async (test) => {
    setLeaderboardTest(test);
    setShowLeaderboard(true);
    try {
      const res = await api.get(`/api/tests/${test.id}/leaderboard`);
      setLeaderboardData(res.data);
    } catch (err) {
      setLeaderboardData([]);
    }
  };

  const handleCreateTest = async (e) => {
    e.preventDefault();
    try {
      await api.post('/api/tests', {
        title,
        description: desc,
        subject,
        topic,
        testType,
        durationMinutes: Number(duration),
        maxAttempts: Number(maxAttempts),
        passingMarks: Number(passingMarks),
        isOfficial,
        bankId: bankId ? Number(bankId) : null
      });
      setShowCreateModal(false);
      fetchTests();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create test');
    }
  };

  const canCreate = user && (user.role === 'TUTOR' || user.role === 'TPO' || user.role === 'SUPER_ADMIN');

  const filteredTests = tests.filter(t => {
    const matchesType = filterType === 'ALL' || t.testType === filterType;
    const matchesSearch = !search ||
      t.title.toLowerCase().includes(search.toLowerCase()) ||
      t.subject.toLowerCase().includes(search.toLowerCase()) ||
      (t.topic && t.topic.toLowerCase().includes(search.toLowerCase()));
    return matchesType && matchesSearch;
  });

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-indigo-700 via-indigo-600 to-purple-600 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <span className="px-3 py-1 bg-white/20 backdrop-blur rounded-full text-xs font-semibold tracking-wide uppercase">
            Assess & Excel
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold mt-2">Quizzes, Mocks & Skill Tests</h1>
          <p className="text-indigo-100 text-sm mt-1 max-w-xl">
            Test your knowledge with practice questions, AI adaptive challenges, and college-certified skill tests.
          </p>
        </div>
        {canCreate && (
          <button
            onClick={() => setShowCreateModal(true)}
            className="px-5 py-2.5 bg-white text-indigo-700 font-bold rounded-xl text-sm shadow hover:bg-indigo-50 transition flex items-center"
          >
            <FiPlus className="mr-1.5 text-base" /> Create Test
          </button>
        )}
      </div>

      {error && (
        <div className="p-4 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400 rounded-xl text-sm flex items-center">
          <FiAlertCircle className="mr-2 flex-shrink-0" /> {error}
        </div>
      )}

      {/* Controls & Filter Tabs */}
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4">
        <div className="flex flex-wrap gap-1.5 p-1 bg-slate-100 dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 text-xs font-semibold">
          {[
            { label: 'All Tests', value: 'ALL' },
            { label: 'Practice', value: 'PRACTICE' },
            { label: 'Mock Exams', value: 'MOCK' },
            { label: 'Skill Verification', value: 'SKILL_VERIFICATION' },
            { label: 'Adaptive', value: 'ADAPTIVE' },
            { label: 'Study Groups', value: 'GROUP' }
          ].map(tab => (
            <button
              key={tab.value}
              onClick={() => setFilterType(tab.value)}
              className={`px-3 py-1.5 rounded-xl transition ${
                filterType === tab.value
                  ? 'bg-white dark:bg-slate-700 text-indigo-600 dark:text-indigo-400 shadow-sm'
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <div className="relative min-w-[240px]">
          <FiSearch className="absolute left-3 top-3 text-slate-400 text-sm" />
          <input
            type="text"
            placeholder="Search test, subject, topic..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
      </div>

      {/* Test Cards Grid */}
      {loading ? (
        <div className="flex justify-center py-16">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        </div>
      ) : filteredTests.length === 0 ? (
        <div className="text-center py-16 bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 p-8">
          <FiBookOpen className="mx-auto text-4xl text-slate-300 dark:text-slate-600 mb-3" />
          <p className="text-base font-semibold text-slate-700 dark:text-slate-300">No tests found</p>
          <p className="text-sm text-slate-500 mt-1">Try switching categories or clearing search query.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredTests.map(test => {
            const hasAttempted = test.myAttemptsCount > 0;
            const reachedMax = test.myAttemptsCount >= test.maxAttempts;

            return (
              <div
                key={test.id}
                className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700 flex flex-col justify-between hover:shadow-md transition"
              >
                <div className="space-y-3">
                  <div className="flex items-center justify-between gap-2">
                    <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold tracking-wide uppercase bg-indigo-50 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-400">
                      {test.testType}
                    </span>
                    {test.isOfficial && (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300 flex items-center">
                        <FiUserCheck className="mr-1" /> OFFICIAL
                      </span>
                    )}
                  </div>

                  <div>
                    <h3 className="text-base font-bold text-slate-900 dark:text-white line-clamp-1">
                      {test.title}
                    </h3>
                    <p className="text-xs font-semibold text-slate-500 dark:text-slate-400 mt-0.5">
                      {test.subject} • {test.topic || 'All Topics'}
                    </p>
                  </div>

                  {test.description && (
                    <p className="text-xs text-slate-600 dark:text-slate-400 line-clamp-2">
                      {test.description}
                    </p>
                  )}

                  <div className="grid grid-cols-2 gap-2 pt-2 border-t border-slate-100 dark:border-slate-700/60 text-xs text-slate-500">
                    <span className="flex items-center">
                      <FiClock className="mr-1 text-slate-400" /> {test.durationMinutes} mins
                    </span>
                    <span className="flex items-center">
                      <FiTarget className="mr-1 text-slate-400" /> Pass: {test.passingMarks}%
                    </span>
                  </div>

                  {hasAttempted && (
                    <div className="p-2.5 rounded-xl bg-slate-50 dark:bg-slate-700/40 text-xs flex items-center justify-between">
                      <span className="text-slate-500">
                        Attempts: {test.myAttemptsCount}/{test.maxAttempts}
                      </span>
                      <span className={`font-bold ${test.passed ? 'text-emerald-600 dark:text-emerald-400' : 'text-slate-700 dark:text-slate-300'}`}>
                        Best: {test.bestScore != null ? `${test.bestScore} pts` : 'Done'} {test.passed ? '(Passed)' : ''}
                      </span>
                    </div>
                  )}
                </div>

                <div className="pt-4 mt-4 border-t border-slate-100 dark:border-slate-700/60 flex items-center space-x-2">
                  <button
                    onClick={() => handleOpenLeaderboard(test)}
                    className="p-2 rounded-xl text-slate-500 hover:text-indigo-600 hover:bg-slate-50 dark:hover:bg-slate-700 text-sm transition"
                    title="View Leaderboard"
                  >
                    <FiBarChart2 />
                  </button>

                  <button
                    onClick={() => handleStartTest(test.id)}
                    disabled={reachedMax}
                    className={`flex-1 py-2 text-xs font-bold rounded-xl transition shadow-sm ${
                      reachedMax
                        ? 'bg-slate-100 dark:bg-slate-700 text-slate-400 cursor-not-allowed'
                        : 'bg-indigo-600 hover:bg-indigo-700 text-white'
                    }`}
                  >
                    {reachedMax ? 'Attempts Exhausted' : hasAttempted ? 'Retake Test' : 'Start Test'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Leaderboard Modal */}
      {showLeaderboard && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl max-w-lg w-full p-6 shadow-xl space-y-4">
            <div className="flex justify-between items-center">
              <div>
                <h3 className="text-lg font-bold text-slate-900 dark:text-white flex items-center">
                  <FiAward className="mr-2 text-amber-500" /> Leaderboard
                </h3>
                <p className="text-xs text-slate-500">{leaderboardTest?.title}</p>
              </div>
              <button
                onClick={() => setShowLeaderboard(false)}
                className="text-slate-400 hover:text-slate-600 text-sm"
              >
                ✕
              </button>
            </div>

            {leaderboardData.length === 0 ? (
              <p className="text-sm text-slate-500 text-center py-6">No test attempts recorded yet.</p>
            ) : (
              <div className="space-y-2 max-h-80 overflow-y-auto pr-1">
                {leaderboardData.map((item, idx) => (
                  <div
                    key={idx}
                    className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-700/40 text-sm"
                  >
                    <div className="flex items-center space-x-3">
                      <span className={`w-6 text-center font-bold text-xs ${
                        idx === 0 ? 'text-amber-500' : idx === 1 ? 'text-slate-400' : idx === 2 ? 'text-amber-700' : 'text-slate-500'
                      }`}>
                        #{idx + 1}
                      </span>
                      <span className="font-semibold text-slate-800 dark:text-slate-200">
                        {item.fullName || item.userName}
                      </span>
                    </div>
                    <div className="text-right">
                      <span className="font-bold text-indigo-600 dark:text-indigo-400">
                        {item.score} pts ({item.percentage}%)
                      </span>
                      <p className="text-[11px] text-slate-400">{item.timeTakenSeconds}s</p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Create Test Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl max-w-lg w-full p-6 shadow-xl space-y-4 max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">Create Test</h3>
            <form onSubmit={handleCreateTest} className="space-y-4 text-sm">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Java Collections Mastery Quiz"
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Description</label>
                <textarea
                  rows="2"
                  value={desc}
                  onChange={e => setDesc(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Subject</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Java"
                    value={subject}
                    onChange={e => setSubject(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Topic</label>
                  <input
                    type="text"
                    placeholder="e.g. Collections"
                    value={topic}
                    onChange={e => setTopic(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Type</label>
                  <select
                    value={testType}
                    onChange={e => setTestType(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  >
                    <option value="PRACTICE">Practice</option>
                    <option value="MOCK">Mock Exam</option>
                    <option value="SKILL_VERIFICATION">Skill Verification</option>
                    <option value="ADAPTIVE">Adaptive Quiz</option>
                    <option value="GROUP">Group Quiz</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Question Bank</label>
                  <select
                    value={bankId}
                    onChange={e => setBankId(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  >
                    <option value="">Auto Select Questions</option>
                    {banks.map(b => (
                      <option key={b.id} value={b.id}>{b.title} ({b.subject})</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Duration (min)</label>
                  <input
                    type="number"
                    min="1"
                    value={duration}
                    onChange={e => setDuration(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Max Attempts</label>
                  <input
                    type="number"
                    min="1"
                    value={maxAttempts}
                    onChange={e => setMaxAttempts(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Pass %</label>
                  <input
                    type="number"
                    min="1"
                    max="100"
                    value={passingMarks}
                    onChange={e => setPassingMarks(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                  />
                </div>
              </div>

              {(user.role === 'TPO' || user.role === 'SUPER_ADMIN') && (
                <div className="flex items-center space-x-2 pt-1">
                  <input
                    type="checkbox"
                    id="isOfficialCheck"
                    checked={isOfficial}
                    onChange={e => setIsOfficial(e.target.checked)}
                    className="h-4 w-4 text-indigo-600 rounded border-slate-300"
                  />
                  <label htmlFor="isOfficialCheck" className="text-xs font-medium text-slate-700 dark:text-slate-300">
                    Flag as Official College Assessment (Eligible for Official Certificate)
                  </label>
                </div>
              )}

              <div className="flex justify-end space-x-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-xl text-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl text-sm"
                >
                  Create Test
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
