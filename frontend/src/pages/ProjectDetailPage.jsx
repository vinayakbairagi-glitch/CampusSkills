import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../utils/api';
import { useAuth } from '../context/AuthContext';
import Avatar from '../components/Avatar';
import {
  FiUsers, FiCalendar, FiCheckSquare, FiPlus, FiArrowLeft,
  FiCheckCircle, FiClock, FiStar, FiAlertCircle, FiAward
} from 'react-icons/fi';

export default function ProjectDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [project, setProject] = useState(null);
  const [members, setMembers] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [msg, setMsg] = useState('');

  // Modals
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [applyRole, setApplyRole] = useState('');
  const [applyMessage, setApplyMessage] = useState('');

  const [showTaskModal, setShowTaskModal] = useState(false);
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDesc, setTaskDesc] = useState('');
  const [taskAssignee, setTaskAssignee] = useState('');
  const [taskDeadline, setTaskDeadline] = useState('');

  const [showRateModal, setShowRateModal] = useState(false);
  const [rateUserId, setRateUserId] = useState(null);
  const [rateScore, setRateScore] = useState(5);
  const [rateComment, setRateComment] = useState('');

  const fetchDetail = async () => {
    try {
      const res = await api.get(`/api/projects/${id}`);
      setProject(res.data.project);
      setMembers(res.data.members || []);
      setTasks(res.data.tasks || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load project');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetail();
  }, [id]);

  const isLeader = project && user && project.leaderId === user.id;
  const myMembership = members.find(m => m.userId === user?.id);
  const isAcceptedMember = myMembership && myMembership.status === 'ACCEPTED';

  const handleApply = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/api/projects/${id}/apply`, {
        roleApplied: applyRole,
        message: applyMessage
      });
      setMsg('Application submitted successfully!');
      setShowApplyModal(false);
      fetchDetail();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to apply');
    }
  };

  const handleActionApplication = async (memberId, action) => {
    try {
      await api.put(`/api/projects/${id}/applications/${memberId}?action=${action}`);
      fetchDetail();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update application');
    }
  };

  const handleCreateTask = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/api/projects/${id}/tasks`, {
        title: taskTitle,
        description: taskDesc,
        assigneeId: taskAssignee ? Number(taskAssignee) : null,
        deadline: taskDeadline || null
      });
      setShowTaskModal(false);
      setTaskTitle('');
      setTaskDesc('');
      setTaskAssignee('');
      fetchDetail();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create task');
    }
  };

  const handleToggleTask = async (taskId) => {
    try {
      await api.put(`/api/projects/tasks/${taskId}`);
      fetchDetail();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to toggle task');
    }
  };

  const handleCompleteProject = async () => {
    if (!window.confirm('Are you sure you want to mark this project as COMPLETED? All accepted members will receive +40 XP!')) return;
    try {
      await api.put(`/api/projects/${id}/complete`);
      setMsg('Project marked as completed! Teammates awarded +40 XP.');
      fetchDetail();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to complete project');
    }
  };

  const handleRate = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/api/projects/${id}/rate`, {
        targetUserId: rateUserId,
        score: rateScore,
        comment: rateComment
      });
      setShowRateModal(false);
      setMsg('Teammate rating submitted!');
      setRateComment('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit rating');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-24 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="max-w-4xl mx-auto p-6 text-center">
        <p className="text-red-500">{error || 'Project not found'}</p>
        <Link to="/projects" className="mt-4 inline-block text-indigo-600 hover:underline">Back to Projects</Link>
      </div>
    );
  }

  const acceptedMembers = members.filter(m => m.status === 'ACCEPTED');
  const pendingMembers = members.filter(m => m.status === 'PENDING');
  const completedTasks = tasks.filter(t => t.completed).length;
  const progressPercent = tasks.length > 0 ? Math.round((completedTasks / tasks.length) * 100) : 0;

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <Link to="/projects" className="inline-flex items-center text-sm text-slate-500 hover:text-indigo-600">
        <FiArrowLeft className="mr-1.5" /> Back to Team Finder
      </Link>

      {error && (
        <div className="p-4 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400 rounded-xl text-sm flex items-center">
          <FiAlertCircle className="mr-2 flex-shrink-0" /> {error}
        </div>
      )}

      {msg && (
        <div className="p-4 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-400 rounded-xl text-sm flex items-center">
          <FiCheckCircle className="mr-2 flex-shrink-0" /> {msg}
        </div>
      )}

      {/* Project Header */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 sm:p-8 shadow-sm border border-slate-200 dark:border-slate-700 space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-3 mb-2">
              <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold ${
                project.status === 'COMPLETED'
                  ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400'
                  : 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400'
              }`}>
                {project.status}
              </span>
              <span className="text-sm text-slate-500 flex items-center">
                <FiUsers className="mr-1" /> {acceptedMembers.length} / {project.teamSizeCap} members
              </span>
              {project.deadline && (
                <span className="text-sm text-slate-500 flex items-center">
                  <FiCalendar className="mr-1" /> Deadline: {project.deadline}
                </span>
              )}
            </div>
            <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 dark:text-white">
              {project.title}
            </h1>
            <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
              Led by <span className="font-medium text-slate-800 dark:text-slate-200">{project.leaderName || 'Leader'}</span>
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            {isLeader && project.status !== 'COMPLETED' && (
              <button
                onClick={handleCompleteProject}
                className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded-xl text-sm transition flex items-center shadow-sm"
              >
                <FiAward className="mr-2" /> Complete Project (+40 XP)
              </button>
            )}

            {!myMembership && project.status === 'OPEN' && acceptedMembers.length < project.teamSizeCap && (
              <button
                onClick={() => setShowApplyModal(true)}
                className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl text-sm transition shadow-sm"
              >
                Apply to Join
              </button>
            )}

            {myMembership && myMembership.status === 'PENDING' && (
              <span className="px-4 py-2 bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300 rounded-xl text-sm font-medium flex items-center">
                <FiClock className="mr-2" /> Application Pending Review
              </span>
            )}
          </div>
        </div>

        <p className="text-slate-700 dark:text-slate-300 leading-relaxed whitespace-pre-wrap">
          {project.description}
        </p>

        {/* Roles & Skills */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t border-slate-100 dark:border-slate-700">
          <div>
            <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Roles Needed</h4>
            <div className="flex flex-wrap gap-2">
              {project.rolesNeeded?.split(',').map((role, idx) => (
                <span key={idx} className="px-3 py-1 bg-slate-100 dark:bg-slate-700 text-slate-700 dark:text-slate-300 rounded-lg text-xs font-medium">
                  {role.trim()}
                </span>
              ))}
            </div>
          </div>
          <div>
            <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Required Skills</h4>
            <div className="flex flex-wrap gap-2">
              {project.requiredSkills?.split(',').map((sk, idx) => (
                <span key={idx} className="px-3 py-1 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-400 rounded-lg text-xs font-medium">
                  {sk.trim()}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Progress Bar */}
        <div>
          <div className="flex justify-between text-sm mb-2">
            <span className="font-semibold text-slate-700 dark:text-slate-300">Task Completion Progress</span>
            <span className="text-indigo-600 dark:text-indigo-400 font-bold">{progressPercent}% ({completedTasks}/{tasks.length})</span>
          </div>
          <div className="w-full bg-slate-100 dark:bg-slate-700 h-3 rounded-full overflow-hidden">
            <div
              className="bg-indigo-600 h-full rounded-full transition-all duration-500"
              style={{ width: `${progressPercent}%` }}
            ></div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Tasks Board */}
        <div className="lg:col-span-2 space-y-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-slate-900 dark:text-white flex items-center">
                <FiCheckSquare className="mr-2 text-indigo-600" /> Project Tasks ({tasks.length})
              </h2>
              {isLeader && (
                <button
                  onClick={() => setShowTaskModal(true)}
                  className="px-3 py-1.5 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 text-xs font-semibold rounded-lg hover:bg-indigo-100 transition flex items-center"
                >
                  <FiPlus className="mr-1" /> Add Task
                </button>
              )}
            </div>

            {tasks.length === 0 ? (
              <p className="text-sm text-slate-500 text-center py-8">No tasks created yet.</p>
            ) : (
              <div className="space-y-3">
                {tasks.map(t => (
                  <div
                    key={t.id}
                    className={`p-4 rounded-xl border transition flex items-start space-x-3 ${
                      t.completed
                        ? 'bg-slate-50 dark:bg-slate-800/40 border-slate-200 dark:border-slate-700 opacity-75'
                        : 'bg-white dark:bg-slate-800 border-slate-200 dark:border-slate-700'
                    }`}
                  >
                    <input
                      type="checkbox"
                      checked={t.completed}
                      onChange={() => handleToggleTask(t.id)}
                      disabled={!isAcceptedMember && !isLeader}
                      className="mt-1 h-4 w-4 text-indigo-600 rounded border-slate-300 focus:ring-indigo-500 cursor-pointer"
                    />
                    <div className="flex-1 min-w-0">
                      <p className={`text-sm font-medium ${t.completed ? 'line-through text-slate-400 dark:text-slate-500' : 'text-slate-800 dark:text-slate-200'}`}>
                        {t.title}
                      </p>
                      {t.description && (
                        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">{t.description}</p>
                      )}
                      <div className="flex items-center space-x-4 mt-2 text-xs text-slate-400">
                        {t.assigneeName && <span>Assigned to: <strong className="text-slate-600 dark:text-slate-300">{t.assigneeName}</strong></span>}
                        {t.deadline && <span>Due: {t.deadline}</span>}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Members & Applications */}
        <div className="space-y-6">
          {/* Accepted Members */}
          <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
            <h3 className="text-base font-bold text-slate-900 dark:text-white mb-4 flex items-center">
              <FiUsers className="mr-2 text-indigo-600" /> Team Members ({acceptedMembers.length})
            </h3>
            <div className="space-y-3">
              {acceptedMembers.map(m => (
                <div key={m.id} className="flex items-center justify-between p-3 rounded-xl bg-slate-50 dark:bg-slate-700/40">
                  <div className="flex items-center space-x-3">
                    <Avatar name={m.userName} src={m.userPhoto} size="sm" />
                    <div>
                      <div className="flex items-center space-x-1.5">
                        <p className="text-sm font-semibold text-slate-800 dark:text-slate-200">{m.userName}</p>
                        {m.userId === project.leaderId && (
                          <span className="text-[10px] bg-indigo-100 text-indigo-700 dark:bg-indigo-900/40 dark:text-indigo-300 px-1.5 py-0.2 rounded font-bold">LEADER</span>
                        )}
                      </div>
                      <p className="text-xs text-slate-500">{m.roleApplied || 'Teammate'}</p>
                    </div>
                  </div>
                  {project.status === 'COMPLETED' && m.userId !== user?.id && (
                    <button
                      onClick={() => {
                        setRateUserId(m.userId);
                        setShowRateModal(true);
                      }}
                      className="p-1.5 text-amber-600 hover:text-amber-700 bg-amber-50 dark:bg-amber-900/30 rounded-lg text-xs font-semibold flex items-center"
                      title="Rate teammate"
                    >
                      <FiStar className="mr-1" /> Rate
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Pending Applications (Leader only) */}
          {isLeader && pendingMembers.length > 0 && (
            <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-amber-200 dark:border-amber-800/40">
              <h3 className="text-base font-bold text-slate-900 dark:text-white mb-4 flex items-center">
                <FiClock className="mr-2 text-amber-500" /> Applications ({pendingMembers.length})
              </h3>
              <div className="space-y-3">
                {pendingMembers.map(m => (
                  <div key={m.id} className="p-3 rounded-xl bg-amber-50/50 dark:bg-amber-900/10 border border-amber-100 dark:border-amber-800/30 space-y-2">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <Avatar name={m.userName} src={m.userPhoto} size="xs" />
                        <div>
                          <p className="text-sm font-semibold text-slate-800 dark:text-slate-200">{m.userName}</p>
                          <p className="text-xs text-slate-500">Applied for: {m.roleApplied}</p>
                        </div>
                      </div>
                    </div>
                    {m.message && (
                      <p className="text-xs text-slate-600 dark:text-slate-300 italic">"{m.message}"</p>
                    )}
                    <div className="flex space-x-2 pt-1">
                      <button
                        onClick={() => handleActionApplication(m.id, 'ACCEPT')}
                        className="flex-1 py-1 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded-lg"
                      >
                        Accept
                      </button>
                      <button
                        onClick={() => handleActionApplication(m.id, 'REJECT')}
                        className="flex-1 py-1 bg-slate-200 hover:bg-slate-300 dark:bg-slate-700 text-slate-700 dark:text-slate-300 text-xs font-semibold rounded-lg"
                      >
                        Decline
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Apply Modal */}
      {showApplyModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl max-w-md w-full p-6 shadow-xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">Apply for Project</h3>
            <form onSubmit={handleApply} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Role you wish to take</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Frontend Developer, UI Designer"
                  value={applyRole}
                  onChange={e => setApplyRole(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Why are you a good fit?</label>
                <textarea
                  rows="3"
                  placeholder="Share your experience, skills, and availability..."
                  value={applyMessage}
                  onChange={e => setApplyMessage(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowApplyModal(false)}
                  className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-xl"
                >
                  Submit Application
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Task Modal */}
      {showTaskModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl max-w-md w-full p-6 shadow-xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">Create New Task</h3>
            <form onSubmit={handleCreateTask} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Task Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Design DB Schema"
                  value={taskTitle}
                  onChange={e => setTaskTitle(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Description (Optional)</label>
                <textarea
                  rows="2"
                  value={taskDesc}
                  onChange={e => setTaskDesc(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Assignee</label>
                  <select
                    value={taskAssignee}
                    onChange={e => setTaskAssignee(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm"
                  >
                    <option value="">Unassigned</option>
                    {acceptedMembers.map(m => (
                      <option key={m.userId} value={m.userId}>{m.userName}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Deadline</label>
                  <input
                    type="date"
                    value={taskDeadline}
                    onChange={e => setTaskDeadline(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm"
                  />
                </div>
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowTaskModal(false)}
                  className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-xl"
                >
                  Add Task
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Rating Modal */}
      {showRateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl max-w-md w-full p-6 shadow-xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white flex items-center">
              <FiStar className="mr-2 text-amber-500" /> Rate Teammate
            </h3>
            <form onSubmit={handleRate} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-2">Rating</label>
                <div className="flex space-x-2">
                  {[1, 2, 3, 4, 5].map(num => (
                    <button
                      key={num}
                      type="button"
                      onClick={() => setRateScore(num)}
                      className={`p-2 rounded-xl text-xl flex items-center justify-center transition ${
                        rateScore >= num ? 'text-amber-500 bg-amber-50 dark:bg-amber-900/30' : 'text-slate-300 dark:text-slate-600'
                      }`}
                    >
                      ★
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Feedback Comment</label>
                <textarea
                  rows="3"
                  required
                  placeholder="How was working with this peer on the project?"
                  value={rateComment}
                  onChange={e => setRateComment(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowRateModal(false)}
                  className="px-4 py-2 text-sm text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-xl"
                >
                  Submit Rating
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
