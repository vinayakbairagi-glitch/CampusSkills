import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../utils/api';
import Avatar from '../components/Avatar';
import {
  FiCompass, FiBookOpen, FiUsers, FiUserCheck, FiArrowRight,
  FiAlertCircle, FiCheckCircle, FiStar, FiZap, FiTarget
} from 'react-icons/fi';

export default function RecommendationsPage() {
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/api/recommendations')
      .then(res => setData(res.data))
      .catch(err => setError(err.response?.data?.message || 'Failed to load recommendations'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center py-24 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  const weakTopics = data?.weakTopics || [];
  const summary = data?.summary || '';

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-12">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-purple-700 via-indigo-600 to-blue-600 rounded-3xl p-6 sm:p-8 text-white shadow-xl">
        <span className="px-3 py-1 bg-white/20 backdrop-blur rounded-full text-xs font-semibold tracking-wide uppercase">
          AI Smart Study Assistant
        </span>
        <h1 className="text-2xl sm:text-3xl font-extrabold mt-2">Personalized Learning Recommendations</h1>
        <p className="text-indigo-100 text-sm mt-1 max-w-2xl">
          Based on your test attempts and quiz performance, we identify weak topic areas under 60% accuracy and automatically suggest tailored practice quizzes, study circles, and verified peer tutors.
        </p>
      </div>

      {error && (
        <div className="p-4 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400 rounded-xl text-sm flex items-center">
          <FiAlertCircle className="mr-2" /> {error}
        </div>
      )}

      {/* AI Summary Highlight Box */}
      {summary && (
        <div className="p-4 sm:p-5 rounded-2xl bg-amber-50/70 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800/40 flex items-start space-x-3">
          <FiZap className="text-amber-500 text-xl flex-shrink-0 mt-0.5" />
          <div>
            <h3 className="text-xs font-bold text-amber-900 dark:text-amber-300 uppercase tracking-wider">
              Diagnostic Summary
            </h3>
            <p className="text-sm font-medium text-amber-800 dark:text-amber-200 mt-0.5">
              {summary}
            </p>
          </div>
        </div>
      )}

      {/* Weak Topics List */}
      {weakTopics.length === 0 ? (
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-12 text-center border border-slate-200 dark:border-slate-700 space-y-3">
          <FiCheckCircle className="mx-auto text-4xl text-emerald-500" />
          <h2 className="text-lg font-bold text-slate-900 dark:text-white">All Topics Are Strong!</h2>
          <p className="text-sm text-slate-500 max-w-md mx-auto">
            You scored 60% or higher across all tested subjects and topics. Take more tests to keep practicing or help peers as a tutor!
          </p>
          <div className="pt-2">
            <Link
              to="/tests"
              className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold transition inline-block"
            >
              Browse All Tests
            </Link>
          </div>
        </div>
      ) : (
        <div className="space-y-8">
          {weakTopics.map((topicItem, idx) => (
            <div
              key={idx}
              className="bg-white dark:bg-slate-800 rounded-3xl p-6 sm:p-8 border border-slate-200 dark:border-slate-700 shadow-sm space-y-6"
            >
              {/* Topic Header & Accuracy Badge */}
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 dark:border-slate-700 pb-4">
                <div>
                  <span className="text-xs font-bold text-rose-500 uppercase tracking-wider">Needs Improvement</span>
                  <h2 className="text-xl font-bold text-slate-900 dark:text-white mt-0.5">
                    {topicItem.topic}
                  </h2>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="text-right">
                    <span className="text-xs text-slate-400 block font-medium">Topic Accuracy</span>
                    <span className="text-lg font-extrabold text-rose-600 dark:text-rose-400">
                      {topicItem.accuracy}%
                    </span>
                  </div>
                  <div className="w-20 bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                    <div
                      className="bg-rose-500 h-full rounded-full"
                      style={{ width: `${topicItem.accuracy}%` }}
                    ></div>
                  </div>
                </div>
              </div>

              {/* Suggestions Grid (3 pillars: Tests, Groups, Tutors) */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* 1. Practice Tests */}
                <div className="bg-slate-50 dark:bg-slate-750/50 rounded-2xl p-5 border border-slate-100 dark:border-slate-700/60 space-y-3">
                  <h3 className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider flex items-center">
                    <FiBookOpen className="mr-1.5" /> (A) Recommended Quizzes
                  </h3>
                  {(!topicItem.suggestedTests || topicItem.suggestedTests.length === 0) ? (
                    <p className="text-xs text-slate-400 py-3">No specific quiz found for this topic.</p>
                  ) : (
                    <div className="space-y-2">
                      {topicItem.suggestedTests.map(test => (
                        <div key={test.id} className="p-3 bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 flex flex-col justify-between">
                          <div>
                            <p className="text-xs font-bold text-slate-800 dark:text-slate-200 line-clamp-1">{test.title}</p>
                            <p className="text-[11px] text-slate-500 mt-0.5">{test.durationMinutes} mins • {test.testType}</p>
                          </div>
                          <Link
                            to="/tests"
                            className="mt-2 text-[11px] font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center"
                          >
                            Practice Now <FiArrowRight className="ml-1" />
                          </Link>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* 2. Study Groups */}
                <div className="bg-slate-50 dark:bg-slate-750/50 rounded-2xl p-5 border border-slate-100 dark:border-slate-700/60 space-y-3">
                  <h3 className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider flex items-center">
                    <FiUsers className="mr-1.5" /> (B) Study Circles
                  </h3>
                  {(!topicItem.suggestedGroups || topicItem.suggestedGroups.length === 0) ? (
                    <p className="text-xs text-slate-400 py-3">No study group currently tagged with this topic.</p>
                  ) : (
                    <div className="space-y-2">
                      {topicItem.suggestedGroups.map(grp => (
                        <div key={grp.id} className="p-3 bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 flex flex-col justify-between">
                          <div>
                            <p className="text-xs font-bold text-slate-800 dark:text-slate-200 line-clamp-1">{grp.name}</p>
                            <p className="text-[11px] text-slate-500 mt-0.5">{grp.timing || 'Daily'} • {grp.category}</p>
                          </div>
                          <Link
                            to={`/groups/${grp.id}`}
                            className="mt-2 text-[11px] font-bold text-indigo-600 dark:text-indigo-400 hover:underline flex items-center"
                          >
                            Join Circle <FiArrowRight className="ml-1" />
                          </Link>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* 3. Peer Tutors */}
                <div className="bg-slate-50 dark:bg-slate-750/50 rounded-2xl p-5 border border-slate-100 dark:border-slate-700/60 space-y-3">
                  <h3 className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider flex items-center">
                    <FiUserCheck className="mr-1.5" /> (C) Matched Tutors
                  </h3>
                  {(!topicItem.suggestedTutors || topicItem.suggestedTutors.length === 0) ? (
                    <p className="text-xs text-slate-400 py-3">No tutor teaches this exact skill yet.</p>
                  ) : (
                    <div className="space-y-2">
                      {topicItem.suggestedTutors.map((tutor, tIdx) => (
                        <div key={tIdx} className="p-3 bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 flex items-center justify-between">
                          <div className="flex items-center space-x-2.5 min-w-0">
                            <Avatar name={tutor.fullName} src={tutor.photoUrl} size="sm" />
                            <div className="min-w-0">
                              <p className="text-xs font-bold text-slate-800 dark:text-slate-200 truncate">{tutor.fullName}</p>
                              <div className="flex items-center space-x-1 text-[10px] text-amber-500">
                                <span>★ {tutor.rating}</span>
                                {tutor.isVerified && <span className="text-emerald-500 ml-1">✓ Verified</span>}
                              </div>
                            </div>
                          </div>
                          <div className="text-right flex-shrink-0">
                            <span className="px-2 py-0.5 rounded-md bg-emerald-50 dark:bg-emerald-900/40 text-emerald-700 dark:text-emerald-300 text-[11px] font-bold">
                              {tutor.matchPercent}%
                            </span>
                            <Link
                              to="/swap"
                              className="block text-[10px] text-indigo-600 dark:text-indigo-400 font-bold hover:underline mt-1"
                            >
                              Request
                            </Link>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
