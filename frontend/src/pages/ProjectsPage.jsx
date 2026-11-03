import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../utils/api';
import Avatar from '../components/Avatar';
import Toast from '../components/Toast';
import {
  FolderGit2,
  Plus,
  Users,
  Calendar,
  CheckCircle2,
  ArrowRight,
  Sparkles,
  X,
  Clock,
} from 'lucide-react';

export default function ProjectsPage() {
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [toast, setToast] = useState(null);

  const [form, setForm] = useState({
    title: '',
    description: '',
    requiredSkills: 'React, Spring Boot, Java',
    rolesNeeded: 'Frontend Dev, Backend Dev, UI Designer',
    teamSizeCap: 4,
    deadline: '',
  });

  const loadProjects = async () => {
    setLoading(true);
    try {
      const data = await apiRequest('/projects');
      setProjects(data || []);
    } catch (err) {
      console.error('Failed to load projects:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  const handleCreateProject = async (e) => {
    e.preventDefault();
    try {
      await apiRequest('/projects', {
        method: 'POST',
        body: JSON.stringify(form),
      });
      setToast({ message: 'Project idea posted!', type: 'success' });
      setShowCreateModal(false);
      loadProjects();
    } catch (err) {
      setToast({ message: err.message || 'Failed to create project', type: 'error' });
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200 dark:border-slate-800">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white flex items-center gap-2">
            <FolderGit2 className="w-8 h-8 text-rose-500" />
            Project Team Finder
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Build hackathon or capstone projects together. Assign tasks, track progress bars, and earn +40 XP!
          </p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="inline-flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-md shadow-brand-500/20 transition-all self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          Post Project Idea
        </button>
      </div>

      {/* Project Cards Grid */}
      {loading ? (
        <div className="py-16 text-center text-slate-400">Loading projects...</div>
      ) : projects.length === 0 ? (
        <div className="p-12 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-400 text-xs">
          No projects posted yet. Be the first to start a project team!
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {projects.map((p) => (
            <div
              key={p.id}
              className="rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 p-6 flex flex-col justify-between shadow-xs hover:border-brand-500/40 transition-all hover:shadow-md"
            >
              <div>
                <div className="flex items-center justify-between gap-2 mb-3">
                  <span
                    className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                      p.status === 'COMPLETED'
                        ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                        : p.status === 'IN_PROGRESS'
                        ? 'bg-indigo-100 text-indigo-800 dark:bg-indigo-950 dark:text-indigo-300'
                        : 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300'
                    }`}
                  >
                    {p.status}
                  </span>

                  <span className="text-xs text-slate-400 font-semibold flex items-center gap-1">
                    <Users className="w-3.5 h-3.5" />
                    {p.acceptedMembersCount || 1} / {p.teamSizeCap}
                  </span>
                </div>

                <h3 className="font-bold text-slate-900 dark:text-white text-base mb-1 hover:text-brand-600 transition-colors">
                  <Link to={`/projects/${p.id}`}>{p.title}</Link>
                </h3>

                <p className="text-xs text-slate-500 dark:text-slate-400 line-clamp-2 leading-relaxed mb-4">
                  {p.description}
                </p>

                {/* Progress Bar */}
                <div className="mb-4">
                  <div className="flex justify-between text-xs font-bold mb-1">
                    <span className="text-slate-400">Task Completion</span>
                    <span className="text-brand-600 dark:text-brand-400">{p.progressPercent || 0}%</span>
                  </div>
                  <div className="w-full bg-slate-100 dark:bg-slate-800 h-2 rounded-full overflow-hidden">
                    <div
                      className="bg-brand-500 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${p.progressPercent || 0}%` }}
                    ></div>
                  </div>
                </div>

                <div className="space-y-1 text-xs bg-slate-50 dark:bg-slate-800/40 p-3 rounded-2xl mb-4">
                  <p className="text-slate-600 dark:text-slate-300">
                    <strong className="text-slate-400">Roles:</strong> {p.rolesNeeded}
                  </p>
                  <p className="text-slate-600 dark:text-slate-300">
                    <strong className="text-slate-400">Skills:</strong> {p.requiredSkills}
                  </p>
                </div>
              </div>

              {/* Action */}
              <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Avatar src={p.leaderPhoto} name={p.leaderName} size="sm" />
                  <span className="text-xs text-slate-500 truncate max-w-[100px]">{p.leaderName}</span>
                </div>

                <Link
                  to={`/projects/${p.id}`}
                  className="px-3 py-1.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs flex items-center gap-1 shadow-xs"
                >
                  View Details
                  <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* MODAL: POST PROJECT */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-lg w-full border border-slate-200 dark:border-slate-800 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">Post Project Idea</h3>
              <button onClick={() => setShowCreateModal(false)} className="text-slate-400"><X className="w-5 h-5" /></button>
            </div>

            <form onSubmit={handleCreateProject} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold mb-1">Project Title</label>
                <input
                  type="text"
                  required
                  value={form.title}
                  onChange={(e) => setForm({ ...form, title: e.target.value })}
                  placeholder="e.g. AI Study Buddy & Note Summarizer"
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Required Skills</label>
                  <input
                    type="text"
                    value={form.requiredSkills}
                    onChange={(e) => setForm({ ...form, requiredSkills: e.target.value })}
                    placeholder="e.g. React, Python, FastAPI"
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">Roles Needed</label>
                  <input
                    type="text"
                    value={form.rolesNeeded}
                    onChange={(e) => setForm({ ...form, rolesNeeded: e.target.value })}
                    placeholder="e.g. Frontend Lead, ML Engineer"
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Team Size Limit</label>
                  <input
                    type="number"
                    min={2}
                    max={10}
                    value={form.teamSizeCap}
                    onChange={(e) => setForm({ ...form, teamSizeCap: parseInt(e.target.value) })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">Deadline Date</label>
                  <input
                    type="date"
                    value={form.deadline}
                    onChange={(e) => setForm({ ...form, deadline: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold mb-1">Description & Goal</label>
                <textarea
                  rows={3}
                  required
                  value={form.description}
                  onChange={(e) => setForm({ ...form, description: e.target.value })}
                  placeholder="Explain the project scope, architecture, and expected output..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setShowCreateModal(false)} className="px-4 py-2 rounded-xl bg-slate-100 font-bold">Cancel</button>
                <button type="submit" className="px-4 py-2 rounded-xl bg-brand-600 text-white font-bold">Post Idea</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
