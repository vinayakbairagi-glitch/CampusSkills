import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import { useWebSocket } from '../context/WebSocketContext';
import {
  FiBell, FiCheck, FiCheckCircle, FiClock, FiAward,
  FiMessageSquare, FiBookOpen, FiAlertCircle
} from 'react-icons/fi';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');
  const { refreshNotifications } = useWebSocket();

  const fetchNotifications = async () => {
    try {
      setError('');
      const res = await api.get('/api/notifications');
      setNotifications(res.data.notifications || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkAllRead = async () => {
    try {
      setError('');
      setMsg('');
      await api.put('/api/notifications');
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
      refreshNotifications();
      setMsg('All notifications marked as read.');
      setTimeout(() => setMsg(''), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to mark notifications as read');
    }
  };

  const getIcon = (type) => {
    switch (type) {
      case 'BADGE':
        return <FiAward className="text-amber-500 text-lg" />;
      case 'DOUBT':
        return <FiMessageSquare className="text-blue-500 text-lg" />;
      case 'TEST':
        return <FiBookOpen className="text-purple-500 text-lg" />;
      case 'TUTORING_SESSION_REMINDER':
      case 'GROUP_SESSION_REMINDER':
        return <FiClock className="text-indigo-500 text-lg" />;
      case 'MODERATION_REPORT':
        return <FiAlertCircle className="text-rose-500 text-lg" />;
      default:
        return <FiBell className="text-indigo-500 text-lg" />;
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-12">
      <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-900 dark:text-white flex items-center">
            <FiBell className="mr-2 text-indigo-600" /> Notifications & Alerts
          </h1>
          <p className="text-xs text-slate-500 mt-0.5">Real-time alerts for swap sessions, doubt answers, badges and group approvals.</p>
        </div>

        <button
          onClick={handleMarkAllRead}
          className="px-4 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-700 dark:hover:bg-slate-600 text-slate-700 dark:text-slate-300 font-bold rounded-xl text-xs transition flex items-center"
        >
          <FiCheck className="mr-1.5" /> Mark All Read
        </button>
      </div>

      {error && (
        <div className="p-3 bg-rose-50 dark:bg-rose-900/20 text-rose-700 dark:text-rose-400 rounded-xl text-xs flex items-center">
          <FiAlertCircle className="mr-1.5" /> {error}
        </div>
      )}

      {msg && (
        <div className="p-3 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-400 rounded-xl text-xs flex items-center">
          <FiCheckCircle className="mr-1.5" /> {msg}
        </div>
      )}

      {loading ? (
        <div className="flex justify-center py-16">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        </div>
      ) : notifications.length === 0 ? (
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-12 text-center border border-slate-200 dark:border-slate-700 space-y-3">
          <FiBell className="mx-auto text-4xl text-slate-300 dark:text-slate-600" />
          <p className="text-sm font-semibold text-slate-700 dark:text-slate-300">You're completely caught up!</p>
          <p className="text-xs text-slate-400">No new notifications to display.</p>
        </div>
      ) : (
        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm overflow-hidden divide-y divide-slate-100 dark:divide-slate-700/60">
          {notifications.map(n => (
            <div
              key={n.id}
              className={`p-4 flex items-start space-x-3 transition ${
                !n.isRead ? 'bg-indigo-50/40 dark:bg-indigo-950/20' : 'hover:bg-slate-50 dark:hover:bg-slate-750'
              }`}
            >
              <div className="p-2 rounded-xl bg-slate-100 dark:bg-slate-700 flex-shrink-0">
                {getIcon(n.notificationType)}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-2">
                  <h3 className={`text-sm ${!n.isRead ? 'font-bold text-slate-900 dark:text-white' : 'font-medium text-slate-800 dark:text-slate-300'}`}>
                    {n.title}
                  </h3>
                  <span className="text-[11px] text-slate-400 flex items-center flex-shrink-0">
                    <FiClock className="mr-1" />
                    {n.createdAt ? new Date(n.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'Recent'}
                  </span>
                </div>
                <p className="text-xs text-slate-600 dark:text-slate-400 mt-1 leading-relaxed">
                  {n.message}
                </p>
              </div>
              {!n.isRead && (
                <span className="w-2 h-2 rounded-full bg-indigo-600 flex-shrink-0 mt-2"></span>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
