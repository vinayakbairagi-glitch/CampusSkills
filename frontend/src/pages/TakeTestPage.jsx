import React, { useState, useEffect, useRef } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import api from '../utils/api';
import {
  FiClock, FiAlertCircle, FiChevronLeft, FiChevronRight,
  FiCheckCircle, FiHelpCircle, FiSend
} from 'react-icons/fi';

export default function TakeTestPage() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();

  const [attemptData, setAttemptData] = useState(location.state?.attemptData || null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [secondsLeft, setSecondsLeft] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [savingStatus, setSavingStatus] = useState('');
  const [error, setError] = useState('');
  const [focusChangeCount, setFocusChangeCount] = useState(0);
  const timerRef = useRef(null);
  const answerSaveQueues = useRef(new Map());

  useEffect(() => {
    if (!attemptData?.attemptId) return undefined;
    const storageKey = `campusskills:proctor-focus:${attemptData.attemptId}`;
    const savedCount = Number(sessionStorage.getItem(storageKey) || 0);
    setFocusChangeCount(Number.isFinite(savedCount) ? savedCount : 0);

    const handleVisibilityChange = () => {
      if (document.visibilityState === 'hidden') {
        setFocusChangeCount(count => {
          const nextCount = count + 1;
          sessionStorage.setItem(storageKey, String(nextCount));
          return nextCount;
        });
      }
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => document.removeEventListener('visibilitychange', handleVisibilityChange);
  }, [attemptData?.attemptId]);

  // Initialize or resume attempt if not passed via route state
  useEffect(() => {
    if (!attemptData) {
      api.post(`/api/tests/${id}/start`)
        .then(res => {
          setAttemptData(res.data);
        })
        .catch(err => {
          setError(err.response?.data?.message || 'Could not load test attempt');
        });
    }
  }, [id, attemptData]);

  // Load answers and remaining timer from attemptData
  useEffect(() => {
    if (attemptData) {
      const initialAnswers = {};
      if (attemptData.questions) {
        attemptData.questions.forEach(q => {
          if (q.savedAnswer) {
            initialAnswers[q.id] = q.savedAnswer;
          }
        });
      }
      setAnswers(initialAnswers);
      setSecondsLeft(attemptData.remainingSeconds != null ? attemptData.remainingSeconds : attemptData.durationMinutes * 60);
    }
  }, [attemptData]);

  // Countdown timer
  useEffect(() => {
    if (secondsLeft === null) return;
    if (secondsLeft <= 0) {
      handleFinalSubmit();
      return;
    }

    timerRef.current = setInterval(() => {
      setSecondsLeft(prev => {
        if (prev <= 1) {
          clearInterval(timerRef.current);
          handleFinalSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timerRef.current);
  }, [secondsLeft]);

  const questions = attemptData?.questions || [];
  const currentQuestion = questions[currentIndex];

  const handleAnswerChange = async (qId, val) => {
    setAnswers(prev => ({ ...prev, [qId]: val }));
    setSavingStatus('Saving...');
    const previousSave = answerSaveQueues.current.get(qId) || Promise.resolve();
    const savePromise = previousSave.catch(() => {}).then(() => api.post(`/api/attempts/${attemptData.attemptId}/answer`, {
      questionId: qId,
      answer: val
    }));
    answerSaveQueues.current.set(qId, savePromise);

    try {
      await savePromise;
      setSavingStatus('Saved');
      setTimeout(() => setSavingStatus(''), 1500);
    } catch (err) {
      setSavingStatus('Error saving');
    } finally {
      if (answerSaveQueues.current.get(qId) === savePromise) {
        answerSaveQueues.current.delete(qId);
      }
    }
  };

  const handleFinalSubmit = async () => {
    if (submitting) return;
    setSubmitting(true);
    try {
      await Promise.all([...answerSaveQueues.current.values()].map(save => save.catch(() => {})));
      await Promise.all(Object.entries(answers).map(([questionId, answer]) =>
        api.post(`/api/attempts/${attemptData.attemptId}/answer`, {
          questionId: Number(questionId),
          answer
        })
      ));
      const res = await api.post(`/api/attempts/${attemptData.attemptId}/submit`);
      navigate(`/tests/${id}/result?attemptId=${attemptData.attemptId}`, { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit test');
      setSubmitting(false);
    }
  };

  const confirmSubmit = () => {
    const answeredCount = Object.keys(answers).filter(k => answers[k] && answers[k].trim() !== '').length;
    const unanswered = questions.length - answeredCount;
    const msg = unanswered > 0
      ? `You have answered ${answeredCount} of ${questions.length} questions (${unanswered} unanswered). Submit test now?`
      : `You have answered all ${questions.length} questions. Submit test now?`;

    if (window.confirm(msg)) {
      handleFinalSubmit();
    }
  };

  if (error) {
    return (
      <div className="max-w-2xl mx-auto p-8 text-center space-y-4">
        <div className="p-4 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400 rounded-2xl flex items-center justify-center text-sm">
          <FiAlertCircle className="mr-2 text-lg" /> {error}
        </div>
        <button
          onClick={() => navigate('/tests')}
          className="px-5 py-2.5 bg-indigo-600 text-white rounded-xl text-sm font-semibold hover:bg-indigo-700"
        >
          Back to Tests
        </button>
      </div>
    );
  }

  if (!attemptData || !currentQuestion) {
    return (
      <div className="flex justify-center items-center py-32 text-slate-500">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  const minutes = Math.floor(secondsLeft / 60);
  const seconds = secondsLeft % 60;
  const isTimeUrgent = secondsLeft < 300; // less than 5 mins

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-12">
      {/* Top Test Header & Timer */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl p-4 sm:p-6 shadow-sm border border-slate-200 dark:border-slate-700 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 sticky top-16 z-20">
        <div>
          <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider">
            {attemptData.testType} Assessment
          </span>
          <h1 className="text-lg sm:text-xl font-bold text-slate-900 dark:text-white">
            {attemptData.title}
          </h1>
        </div>

        <div className="flex items-center space-x-4 w-full sm:w-auto justify-between sm:justify-end">
          {savingStatus && (
            <span className="text-xs text-slate-400 animate-pulse font-medium">
              {savingStatus}
            </span>
          )}

          {/* Countdown Clock */}
          <div className={`flex items-center px-4 py-2 rounded-xl text-sm font-bold border transition ${
            isTimeUrgent
              ? 'bg-red-50 border-red-200 text-red-600 dark:bg-red-900/30 dark:border-red-800 dark:text-red-400 animate-pulse'
              : 'bg-indigo-50 border-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:border-indigo-800 dark:text-indigo-300'
          }`}>
            <FiClock className="mr-2 text-base" />
            <span>
              {String(minutes).padStart(2, '0')}:{String(seconds).padStart(2, '0')}
            </span>
          </div>

          <button
            onClick={confirmSubmit}
            disabled={submitting}
            className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-bold text-sm rounded-xl transition shadow flex items-center"
          >
            <FiSend className="mr-1.5" /> Submit Test
          </button>
        </div>
      </div>

      <div className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900 dark:border-amber-900/60 dark:bg-amber-950/30 dark:text-amber-200">
        <p className="font-semibold">Local exam monitoring</p>
        <p className="mt-1 text-xs leading-relaxed">
          This page counts when it is hidden. It does not use a camera, send data to AI, or automatically change your score.
          {focusChangeCount > 0 && ` Page hidden ${focusChangeCount} ${focusChangeCount === 1 ? 'time' : 'times'} during this attempt.`}
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Main Question Card (3 columns) */}
        <div className="lg:col-span-3 space-y-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 sm:p-8 shadow-sm border border-slate-200 dark:border-slate-700 space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-700 pb-4">
              <span className="text-sm font-bold text-slate-700 dark:text-slate-300">
                Question {currentIndex + 1} of {questions.length}
              </span>
              <div className="flex items-center space-x-2">
                <span className="px-2 py-0.5 rounded-lg text-xs font-semibold bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-300">
                  {currentQuestion.difficulty}
                </span>
                <span className="text-xs text-slate-500 font-medium">
                  +{currentQuestion.marks} marks {currentQuestion.negativeMarks > 0 ? `(-${currentQuestion.negativeMarks})` : ''}
                </span>
              </div>
            </div>

            {/* Question Text */}
            <p className="text-base sm:text-lg font-medium text-slate-900 dark:text-white leading-relaxed whitespace-pre-wrap">
              {currentQuestion.questionText}
            </p>

            {/* Answer Controls based on questionType */}
            <div className="pt-2">
              {/* MCQ */}
              {currentQuestion.questionType === 'MCQ' && (
                <div className="space-y-3">
                  {(currentQuestion.options || []).map((opt, oIdx) => (
                    <label
                      key={oIdx}
                      className={`flex items-center p-3.5 rounded-xl border cursor-pointer transition ${
                        answers[currentQuestion.id] === opt
                          ? 'bg-indigo-50/80 border-indigo-500 text-indigo-900 dark:bg-indigo-950/40 dark:border-indigo-400 dark:text-indigo-200'
                          : 'bg-white dark:bg-slate-800/60 border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-700/40'
                      }`}
                    >
                      <input
                        type="radio"
                        name={`q_${currentQuestion.id}`}
                        value={opt}
                        checked={answers[currentQuestion.id] === opt}
                        onChange={() => handleAnswerChange(currentQuestion.id, opt)}
                        className="h-4 w-4 text-indigo-600 border-slate-300 focus:ring-indigo-500 mr-3"
                      />
                      <span className="text-sm font-medium">{opt}</span>
                    </label>
                  ))}
                </div>
              )}

              {/* TRUE_FALSE */}
              {currentQuestion.questionType === 'TRUE_FALSE' && (
                <div className="grid grid-cols-2 gap-4">
                  {['True', 'False'].map(choice => (
                    <button
                      key={choice}
                      type="button"
                      onClick={() => handleAnswerChange(currentQuestion.id, choice)}
                      className={`py-4 rounded-xl border text-sm font-bold transition flex items-center justify-center ${
                        answers[currentQuestion.id]?.toLowerCase() === choice.toLowerCase()
                          ? 'bg-indigo-600 border-indigo-600 text-white shadow'
                          : 'bg-white dark:bg-slate-800 border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700'
                      }`}
                    >
                      {choice}
                    </button>
                  ))}
                </div>
              )}

              {/* FILL_BLANK */}
              {currentQuestion.questionType === 'FILL_BLANK' && (
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 uppercase">Type your answer</label>
                  <input
                    type="text"
                    value={answers[currentQuestion.id] || ''}
                    onChange={e => handleAnswerChange(currentQuestion.id, e.target.value)}
                    placeholder="Enter missing word or phrase..."
                    className="w-full px-4 py-3 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              )}

              {/* NUMERIC */}
              {currentQuestion.questionType === 'NUMERIC' && (
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 uppercase">Number or fraction (tolerance ±0.01)</label>
                  <input
                    type="text"
                    inputMode="decimal"
                    value={answers[currentQuestion.id] || ''}
                    onChange={e => handleAnswerChange(currentQuestion.id, e.target.value)}
                    placeholder="e.g. 42, 3.14, or 1/2"
                    className="w-full sm:w-64 px-4 py-3 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              )}

              {/* SHORT */}
              {currentQuestion.questionType === 'SHORT' && (
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 uppercase">Short Answer</label>
                  <input
                    type="text"
                    value={answers[currentQuestion.id] || ''}
                    onChange={e => handleAnswerChange(currentQuestion.id, e.target.value)}
                    placeholder="Brief exact answer..."
                    className="w-full px-4 py-3 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              )}

              {/* MATCH */}
              {currentQuestion.questionType === 'MATCH' && (
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 uppercase">Match Result / Code</label>
                  <input
                    type="text"
                    value={answers[currentQuestion.id] || ''}
                    onChange={e => handleAnswerChange(currentQuestion.id, e.target.value)}
                    placeholder="Enter matching pairs..."
                    className="w-full px-4 py-3 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-xl text-sm font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              )}
            </div>

            {/* Bottom Nav Buttons */}
            <div className="flex items-center justify-between pt-6 border-t border-slate-100 dark:border-slate-700">
              <button
                type="button"
                onClick={() => setCurrentIndex(prev => Math.max(0, prev - 1))}
                disabled={currentIndex === 0}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 disabled:opacity-40 text-slate-700 dark:text-slate-300 rounded-xl text-xs font-bold transition flex items-center"
              >
                <FiChevronLeft className="mr-1" /> Previous
              </button>

              <button
                type="button"
                onClick={() => setCurrentIndex(prev => Math.min(questions.length - 1, prev + 1))}
                disabled={currentIndex === questions.length - 1}
                className="px-4 py-2 bg-slate-100 dark:bg-slate-700 hover:bg-slate-200 dark:hover:bg-slate-600 disabled:opacity-40 text-slate-700 dark:text-slate-300 rounded-xl text-xs font-bold transition flex items-center"
              >
                Next <FiChevronRight className="ml-1" />
              </button>
            </div>
          </div>
        </div>

        {/* Right Sidebar: Question Navigator (1 column) */}
        <div className="space-y-4">
          <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-sm border border-slate-200 dark:border-slate-700 space-y-4">
            <h3 className="text-sm font-bold text-slate-900 dark:text-white uppercase tracking-wider">
              Question Navigator
            </h3>

            <div className="grid grid-cols-5 gap-2">
              {questions.map((q, idx) => {
                const isAnswered = answers[q.id] && String(answers[q.id]).trim() !== '';
                const isCurrent = idx === currentIndex;

                return (
                  <button
                    key={q.id}
                    onClick={() => setCurrentIndex(idx)}
                    className={`h-9 w-full rounded-xl text-xs font-bold flex items-center justify-center transition ${
                      isCurrent
                        ? 'ring-2 ring-indigo-500 ring-offset-2 dark:ring-offset-slate-800'
                        : ''
                    } ${
                      isAnswered
                        ? 'bg-emerald-500 text-white'
                        : 'bg-slate-100 dark:bg-slate-700 text-slate-700 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-600'
                    }`}
                  >
                    {idx + 1}
                  </button>
                );
              })}
            </div>

            <div className="pt-4 border-t border-slate-100 dark:border-slate-700/60 space-y-2 text-xs text-slate-500">
              <div className="flex items-center space-x-2">
                <span className="h-3 w-3 rounded-md bg-emerald-500 inline-block"></span>
                <span>Answered ({Object.keys(answers).filter(k => answers[k] && answers[k].trim() !== '').length})</span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="h-3 w-3 rounded-md bg-slate-200 dark:bg-slate-700 inline-block"></span>
                <span>Unanswered ({questions.length - Object.keys(answers).filter(k => answers[k] && answers[k].trim() !== '').length})</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
