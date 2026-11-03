import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../utils/api';
import Avatar from '../components/Avatar';
import BadgePill from '../components/BadgePill';
import Toast from '../components/Toast';
import {
  Coins,
  Repeat,
  Search,
  Sparkles,
  ShieldCheck,
  Calendar,
  Clock,
  Video,
  Star,
  Check,
  X,
  Plus,
  Trash2,
  CheckCircle2,
  AlertCircle,
  ChevronRight,
  Filter,
  GraduationCap,
} from 'lucide-react';

const getCollegeDomain = (member) =>
  (member?.collegeDomain || member?.email?.split('@').pop() || '').toLowerCase();

const getTeachableSkills = (tutor) => {
  const skills = tutor?.skills || tutor?.allTeachableSkills || [];
  return skills
    .map((skill) => typeof skill === 'string'
      ? { skillId: null, skillName: skill }
      : skill)
    .filter((skill) => skill.skillId != null && skill.skillName);
};

export default function SkillSwapPage() {
  const { user, credits, fetchCurrentUser } = useAuth();
  const [activeTab, setActiveTab] = useState('matches'); // matches, browse, requests, sessions, myskills
  const [matchScope, setMatchScope] = useState('same');
  const [matches, setMatches] = useState([]);
  const [tutors, setTutors] = useState([]);
  const [searchSkill, setSearchSkill] = useState('');
  const [myRequests, setMyRequests] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [mySkills, setMySkills] = useState([]);
  const [availability, setAvailability] = useState([]);
  const [allSkills, setAllSkills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);

  // Modals state
  const [showRequestModal, setShowRequestModal] = useState(null); // target tutor/match
  const [requestMessage, setRequestMessage] = useState('');
  const [selectedSkillId, setSelectedSkillId] = useState('');
  const [ratingModal, setRatingModal] = useState(null); // target session to rate
  const [ratingStars, setRatingStars] = useState(5);
  const [ratingComment, setRatingComment] = useState('');
  const [newSkillName, setNewSkillName] = useState('');
  const [newSkillType, setNewSkillType] = useState('WANT_TO_LEARN');
  const [newSkillProficiency, setNewSkillProficiency] = useState('INTERMEDIATE');
  const currentCollegeDomain = getCollegeDomain(user);
  const sameCollegeMatches = matches.filter((match) =>
    currentCollegeDomain && getCollegeDomain(match.user) === currentCollegeDomain);
  const visibleMatches = matchScope === 'same' ? sameCollegeMatches : matches;

  const loadData = async () => {
    setLoading(true);
    try {
      if (activeTab === 'matches') {
        const data = await apiRequest('/matches');
        setMatches(data || []);
      } else if (activeTab === 'browse') {
        const data = await apiRequest(`/tutors?skill=${encodeURIComponent(searchSkill)}`);
        setTutors(data || []);
      } else if (activeTab === 'requests') {
        const data = await apiRequest('/swap-requests');
        setMyRequests(data || []);
      } else if (activeTab === 'sessions') {
        const data = await apiRequest('/sessions');
        setSessions(data || []);
      } else if (activeTab === 'myskills') {
        const [userSkillsData, availData, allSkillsData] = await Promise.all([
          apiRequest('/skills/my'),
          apiRequest('/skills/availability'),
          apiRequest('/skills'),
        ]);
        setMySkills(userSkillsData || []);
        setAvailability(availData || []);
        setAllSkills(allSkillsData || []);
      }
    } catch (err) {
      console.error('Error loading swap data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [activeTab]);

  const handleSearchTutors = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await apiRequest(`/tutors?skill=${encodeURIComponent(searchSkill)}`);
      setTutors(data || []);
    } catch (err) {
      setToast({ message: 'Error searching tutors', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleSendSwapRequest = async (e) => {
    e.preventDefault();
    if (credits < 1) {
      setToast({ message: 'You need at least 1 SkillCredit to send a swap request!', type: 'error' });
      return;
    }
    if (!selectedSkillId) {
      setToast({ message: 'Choose one of this peer’s teachable skills first.', type: 'error' });
      return;
    }

    try {
      await apiRequest('/swap-requests', {
        method: 'POST',
        body: JSON.stringify({
          receiverId: showRequestModal.user.id,
          skillId: parseInt(selectedSkillId),
          message: requestMessage,
        }),
      });
      setToast({ message: 'Swap request sent successfully!', type: 'success' });
      setShowRequestModal(null);
      setSelectedSkillId('');
      setRequestMessage('');
      fetchCurrentUser();
    } catch (err) {
      setToast({ message: err.message || 'Failed to send request', type: 'error' });
    }
  };

  const openRequestModal = async (tutor) => {
    let requestTarget = tutor;
    let skills = getTeachableSkills(requestTarget);

    if (skills.length === 0 && Array.isArray(tutor?.allTeachableSkills)) {
      try {
        const tutorList = await apiRequest('/tutors');
        const tutorDetails = tutorList.find((candidate) => candidate.user.id === tutor.user.id);
        if (tutorDetails) {
          requestTarget = { ...tutor, ...tutorDetails };
          skills = getTeachableSkills(requestTarget);
        }
      } catch (err) {
        setToast({ message: err.message || 'Could not load this tutor’s teachable skills.', type: 'error' });
        return;
      }
    }

    setSelectedSkillId(skills[0] ? String(skills[0].skillId) : '');
    setRequestMessage('');
    setShowRequestModal(requestTarget);
  };

  const handleActionRequest = async (id, action) => {
    try {
      await apiRequest(`/swap-requests/${id}`, {
        method: 'PUT',
        body: JSON.stringify({ action }),
      });
      setToast({ message: `Request ${action.toLowerCase()}ed!`, type: 'success' });
      loadData();
      fetchCurrentUser();
    } catch (err) {
      setToast({ message: err.message || 'Action failed', type: 'error' });
    }
  };

  const handleCompleteSession = async (sessionId) => {
    try {
      await apiRequest(`/sessions/${sessionId}/complete`, { method: 'PUT' });
      setToast({ message: 'Session marked as complete! Credits & XP awarded.', type: 'success' });
      loadData();
      fetchCurrentUser();
    } catch (err) {
      setToast({ message: err.message || 'Error completing session', type: 'error' });
    }
  };

  const handleSubmitRating = async (e) => {
    e.preventDefault();
    try {
      await apiRequest(`/sessions/${ratingModal.id}/rating`, {
        method: 'POST',
        body: JSON.stringify({
          rating: ratingStars,
          comment: ratingComment,
          context: 'SESSION',
        }),
      });
      setToast({ message: 'Rating submitted! Thank you.', type: 'success' });
      setRatingModal(null);
      setRatingComment('');
      loadData();
    } catch (err) {
      setToast({ message: err.message || 'Failed to submit rating', type: 'error' });
    }
  };

  const handleAddSkill = async (e) => {
    e.preventDefault();
    if (!newSkillName.trim()) return;
    try {
      await apiRequest('/skills/user', {
        method: 'POST',
        body: JSON.stringify({
          skillName: newSkillName.trim(),
          skillType: newSkillType,
          proficiency: newSkillProficiency,
        }),
      });
      setToast({ message: 'Skill added successfully!', type: 'success' });
      setNewSkillName('');
      loadData();
    } catch (err) {
      setToast({ message: err.message || 'Failed to add skill', type: 'error' });
    }
  };

  const handleDeleteSkill = async (skillId) => {
    try {
      await apiRequest(`/skills/user/${skillId}`, { method: 'DELETE' });
      setToast({ message: 'Skill removed', type: 'info' });
      loadData();
    } catch (err) {
      setToast({ message: err.message || 'Failed to delete skill', type: 'error' });
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6">
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}

      {/* Header & Balance */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-200 dark:border-slate-800">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
              Skill Swap & Peer Tutoring
            </h1>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300 border border-amber-200 dark:border-amber-800 flex items-center gap-1">
              <Coins className="w-3.5 h-3.5 fill-amber-500 text-amber-500" />
              {credits} Credits Available
            </span>
          </div>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
            Exchange skills 1-on-1: Teach 1 hour to earn 1 credit, spend credits to learn any skill!
          </p>
        </div>

        {/* Tab Buttons */}
        <div className="flex items-center gap-1.5 p-1 rounded-2xl bg-slate-100 dark:bg-slate-800 self-start sm:self-auto overflow-x-auto max-w-full">
          {[
            { id: 'matches', label: 'Smart Matches' },
            { id: 'browse', label: 'Browse Tutors' },
            { id: 'requests', label: 'Swap Requests' },
            { id: 'sessions', label: 'My Sessions' },
            { id: 'myskills', label: 'My Skills & Slots' },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold whitespace-nowrap transition-all ${
                activeTab === tab.id
                  ? 'bg-white dark:bg-slate-900 text-slate-900 dark:text-white shadow-xs'
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* TAB 1: SMART MATCHES */}
      {activeTab === 'matches' && (
        <div className="space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 p-4 rounded-2xl bg-indigo-50 dark:bg-indigo-950/30 border border-indigo-200 dark:border-indigo-900">
            <div className="flex items-start gap-2 text-xs">
              <GraduationCap className="w-4 h-4 text-indigo-600 dark:text-indigo-400 shrink-0 mt-0.5" />
              <div>
                <strong className="text-indigo-900 dark:text-indigo-200">Your campus: {currentCollegeDomain || 'Not identified'}</strong>
                <p className="text-indigo-700 dark:text-indigo-300 mt-0.5">
                  Same-college peers are prioritized. You can also browse students from other colleges.
                </p>
              </div>
            </div>
            <div className="flex items-center gap-1 p-1 rounded-xl bg-white/80 dark:bg-slate-900/80 shrink-0">
              {[
                { id: 'same', label: `My college (${sameCollegeMatches.length})` },
                { id: 'all', label: `All colleges (${matches.length})` },
              ].map((scope) => (
                <button
                  key={scope.id}
                  type="button"
                  onClick={() => setMatchScope(scope.id)}
                  className={`px-3 py-1.5 rounded-lg text-[11px] font-bold transition-colors ${
                    matchScope === scope.id
                      ? 'bg-brand-600 text-white'
                      : 'text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800'
                  }`}
                >
                  {scope.label}
                </button>
              ))}
            </div>
          </div>
          <div className="p-4 rounded-2xl bg-brand-50/60 dark:bg-brand-950/20 border border-brand-200/60 dark:border-brand-800/40 text-xs text-brand-900 dark:text-brand-300 flex items-center justify-between">
            <span className="flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-brand-600" />
              <strong>Smart Matching Algorithm:</strong> 70% 2-way skill coverage + 20% peer rating + 10% availability overlap.
            </span>
          </div>

          {loading ? (
            <div className="py-16 text-center text-slate-400">Computing smart matches...</div>
          ) : matches.length === 0 ? (
            <div className="p-12 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
              <Repeat className="w-10 h-10 text-slate-300 mx-auto mb-3" />
              <h3 className="font-bold text-slate-800 dark:text-slate-200 mb-1">No Matches Found Yet</h3>
              <p className="text-xs text-slate-400 max-w-sm mx-auto mb-4">
                Add skills you CAN_TEACH and WANT_TO_LEARN in "My Skills & Slots" to unlock personalized reciprocal matches!
              </p>
              <button
                onClick={() => setActiveTab('myskills')}
                className="px-4 py-2 rounded-xl bg-brand-600 text-white font-bold text-xs"
              >
                Setup My Skills
              </button>
            </div>
          ) : visibleMatches.length === 0 ? (
            <div className="p-10 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800">
              <GraduationCap className="w-10 h-10 text-slate-300 mx-auto mb-3" />
              <h3 className="font-bold text-slate-800 dark:text-slate-200 mb-1">No peers from this college yet</h3>
              <p className="text-xs text-slate-400 max-w-sm mx-auto mb-4">
                You can still meet verified students from other colleges while your campus community grows.
              </p>
              <button
                type="button"
                onClick={() => setMatchScope('all')}
                className="px-4 py-2 rounded-xl bg-brand-600 text-white font-bold text-xs"
              >
                Browse all colleges
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
              {visibleMatches.map((m) => (
                <div
                  key={m.user.id}
                  className="rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 p-6 flex flex-col justify-between shadow-xs hover:border-brand-500/50 transition-all hover:shadow-md"
                >
                  <div>
                    {/* User Header */}
                    <div className="flex items-start justify-between gap-3 mb-4">
                      <div className="flex items-center gap-3">
                        <Avatar src={m.user.photoUrl} name={m.user.fullName} size="lg" />
                        <div>
                          <div className="flex items-center gap-1.5">
                            <h3 className="font-bold text-slate-900 dark:text-white text-sm">
                              {m.user.fullName}
                            </h3>
                            {m.hasVerifiedTutorTick && (
                              <ShieldCheck className="w-4 h-4 text-emerald-500 shrink-0" title="Verified Tutor" />
                            )}
                          </div>
                          <p className="text-xs text-slate-400">
                            {m.user.branch} • Year {m.user.studyYear}
                          </p>
                          <span className={`inline-flex mt-1 px-1.5 py-0.5 rounded-md text-[9px] font-bold ${
                            getCollegeDomain(m.user) === currentCollegeDomain
                              ? 'bg-indigo-100 text-indigo-700 dark:bg-indigo-950 dark:text-indigo-300'
                              : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300'
                          }`}>
                            {getCollegeDomain(m.user) === currentCollegeDomain ? 'Your college' : 'Other college'} · {getCollegeDomain(m.user)}
                          </span>
                        </div>
                      </div>

                      {/* Match Score Badge */}
                      <div className="px-2.5 py-1 rounded-xl bg-emerald-50 dark:bg-emerald-950/60 border border-emerald-200 dark:border-emerald-800 text-emerald-700 dark:text-emerald-300 font-extrabold text-xs text-center shrink-0">
                        {m.matchScore}%
                        <span className="block text-[9px] font-medium text-emerald-600/70">Match</span>
                      </div>
                    </div>

                    {/* What they can teach / you can teach */}
                    <div className="space-y-2.5 text-xs mb-5">
                      <div className="p-2.5 rounded-xl bg-brand-50/50 dark:bg-brand-950/30 border border-brand-100 dark:border-brand-900/50">
                        <span className="font-bold text-brand-800 dark:text-brand-300 block mb-1">
                          They can teach you:
                        </span>
                        <div className="flex flex-wrap gap-1">
                          {m.theyCanTeachYou && m.theyCanTeachYou.length > 0 ? (
                            m.theyCanTeachYou.map((s) => (
                              <span key={s} className="px-2 py-0.5 rounded-md bg-white dark:bg-slate-800 text-brand-700 dark:text-brand-300 font-semibold text-[11px] border border-brand-200/50 dark:border-brand-800/50">
                                {s}
                              </span>
                            ))
                          ) : getTeachableSkills(m).length > 0 ? (
                            <>
                              <span className="w-full text-slate-500 dark:text-slate-400">
                                No direct wishlist match — they also teach:
                              </span>
                              {getTeachableSkills(m).map((skill) => (
                                <span key={skill.skillId} className="px-2 py-0.5 rounded-md bg-white dark:bg-slate-800 text-brand-700 dark:text-brand-300 font-semibold text-[11px] border border-brand-200/50 dark:border-brand-800/50">
                                  {skill.skillName}
                                </span>
                              ))}
                            </>
                          ) : (
                            <span className="text-slate-400 italic">No teaching skills listed yet.</span>
                          )}
                        </div>
                      </div>

                      <div className="p-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/40 border border-slate-100 dark:border-slate-800">
                        <span className="font-bold text-slate-700 dark:text-slate-300 block mb-1">
                          You can teach them:
                        </span>
                        <div className="flex flex-wrap gap-1">
                          {m.youCanTeachThem && m.youCanTeachThem.length > 0 ? (
                            m.youCanTeachThem.map((s) => (
                              <span key={s} className="px-2 py-0.5 rounded-md bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-semibold text-[11px] border border-slate-200 dark:border-slate-700">
                                {s}
                              </span>
                            ))
                          ) : (
                            <span className="text-slate-400 italic">No reciprocal wish-list match</span>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Rating & Action Button */}
                  <div className="pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                    <div className="text-xs">
                      <span className="font-bold text-slate-800 dark:text-slate-200">★ {m.user.averageRating}</span>
                      <span className="text-slate-400 ml-1">({m.user.reliabilityScore}% trust)</span>
                    </div>

                    <button
                      disabled={getTeachableSkills(m).length === 0}
                      onClick={() => openRequestModal(m)}
                      className="px-3.5 py-1.5 rounded-xl bg-brand-600 hover:bg-brand-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-bold text-xs flex items-center gap-1 shadow-xs transition-colors"
                    >
                      <Coins className="w-3.5 h-3.5" />
                      {getTeachableSkills(m).length > 0 ? 'Swap Skills' : 'No teaching skills'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 2: BROWSE TUTORS */}
      {activeTab === 'browse' && (
        <div className="space-y-6">
          {/* Search form */}
          <form onSubmit={handleSearchTutors} className="flex gap-2 max-w-md">
            <div className="relative flex-1">
              <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
              <input
                type="text"
                value={searchSkill}
                onChange={(e) => setSearchSkill(e.target.value)}
                placeholder="Search tutor by skill (e.g. Java, React, Figma)..."
                className="w-full pl-10 pr-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-sm focus:ring-2 focus:ring-brand-500 outline-hidden"
              />
            </div>
            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-xs"
            >
              Search
            </button>
          </form>

          {loading ? (
            <div className="py-16 text-center text-slate-400">Loading tutors...</div>
          ) : tutors.length === 0 ? (
            <div className="p-8 text-center text-slate-400 text-xs">No tutors found for this skill filter.</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
              {tutors.map((t) => (
                <div
                  key={t.user.id}
                  className="rounded-3xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 p-6 flex flex-col justify-between shadow-xs"
                >
                  <div>
                    <div className="flex items-start justify-between gap-3 mb-4">
                      <div className="flex items-center gap-3">
                        <Avatar src={t.user.photoUrl} name={t.user.fullName} size="lg" />
                        <div>
                          <div className="flex items-center gap-1.5">
                            <h3 className="font-bold text-slate-900 dark:text-white text-sm">
                              {t.user.fullName}
                            </h3>
                            {t.isVerifiedTutor && (
                              <ShieldCheck className="w-4 h-4 text-emerald-500 shrink-0" title="Verified Tutor" />
                            )}
                          </div>
                          <p className="text-xs text-slate-400">{t.user.branch} • Year {t.user.studyYear}</p>
                          <span className={`inline-flex mt-1 px-1.5 py-0.5 rounded-md text-[9px] font-bold ${
                            getCollegeDomain(t.user) === currentCollegeDomain
                              ? 'bg-indigo-100 text-indigo-700 dark:bg-indigo-950 dark:text-indigo-300'
                              : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300'
                          }`}>
                            {getCollegeDomain(t.user) === currentCollegeDomain ? 'Your college' : 'Other college'} · {getCollegeDomain(t.user)}
                          </span>
                        </div>
                      </div>
                      <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400">
                        ★ {t.user.averageRating}
                      </span>
                    </div>

                    <div className="mb-4">
                      <span className="text-xs font-bold text-slate-500 dark:text-slate-400 block mb-1.5">
                        Teachable Skills:
                      </span>
                      <div className="flex flex-wrap gap-1.5">
                        {t.skills.map((s) => (
                          <span
                            key={s.id}
                            className={`px-2 py-0.5 rounded-md text-xs font-semibold border ${
                              s.verified
                                ? 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/40 dark:text-emerald-300 dark:border-emerald-800'
                                : 'bg-slate-100 text-slate-700 border-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:border-slate-700'
                            }`}
                          >
                            {s.skillName} • {s.proficiency}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>

                  <div className="pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                    <span className="text-[11px] text-slate-400">1 Credit / hour</span>
                    <button
                      onClick={() => openRequestModal(t)}
                      className="px-3.5 py-1.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-xs"
                    >
                      Request Session
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 3: SWAP REQUESTS */}
      {activeTab === 'requests' && (
        <div className="space-y-4">
          {loading ? (
            <div className="py-16 text-center text-slate-400">Loading requests...</div>
          ) : myRequests.length === 0 ? (
            <div className="p-12 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-400 text-xs">
              No swap requests sent or received yet.
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {myRequests.map((req) => (
                <div
                  key={req.id}
                  className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-start justify-between gap-2 mb-3">
                      <div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                          {req.isSender ? 'Outgoing Request Sent' : 'Incoming Request Received'}
                        </span>
                        <h4 className="font-bold text-slate-900 dark:text-white text-base">
                          {req.skillName} Tutoring
                        </h4>
                        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                          {req.isSender ? `To: ${req.receiverName}` : `From: ${req.senderName}`}
                        </p>
                      </div>

                      <span
                        className={`px-2.5 py-0.5 rounded-full text-xs font-bold ${
                          req.status === 'ACCEPTED'
                            ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                            : req.status === 'PENDING'
                            ? 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300'
                            : 'bg-rose-100 text-rose-800 dark:bg-rose-950 dark:text-rose-300'
                        }`}
                      >
                        {req.status}
                      </span>
                    </div>

                    {req.message && (
                      <p className="text-xs text-slate-600 dark:text-slate-300 italic bg-slate-50 dark:bg-slate-800 p-3 rounded-2xl mb-4">
                        "{req.message}"
                      </p>
                    )}
                  </div>

                  {/* Actions */}
                  <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-end gap-2">
                    {!req.isSender && req.status === 'PENDING' && (
                      <>
                        <button
                          onClick={() => handleActionRequest(req.id, 'ACCEPT')}
                          className="px-3 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs"
                        >
                          Accept & Schedule
                        </button>
                        <button
                          onClick={() => handleActionRequest(req.id, 'REJECT')}
                          className="px-3 py-1.5 rounded-xl bg-rose-100 hover:bg-rose-200 text-rose-700 font-bold text-xs"
                        >
                          Decline
                        </button>
                      </>
                    )}

                    {req.isSender && req.status === 'PENDING' && (
                      <button
                        onClick={() => handleActionRequest(req.id, 'CANCEL')}
                        className="px-3 py-1.5 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 text-slate-600 dark:text-slate-300 font-bold text-xs"
                      >
                        Cancel Request
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 4: MY SESSIONS */}
      {activeTab === 'sessions' && (
        <div className="space-y-4">
          {loading ? (
            <div className="py-16 text-center text-slate-400">Loading sessions...</div>
          ) : sessions.length === 0 ? (
            <div className="p-12 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-400 text-xs">
              No sessions scheduled yet.
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {sessions.map((s) => (
                <div
                  key={s.id}
                  className="p-5 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-start justify-between gap-2 mb-3">
                      <div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                          {s.isTutor ? 'You are Teaching' : 'You are Learning'}
                        </span>
                        <h4 className="font-bold text-slate-900 dark:text-white text-base">
                          {s.skillName} Tutoring
                        </h4>
                        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                          {s.isTutor ? `Learner: ${s.studentName}` : `Tutor: ${s.tutorName}`}
                        </p>
                      </div>

                      <span
                        className={`px-2.5 py-0.5 rounded-full text-xs font-bold ${
                          s.status === 'COMPLETED'
                            ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300'
                            : 'bg-indigo-100 text-indigo-800 dark:bg-indigo-950 dark:text-indigo-300'
                        }`}
                      >
                        {s.status}
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 text-xs bg-slate-50 dark:bg-slate-800/60 p-3 rounded-2xl mb-4">
                      <div>
                        <span className="text-slate-400 block">Duration</span>
                        <span className="font-semibold">{s.durationMinutes} minutes</span>
                      </div>
                      <div>
                        <span className="text-slate-400 block">Credit Exchange</span>
                        <span className="font-semibold text-amber-600 dark:text-amber-400">{s.creditsCharged} Credit(s)</span>
                      </div>
                    </div>
                  </div>

                  {/* Footer Actions */}
                  <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                    {s.meetingLink ? (
                      <a
                        href={s.meetingLink}
                        target="_blank"
                        rel="noreferrer"
                        className="text-xs font-bold text-brand-600 dark:text-brand-400 flex items-center gap-1 hover:underline"
                      >
                        <Video className="w-3.5 h-3.5" />
                        Join Room
                      </a>
                    ) : (
                      <span className="text-xs text-slate-400">{s.location || 'In-person on campus'}</span>
                    )}

                    <div className="flex items-center gap-2">
                      {s.isTutor && s.status === 'SCHEDULED' && (
                        <button
                          onClick={() => handleCompleteSession(s.id)}
                          className="px-3 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs"
                        >
                          Mark Complete & Collect Credits
                        </button>
                      )}

                      {s.status === 'COMPLETED' && !s.alreadyRated && (
                        <button
                          onClick={() => setRatingModal(s)}
                          className="px-3 py-1.5 rounded-xl bg-amber-500 hover:bg-amber-600 text-white font-bold text-xs flex items-center gap-1 shadow-xs"
                        >
                          <Star className="w-3.5 h-3.5" />
                          Rate Peer
                        </button>
                      )}

                      {s.alreadyRated && (
                        <span className="text-xs font-semibold text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                          <Check className="w-3.5 h-3.5" /> Rated
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 5: MY SKILLS & AVAILABILITY */}
      {activeTab === 'myskills' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Add Skill Form & List */}
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-6">
            <div>
              <h3 className="font-bold text-slate-900 dark:text-white text-base">My Skill Portfolio</h3>
              <p className="text-xs text-slate-400 mt-0.5">
                Skills you can teach will appear in search results; skills you want to learn boost smart matching.
              </p>
            </div>

            {/* Form */}
            <form onSubmit={handleAddSkill} className="space-y-3 p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800">
              <div>
                <label className="block text-xs font-bold text-slate-600 dark:text-slate-400 mb-1">
                  Skill Name
                </label>
                <input
                  type="text"
                  required
                  value={newSkillName}
                  onChange={(e) => setNewSkillName(e.target.value)}
                  placeholder="e.g. Next.js, Docker, Kotlin, SQL..."
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-600 dark:text-slate-400 mb-1">
                    Direction
                  </label>
                  <select
                    value={newSkillType}
                    onChange={(e) => setNewSkillType(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                  >
                    <option value="CAN_TEACH">I can teach this</option>
                    <option value="WANT_TO_LEARN">I want to learn this</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-600 dark:text-slate-400 mb-1">
                    Proficiency
                  </label>
                  <select
                    value={newSkillProficiency}
                    onChange={(e) => setNewSkillProficiency(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                  >
                    <option value="BEGINNER">Beginner</option>
                    <option value="INTERMEDIATE">Intermediate</option>
                    <option value="ADVANCED">Advanced</option>
                    <option value="EXPERT">Expert</option>
                  </select>
                </div>
              </div>

              <button
                type="submit"
                className="w-full py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs shadow-xs"
              >
                + Add to Profile
              </button>
            </form>

            {/* List */}
            <div className="space-y-2">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">Current Skills</h4>
              {mySkills.length === 0 ? (
                <p className="text-xs text-slate-400 italic">No skills listed yet.</p>
              ) : (
                mySkills.map((s) => (
                  <div
                    key={s.id}
                    className="p-3 rounded-2xl bg-white dark:bg-slate-800/80 border border-slate-100 dark:border-slate-700 flex items-center justify-between"
                  >
                    <div>
                      <div className="flex items-center gap-1.5">
                        <span className="font-bold text-slate-900 dark:text-white text-xs">{s.skillName}</span>
                        {s.verified && <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" title="Verified Skill" />}
                      </div>
                      <span className="text-[10px] text-slate-400 font-semibold">
                        {s.skillType === 'CAN_TEACH' ? '🎓 Teach' : '🎯 Learn'} • {s.proficiency}
                      </span>
                    </div>

                    <button
                      onClick={() => handleDeleteSkill(s.id)}
                      className="p-1.5 text-slate-400 hover:text-rose-500 transition-colors"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Availability Slots Info */}
          <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
            <div>
              <h3 className="font-bold text-slate-900 dark:text-white text-base">Weekly Availability Slots</h3>
              <p className="text-xs text-slate-400 mt-0.5">
                Set the days and time windows when you are free for 1-on-1 tutoring sessions on campus or online.
              </p>
            </div>

            <div className="space-y-2">
              {availability.length === 0 ? (
                <div className="p-8 text-center text-slate-400 text-xs">
                  No availability slots configured yet. Visit your Profile tab to configure your weekly hours!
                </div>
              ) : (
                availability.map((a) => (
                  <div
                    key={a.id}
                    className="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs"
                  >
                    <div className="flex items-center gap-2">
                      <Calendar className="w-4 h-4 text-brand-600" />
                      <span className="font-bold text-slate-800 dark:text-slate-200">{a.dayOfWeek}</span>
                    </div>
                    <div className="flex items-center gap-2 text-slate-500 dark:text-slate-400">
                      <span>{a.startTime} - {a.endTime}</span>
                      <span>•</span>
                      <span className="font-semibold text-slate-700 dark:text-slate-300">{a.mode}</span>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}

      {/* MODAL: SEND SWAP REQUEST */}
      {showRequestModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-md w-full border border-slate-200 dark:border-slate-800 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">
                Request Session with {showRequestModal.user.fullName}
              </h3>
              <button onClick={() => { setShowRequestModal(null); setSelectedSkillId(''); }} className="text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSendSwapRequest} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-600 dark:text-slate-400 mb-1">
                  Select Skill to Learn
                </label>
                <select
                  required
                  value={selectedSkillId}
                  onChange={(e) => setSelectedSkillId(e.target.value)}
                  disabled={getTeachableSkills(showRequestModal).length === 0}
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                >
                  <option value="">-- Choose a skill they teach --</option>
                  {getTeachableSkills(showRequestModal).map((skill) => (
                    <option key={skill.skillId} value={skill.skillId}>
                      {skill.skillName}
                    </option>
                  ))}
                </select>
                {getTeachableSkills(showRequestModal).length === 0 && (
                  <p className="mt-1 text-xs text-rose-600 dark:text-rose-400">
                    This peer has no selectable teaching skills yet. Choose another tutor or check back later.
                  </p>
                )}
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-600 dark:text-slate-400 mb-1">
                  Message / Goal for Session
                </label>
                <textarea
                  rows={3}
                  value={requestMessage}
                  onChange={(e) => setRequestMessage(e.target.value)}
                  placeholder="e.g. Can you help me understand Dijkstra's algorithm for my test?"
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                />
              </div>

              <div className="p-3 rounded-2xl bg-amber-50 dark:bg-amber-950/30 text-amber-800 dark:text-amber-300 text-xs flex items-center gap-2">
                <Coins className="w-4 h-4 shrink-0 text-amber-500" />
                <span>You will spend 1 SkillCredit upon session completion.</span>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => { setShowRequestModal(null); setSelectedSkillId(''); }}
                  className="px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 text-xs font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={!selectedSkillId}
                  className="px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold shadow-xs"
                >
                  Send Request
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: RATE PEER SESSION */}
      {ratingModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-md w-full border border-slate-200 dark:border-slate-800 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">
                Rate Session: {ratingModal.skillName}
              </h3>
              <button onClick={() => setRatingModal(null)} className="text-slate-400 hover:text-slate-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSubmitRating} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-600 dark:text-slate-400 mb-2">
                  Rating (1 to 5 Stars)
                </label>
                <div className="flex items-center gap-2">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setRatingStars(star)}
                      className="p-1 text-amber-400 hover:scale-110 transition-transform"
                    >
                      <Star
                        className={`w-7 h-7 ${star <= ratingStars ? 'fill-amber-400 text-amber-400' : 'text-slate-300'}`}
                      />
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-600 dark:text-slate-400 mb-1">
                  Feedback & Review
                </label>
                <textarea
                  rows={3}
                  value={ratingComment}
                  onChange={(e) => setRatingComment(e.target.value)}
                  placeholder="How was the explanation, clarity, and pacing?"
                  className="w-full px-3 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs focus:ring-2 focus:ring-brand-500 outline-hidden"
                />
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setRatingModal(null)}
                  className="px-4 py-2 rounded-xl bg-slate-100 dark:bg-slate-800 text-xs font-bold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold shadow-xs"
                >
                  Submit Review
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
