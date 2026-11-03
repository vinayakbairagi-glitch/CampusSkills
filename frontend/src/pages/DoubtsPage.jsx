import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../utils/api';
import Avatar from '../components/Avatar';
import Toast from '../components/Toast';
import {
  HelpCircle,
  Search,
  Plus,
  CheckCircle2,
  Clock,
  ThumbsUp,
  MessageSquare,
  ShieldCheck,
  X,
  ArrowRight,
  Sparkles,
} from 'lucide-react';

export default function DoubtsPage() {
  const { user } = useAuth();
  const [doubts, setDoubts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [subjectFilter, setSubjectFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [showAskModal, setShowAskModal] = useState(false);
  const [toast, setToast] = useState(null);

  // Ask Form with Live Duplicate Search
  const [newDoubt, setNewDoubt] = useState({
    subject: 'Java',
    topic: 'Collections',
    title: '',
    description: '',
  });
  const [duplicateMatches, setDuplicateMatches] = useState([]);

  const loadDoubts = async () => {
    setLoading(true);
    try {
      let url = `/doubts?query=${encodeURIComponent(searchQuery)}`;
      if (subjectFilter) url += `&subject=${subjectFilter}`;
      if (statusFilter) url += `&status=${statusFilter}`;

      const data = await apiRequest(url);
      setDoubts(data || []);
    } catch (err) {
      console.error('Error fetching doubts:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDoubts();
  }, [subjectFilter, statusFilter]);

  // Live search duplicates when typing in Ask modal
  useEffect(() => {
    if (!showAskModal || newDoubt.title.trim().length < 3) {
      setDuplicateMatches([]);
      return;
    }
    const timer = setTimeout(async () => {
      try {
        const matches = await apiRequest(`/doubts?query=${encodeURIComponent(newDoubt.title.trim())}`);
        setDuplicateMatches(matches.slice(0, 3));
      } catch (e) {}
    }, 300);
    return () => clearTimeout(timer);
  }, [newDoubt.title, showAskModal]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    loadDoubts();
  };

  const handlePostDoubt = async (e) => {
    e.preventDefault();
    try {
      await apiRequest('/doubts', {
        method: 'POST',
        body: JSON.stringify(newDoubt),
      });
      setToast({ message: 'Doubt posted! The campus community has been alerted.', type: 'success' });
      setShowAskModal(false);
      setNewDoubt({ subject: 'Java', topic: 'Collections', title: '', description: '' });
      loadDoubts();
    } catch (err) {
      setToast({ message: err.message || 'Failed to post doubt', type: 'error' });
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200 dark:border-slate-800">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <HelpCircle className="w-8 h-8 text-amber-500" />
            Doubt-Solving Board
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Get answers from peers & verified seniors. Best answers earn +1 SkillCredit and +25 XP!
          </p>
        </div>

        <button
          onClick={() => setShowAskModal(true)}
          className="inline-flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-md shadow-brand-500/20 transition-all self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          Ask a Doubt
        </button>
      </div>

      {/* Search & Filters */}
      <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-xs">
        <form onSubmit={handleSearchSubmit} className="flex-1 relative">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search doubts by title, keyword, or subject..."
            className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
          />
        </form>

        <div className="flex items-center gap-2">
          <select
            value={subjectFilter}
            onChange={(e) => setSubjectFilter(e.target.value)}
            className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs font-semibold"
          >
            <option value="">All Subjects</option>
            <option value="Java">Java</option>
            <option value="DSA">DSA</option>
            <option value="Spring Boot">Spring Boot</option>
            <option value="React">React</option>
            <option value="Python">Python</option>
            <option value="SQL">SQL</option>
            <option value="Aptitude">Aptitude</option>
          </select>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs font-semibold"
          >
            <option value="">All Status</option>
            <option value="OPEN">Unsolved (OPEN)</option>
            <option value="SOLVED">Solved</option>
          </select>
        </div>
      </div>

      {/* Doubts List */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">Loading doubts board...</div>
      ) : doubts.length === 0 ? (
        <div className="p-12 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-400 text-xs">
          No doubts found matching your query.
        </div>
      ) : (
        <div className="space-y-4">
          {doubts.map((d) => (
            <Link
              key={d.id}
              to={`/doubts/${d.id}`}
              className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 shadow-xs hover:border-brand-500/40 transition-all hover:shadow-md group block"
            >
              <div className="flex items-start gap-4 flex-1">
                <Avatar src={d.askerPhoto} name={d.askerName} size="md" />
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <span className="px-2 py-0.5 rounded-md text-[10px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
                      {d.subject} • {d.topic}
                    </span>
                    {d.status === 'SOLVED' ? (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 flex items-center gap-1">
                        <CheckCircle2 className="w-3 h-3" /> SOLVED
                      </span>
                    ) : (
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300">
                        OPEN
                      </span>
                    )}
                  </div>

                  <h3 className="font-bold text-slate-900 dark:text-white text-base group-hover:text-brand-600 dark:group-hover:text-brand-400 transition-colors">
                    {d.title}
                  </h3>

                  <p className="text-xs text-slate-500 dark:text-slate-400 line-clamp-1 mt-1">
                    {d.description}
                  </p>

                  <div className="flex items-center gap-3 text-[11px] text-slate-400 mt-2 font-medium">
                    <span>Asked by {d.askerName} ({d.askerBranch || 'CSE'}, Year {d.askerYear || 2})</span>
                    <span>•</span>
                    <span>{d.createdAt ? new Date(d.createdAt).toLocaleDateString() : 'Recent'}</span>
                  </div>
                </div>
              </div>

              {/* Answers pill */}
              <div className="flex items-center gap-2 px-3.5 py-2 rounded-2xl bg-slate-50 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border border-slate-100 dark:border-slate-700 shrink-0 text-xs font-bold">
                <MessageSquare className="w-4 h-4 text-brand-600" />
                <span>{d.answersCount || 0} Answers</span>
                <ArrowRight className="w-3.5 h-3.5 text-slate-400 group-hover:translate-x-0.5 transition-transform" />
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* MODAL: ASK A DOUBT */}
      {showAskModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-lg w-full border border-slate-200 dark:border-slate-800 p-6 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">Ask a Doubt</h3>
              <button onClick={() => setShowAskModal(false)} className="text-slate-400"><X className="w-5 h-5" /></button>
            </div>

            <form onSubmit={handlePostDoubt} className="space-y-3 text-xs">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Subject</label>
                  <input
                    type="text"
                    required
                    value={newDoubt.subject}
                    onChange={(e) => setNewDoubt({ ...newDoubt, subject: e.target.value })}
                    placeholder="e.g. Java, DSA, Web Dev"
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">Topic</label>
                  <input
                    type="text"
                    required
                    value={newDoubt.topic}
                    onChange={(e) => setNewDoubt({ ...newDoubt, topic: e.target.value })}
                    placeholder="e.g. Collections, DP, Hooks"
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold mb-1">Doubt Title / Question</label>
                <input
                  type="text"
                  required
                  value={newDoubt.title}
                  onChange={(e) => setNewDoubt({ ...newDoubt, title: e.target.value })}
                  placeholder="e.g. Difference between HashMap and ConcurrentHashMap?"
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs font-semibold"
                />
              </div>

              {/* Live Duplicate Search Box on Ask Form */}
              {duplicateMatches.length > 0 && (
                <div className="p-3 rounded-2xl bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-800 space-y-1.5 animate-in fade-in">
                  <p className="font-bold text-amber-800 dark:text-amber-300 text-[11px] flex items-center gap-1">
                    <Sparkles className="w-3.5 h-3.5" /> Similar existing doubts found:
                  </p>
                  {duplicateMatches.map((dm) => (
                    <Link
                      key={dm.id}
                      to={`/doubts/${dm.id}`}
                      className="block p-1.5 rounded-lg hover:bg-amber-100 dark:hover:bg-amber-900/40 text-amber-900 dark:text-amber-200 font-medium text-[11px] truncate"
                    >
                      • {dm.title} {dm.status === 'SOLVED' && '✅'}
                    </Link>
                  ))}
                </div>
              )}

              <div>
                <label className="block font-bold mb-1">Detailed Description & Code</label>
                <textarea
                  rows={4}
                  required
                  value={newDoubt.description}
                  onChange={(e) => setNewDoubt({ ...newDoubt, description: e.target.value })}
                  placeholder="Provide context, compiler errors, or what you've already attempted..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setShowAskModal(false)} className="px-4 py-2 rounded-xl bg-slate-100 font-bold">
                  Cancel
                </button>
                <button type="submit" className="px-4 py-2 rounded-xl bg-brand-600 text-white font-bold shadow-xs">
                  Post Doubt
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
