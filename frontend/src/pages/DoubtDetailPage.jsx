import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiRequest } from '../utils/api';
import Avatar from '../components/Avatar';
import Toast from '../components/Toast';
import {
  HelpCircle,
  ThumbsUp,
  Award,
  ShieldCheck,
  CheckCircle2,
  Flag,
  ArrowLeft,
  Send,
  Sparkles,
  X,
  Clock,
} from 'lucide-react';

export default function DoubtDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [newAnswer, setNewAnswer] = useState('');
  const [toast, setToast] = useState(null);

  // Report Modal
  const [reportModal, setReportModal] = useState(null); // { type, id }
  const [reportReason, setReportReason] = useState('');

  const loadDoubt = async () => {
    try {
      const result = await apiRequest(`/doubts/${id}`);
      setData(result);
    } catch (err) {
      console.error('Error loading doubt:', err);
      setToast({ message: err.message || 'Failed to load doubt', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDoubt();
  }, [id]);

  const handlePostAnswer = async (e) => {
    e.preventDefault();
    if (!newAnswer.trim()) return;

    try {
      await apiRequest(`/doubts/${id}/answers`, {
        method: 'POST',
        body: JSON.stringify({ content: newAnswer.trim() }),
      });
      setToast({ message: 'Answer posted! +5 XP awarded.', type: 'success' });
      setNewAnswer('');
      loadDoubt();
    } catch (err) {
      setToast({ message: err.message || 'Failed to post answer', type: 'error' });
    }
  };

  const handleToggleUpvote = async (answerId) => {
    try {
      await apiRequest(`/answers/${answerId}/upvote`, { method: 'POST' });
      loadDoubt();
    } catch (err) {
      setToast({ message: err.message || 'Upvote failed', type: 'error' });
    }
  };

  const handleMarkBest = async (answerId) => {
    try {
      await apiRequest(`/answers/${answerId}/best`, { method: 'PUT' });
      setToast({ message: 'Best answer marked! Author awarded +1 Credit & +25 XP.', type: 'success' });
      loadDoubt();
    } catch (err) {
      setToast({ message: err.message || 'Failed to mark best', type: 'error' });
    }
  };

  const handleVerifyAnswer = async (answerId) => {
    try {
      await apiRequest(`/answers/${answerId}/verify`, { method: 'PUT' });
      setToast({ message: 'Answer verified by senior/faculty!', type: 'success' });
      loadDoubt();
    } catch (err) {
      setToast({ message: err.message || 'Verification failed', type: 'error' });
    }
  };

  const handleSubmitReport = async (e) => {
    e.preventDefault();
    try {
      await apiRequest('/reports', {
        method: 'POST',
        body: JSON.stringify({
          reportedType: reportModal.type,
          reportedId: reportModal.id,
          reason: reportReason,
        }),
      });
      setToast({ message: 'Report submitted to admin queue for review.', type: 'info' });
      setReportModal(null);
      setReportReason('');
    } catch (err) {
      setToast({ message: err.message || 'Failed to submit report', type: 'error' });
    }
  };

  if (loading || !data) {
    return (
      <div className="p-8 flex items-center justify-center min-h-[60vh]">
        <div className="w-8 h-8 rounded-full border-2 border-brand-500 border-t-transparent animate-spin"></div>
      </div>
    );
  }

  const { isAsker, answers } = data;
  const isSeniorOrFaculty = (user?.studyYear && user.studyYear >= 3) || user?.role === 'TPO' || user?.role === 'SUPER_ADMIN';

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-4xl mx-auto space-y-6">
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}

      <Link to="/doubts" className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-slate-800 dark:hover:text-slate-200">
        <ArrowLeft className="w-4 h-4" /> Back to Doubts Board
      </Link>

      {/* Main Doubt Card */}
      <div className="p-6 sm:p-8 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs space-y-4">
        <div className="flex items-start justify-between gap-4">
          <div className="flex items-center gap-3">
            <Avatar src={data.askerPhoto} name={data.askerName} size="md" />
            <div>
              <h3 className="font-bold text-slate-900 dark:text-white text-sm">{data.askerName}</h3>
              <p className="text-xs text-slate-400">
                {data.askerBranch || 'CSE'} • Year {data.askerYear || 2}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {data.status === 'SOLVED' ? (
              <span className="px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300 flex items-center gap-1">
                <CheckCircle2 className="w-3.5 h-3.5" /> SOLVED
              </span>
            ) : (
              <span className="px-3 py-1 rounded-full text-xs font-bold bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300">
                OPEN
              </span>
            )}
            <button
              onClick={() => setReportModal({ type: 'DOUBT', id: data.id })}
              className="p-1.5 text-slate-400 hover:text-rose-500 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
              title="Report content"
            >
              <Flag className="w-4 h-4" />
            </button>
          </div>
        </div>

        <div className="pt-2">
          <span className="px-2.5 py-0.5 rounded-md text-[10px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
            {data.subject} • {data.topic}
          </span>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-white mt-2 mb-3">
            {data.title}
          </h1>
          <div className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 text-xs sm:text-sm text-slate-700 dark:text-slate-300 whitespace-pre-line leading-relaxed border border-slate-100 dark:border-slate-800 font-sans">
            {data.description}
          </div>
        </div>
      </div>

      {/* Answers Section */}
      <div className="space-y-4">
        <h2 className="text-lg font-bold text-slate-900 dark:text-white flex items-center gap-2">
          <span>Answers ({answers.length})</span>
        </h2>

        {answers.length === 0 ? (
          <div className="p-8 text-center rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-400 text-xs">
            No answers yet. Be the first to answer and earn +5 XP!
          </div>
        ) : (
          answers.map((ans) => (
            <div
              key={ans.id}
              className={`p-6 rounded-3xl bg-white dark:bg-slate-900 border transition-all ${
                ans.isBest
                  ? 'border-2 border-emerald-500 shadow-md bg-emerald-50/10 dark:bg-emerald-950/10'
                  : 'border-slate-200 dark:border-slate-800 shadow-xs'
              }`}
            >
              <div className="flex items-start justify-between gap-4 mb-3">
                <div className="flex items-center gap-3">
                  <Avatar src={ans.authorPhoto} name={ans.authorName} size="md" />
                  <div>
                    <div className="flex items-center gap-1.5">
                      <h4 className="font-bold text-slate-900 dark:text-white text-xs">{ans.authorName}</h4>
                      {ans.isVerified && (
                        <span className="inline-flex items-center gap-1 px-2 py-0.2 rounded-full text-[10px] font-bold bg-blue-100 text-blue-700 dark:bg-blue-950 dark:text-blue-300 border border-blue-200 dark:border-blue-800" title="Senior/Faculty Verified Answer">
                          <ShieldCheck className="w-3 h-3 text-blue-600" /> Verified
                        </span>
                      )}
                    </div>
                    <span className="text-[10px] text-slate-400 font-semibold">
                      Year {ans.authorYear || 3} • {ans.authorBranch || 'CSE'}
                    </span>
                  </div>
                </div>

                {ans.isBest && (
                  <span className="px-3 py-1 rounded-full text-xs font-bold bg-emerald-500 text-white flex items-center gap-1 shadow-xs">
                    <Award className="w-3.5 h-3.5" /> BEST ANSWER (+1 Credit)
                  </span>
                )}
              </div>

              {/* Answer Content */}
              <p className="text-xs sm:text-sm text-slate-700 dark:text-slate-300 whitespace-pre-line leading-relaxed mb-4">
                {ans.content}
              </p>

              {/* Bottom Actions */}
              <div className="pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleToggleUpvote(ans.id)}
                    className={`flex items-center gap-1.5 px-3 py-1 rounded-xl text-xs font-bold transition-colors ${
                      ans.hasUpvoted
                        ? 'bg-brand-600 text-white'
                        : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200'
                    }`}
                  >
                    <ThumbsUp className="w-3.5 h-3.5" />
                    <span>{ans.upvoteCount || 0}</span>
                  </button>

                  <button
                    onClick={() => setReportModal({ type: 'ANSWER', id: ans.id })}
                    className="p-1.5 text-slate-400 hover:text-rose-500"
                    title="Report answer"
                  >
                    <Flag className="w-3.5 h-3.5" />
                  </button>
                </div>

                <div className="flex items-center gap-2">
                  {/* Asker can mark best */}
                  {isAsker && !ans.isBest && (
                    <button
                      onClick={() => handleMarkBest(ans.id)}
                      className="px-3 py-1 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold flex items-center gap-1 shadow-xs"
                    >
                      <Award className="w-3.5 h-3.5" /> Pick Best Answer
                    </button>
                  )}

                  {/* Senior/Faculty verify */}
                  {isSeniorOrFaculty && !ans.isVerified && (
                    <button
                      onClick={() => handleVerifyAnswer(ans.id)}
                      className="px-3 py-1 rounded-xl bg-blue-50 dark:bg-blue-950 text-blue-700 dark:text-blue-300 border border-blue-200 dark:border-blue-800 text-xs font-bold flex items-center gap-1 hover:bg-blue-100"
                    >
                      <ShieldCheck className="w-3.5 h-3.5" /> Verify Answer
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Answer Input (Anyone except asker) */}
      {!isAsker ? (
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xs">
          <h3 className="font-bold text-slate-900 dark:text-white text-sm mb-3">Your Answer</h3>
          <form onSubmit={handlePostAnswer} className="space-y-3">
            <textarea
              rows={4}
              required
              value={newAnswer}
              onChange={(e) => setNewAnswer(e.target.value)}
              placeholder="Explain the solution clearly. Include code snippets or references..."
              className="w-full p-3 rounded-2xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-xs sm:text-sm focus:ring-2 focus:ring-brand-500 outline-hidden"
            />
            <div className="flex items-center justify-between">
              <span className="text-[11px] text-slate-400">Posting an answer awards +5 XP. If picked Best, earn +1 Credit & +25 XP!</span>
              <button
                type="submit"
                className="px-5 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-bold text-xs flex items-center gap-1.5 shadow-xs"
              >
                <Send className="w-3.5 h-3.5" /> Post Answer
              </button>
            </div>
          </form>
        </div>
      ) : (
        <div className="p-4 rounded-2xl bg-slate-100 dark:bg-slate-800/60 text-slate-500 text-center text-xs italic">
          You asked this doubt. Review the answers above and pick the best one to reward your peer with +1 Credit!
        </div>
      )}

      {/* MODAL: REPORT CONTENT */}
      {reportModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-2xl max-w-md w-full border border-slate-200 dark:border-slate-800 p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-slate-900 dark:text-white text-base">Report {reportModal.type}</h3>
              <button onClick={() => setReportModal(null)} className="text-slate-400"><X className="w-5 h-5" /></button>
            </div>
            <form onSubmit={handleSubmitReport} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold mb-1">Reason for report</label>
                <textarea
                  rows={3}
                  required
                  value={reportReason}
                  onChange={(e) => setReportReason(e.target.value)}
                  placeholder="Explain why this content violates college peer standards (spam, misinformation, abusive)..."
                  className="w-full p-2.5 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-xs"
                />
              </div>
              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setReportModal(null)} className="px-4 py-2 rounded-xl bg-slate-100 font-bold">Cancel</button>
                <button type="submit" className="px-4 py-2 rounded-xl bg-rose-600 text-white font-bold">Submit Report</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
