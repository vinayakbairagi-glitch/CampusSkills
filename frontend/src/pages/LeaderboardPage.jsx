import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import Avatar from '../components/Avatar';
import BadgePill from '../components/BadgePill';
import { FiAward, FiStar, FiShield, FiTrendingUp, FiZap } from 'react-icons/fi';

export default function LeaderboardPage() {
  const [period, setPeriod] = useState('all');
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchLeaderboard = async (p) => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get(`/api/leaderboard?period=${p}`);
      setData(res.data);
    } catch (err) {
      setData([]);
      setError(err.response?.data?.message || 'Failed to load the leaderboard');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeaderboard(period);
  }, [period]);

  return (
    <div className="max-w-5xl mx-auto space-y-6 pb-12">
      {/* Banner */}
      <div className="bg-gradient-to-r from-amber-600 via-orange-600 to-indigo-700 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <span className="px-3 py-1 bg-white/20 backdrop-blur rounded-full text-xs font-semibold tracking-wide uppercase">
            Campus Recognition
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold mt-2">Campus Leaderboard & Hall of Fame</h1>
          <p className="text-orange-100 text-sm mt-1 max-w-xl">
            Celebrating our top peer tutors, doubt solvers, and quiz champions ranked by learning XP and reliability trust score.
          </p>
        </div>

        {/* Toggle Period */}
        <div className="flex bg-black/20 p-1.5 rounded-2xl backdrop-blur-md">
          <button
            onClick={() => setPeriod('all')}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition ${
              period === 'all' ? 'bg-white text-slate-900 shadow' : 'text-white/80 hover:text-white'
            }`}
          >
            All-Time
          </button>
          <button
            onClick={() => setPeriod('week')}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition ${
              period === 'week' ? 'bg-white text-slate-900 shadow' : 'text-white/80 hover:text-white'
            }`}
          >
            Active This Week
          </button>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400 rounded-xl text-sm" role="alert">
          {error}
        </div>
      )}

      {/* Top 3 Podium Cards */}
      {!loading && data.length >= 3 && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-2">
          {/* #2 Silver */}
          <div className="bg-white dark:bg-slate-800 rounded-3xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm flex flex-col items-center text-center space-y-3 order-2 md:order-1">
            <span className="w-8 h-8 rounded-full bg-slate-100 dark:bg-slate-700 text-slate-500 font-extrabold flex items-center justify-center text-sm shadow-inner">
              2
            </span>
            <Avatar name={data[1].fullName} src={data[1].photoUrl} size="lg" />
            <div>
              <h3 className="font-bold text-slate-900 dark:text-white text-base">{data[1].fullName}</h3>
              <p className="text-xs text-slate-500">{data[1].branch} • Year {data[1].studyYear}</p>
            </div>
            <div className="flex items-center space-x-2">
              <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-indigo-50 dark:bg-indigo-900/40 text-indigo-700 dark:text-indigo-300">
                {data[1].xp} XP
              </span>
              <span className="text-xs font-semibold text-slate-500">
                ★ {data[1].averageRating}
              </span>
            </div>
          </div>

          {/* #1 Gold */}
          <div className="bg-gradient-to-b from-amber-50 to-white dark:from-amber-950/20 dark:to-slate-800 rounded-3xl p-8 border-2 border-amber-400 dark:border-amber-600 shadow-md flex flex-col items-center text-center space-y-3 order-1 md:order-2 transform md:-translate-y-2">
            <div className="w-10 h-10 rounded-full bg-amber-400 text-white font-black flex items-center justify-center text-lg shadow">
              👑
            </div>
            <Avatar name={data[0].fullName} src={data[0].photoUrl} size="xl" />
            <div>
              <span className="px-2 py-0.5 rounded-full text-[10px] font-extrabold bg-amber-100 dark:bg-amber-900/60 text-amber-800 dark:text-amber-200 uppercase">
                #1 Campus Champion
              </span>
              <h3 className="font-extrabold text-slate-900 dark:text-white text-lg mt-1">{data[0].fullName}</h3>
              <p className="text-xs text-slate-500">{data[0].branch} • Year {data[0].studyYear}</p>
            </div>
            <div className="flex items-center space-x-3">
              <span className="px-3 py-1 rounded-full text-sm font-extrabold bg-amber-500 text-white shadow-sm">
                {data[0].xp} XP
              </span>
              <span className="text-xs font-bold text-amber-600 dark:text-amber-400 flex items-center">
                <FiStar className="mr-0.5" /> {data[0].averageRating}
              </span>
            </div>
          </div>

          {/* #3 Bronze */}
          <div className="bg-white dark:bg-slate-800 rounded-3xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm flex flex-col items-center text-center space-y-3 order-3">
            <span className="w-8 h-8 rounded-full bg-amber-100 dark:bg-amber-900/40 text-amber-700 dark:text-amber-300 font-extrabold flex items-center justify-center text-sm">
              3
            </span>
            <Avatar name={data[2].fullName} src={data[2].photoUrl} size="lg" />
            <div>
              <h3 className="font-bold text-slate-900 dark:text-white text-base">{data[2].fullName}</h3>
              <p className="text-xs text-slate-500">{data[2].branch} • Year {data[2].studyYear}</p>
            </div>
            <div className="flex items-center space-x-2">
              <span className="px-2.5 py-1 rounded-full text-xs font-bold bg-indigo-50 dark:bg-indigo-900/40 text-indigo-700 dark:text-indigo-300">
                {data[2].xp} XP
              </span>
              <span className="text-xs font-semibold text-slate-500">
                ★ {data[2].averageRating}
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Leaderboard Table */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 dark:border-slate-700 flex justify-between items-center">
          <h2 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider">
            Rankings ({data.length} Peers)
          </h2>
          <span className="text-xs text-slate-400">
            Trust Score = 60% Rating + 40% Attendance
          </span>
        </div>

        {loading ? (
          <div className="flex justify-center py-16">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
          </div>
        ) : data.length === 0 ? (
          <p className="text-sm text-slate-500 text-center py-12">No data available for this timeframe.</p>
        ) : (
          <div className="divide-y divide-slate-100 dark:divide-slate-700/60">
            {data.map((user, idx) => (
              <div
                key={user.userId || idx}
                className="p-4 hover:bg-slate-50/70 dark:hover:bg-slate-750/50 transition flex items-center justify-between gap-4"
              >
                <div className="flex items-center space-x-4 min-w-0">
                  <span className={`w-7 text-center font-bold text-sm ${
                    idx === 0 ? 'text-amber-500 font-extrabold' : idx === 1 ? 'text-slate-400 font-extrabold' : idx === 2 ? 'text-amber-700 font-extrabold' : 'text-slate-500'
                  }`}>
                    #{idx + 1}
                  </span>
                  <Avatar name={user.fullName} src={user.photoUrl} size="md" />
                  <div className="min-w-0">
                    <div className="flex items-center space-x-2">
                      <p className="text-sm font-bold text-slate-900 dark:text-white truncate">
                        {user.fullName}
                      </p>
                      <span className="text-[10px] px-2 py-0.2 rounded-full font-bold bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300">
                        {user.level}
                      </span>
                    </div>
                    <p className="text-xs text-slate-400">
                      {user.branch || 'Campus'} • Year {user.studyYear || '1'}
                    </p>
                    {user.badges && user.badges.length > 0 && (
                      <div className="flex flex-wrap gap-1 mt-1">
                        {user.badges.slice(0, 3).map((b, bIdx) => (
                          <BadgePill key={bIdx} badge={b} size="xs" />
                        ))}
                      </div>
                    )}
                  </div>
                </div>

                <div className="flex items-center space-x-6 text-right flex-shrink-0">
                  <div>
                    <span className="text-xs font-bold text-slate-800 dark:text-slate-200 flex items-center justify-end">
                      <FiShield className="mr-1 text-emerald-500" /> {user.reliabilityScore}%
                    </span>
                    <span className="text-[11px] text-slate-400">Trust Score</span>
                  </div>

                  <div>
                    <span className="text-xs font-bold text-amber-500 flex items-center justify-end">
                      <FiStar className="mr-1" /> {user.averageRating}
                    </span>
                    <span className="text-[11px] text-slate-400">Rating</span>
                  </div>

                  <div className="min-w-[70px]">
                    <span className="text-sm font-extrabold text-indigo-600 dark:text-indigo-400 block">
                      {user.xp} XP
                    </span>
                    <span className="text-[11px] text-slate-400">Points</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
