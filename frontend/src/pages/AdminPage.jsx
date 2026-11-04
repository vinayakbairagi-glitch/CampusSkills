import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import { useAuth } from '../context/AuthContext';
import Avatar from '../components/Avatar';
import {
  FiUsers, FiUserCheck, FiAlertTriangle, FiBarChart2,
  FiDownload, FiPrinter, FiCheck, FiX, FiShield, FiSearch,
  FiBookOpen, FiActivity, FiLayers, FiAlertCircle, FiCheckCircle,
  FiHardDrive, FiRefreshCw
} from 'react-icons/fi';

export default function AdminPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');

  const [stats, setStats] = useState(null);
  const [students, setStudents] = useState([]);
  const [reports, setReports] = useState([]);
  const [officialResults, setOfficialResults] = useState([]);
  const [searchStudent, setSearchStudent] = useState('');
  const [loading, setLoading] = useState(true);
  const [studentsLoaded, setStudentsLoaded] = useState(false);
  const [reportsLoaded, setReportsLoaded] = useState(false);
  const [officialResultsLoaded, setOfficialResultsLoaded] = useState(false);
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');
  const [backups, setBackups] = useState([]);
  const [backupsLoading, setBackupsLoading] = useState(false);
  const [creatingBackup, setCreatingBackup] = useState(false);

  const isSuperAdmin = user?.role === 'SUPER_ADMIN';

  const fetchStats = async () => {
    setLoading(true);
    try {
      const res = await api.get('/api/admin/stats');
      setStats(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load administration statistics');
    } finally {
      setLoading(false);
    }
  };

  const fetchStudents = async () => {
    try {
      const res = await api.get('/api/admin/students');
      setStudents(res.data);
      setStudentsLoaded(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load students');
    }
  };

  const fetchReports = async () => {
    try {
      const res = await api.get('/api/admin/reports');
      setReports(res.data);
      setReportsLoaded(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load reports');
    }
  };

  const fetchOfficialResults = async () => {
    try {
      const res = await api.get('/api/admin/official-tests');
      setOfficialResults(res.data);
      setOfficialResultsLoaded(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load official test results');
    }
  };

  const fetchAll = async () => {
    setLoading(true);
    try {
      const [stRes, uRes, rRes, oRes] = await Promise.all([
        api.get('/api/admin/stats'),
        api.get('/api/admin/students'),
        api.get('/api/admin/reports'),
        api.get('/api/admin/official-tests')
      ]);
      setStats(stRes.data);
      setStudents(uRes.data);
      setReports(rRes.data);
      setOfficialResults(oRes.data);
      setStudentsLoaded(true);
      setReportsLoaded(true);
      setOfficialResultsLoaded(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load administration data');
    } finally {
      setLoading(false);
    }
  };

  const fetchBackups = async () => {
    setBackupsLoading(true);
    try {
      const res = await api.get('/api/admin/backups');
      setBackups(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load local backups');
    } finally {
      setBackupsLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  useEffect(() => {
    if ((activeTab === 'pending' || activeTab === 'students') && !studentsLoaded) {
      fetchStudents();
    } else if (activeTab === 'reports' && !reportsLoaded) {
      fetchReports();
    } else if (activeTab === 'official' && !officialResultsLoaded) {
      fetchOfficialResults();
    } else if (activeTab === 'backups' && isSuperAdmin) {
      fetchBackups();
    }
  }, [activeTab, isSuperAdmin, studentsLoaded, reportsLoaded, officialResultsLoaded]);

  const handleCreateBackup = async () => {
    setCreatingBackup(true);
    setError('');
    try {
      const res = await api.post('/api/admin/backups');
      setMsg(`Backup created: ${res.data.filename}`);
      await fetchBackups();
    } catch (err) {
      setError(err.response?.data?.message || 'Could not create a local backup');
    } finally {
      setCreatingBackup(false);
    }
  };

  const handleVerifyStudent = async (studentId, approve) => {
    try {
      await api.put(`/api/admin/students/${studentId}/verify?approve=${approve}`);
      setMsg(`Student ${approve ? 'verified and approved' : 'rejected'}.`);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Verification failed');
    }
  };

  const handleChangeRole = async (studentId, newRole) => {
    try {
      await api.put(`/api/admin/students/${studentId}/role?role=${newRole}`);
      setMsg(`User role updated to ${newRole}`);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Role change failed');
    }
  };

  const handleActionReport = async (reportId, action) => {
    try {
      await api.put(`/api/admin/reports/${reportId}?action=${action}`);
      setMsg(`Report actioned: ${action}`);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Report action failed');
    }
  };

  const handleExport = (endpoint, filename) => {
    api.get(endpoint, { responseType: 'blob' })
      .then(res => {
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
        link.remove();
      })
      .catch(() => setError('Export failed'));
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-24 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  const tabDataLoading =
    ((activeTab === 'pending' || activeTab === 'students') && !studentsLoaded) ||
    (activeTab === 'reports' && !reportsLoaded) ||
    (activeTab === 'official' && !officialResultsLoaded);

  if (tabDataLoading) {
    return (
      <div className="flex justify-center items-center py-24 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  const pendingStudents = students.filter(s => s.status === 'PENDING');
  const filteredStudents = students.filter(s => {
    if (!searchStudent) return true;
    const q = searchStudent.toLowerCase();
    return s.fullName?.toLowerCase().includes(q) ||
           s.email?.toLowerCase().includes(q) ||
           s.rollNumber?.toLowerCase().includes(q) ||
           s.branch?.toLowerCase().includes(q);
  });

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-16">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <span className="px-3 py-1 bg-white/10 backdrop-blur rounded-full text-xs font-semibold tracking-wide uppercase">
            Administrative Console
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold mt-2">Campus Administration & TPO Dashboard</h1>
          <p className="text-slate-300 text-sm mt-1 max-w-xl">
            Verify pending student roll numbers, moderate community reports, inspect official test score sheets, and export analytics.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => window.print()}
            className="px-4 py-2 bg-white/10 hover:bg-white/20 text-white font-bold rounded-xl text-xs backdrop-blur transition flex items-center border border-white/20"
          >
            <FiPrinter className="mr-1.5" /> Print / Save PDF
          </button>
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

      {/* Navigation Tabs */}
      <div className="flex flex-wrap gap-2 border-b border-slate-200 dark:border-slate-700 pb-2">
        {[
          { id: 'overview', label: 'Overview & Analytics', icon: FiBarChart2 },
          { id: 'pending', label: `Pending Verifications (${pendingStudents.length})`, icon: FiUserCheck },
          { id: 'students', label: `All Students (${students.length})`, icon: FiUsers },
          { id: 'reports', label: `Reports Queue (${reports.filter(r => r.status === 'PENDING').length})`, icon: FiAlertTriangle },
          { id: 'official', label: `Placement Mock Results (${officialResults.length})`, icon: FiBookOpen },
          ...(isSuperAdmin ? [{ id: 'backups', label: 'Local Backups', icon: FiHardDrive }] : [])
        ].map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition flex items-center space-x-2 ${
                isActive
                  ? 'bg-indigo-600 text-white shadow-sm'
                  : 'bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white border border-slate-200 dark:border-slate-700'
              }`}
            >
              <Icon />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* Tab 1: Overview & Analytics */}
      {activeTab === 'overview' && stats && (
        <div className="space-y-6">
          {/* 8 Stats Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-slate-400 uppercase">Total Students</span>
              <p className="text-2xl font-extrabold text-slate-900 dark:text-white mt-1">{stats.totalStudents}</p>
              <span className="text-[10px] text-slate-400">{stats.tutors} verified tutors</span>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-amber-500 uppercase">Pending Approval</span>
              <p className="text-2xl font-extrabold text-amber-600 dark:text-amber-400 mt-1">{stats.pendingStudents}</p>
              <span className="text-[10px] text-slate-400">Awaiting roll verification</span>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-slate-400 uppercase">Completed Swaps</span>
              <p className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 mt-1">{stats.completedSessions}</p>
              <span className="text-[10px] text-slate-400">{stats.groups} study circles</span>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-slate-400 uppercase">Doubts Solved</span>
              <p className="text-2xl font-extrabold text-indigo-600 dark:text-indigo-400 mt-1">{stats.doubtsSolvedPercent}%</p>
              <span className="text-[10px] text-slate-400">{stats.doubtsTotal} doubts asked</span>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-slate-400 uppercase">Tests Taken</span>
              <p className="text-2xl font-extrabold text-purple-600 dark:text-purple-400 mt-1">{stats.testsTaken}</p>
              <span className="text-[10px] text-slate-400">Total attempts recorded</span>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-slate-400 uppercase">Attendance Rate</span>
              <p className="text-2xl font-extrabold text-blue-600 dark:text-blue-400 mt-1">{stats.groupAttendanceRate}%</p>
              <span className="text-[10px] text-slate-400">Across study sessions</span>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-slate-400 uppercase">Active This Week</span>
              <p className="text-2xl font-extrabold text-teal-600 dark:text-teal-400 mt-1">{stats.activeThisWeek}</p>
              <span className="text-[10px] text-slate-400">Active learners</span>
            </div>

            <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 shadow-sm">
              <span className="text-[11px] font-bold text-rose-500 uppercase">Inactive &gt;14d</span>
              <p className="text-2xl font-extrabold text-rose-600 dark:text-rose-400 mt-1">{stats.inactiveCount}</p>
              <span className="text-[10px] text-slate-400">Needs engagement ping</span>
            </div>
          </div>

          {/* CSV Export Strip */}
          <div className="bg-slate-50 dark:bg-slate-800/60 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 flex flex-wrap items-center justify-between gap-3">
            <span className="text-xs font-bold text-slate-700 dark:text-slate-300">
              Download Campus Data (Opens in Microsoft Excel / Sheets):
            </span>
            <div className="flex flex-wrap gap-2">
              <button
                onClick={() => handleExport('/api/admin/export/students.csv', 'students.csv')}
                className="px-3 py-1.5 bg-white dark:bg-slate-700 hover:bg-slate-100 text-slate-800 dark:text-slate-200 rounded-xl text-xs font-bold border border-slate-200 dark:border-slate-600 flex items-center shadow-xs"
              >
                <FiDownload className="mr-1" /> Students CSV
              </button>
              <button
                onClick={() => handleExport('/api/admin/export/sessions.csv', 'sessions.csv')}
                className="px-3 py-1.5 bg-white dark:bg-slate-700 hover:bg-slate-100 text-slate-800 dark:text-slate-200 rounded-xl text-xs font-bold border border-slate-200 dark:border-slate-600 flex items-center shadow-xs"
              >
                <FiDownload className="mr-1" /> Tutoring Sessions CSV
              </button>
              <button
                onClick={() => handleExport('/api/admin/export/tests.csv', 'tests.csv')}
                className="px-3 py-1.5 bg-white dark:bg-slate-700 hover:bg-slate-100 text-slate-800 dark:text-slate-200 rounded-xl text-xs font-bold border border-slate-200 dark:border-slate-600 flex items-center shadow-xs"
              >
                <FiDownload className="mr-1" /> Test Results CSV
              </button>
            </div>
          </div>

          {/* Visual Charts */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Popular Skills Chart */}
            <div className="bg-white dark:bg-slate-800 rounded-3xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-4">
              <h3 className="text-sm font-bold text-slate-900 dark:text-white flex items-center justify-between">
                <span>Popular Skills (Teachers vs Learners)</span>
                <span className="text-xs font-normal text-slate-400">Demand & Supply</span>
              </h3>
              <div className="space-y-3">
                {(stats.popularSkillsChart || []).slice(0, 6).map((item, idx) => (
                  <div key={idx} className="space-y-1">
                    <div className="flex justify-between text-xs font-semibold">
                      <span className="text-slate-800 dark:text-slate-200">{item.skill}</span>
                      <span className="text-slate-400">
                        <strong className="text-emerald-600 dark:text-emerald-400">{item.teachers} Tutors</strong> /{' '}
                        <strong className="text-indigo-600 dark:text-indigo-400">{item.learners} Learners</strong>
                      </span>
                    </div>
                    <div className="flex h-2.5 rounded-full overflow-hidden bg-slate-100 dark:bg-slate-700">
                      <div
                        className="bg-emerald-500 h-full"
                        style={{ width: `${item.teachers * 25}%` }}
                        title={`${item.teachers} teachers`}
                      ></div>
                      <div
                        className="bg-indigo-500 h-full"
                        style={{ width: `${item.learners * 25}%` }}
                        title={`${item.learners} learners`}
                      ></div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Weakest Subjects Chart */}
            <div className="bg-white dark:bg-slate-800 rounded-3xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-4">
              <h3 className="text-sm font-bold text-slate-900 dark:text-white flex items-center justify-between">
                <span>Subject Proficiency Averages</span>
                <span className="text-xs font-normal text-slate-400">Mean Score %</span>
              </h3>
              <div className="space-y-3">
                {(stats.weakestSubjectsChart || []).map((sub, idx) => (
                  <div key={idx} className="space-y-1">
                    <div className="flex justify-between text-xs font-semibold">
                      <span className="text-slate-800 dark:text-slate-200">{sub.subject}</span>
                      <span className={sub.averageScore < 60 ? 'text-rose-500 font-bold' : 'text-emerald-500 font-bold'}>
                        {sub.averageScore}%
                      </span>
                    </div>
                    <div className="w-full bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full ${sub.averageScore < 60 ? 'bg-rose-500' : 'bg-emerald-500'}`}
                        style={{ width: `${sub.averageScore}%` }}
                      ></div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tab 2: Pending Verifications */}
      {activeTab === 'pending' && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-100 dark:border-slate-700">
            <h2 className="text-sm font-bold text-slate-900 dark:text-white">
              Pending Student Verifications ({pendingStudents.length})
            </h2>
            <p className="text-xs text-slate-500">Only verified students can access peer tutoring and quizzes.</p>
          </div>

          {pendingStudents.length === 0 ? (
            <p className="text-sm text-slate-500 text-center py-12">No pending student registrations.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 dark:bg-slate-750 text-slate-500 uppercase font-semibold border-b border-slate-100 dark:border-slate-700">
                  <tr>
                    <th className="p-4">Student</th>
                    <th className="p-4">Roll Number</th>
                    <th className="p-4">College Email</th>
                    <th className="p-4">Branch & Year</th>
                    <th className="p-4 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-700/60">
                  {pendingStudents.map(student => (
                    <tr key={student.id} className="hover:bg-slate-50 dark:hover:bg-slate-750/50">
                      <td className="p-4 font-bold text-slate-900 dark:text-white flex items-center space-x-2">
                        <Avatar name={student.fullName} src={student.photoUrl} size="sm" />
                        <span>{student.fullName}</span>
                      </td>
                      <td className="p-4 font-mono font-bold text-indigo-600 dark:text-indigo-400">
                        {student.rollNumber}
                      </td>
                      <td className="p-4 text-slate-600 dark:text-slate-300">
                        {student.email}
                      </td>
                      <td className="p-4 text-slate-500">
                        {student.branch || 'N/A'} • Year {student.studyYear || '1'}
                      </td>
                      <td className="p-4 text-right space-x-2">
                        <button
                          onClick={() => handleVerifyStudent(student.id, true)}
                          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg font-bold text-xs shadow-xs"
                        >
                          Verify & Approve
                        </button>
                        <button
                          onClick={() => handleVerifyStudent(student.id, false)}
                          className="px-3 py-1.5 bg-slate-200 dark:bg-slate-700 hover:bg-slate-300 text-slate-700 dark:text-slate-300 rounded-lg font-bold text-xs"
                        >
                          Reject
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Tab 3: All Students & Roles */}
      {activeTab === 'students' && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm overflow-hidden space-y-4 p-4">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
            <h2 className="text-sm font-bold text-slate-900 dark:text-white">
              Student Directory ({students.length})
            </h2>
            <div className="relative w-full sm:w-64">
              <FiSearch className="absolute left-3 top-2.5 text-slate-400 text-xs" />
              <input
                type="text"
                placeholder="Search name, roll, email..."
                value={searchStudent}
                onChange={e => setSearchStudent(e.target.value)}
                className="w-full pl-8 pr-3 py-1.5 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-750 text-slate-500 uppercase font-semibold border-b border-slate-100 dark:border-slate-700">
                <tr>
                  <th className="p-3">Student</th>
                  <th className="p-3">Roll No</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">Role</th>
                  <th className="p-3">XP</th>
                  <th className="p-3">Credits</th>
                  {isSuperAdmin && <th className="p-3 text-right">Change Role</th>}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-700/60">
                {filteredStudents.map(s => (
                  <tr key={s.id} className="hover:bg-slate-50 dark:hover:bg-slate-750/50">
                    <td className="p-3 font-semibold text-slate-900 dark:text-white flex items-center space-x-2">
                      <Avatar name={s.fullName} src={s.photoUrl} size="xs" />
                      <span>{s.fullName}</span>
                    </td>
                    <td className="p-3 font-mono">{s.rollNumber}</td>
                    <td className="p-3">
                      <span className={`px-2 py-0.5 rounded-md font-bold text-[10px] ${
                        s.status === 'VERIFIED'
                          ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400'
                          : s.status === 'PENDING'
                          ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'
                          : 'bg-rose-100 text-rose-700 dark:bg-rose-900/30 dark:text-rose-400'
                      }`}>
                        {s.status}
                      </span>
                    </td>
                    <td className="p-3 font-bold text-slate-700 dark:text-slate-300">{s.role}</td>
                    <td className="p-3 text-indigo-600 font-bold">{s.xp || 0}</td>
                    <td className="p-3 text-amber-600 font-bold">{s.skillCredits || 0}</td>
                    {isSuperAdmin && (
                      <td className="p-3 text-right">
                        <select
                          value={s.role}
                          onChange={e => handleChangeRole(s.id, e.target.value)}
                          className="px-2 py-1 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-lg text-xs"
                        >
                          <option value="STUDENT">STUDENT</option>
                          <option value="TUTOR">TUTOR</option>
                          <option value="TPO">TPO</option>
                          <option value="SUPER_ADMIN">SUPER_ADMIN</option>
                        </select>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab 4: Reports Queue */}
      {activeTab === 'reports' && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-100 dark:border-slate-700">
            <h2 className="text-sm font-bold text-slate-900 dark:text-white">
              Community Moderation Queue ({reports.length})
            </h2>
            <p className="text-xs text-slate-500">Review community reports and blocked content submissions with their captured evidence.</p>
          </div>

          {reports.length === 0 ? (
            <p className="text-sm text-slate-500 text-center py-12">No reports recorded. Clean campus community!</p>
          ) : (
            <div className="divide-y divide-slate-100 dark:divide-slate-700/60">
              {reports.map(r => (
                <div key={r.id} className="p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                  <div>
                    <div className="flex items-center space-x-2">
                      <span className="px-2 py-0.5 rounded-md font-bold bg-rose-50 text-rose-700 dark:bg-rose-900/30 dark:text-rose-400">
                        {r.reportedType} #{r.reportedId}
                      </span>
                      <span className="text-slate-400">Status: {r.status}</span>
                    </div>
                    <p className="text-slate-800 dark:text-slate-200 font-semibold mt-1">
                      {r.reportedType === 'USER' && r.reason?.startsWith('AUTOMATED CONTENT MODERATION')
                        ? 'Blocked submission evidence:'
                        : 'Reason:'}
                      <span className="ml-1 font-normal whitespace-pre-wrap break-words">{r.reason}</span>
                    </p>
                    {r.details && <p className="text-slate-500 mt-0.5 italic">"{r.details}"</p>}
                  </div>

                  {r.status === 'PENDING' && (
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => handleActionReport(r.id, 'DISMISS')}
                        className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300 rounded-lg font-bold"
                      >
                        Dismiss
                      </button>
                      <button
                        onClick={() => handleActionReport(r.id, 'REMOVE_CONTENT')}
                        className="px-3 py-1.5 bg-rose-600 hover:bg-rose-700 text-white rounded-lg font-bold"
                      >
                        {r.reportedType === 'USER' ? 'Confirm Block' : 'Remove Content'}
                      </button>
                      <button
                        onClick={() => handleActionReport(r.id, 'SUSPEND_USER')}
                        className="px-3 py-1.5 bg-slate-900 hover:bg-black text-white rounded-lg font-bold"
                      >
                        Suspend User
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab 5: Placement Mock Results */}
      {activeTab === 'official' && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-100 dark:border-slate-700">
            <h2 className="text-sm font-bold text-slate-900 dark:text-white">
              Official Placement Mock Exams Leaderboard
            </h2>
            <p className="text-xs text-slate-500">Official college score sheet for recruitment drive filtering.</p>
          </div>

          {officialResults.length === 0 ? (
            <p className="text-sm text-slate-500 text-center py-12">No official test attempts recorded yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 dark:bg-slate-750 text-slate-500 uppercase font-semibold border-b border-slate-100 dark:border-slate-700">
                  <tr>
                    <th className="p-3">Rank</th>
                    <th className="p-3">Student</th>
                    <th className="p-3">Roll Number</th>
                    <th className="p-3">Assessment</th>
                    <th className="p-3">Score</th>
                    <th className="p-3">Percentage</th>
                    <th className="p-3">Time</th>
                    <th className="p-3">Result</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-700/60">
                  {officialResults.map((item, idx) => (
                    <tr key={idx} className="hover:bg-slate-50 dark:hover:bg-slate-750/50">
                      <td className="p-3 font-bold text-indigo-600 dark:text-indigo-400">#{idx + 1}</td>
                      <td className="p-3 font-semibold text-slate-900 dark:text-white">{item.fullName || item.userName}</td>
                      <td className="p-3 font-mono">{item.rollNumber}</td>
                      <td className="p-3 font-medium">{item.testTitle}</td>
                      <td className="p-3 font-bold">{item.score} / {item.totalMarks}</td>
                      <td className="p-3 font-extrabold text-indigo-600">{item.percentage}%</td>
                      <td className="p-3 text-slate-400">{item.timeTakenSeconds}s</td>
                      <td className="p-3">
                        <span className={`px-2 py-0.5 rounded-md font-bold text-[10px] ${
                          item.passed ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300' : 'bg-rose-100 text-rose-800'
                        }`}>
                          {item.passed ? 'PASSED' : 'FAILED'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

        </div>
      )}

      {activeTab === 'backups' && isSuperAdmin && (
        <section className="space-y-4 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm dark:border-slate-700 dark:bg-slate-800">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-sm font-bold text-slate-900 dark:text-white">Local database backups</h2>
              <p className="mt-1 text-xs text-slate-500">
                A backup is created when the app starts and every day at 2:00 AM. The seven newest copies are kept in the local backups folder.
              </p>
            </div>
            <button
              type="button"
              onClick={handleCreateBackup}
              disabled={creatingBackup}
              className="inline-flex shrink-0 items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-xs font-bold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {creatingBackup ? <FiRefreshCw className="animate-spin" /> : <FiHardDrive />}
              {creatingBackup ? 'Creating backup...' : 'Create backup now'}
            </button>
          </div>

          {backupsLoading ? (
            <div className="flex justify-center py-8">
              <div className="h-7 w-7 animate-spin rounded-full border-b-2 border-indigo-600" />
            </div>
          ) : backups.length === 0 ? (
            <p className="rounded-xl bg-slate-50 p-4 text-xs text-slate-500 dark:bg-slate-900">
              No backups found yet. A startup backup will be created automatically; you can also create one now.
            </p>
          ) : (
            <div className="divide-y divide-slate-100 rounded-xl border border-slate-200 dark:divide-slate-700 dark:border-slate-700">
              {backups.map(backup => (
                <div key={backup.filename} className="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between">
                  <div className="min-w-0">
                    <p className="truncate text-xs font-bold text-slate-800 dark:text-slate-100">{backup.filename}</p>
                    <p className="mt-1 text-[11px] text-slate-500">
                      {new Date(backup.createdAt).toLocaleString()} · {(backup.sizeBytes / (1024 * 1024)).toFixed(2)} MB
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => handleExport(`/api/admin/backups/${encodeURIComponent(backup.filename)}`, backup.filename)}
                    className="inline-flex shrink-0 items-center justify-center gap-1.5 rounded-lg border border-slate-200 px-3 py-2 text-xs font-bold text-slate-700 transition hover:bg-slate-50 dark:border-slate-600 dark:text-slate-200 dark:hover:bg-slate-700"
                  >
                    <FiDownload /> Download
                  </button>
                </div>
              ))}
            </div>
          )}
        </section>
      )}
    </div>
  );
}
