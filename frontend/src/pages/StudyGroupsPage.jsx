import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../utils/api';
import Toast from '../components/Toast';
import {
  Users,
  Search,
  Plus,
  Filter,
  Lock,
  Globe,
  Clock,
  Sparkles,
  BookOpen,
  CheckCircle2,
  X,
  ArrowRight,
} from 'lucide-react';

export default function StudyGroupsPage() {
  const { user } = useAuth();
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState('');
  const [mode, setMode] = useState('');
  const [myGroupsOnly, setMyGroupsOnly] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [toast, setToast] = useState(null);

  const [newGroup, setNewGroup] = useState({
    name: '',
    subject: '',
    category: 'PLACEMENT',
    branch: 'Computer Science',
    studyYear: 3,
    timing: 'Daily 7:00 PM',
    mode: 'ONLINE',
    memberLimit: 30,
    visibility: 'PUBLIC',
    description: '',
  });

  const loadGroups = async () => {
    setLoading(true);
    try {
      let url = `/groups?query=${encodeURIComponent(query)}`;
      if (category) url += `&category=${category}`;
      if (mode) url += `&mode=${mode}`;
      if (myGroupsOnly) url += `&myGroups=true`;

      const data = await apiRequest(url);
      setGroups(data || []);
    } catch (err) {
      console.error('Error fetching groups:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGroups();
  }, [category, mode, myGroupsOnly]);

  const handleSearch = (e) => {
    e.preventDefault();
    loadGroups();
  };

  const handleCreateGroup = async (e) => {
    e.preventDefault();
    try {
      await apiRequest('/groups', {
        method: 'POST',
        body: JSON.stringify(newGroup),
      });
      setToast({ message: 'Study group created successfully!', type: 'success' });
      setShowCreateModal(false);
      loadGroups();
    } catch (err) {
      setToast({ message: err.message || 'Failed to create group', type: 'error' });
    }
  };

  const handleJoinGroup = async (groupId) => {
    try {
      const member = await apiRequest(`/groups/${groupId}/join`, { method: 'POST' });
      if (member.status === 'ACTIVE') {
        setToast({ message: 'Joined group successfully!', type: 'success' });
      } else {
        setToast({ message: 'Join request submitted for admin approval.', type: 'info' });
      }
      loadGroups();
    } catch (err) {
      setToast({ message: err.message || 'Failed to join group', type: 'error' });
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200 dark:border-slate-800">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <Users className="w-8 h-8 text-indigo-600" />
            Study Groups
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Collaborate daily with batchmates, attend scheduled review sessions, and share learning resources.
          </p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="inline-flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-md shadow-brand-500/20 transition-all self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          Create Study Group
        </button>
      </div>

      {/* Search & Filter Bar */}
      <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-xs">
        <form onSubmit={handleSearch} className="flex-1 relative">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search groups by name, subject, or description..."
            className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
          />
        </form>

        <div className="flex items-center gap-2 flex-wrap">
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs font-semibold"
          >
            <option value="">All Categories</option>
            <option value="PLACEMENT">Placement Prep</option>
            <option value="SUBJECT">Subject Exam</option>
            <option value="SKILL">Tech Skill</option>
            <option value="EXAM">Competitive Exam</option>
          </select>

          <select
            value={mode}
            onChange={(e) => setMode(e.target.value)}
            className="px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs font-semibold"
          >
            <option value="">All Modes</option>
            <option value="ONLINE">Online</option>
            <option value="OFFLINE">Offline</option>
            <option value="HYBRID">Hybrid</option>
          </select>

          <label className="flex items-center gap-2 px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs font-semibold cursor-pointer select-none">
            <input
              type="checkbox"
              checked={myGroupsOnly}
              onChange={(e) => setMyGroupsOnly(e.target.checked)}
              className="rounded text-brand-600 focus:ring-brand-500"
            />
            My Groups
          </label>
        </div>
      </div>

      {/* Groups Grid */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">Loading study groups...</div>
      ) : groups.length === 0 ? (
        <div className="p-12 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-400 text-xs">
          No groups matching your search criteria.
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {groups.map((g) => (
            <div
              key={g.id}
              className="rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 p-6 flex flex-col justify-between shadow-xs hover:border-brand-500/40 transition-all hover:shadow-md"
            >
              <div>
                {/* Badges */}
                <div className="flex items-center justify-between gap-2 mb-3">
                  <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-50 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                    {g.category}
                  </span>
                  <div className="flex items-center gap-1.5 text-xs text-slate-400 font-medium">
                    {g.visibility === 'PRIVATE' ? (
                      <span className="flex items-center gap-1 text-amber-600 dark:text-amber-400 text-[10px] font-bold">
                        <Lock className="w-3 h-3" /> Private
                      </span>
                    ) : (
                      <span className="flex items-center gap-1 text-emerald-600 dark:text-emerald-400 text-[10px] font-bold">
                        <Globe className="w-3 h-3" /> Public
                      </span>
                    )}
                  </div>
                </div>

                <h3 className="font-bold text-slate-900 dark:text-white text-base mb-1 hover:text-brand-600 transition-colors">
                  <Link to={`/groups/${g.id}`}>{g.name}</Link>
                </h3>

                <p className="text-xs text-slate-500 dark:text-slate-400 line-clamp-2 leading-relaxed mb-4">
                  {g.description || 'Collaborative study room for peer review & exercises.'}
                </p>

                <div className="space-y-1.5 text-xs bg-slate-50 dark:bg-slate-800/40 p-3 rounded-2xl mb-4">
                  <div className="flex justify-between text-slate-600 dark:text-slate-300">
                    <span className="text-slate-400">Subject:</span>
                    <span className="font-semibold">{g.subject}</span>
                  </div>
                  {g.timing && (
                    <div className="flex justify-between text-slate-600 dark:text-slate-300">
                      <span className="text-slate-400">Schedule:</span>
                      <span className="font-semibold flex items-center gap-1">
                        <Clock className="w-3 h-3" /> {g.timing}
                      </span>
                    </div>
                  )}
                  <div className="flex justify-between text-slate-600 dark:text-slate-300">
                    <span className="text-slate-400">Capacity:</span>
                    <span className="font-semibold">
                      {g.memberCount} / {g.memberLimit} Members
                    </span>
                  </div>
                </div>
              </div>

              {/* Action */}
              <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">{g.mode}</span>

                {g.isMember ? (
                  <Link
                    to={`/groups/${g.id}`}
                    className="px-3.5 py-1.5 rounded-xl bg-brand-50 hover:bg-brand-100 dark:bg-brand-950/50 dark:hover:bg-brand-900/50 text-brand-700 dark:text-brand-300 font-bold text-xs flex items-center gap-1"
                  >
                    Enter Group
                    <ArrowRight className="w-3.5 h-3.5" />
                  </Link>
                ) : g.isPending ? (
                  <span className="px-3 py-1.5 rounded-xl bg-amber-50 dark:bg-amber-950 text-amber-700 dark:text-amber-300 font-bold text-xs">
                    Pending Approval
                  </span>
                ) : (
                  <button
                    onClick={() => handleJoinGroup(g.id)}
                    className="px-3.5 py-1.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-xs"
                  >
                    {g.visibility === 'PRIVATE' ? 'Request to Join' : 'Join Instantly'}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* MODAL: CREATE STUDY GROUP */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-lg w-full border border-slate-200 dark:border-slate-800 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">Create New Study Group</h3>
              <button onClick={() => setShowCreateModal(false)} className="text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateGroup} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold text-slate-600 dark:text-slate-400 mb-1">Group Name</label>
                <input
                  type="text"
                  required
                  value={newGroup.name}
                  onChange={(e) => setNewGroup({ ...newGroup, name: e.target.value })}
                  placeholder="e.g. DSA Placement Prep – Daily 7 PM"
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-600 dark:text-slate-400 mb-1">Subject</label>
                  <input
                    type="text"
                    required
                    value={newGroup.subject}
                    onChange={(e) => setNewGroup({ ...newGroup, subject: e.target.value })}
                    placeholder="e.g. Java, DSA, Web Dev"
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                  />
                </div>
                <div>
                  <label className="block font-bold text-slate-600 dark:text-slate-400 mb-1">Category</label>
                  <select
                    value={newGroup.category}
                    onChange={(e) => setNewGroup({ ...newGroup, category: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                  >
                    <option value="PLACEMENT">Placement Prep</option>
                    <option value="SUBJECT">Subject Exam</option>
                    <option value="SKILL">Tech Skill</option>
                    <option value="EXAM">Competitive Exam</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block font-bold text-slate-600 dark:text-slate-400 mb-1">Timing</label>
                  <input
                    type="text"
                    value={newGroup.timing}
                    onChange={(e) => setNewGroup({ ...newGroup, timing: e.target.value })}
                    placeholder="e.g. Daily 7 PM"
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                  />
                </div>
                <div>
                  <label className="block font-bold text-slate-600 dark:text-slate-400 mb-1">Mode</label>
                  <select
                    value={newGroup.mode}
                    onChange={(e) => setNewGroup({ ...newGroup, mode: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                  >
                    <option value="ONLINE">Online</option>
                    <option value="OFFLINE">Offline</option>
                    <option value="HYBRID">Hybrid</option>
                  </select>
                </div>
                <div>
                  <label className="block font-bold text-slate-600 dark:text-slate-400 mb-1">Visibility</label>
                  <select
                    value={newGroup.visibility}
                    onChange={(e) => setNewGroup({ ...newGroup, visibility: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                  >
                    <option value="PUBLIC">Public</option>
                    <option value="PRIVATE">Private</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-600 dark:text-slate-400 mb-1">Description</label>
                <textarea
                  rows={3}
                  value={newGroup.description}
                  onChange={(e) => setNewGroup({ ...newGroup, description: e.target.value })}
                  placeholder="Outline study goals, materials, syllabus..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                />
              </div>

              <div className="flex justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 rounded-xl bg-slate-100 dark:bg-slate-800 font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold shadow-xs"
                >
                  Create Group
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
