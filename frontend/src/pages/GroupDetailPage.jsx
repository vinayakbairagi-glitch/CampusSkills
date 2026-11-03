import React, { useEffect, useState, useRef } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useWebSocket } from '../context/WebSocketContext';
import { apiRequest, uploadFile } from '../utils/api';
import Avatar from '../components/Avatar';
import Toast from '../components/Toast';
import {
  Users,
  MessageSquare,
  Calendar,
  FileText,
  Trophy,
  Send,
  Video,
  Upload,
  Link2,
  Check,
  X,
  Plus,
  ShieldCheck,
  Clock,
  CheckCircle2,
  ArrowLeft,
  ChevronRight,
  ExternalLink,
} from 'lucide-react';

export default function GroupDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const { stompClient } = useWebSocket();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('chat'); // chat, sessions, resources, members, leaderboard
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [toast, setToast] = useState(null);

  // Modals state
  const [showSessionModal, setShowSessionModal] = useState(false);
  const [showResourceModal, setShowResourceModal] = useState(false);
  const [showAttendanceModal, setShowAttendanceModal] = useState(null); // target session
  const [selectedAttendees, setSelectedAttendees] = useState([]);

  const [sessionForm, setSessionForm] = useState({
    title: '',
    description: '',
    durationMinutes: 60,
    mode: 'ONLINE',
    meetingLink: '',
    location: '',
    recurrenceWeeks: 0,
  });

  const [resourceForm, setResourceForm] = useState({
    title: '',
    resourceType: 'LINK',
    urlOrPath: '',
  });
  const [uploading, setUploading] = useState(false);

  const messagesEndRef = useRef(null);

  const loadGroup = async () => {
    try {
      const groupData = await apiRequest(`/groups/${id}`);
      setData(groupData);
      const msgs = await apiRequest(`/groups/${id}/messages`);
      setMessages(msgs || []);
    } catch (err) {
      console.error('Error loading group:', err);
      setToast({ message: err.message || 'Error loading group', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGroup();
  }, [id]);

  // STOMP WebSocket Live Chat subscription
  useEffect(() => {
    if (!stompClient || !stompClient.connected) return;

    const sub = stompClient.subscribe(`/topic/groups/${id}`, (msg) => {
      try {
        const received = JSON.parse(msg.body);
        setMessages((prev) => [...prev, received]);
      } catch (e) {
        console.error('Error parsing live message:', e);
      }
    });

    return () => {
      sub.unsubscribe();
    };
  }, [stompClient, id]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, activeTab]);

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim()) return;

    try {
      const content = newMessage.trim();
      setNewMessage('');

      // Send via STOMP if connected, or REST fallback
      if (stompClient && stompClient.connected) {
        stompClient.publish({
          destination: `/app/groups/${id}/chat`,
          body: JSON.stringify({ content }),
        });
      } else {
        const saved = await apiRequest(`/groups/${id}/messages`, {
          method: 'POST',
          body: JSON.stringify({ content }),
        });
        setMessages((prev) => [...prev, saved]);
      }
    } catch (err) {
      setToast({ message: 'Failed to send message', type: 'error' });
    }
  };

  const handleScheduleSession = async (e) => {
    e.preventDefault();
    try {
      await apiRequest(`/groups/${id}/sessions`, {
        method: 'POST',
        body: JSON.stringify(sessionForm),
      });
      setToast({ message: 'Study session scheduled!', type: 'success' });
      setShowSessionModal(false);
      loadGroup();
    } catch (err) {
      setToast({ message: err.message || 'Failed to schedule', type: 'error' });
    }
  };

  const handleCheckIn = async (sessionId) => {
    try {
      await apiRequest(`/group-sessions/${sessionId}/checkin`, { method: 'POST' });
      setToast({ message: 'Checked in! +10 XP awarded.', type: 'success' });
      loadGroup();
    } catch (err) {
      setToast({ message: err.message || 'Check-in failed', type: 'error' });
    }
  };

  const handleSaveAttendanceSheet = async (sessionId) => {
    try {
      await apiRequest(`/group-sessions/${sessionId}/attendance`, {
        method: 'PUT',
        body: JSON.stringify(selectedAttendees),
      });
      setToast({ message: 'Attendance recorded successfully!', type: 'success' });
      setShowAttendanceModal(null);
      loadGroup();
    } catch (err) {
      setToast({ message: err.message || 'Error recording attendance', type: 'error' });
    }
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setUploading(true);
    try {
      const url = await uploadFile(file);
      const resourceType = file.name.toLowerCase().endsWith('.pdf') ? 'PDF' : 'DOCUMENT';
      setResourceForm((prev) => ({ ...prev, urlOrPath: url, resourceType }));
      setToast({ message: 'File uploaded successfully!', type: 'success' });
    } catch (err) {
      setToast({ message: err.message || 'Upload failed', type: 'error' });
    } finally {
      setUploading(false);
    }
  };

  const handleAddResource = async (e) => {
    e.preventDefault();
    try {
      await apiRequest(`/groups/${id}/resources`, {
        method: 'POST',
        body: JSON.stringify(resourceForm),
      });
      setToast({ message: 'Resource shared in group!', type: 'success' });
      setShowResourceModal(false);
      setResourceForm({ title: '', resourceType: 'LINK', urlOrPath: '' });
      loadGroup();
    } catch (err) {
      setToast({ message: err.message || 'Failed to add resource', type: 'error' });
    }
  };

  const handleManageMember = async (targetUserId, action) => {
    try {
      await apiRequest(`/groups/${id}/members/${targetUserId}?action=${action}`, { method: 'PUT' });
      setToast({ message: `Member ${action.toLowerCase()}ed`, type: 'info' });
      loadGroup();
    } catch (err) {
      setToast({ message: err.message || 'Action failed', type: 'error' });
    }
  };

  if (loading || !data) {
    return (
      <div className="p-8 flex items-center justify-center min-h-[60vh]">
        <div className="w-8 h-8 rounded-full border-2 border-brand-500 border-t-transparent animate-spin"></div>
      </div>
    );
  }

  const { group, memberCount, isMember, isAdmin, members, sessions, resources, attendanceRate, quizAverage } = data;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}

      {/* Back button & Banner */}
      <div>
        <Link to="/groups" className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-slate-800 dark:hover:text-slate-200 mb-3">
          <ArrowLeft className="w-4 h-4" /> Back to Study Groups
        </Link>

        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-50 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
                {group.category}
              </span>
              <span className="text-xs text-slate-400 font-semibold">• {group.mode}</span>
              {isAdmin && (
                <span className="px-2 py-0.5 rounded-md bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 text-[10px] font-bold">
                  Group Admin
                </span>
              )}
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
              {group.name}
            </h1>
            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1 max-w-2xl">
              {group.description}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <div className="bg-slate-50 dark:bg-slate-800 p-3 rounded-2xl text-center border border-slate-100 dark:border-slate-700">
              <span className="text-lg font-bold text-slate-900 dark:text-white">{memberCount}</span>
              <span className="block text-[10px] text-slate-400 uppercase font-bold">Members</span>
            </div>

            {isAdmin && (
              <button
                onClick={() => setShowSessionModal(true)}
                className="px-4 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-xs"
              >
                + Schedule Session
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 dark:border-slate-800 pb-2 overflow-x-auto">
        {[
          { id: 'chat', label: 'Live Chat', icon: MessageSquare },
          { id: 'sessions', label: 'Sessions & Attendance', icon: Calendar },
          { id: 'resources', label: 'Resources Vault', icon: FileText },
          { id: 'members', label: 'Members', icon: Users },
          { id: 'leaderboard', label: 'Progress & Leaderboard', icon: Trophy },
        ].map((tab) => {
          const Icon = tab.icon;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition-all whitespace-nowrap ${
                activeTab === tab.id
                  ? 'bg-brand-600 text-white shadow-xs'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
              }`}
            >
              <Icon className="w-4 h-4" />
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* TAB 1: LIVE CHAT OVER STOMP */}
      {activeTab === 'chat' && (
        <div className="rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex flex-col h-[550px] overflow-hidden">
          {/* Messages list */}
          <div className="flex-1 p-4 sm:p-6 overflow-y-auto space-y-4">
            {messages.length === 0 ? (
              <div className="h-full flex items-center justify-center text-center text-slate-400 text-xs">
                <div>
                  <MessageSquare className="w-8 h-8 mx-auto mb-2 opacity-50" />
                  <p>Welcome to the group chat! Start the conversation.</p>
                </div>
              </div>
            ) : (
              messages.map((m) => {
                const isMe = m.senderId === user?.id;
                return (
                  <div key={m.id} className={`flex items-start gap-2.5 ${isMe ? 'flex-row-reverse' : ''}`}>
                    <Avatar src={m.senderPhoto} name={m.senderName} size="sm" />
                    <div className={`max-w-md ${isMe ? 'text-right' : ''}`}>
                      <div className="flex items-baseline gap-2 mb-0.5">
                        <span className="text-[11px] font-bold text-slate-700 dark:text-slate-300">
                          {isMe ? 'You' : m.senderName}
                        </span>
                        <span className="text-[9px] text-slate-400">
                          {m.createdAt ? new Date(m.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''}
                        </span>
                      </div>
                      <div
                        className={`p-3 rounded-2xl text-xs leading-relaxed inline-block ${
                          isMe
                            ? 'bg-brand-600 text-white rounded-tr-xs'
                            : 'bg-slate-100 dark:bg-slate-800 text-slate-900 dark:text-slate-100 rounded-tl-xs'
                        }`}
                      >
                        {m.content}
                      </div>
                    </div>
                  </div>
                );
              })
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Chat Input */}
          <form onSubmit={handleSendMessage} className="p-3 sm:p-4 border-t border-slate-100 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/40 flex items-center gap-2">
            <input
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              placeholder="Type message to group..."
              className="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
            />
            <button
              type="submit"
              className="p-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white shadow-xs transition-colors"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>
        </div>
      )}

      {/* TAB 2: SESSIONS & ATTENDANCE */}
      {activeTab === 'sessions' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="font-bold text-slate-900 dark:text-white text-base">Study Group Sessions</h3>
            {isAdmin && (
              <button
                onClick={() => setShowSessionModal(true)}
                className="px-3.5 py-1.5 rounded-xl bg-brand-600 text-white font-bold text-xs"
              >
                + Schedule New Session
              </button>
            )}
          </div>

          {sessions.length === 0 ? (
            <div className="p-8 text-center text-slate-400 text-xs rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
              No sessions scheduled yet.
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {sessions.map((s) => (
                <div key={s.id} className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex flex-col justify-between">
                  <div>
                    <h4 className="font-bold text-slate-900 dark:text-white text-base mb-1">{s.title}</h4>
                    <p className="text-xs text-slate-500 dark:text-slate-400 mb-3">{s.description}</p>
                    <div className="text-xs bg-slate-50 dark:bg-slate-800 p-2.5 rounded-xl space-y-1 mb-4">
                      <p className="text-slate-600 dark:text-slate-300">
                        <Clock className="w-3.5 h-3.5 inline mr-1 text-slate-400" />
                        {s.durationMinutes} mins • {s.mode}
                      </p>
                    </div>
                  </div>

                  <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                    {s.meetingLink ? (
                      <a href={s.meetingLink} target="_blank" rel="noreferrer" className="text-xs font-bold text-brand-600 flex items-center gap-1">
                        <Video className="w-3.5 h-3.5" /> Join Link
                      </a>
                    ) : <span className="text-xs text-slate-400">{s.location || 'Offline'}</span>}

                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleCheckIn(s.id)}
                        className="px-3 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-xs"
                      >
                        Check In (+10 XP)
                      </button>

                      {isAdmin && (
                        <button
                          onClick={() => {
                            setShowAttendanceModal(s);
                            setSelectedAttendees(members.map(m => m.userId));
                          }}
                          className="px-3 py-1.5 rounded-xl bg-slate-100 dark:bg-slate-800 font-bold text-xs"
                        >
                          Mark Sheet
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 3: RESOURCES */}
      {activeTab === 'resources' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="font-bold text-slate-900 dark:text-white text-base">Shared Resource Vault</h3>
            {isMember && (
              <button
                onClick={() => setShowResourceModal(true)}
                className="px-3.5 py-1.5 rounded-xl bg-brand-600 text-white font-bold text-xs"
              >
                + Share Resource
              </button>
            )}
          </div>
          {!isMember && (
            <p className="text-xs text-slate-500">Join this group to share study materials with its members.</p>
          )}

          {resources.length === 0 ? (
            <div className="p-8 text-center text-slate-400 text-xs rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
              No shared resources yet.
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {resources.map((r) => (
                <div key={r.id} className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-xl bg-brand-500/10 text-brand-600 flex items-center justify-center shrink-0">
                      <FileText className="w-5 h-5" />
                    </div>
                    <div>
                      <h4 className="font-bold text-slate-900 dark:text-white text-xs truncate max-w-xs">{r.title}</h4>
                      <span className="text-[10px] text-slate-400 font-bold uppercase">{r.resourceType}</span>
                    </div>
                  </div>
                  <a
                    href={r.urlOrPath}
                    target="_blank"
                    rel="noreferrer"
                    className="p-2 text-brand-600 hover:bg-brand-50 rounded-lg"
                  >
                    <ExternalLink className="w-4 h-4" />
                  </a>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 4: MEMBERS */}
      {activeTab === 'members' && (
        <div className="space-y-4">
          <h3 className="font-bold text-slate-900 dark:text-white text-base">Group Members ({members.length})</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {members.map((m) => (
              <div key={m.id} className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex items-center justify-between shadow-xs">
                <div className="flex items-center gap-2.5">
                  <Avatar src={m.photoUrl} name={m.fullName} size="md" />
                  <div>
                    <h4 className="font-bold text-slate-900 dark:text-white text-xs">{m.fullName}</h4>
                    <span className="text-[10px] text-slate-400">{m.role} • {m.status}</span>
                  </div>
                </div>

                {isAdmin && m.userId !== user?.id && (
                  <div className="flex items-center gap-1">
                    {m.status === 'PENDING' && (
                      <button
                        onClick={() => handleManageMember(m.userId, 'APPROVE')}
                        className="px-2 py-1 rounded bg-emerald-600 text-white text-[10px] font-bold"
                      >
                        Approve
                      </button>
                    )}
                    {m.role !== 'ADMIN' && (
                      <button
                        onClick={() => handleManageMember(m.userId, 'PROMOTE')}
                        className="p-1 text-indigo-500 hover:bg-indigo-50 rounded"
                        title="Promote to admin"
                      >
                        <ShieldCheck className="w-3.5 h-3.5" />
                      </button>
                    )}
                    <button
                      onClick={() => handleManageMember(m.userId, 'REMOVE')}
                      className="p-1 text-rose-500 hover:bg-rose-50 rounded"
                      title="Remove member"
                    >
                      <X className="w-3.5 h-3.5" />
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB 5: PROGRESS & LEADERBOARD */}
      {activeTab === 'leaderboard' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
            <h3 className="font-bold text-slate-900 dark:text-white text-base mb-4">Group Progress Tracker</h3>
            <div className="space-y-4 text-xs">
              <div>
                <div className="flex justify-between font-bold mb-1">
                  <span>Group Attendance Rate</span>
                  <span className="text-emerald-600">{attendanceRate}%</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-800 h-2.5 rounded-full overflow-hidden">
                  <div className="bg-emerald-500 h-2.5 rounded-full" style={{ width: `${attendanceRate}%` }}></div>
                </div>
              </div>

              <div>
                <div className="flex justify-between font-bold mb-1">
                  <span>Group Quiz Average</span>
                  <span className="text-indigo-600">{quizAverage}%</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-800 h-2.5 rounded-full overflow-hidden">
                  <div className="bg-indigo-500 h-2.5 rounded-full" style={{ width: `${quizAverage}%` }}></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* MODAL: SCHEDULE SESSION */}
      {showSessionModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-md w-full border border-slate-200 dark:border-slate-800 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">Schedule Group Study Session</h3>
              <button onClick={() => setShowSessionModal(false)} className="text-slate-400"><X className="w-5 h-5" /></button>
            </div>
            <form onSubmit={handleScheduleSession} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold mb-1">Topic / Title</label>
                <input
                  type="text"
                  required
                  value={sessionForm.title}
                  onChange={(e) => setSessionForm({ ...sessionForm, title: e.target.value })}
                  placeholder="e.g. Graph BFS & DFS Problem Solving"
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold mb-1">Duration (min)</label>
                  <input
                    type="number"
                    value={sessionForm.durationMinutes}
                    onChange={(e) => setSessionForm({ ...sessionForm, durationMinutes: parseInt(e.target.value) })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
                <div>
                  <label className="block font-bold mb-1">Recurrence (Weeks)</label>
                  <input
                    type="number"
                    max={12}
                    value={sessionForm.recurrenceWeeks}
                    onChange={(e) => setSessionForm({ ...sessionForm, recurrenceWeeks: parseInt(e.target.value) })}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold mb-1">Google Meet / Video Link</label>
                <input
                  type="url"
                  value={sessionForm.meetingLink}
                  onChange={(e) => setSessionForm({ ...sessionForm, meetingLink: e.target.value })}
                  placeholder="https://meet.google.com/..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setShowSessionModal(false)} className="px-4 py-2 rounded-xl bg-slate-100 font-bold">Cancel</button>
                <button type="submit" className="px-4 py-2 rounded-xl bg-brand-600 text-white font-bold">Schedule</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: SHARE RESOURCE */}
      {showResourceModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-md w-full border border-slate-200 dark:border-slate-800 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">Share Study Resource</h3>
              <button onClick={() => setShowResourceModal(false)} className="text-slate-400"><X className="w-5 h-5" /></button>
            </div>
            <form onSubmit={handleAddResource} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold mb-1">Resource Title</label>
                <input
                  type="text"
                  required
                  value={resourceForm.title}
                  onChange={(e) => setResourceForm({ ...resourceForm, title: e.target.value })}
                  placeholder="e.g. Dynamic Programming Cheatsheet"
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>

              <div>
                <label className="block font-bold mb-1">Upload PDF / Document</label>
                <input
                  type="file"
                  accept=".pdf,.doc,.docx,.txt,.md,.csv,.xlsx,.zip,.rar,.java,.py,.cpp,.c,.js,.ts,.json,.xml,.sql"
                  onChange={handleFileUpload}
                  className="w-full text-xs"
                />
                <span className="mt-1 block text-[10px] text-slate-500">PDFs, documents, spreadsheets, archives, and code files are supported. Images are disabled to help prevent sharing nudity.</span>
                {uploading && <span className="text-[10px] text-brand-600">Uploading file...</span>}
              </div>

              <div>
                <label className="block font-bold mb-1">Or Link URL (YouTube / Web)</label>
                <input
                  type="text"
                  value={resourceForm.urlOrPath}
                  onChange={(e) => setResourceForm({ ...resourceForm, urlOrPath: e.target.value })}
                  placeholder="https://youtube.com/... or /uploads/..."
                  required
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setShowResourceModal(false)} className="px-4 py-2 rounded-xl bg-slate-100 font-bold">Cancel</button>
                <button type="submit" disabled={uploading} className="px-4 py-2 rounded-xl bg-brand-600 text-white font-bold disabled:cursor-not-allowed disabled:opacity-60">Share Resource</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
