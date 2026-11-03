import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import { useAuth } from '../context/AuthContext';
import Avatar from '../components/Avatar';
import BadgePill from '../components/BadgePill';
import {
  FiUser, FiMail, FiBook, FiAward, FiShield, FiStar,
  FiClock, FiPlus, FiTrash2, FiCheck, FiCheckCircle, FiAlertCircle, FiEdit3
} from 'react-icons/fi';

export default function ProfilePage() {
  const { user, refreshUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [mySkills, setMySkills] = useState([]);
  const [availability, setAvailability] = useState([]);
  const [allSkills, setAllSkills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [msg, setMsg] = useState('');

  // Edit Profile States
  const [isEditing, setIsEditing] = useState(false);
  const [fullName, setFullName] = useState('');
  const [branch, setBranch] = useState('');
  const [studyYear, setStudyYear] = useState(1);
  const [bio, setBio] = useState('');
  const [preferredMode, setPreferredMode] = useState('BOTH');
  const [photoUrl, setPhotoUrl] = useState('');

  // Add Skill States
  const [skillName, setSkillName] = useState('');
  const [skillCategory, setSkillCategory] = useState('Programming');
  const [skillType, setSkillType] = useState('CAN_TEACH');
  const [proficiency, setProficiency] = useState('INTERMEDIATE');

  // Add Availability Slot States
  const [slotDay, setSlotDay] = useState('MONDAY');
  const [slotStart, setSlotStart] = useState('17:00');
  const [slotEnd, setSlotEnd] = useState('19:00');
  const [slotMode, setSlotMode] = useState('BOTH');

  const fetchAll = async () => {
    try {
      const [pRes, sRes, aRes, allRes] = await Promise.all([
        api.get('/api/students/me'),
        api.get('/api/skills/my'),
        api.get('/api/skills/availability'),
        api.get('/api/skills')
      ]);
      setProfile(pRes.data);
      setFullName(pRes.data.fullName || '');
      setBranch(pRes.data.branch || '');
      setStudyYear(pRes.data.studyYear || 1);
      setBio(pRes.data.bio || '');
      setPreferredMode(pRes.data.preferredMode || 'BOTH');
      setPhotoUrl(pRes.data.photoUrl || '');

      setMySkills(sRes.data || []);
      setAvailability(aRes.data || []);
      setAllSkills(allRes.data || []);
    } catch (err) {
      setError('Failed to load profile details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    try {
      const res = await api.put('/api/students/me/profile', {
        fullName,
        branch,
        studyYear: Number(studyYear),
        bio,
        preferredMode,
        photoUrl
      });
      setProfile(res.data);
      setIsEditing(false);
      setMsg('Profile updated successfully!');
      refreshUser();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile');
    }
  };

  const handleAddSkill = async (e) => {
    e.preventDefault();
    if (!skillName.trim()) return;
    try {
      await api.post('/api/skills/user', {
        skillName: skillName.trim(),
        category: skillCategory,
        skillType,
        proficiencyLevel: proficiency
      });
      setSkillName('');
      setMsg('Skill added successfully!');
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add skill');
    }
  };

  const handleDeleteSkill = async (id) => {
    try {
      await api.delete(`/api/skills/user/${id}`);
      fetchAll();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete skill');
    }
  };

  const handleAddAvailabilitySlot = async () => {
    const newSlots = [
      ...availability.map(a => ({
        dayOfWeek: a.dayOfWeek,
        startTime: a.startTime,
        endTime: a.endTime,
        mode: a.mode
      })),
      {
        dayOfWeek: slotDay,
        startTime: slotStart,
        endTime: slotEnd,
        mode: slotMode
      }
    ];

    try {
      const res = await api.post('/api/skills/availability', { slots: newSlots });
      setAvailability(res.data);
      setMsg('Availability schedule updated!');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update availability');
    }
  };

  const handleDeleteSlot = async (idx) => {
    const updated = availability.filter((_, i) => i !== idx).map(a => ({
      dayOfWeek: a.dayOfWeek,
      startTime: a.startTime,
      endTime: a.endTime,
      mode: a.mode
    }));

    try {
      const res = await api.post('/api/skills/availability', { slots: updated });
      setAvailability(res.data);
      setMsg('Slot removed.');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to remove slot');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-24 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  const teachSkills = mySkills.filter(s => s.skillType === 'CAN_TEACH');
  const learnSkills = mySkills.filter(s => s.skillType === 'WANT_TO_LEARN');

  return (
    <div className="max-w-5xl mx-auto space-y-6 pb-12">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-indigo-700 via-indigo-600 to-purple-600 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-center justify-between gap-6">
        <div className="flex flex-col sm:flex-row items-center space-y-4 sm:space-y-0 sm:space-x-5 text-center sm:text-left">
          <Avatar name={profile?.fullName} src={profile?.photoUrl} size="xl" />
          <div>
            <div className="flex items-center space-x-2 justify-center sm:justify-start">
              <h1 className="text-2xl font-extrabold">{profile?.fullName}</h1>
              <span className="px-2 py-0.5 rounded-full text-xs font-bold bg-white/20 uppercase">
                {profile?.role}
              </span>
            </div>
            <p className="text-indigo-100 text-sm mt-0.5">
              {profile?.email} • Roll: {profile?.rollNumber}
            </p>
            <p className="text-xs text-indigo-200 mt-1">
              College community: {profile?.collegeDomain || profile?.email?.split('@').pop() || 'Not identified'}
            </p>
            <p className="text-xs text-indigo-200 mt-1">
              {profile?.branch || 'General'} Engineering • Year {profile?.studyYear || 1} • Prefers: {profile?.preferredMode || 'BOTH'}
            </p>
          </div>
        </div>

        <button
          onClick={() => setIsEditing(!isEditing)}
          className="px-4 py-2 bg-white text-indigo-700 font-bold rounded-xl text-xs sm:text-sm shadow hover:bg-indigo-50 transition flex items-center flex-shrink-0"
        >
          <FiEdit3 className="mr-1.5" /> {isEditing ? 'Cancel Editing' : 'Edit Profile'}
        </button>
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

      {/* Gamification Stats Strip */}
      <div className="grid grid-cols-2 sm:grid-cols-5 gap-4">
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">SkillCredits</span>
          <p className="text-xl font-extrabold text-indigo-600 dark:text-indigo-400 mt-1">
            {profile?.skillCredits} 🪙
          </p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Learning XP</span>
          <p className="text-xl font-extrabold text-purple-600 dark:text-purple-400 mt-1">
            {profile?.xp} XP
          </p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Current Level</span>
          <p className="text-sm font-bold text-slate-800 dark:text-slate-200 mt-2">
            {profile?.level || 'Beginner'}
          </p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Trust Score</span>
          <p className="text-xl font-extrabold text-emerald-600 dark:text-emerald-400 mt-1">
            {profile?.reliabilityScore}%
          </p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm col-span-2 sm:col-span-1">
          <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Peer Rating</span>
          <p className="text-xl font-extrabold text-amber-500 mt-1">
            ★ {profile?.averageRating}
          </p>
        </div>
      </div>

      {/* Badges Earned */}
      {profile?.badges && profile.badges.length > 0 && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-5 border border-slate-200 dark:border-slate-700 shadow-sm space-y-3">
          <h2 className="text-xs font-bold text-slate-400 uppercase tracking-wider flex items-center">
            <FiAward className="mr-1.5 text-amber-500" /> Earned Badges & Honors
          </h2>
          <div className="flex flex-wrap gap-2">
            {profile.badges.map((b, idx) => (
              <BadgePill key={idx} badge={b} size="sm" />
            ))}
          </div>
        </div>
      )}

      {/* Edit Profile Form */}
      {isEditing && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-4">
          <h2 className="text-base font-bold text-slate-900 dark:text-white">Edit Profile Details</h2>
          <form onSubmit={handleUpdateProfile} className="space-y-4 text-sm">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Full Name</label>
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={e => setFullName(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Photo URL</label>
                <input
                  type="url"
                  placeholder="https://images.unsplash.com/..."
                  value={photoUrl}
                  onChange={e => setPhotoUrl(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Branch / Dept</label>
                <input
                  type="text"
                  value={branch}
                  onChange={e => setBranch(e.target.value)}
                  placeholder="e.g. Computer Science"
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Study Year</label>
                <select
                  value={studyYear}
                  onChange={e => setStudyYear(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                >
                  <option value={1}>Year 1 (Freshman)</option>
                  <option value={2}>Year 2 (Sophomore)</option>
                  <option value={3}>Year 3 (Junior)</option>
                  <option value={4}>Year 4 (Senior)</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Preferred Mode</label>
                <select
                  value={preferredMode}
                  onChange={e => setPreferredMode(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
                >
                  <option value="BOTH">Both Online & Offline</option>
                  <option value="ONLINE">Online Only</option>
                  <option value="OFFLINE">Offline / In-Person</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-500 uppercase mb-1">Bio / About Me</label>
              <textarea
                rows="3"
                value={bio}
                onChange={e => setBio(e.target.value)}
                placeholder="Share your interests, current projects, or teaching style..."
                className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl"
              />
            </div>

            <div className="flex justify-end space-x-3 pt-2">
              <button
                type="button"
                onClick={() => setIsEditing(false)}
                className="px-4 py-2 text-slate-600 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-xl text-xs font-bold"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold"
              >
                Save Changes
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Skills Manager (Can Teach vs Want to Learn) */}
      <div className="bg-white dark:bg-slate-800 rounded-3xl p-6 sm:p-8 border border-slate-200 dark:border-slate-700 shadow-sm space-y-6">
        <div>
          <h2 className="text-lg font-bold text-slate-900 dark:text-white">Skills Matrix</h2>
          <p className="text-xs text-slate-500 mt-0.5">Manage what you can teach to earn SkillCredits, and what you want to learn.</p>
        </div>

        {/* Add Skill Form */}
        <form onSubmit={handleAddSkill} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-700/40 border border-slate-200 dark:border-slate-700 grid grid-cols-1 sm:grid-cols-5 gap-3 items-end text-xs">
          <div className="sm:col-span-2">
            <label className="block font-semibold text-slate-500 uppercase mb-1">Skill Name</label>
            <input
              type="text"
              required
              placeholder="e.g. React, Spring Boot, Figma"
              value={skillName}
              onChange={e => setSkillName(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block font-semibold text-slate-500 uppercase mb-1">Type</label>
            <select
              value={skillType}
              onChange={e => setSkillType(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
            >
              <option value="CAN_TEACH">I Can Teach</option>
              <option value="WANT_TO_LEARN">Want to Learn</option>
            </select>
          </div>

          <div>
            <label className="block font-semibold text-slate-500 uppercase mb-1">Proficiency</label>
            <select
              value={proficiency}
              onChange={e => setProficiency(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
            >
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
            </select>
          </div>

          <button
            type="submit"
            className="w-full py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs transition flex items-center justify-center shadow-sm"
          >
            <FiPlus className="mr-1" /> Add Skill
          </button>
        </form>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-2">
          {/* Can Teach */}
          <div className="space-y-3">
            <h3 className="text-xs font-bold text-emerald-600 dark:text-emerald-400 uppercase tracking-wider flex items-center">
              <span className="w-2 h-2 rounded-full bg-emerald-500 mr-2"></span>
              Skills I Can Teach ({teachSkills.length})
            </h3>
            {teachSkills.length === 0 ? (
              <p className="text-xs text-slate-400 italic">No teaching skills listed yet.</p>
            ) : (
              <div className="space-y-2">
                {teachSkills.map(s => (
                  <div key={s.id} className="p-3 bg-slate-50 dark:bg-slate-700/40 rounded-xl border border-slate-200 dark:border-slate-700 flex items-center justify-between text-xs">
                    <div>
                      <div className="flex items-center space-x-2">
                        <span className="font-bold text-slate-800 dark:text-slate-200">{s.skillName}</span>
                        {s.verified && (
                          <span className="text-[10px] bg-emerald-100 text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-300 font-bold px-1.5 py-0.2 rounded">
                            VERIFIED
                          </span>
                        )}
                      </div>
                      <span className="text-slate-400 text-[11px]">{s.proficiencyLevel}</span>
                    </div>
                    <button
                      onClick={() => handleDeleteSkill(s.id)}
                      className="text-slate-400 hover:text-rose-500 transition p-1"
                      title="Remove skill"
                    >
                      <FiTrash2 />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Want to Learn */}
          <div className="space-y-3">
            <h3 className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider flex items-center">
              <span className="w-2 h-2 rounded-full bg-indigo-500 mr-2"></span>
              Skills I Want to Learn ({learnSkills.length})
            </h3>
            {learnSkills.length === 0 ? (
              <p className="text-xs text-slate-400 italic">No learning skills listed yet.</p>
            ) : (
              <div className="space-y-2">
                {learnSkills.map(s => (
                  <div key={s.id} className="p-3 bg-slate-50 dark:bg-slate-700/40 rounded-xl border border-slate-200 dark:border-slate-700 flex items-center justify-between text-xs">
                    <div>
                      <span className="font-bold text-slate-800 dark:text-slate-200 block">{s.skillName}</span>
                      <span className="text-slate-400 text-[11px]">{s.proficiencyLevel}</span>
                    </div>
                    <button
                      onClick={() => handleDeleteSkill(s.id)}
                      className="text-slate-400 hover:text-rose-500 transition p-1"
                      title="Remove skill"
                    >
                      <FiTrash2 />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Weekly Availability Schedule */}
      <div className="bg-white dark:bg-slate-800 rounded-3xl p-6 sm:p-8 border border-slate-200 dark:border-slate-700 shadow-sm space-y-6">
        <div>
          <h2 className="text-lg font-bold text-slate-900 dark:text-white flex items-center">
            <FiClock className="mr-2 text-indigo-600" /> Weekly Availability Slots
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">Define your free time slots so peers and algorithms can schedule swap sessions smoothly.</p>
        </div>

        {/* Add Slot Control */}
        <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-700/40 border border-slate-200 dark:border-slate-700 grid grid-cols-1 sm:grid-cols-5 gap-3 items-end text-xs">
          <div>
            <label className="block font-semibold text-slate-500 uppercase mb-1">Day</label>
            <select
              value={slotDay}
              onChange={e => setSlotDay(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
            >
              {['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'].map(d => (
                <option key={d} value={d}>{d}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block font-semibold text-slate-500 uppercase mb-1">Start Time</label>
            <input
              type="time"
              value={slotStart}
              onChange={e => setSlotStart(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block font-semibold text-slate-500 uppercase mb-1">End Time</label>
            <input
              type="time"
              value={slotEnd}
              onChange={e => setSlotEnd(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block font-semibold text-slate-500 uppercase mb-1">Mode</label>
            <select
              value={slotMode}
              onChange={e => setSlotMode(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-600 rounded-xl text-xs"
            >
              <option value="BOTH">Both</option>
              <option value="ONLINE">Online</option>
              <option value="OFFLINE">Offline</option>
            </select>
          </div>

          <button
            type="button"
            onClick={handleAddAvailabilitySlot}
            className="w-full py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold rounded-xl text-xs transition flex items-center justify-center shadow-sm"
          >
            <FiPlus className="mr-1" /> Add Slot
          </button>
        </div>

        {/* Existing Slots */}
        {availability.length === 0 ? (
          <p className="text-xs text-slate-400 italic py-2">No availability slots set. Add your preferred tutoring hours above.</p>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
            {availability.map((a, idx) => (
              <div
                key={idx}
                className="p-3 bg-slate-50 dark:bg-slate-700/40 rounded-xl border border-slate-200 dark:border-slate-700 flex items-center justify-between text-xs"
              >
                <div>
                  <span className="font-bold text-slate-800 dark:text-slate-200 block">{a.dayOfWeek}</span>
                  <span className="text-slate-500">{a.startTime} - {a.endTime} • {a.mode}</span>
                </div>
                <button
                  type="button"
                  onClick={() => handleDeleteSlot(idx)}
                  className="text-slate-400 hover:text-rose-500 transition p-1"
                >
                  <FiTrash2 />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
