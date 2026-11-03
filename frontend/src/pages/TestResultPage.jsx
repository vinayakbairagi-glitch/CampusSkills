import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import api from '../utils/api';
import CertificateModal from '../components/CertificateModal';
import {
  FiCheckCircle, FiXCircle, FiAward, FiClock, FiBarChart2,
  FiArrowRight, FiBookOpen, FiPrinter, FiAlertCircle
} from 'react-icons/fi';

export default function TestResultPage() {
  const [searchParams] = useSearchParams();
  const attemptId = searchParams.get('attemptId');
  const navigate = useNavigate();

  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showCertificate, setShowCertificate] = useState(false);

  useEffect(() => {
    if (!attemptId) {
      setError('Attempt ID is missing');
      setLoading(false);
      return;
    }

    api.get(`/api/attempts/${attemptId}/result`)
      .then(res => setResult(res.data))
      .catch(err => setError(err.response?.data?.message || 'Failed to load test results'))
      .finally(() => setLoading(false));
  }, [attemptId]);

  if (loading) {
    return (
      <div className="flex justify-center items-center py-24 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (error || !result) {
    return (
      <div className="max-w-xl mx-auto p-8 text-center space-y-4">
        <div className="p-4 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400 rounded-2xl flex items-center justify-center text-sm">
          <FiAlertCircle className="mr-2" /> {error || 'Result not found'}
        </div>
        <Link to="/tests" className="inline-block px-5 py-2.5 bg-indigo-600 text-white rounded-xl text-sm font-semibold hover:bg-indigo-700">
          Back to Tests
        </Link>
      </div>
    );
  }

  const {
    title, subject, score, totalMarks, percentage, passed,
    timeTakenSeconds, rank: rankInTest, totalParticipants, topicAccuracy,
    questionsReview: questions = [], canHaveCertificate
  } = result;

  const minutes = Math.floor((timeTakenSeconds || 0) / 60);
  const seconds = (timeTakenSeconds || 0) % 60;
  const correctCount = questions.filter(question => question.isCorrect).length;

  return (
    <div className="max-w-4xl mx-auto space-y-8 pb-16">
      {/* Top Banner Card */}
      <div className={`rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-center justify-between gap-6 ${
        passed
          ? 'bg-gradient-to-r from-emerald-600 via-teal-600 to-indigo-600'
          : 'bg-gradient-to-r from-rose-600 via-orange-600 to-amber-600'
      }`}>
        <div className="text-center md:text-left space-y-1">
          <span className="px-3 py-1 bg-white/20 backdrop-blur rounded-full text-xs font-bold uppercase tracking-wider">
            {passed ? 'Assessment Passed' : 'Needs Practice'}
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold">{title}</h1>
          <p className="text-white/80 text-sm">{subject}</p>
        </div>

        <div className="flex flex-col items-center bg-white/10 backdrop-blur-md px-8 py-5 rounded-2xl border border-white/20">
          <span className="text-4xl font-extrabold">{percentage}%</span>
          <span className="text-xs uppercase tracking-wider font-semibold opacity-90 mt-1">
            {score} / {totalMarks} Marks
          </span>
        </div>
      </div>

      {/* Stats Summary Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-xs font-semibold text-slate-400 uppercase">Status</span>
          <p className={`text-base font-bold mt-1 flex items-center justify-center ${passed ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-600 dark:text-rose-400'}`}>
            {passed ? <FiCheckCircle className="mr-1" /> : <FiXCircle className="mr-1" />}
            {passed ? 'PASSED' : 'FAILED'}
          </p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-xs font-semibold text-slate-400 uppercase">Test Rank</span>
          <p className="text-base font-bold text-slate-900 dark:text-white mt-1 flex items-center justify-center">
            <FiAward className="mr-1 text-amber-500" />
            #{rankInTest || 1} <span className="text-xs text-slate-400 ml-1">of {totalParticipants || 1}</span>
          </p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-xs font-semibold text-slate-400 uppercase">Time Spent</span>
          <p className="text-base font-bold text-slate-900 dark:text-white mt-1 flex items-center justify-center">
            <FiClock className="mr-1 text-indigo-500" />
            {minutes}m {seconds}s
          </p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 text-center shadow-sm">
          <span className="text-xs font-semibold text-slate-400 uppercase">XP Awarded</span>
          <p className="text-base font-bold text-indigo-600 dark:text-indigo-400 mt-1">
            +{10 + (passed ? 20 : 0)} XP
          </p>
        </div>
      </div>

      <div className="flex items-start gap-3 p-4 rounded-2xl bg-indigo-50 dark:bg-indigo-950/30 border border-indigo-200 dark:border-indigo-800 text-sm text-indigo-900 dark:text-indigo-200">
        <FiCheckCircle className="w-5 h-5 shrink-0 text-indigo-600 dark:text-indigo-400" />
        <p>
          You answered <strong>{correctCount} of {questions.length}</strong> questions correctly.
          The overall pass/fail status is based on the quiz's passing score; each question below shows whether your answer matched the correct answer.
        </p>
      </div>

      {/* Certificate Action if eligible */}
      {canHaveCertificate && (
        <div className="bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800 rounded-2xl p-5 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center space-x-3 text-center sm:text-left">
            <div className="p-3 bg-amber-500 text-white rounded-xl">
              <FiAward className="text-2xl" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-slate-900 dark:text-white">Certificate of Achievement Earned!</h3>
              <p className="text-xs text-slate-600 dark:text-slate-400">
                You passed an official assessment and earned an authenticated credential.
              </p>
            </div>
          </div>
          <button
            onClick={() => setShowCertificate(true)}
            className="px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold rounded-xl transition flex items-center shadow-sm flex-shrink-0"
          >
            <FiPrinter className="mr-1.5" /> View / Print Certificate
          </button>
        </div>
      )}

      {/* Topic Accuracy Bars */}
      {topicAccuracy && Object.keys(topicAccuracy).length > 0 && (
        <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-4">
          <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center">
            <FiBarChart2 className="mr-2 text-indigo-600" /> Topic-wise Performance
          </h2>
          <div className="space-y-3">
            {Object.entries(topicAccuracy).map(([topicName, acc]) => (
              <div key={topicName} className="space-y-1">
                <div className="flex justify-between text-xs font-semibold">
                  <span className="text-slate-700 dark:text-slate-300">{topicName}</span>
                  <span className={acc < 60 ? 'text-rose-500' : 'text-emerald-500'}>{acc}%</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-700 h-2.5 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all duration-500 ${
                      acc < 60 ? 'bg-rose-500' : 'bg-emerald-500'
                    }`}
                    style={{ width: `${acc}%` }}
                  ></div>
                </div>
              </div>
            ))}
          </div>

          <div className="pt-2">
            <Link
              to="/recommendations"
              className="text-xs font-semibold text-indigo-600 dark:text-indigo-400 hover:underline inline-flex items-center"
            >
              View personalized study recommendations for weak topics <FiArrowRight className="ml-1" />
            </Link>
          </div>
        </div>
      )}

      {/* Detailed Per-Question Review */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-6">
        <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center">
          <FiBookOpen className="mr-2 text-indigo-600" /> Question-by-Question Review
        </h2>

        <div className="space-y-6">
          {(questions || []).map((q, idx) => (
            <div
              key={q.id || idx}
              className={`p-4 rounded-xl border space-y-3 ${
                q.isCorrect
                  ? 'bg-emerald-50/40 dark:bg-emerald-950/20 border-emerald-200 dark:border-emerald-800/40'
                  : 'bg-rose-50/40 dark:bg-rose-950/20 border-rose-200 dark:border-rose-800/40'
              }`}
            >
              <div className="flex items-start justify-between gap-2">
                <span className="text-xs font-bold text-slate-500">Q{idx + 1}.</span>
                <span className={`px-2 py-0.5 rounded-md text-[11px] font-bold flex items-center ${
                  q.isCorrect
                    ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300'
                    : 'bg-rose-100 text-rose-800 dark:bg-rose-900/40 dark:text-rose-300'
                }`}>
                  {q.isCorrect ? <FiCheckCircle className="mr-1" /> : <FiXCircle className="mr-1" />}
                  {q.isCorrect ? `Correct (+${q.marksAwarded})` : `Incorrect (${q.marksAwarded})`}
                </span>
              </div>

              <p className="text-sm font-semibold text-slate-900 dark:text-white leading-relaxed">
                {q.questionText}
              </p>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs pt-2 border-t border-slate-200/60 dark:border-slate-700/60">
                <div>
                  <span className="text-slate-400 font-semibold block">Your Answer:</span>
                  <span className={`font-medium ${q.isCorrect ? 'text-emerald-700 dark:text-emerald-400' : 'text-rose-700 dark:text-rose-400'}`}>
                    {q.studentAnswer || '(No answer provided)'}
                  </span>
                </div>
                <div>
                  <span className="text-slate-400 font-semibold block">Correct Answer:</span>
                  <span className="font-bold text-slate-800 dark:text-slate-200">
                    {q.correctAnswer}
                  </span>
                </div>
              </div>

              {q.explanation && (
                <div className="pt-2 text-xs text-slate-600 dark:text-slate-400 bg-white/70 dark:bg-slate-800/70 p-2.5 rounded-lg border border-slate-200 dark:border-slate-700">
                  <span className="font-bold text-indigo-600 dark:text-indigo-400 mr-1">Explanation:</span>
                  {q.explanation}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Certificate Modal */}
      {showCertificate && (
        <CertificateModal
          certificate={{
            title: `Certificate of Competence: ${title}`,
            recipientName: result.userName || 'Student',
            description: `Successfully passed ${title} in ${subject} with a score of ${percentage}%.`,
            date: new Date().toLocaleDateString(),
            badge: `${subject} Certified`
          }}
          onClose={() => setShowCertificate(false)}
        />
      )}
    </div>
  );
}
