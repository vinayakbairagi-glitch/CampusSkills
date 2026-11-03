import React, { useEffect, useState } from 'react';
import { BookOpen, ChevronLeft, ChevronRight, X } from 'lucide-react';

const steps = [
  {
    title: 'Find a skill partner',
    description: 'Open Skill Swap to find verified peers who can teach what you want to learn. Your campus peers are shown first, and you can browse other colleges too.',
    action: 'Explore Skill Swap',
  },
  {
    title: 'Learn together',
    description: 'Join a study group or book a peer tutoring session. Check your notifications for requests, session updates, and upcoming session reminders.',
    action: 'Open Study Groups',
  },
  {
    title: 'Practice and improve',
    description: 'Use Tests & Quizzes for practice sets, mock assessments, and skill verification. Your scores and explanations appear in your results.',
    action: 'View Tests & Quizzes',
  },
  {
    title: 'Track your progress',
    description: 'Your dashboard shows SkillCredits, streaks, and placement readiness. Build your profile and teaching skills so peers can find you.',
    action: 'You are ready!',
  },
];

export default function FirstTimeGuide({ userId }) {
  const [isOpen, setIsOpen] = useState(false);
  const [stepIndex, setStepIndex] = useState(0);

  useEffect(() => {
    if (!userId) return;
    setIsOpen(localStorage.getItem(`campusskills:guide-completed:${userId}`) !== 'true');
    setStepIndex(0);
  }, [userId]);

  const closeGuide = () => {
    if (userId) {
      localStorage.setItem(`campusskills:guide-completed:${userId}`, 'true');
    }
    setIsOpen(false);
  };

  const restartGuide = () => {
    setStepIndex(0);
    setIsOpen(true);
  };

  return (
    <>
      <button
        type="button"
        onClick={restartGuide}
        className="inline-flex items-center gap-2 rounded-xl border border-white/30 bg-white/10 px-3 py-2 text-xs font-bold text-white transition hover:bg-white/20"
      >
        <BookOpen className="h-4 w-4" />
        Quick guide
      </button>

      {isOpen && (
        <div
          className="fixed inset-0 z-[70] flex items-center justify-center bg-slate-950/60 p-4 backdrop-blur-sm"
          role="presentation"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) closeGuide();
          }}
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-labelledby="guide-title"
            className="w-full max-w-lg rounded-3xl border border-slate-200 bg-white p-6 shadow-2xl dark:border-slate-700 dark:bg-slate-900"
          >
            <div className="mb-5 flex items-start justify-between gap-4">
              <div>
                <p className="text-xs font-bold uppercase tracking-wider text-brand-600 dark:text-brand-400">
                  CampusSkills quick tour · {stepIndex + 1} of {steps.length}
                </p>
                <h2 id="guide-title" className="mt-2 text-xl font-extrabold text-slate-900 dark:text-white">
                  {steps[stepIndex].title}
                </h2>
              </div>
              <button
                type="button"
                onClick={closeGuide}
                aria-label="Close quick guide"
                className="rounded-lg p-2 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700 dark:hover:bg-slate-800 dark:hover:text-white"
              >
                <X className="h-5 w-5" />
              </button>
            </div>

            <p className="min-h-20 text-sm leading-relaxed text-slate-600 dark:text-slate-300">
              {steps[stepIndex].description}
            </p>

            <div className="mt-6 flex items-center justify-between gap-3">
              <button
                type="button"
                onClick={closeGuide}
                className="text-xs font-semibold text-slate-500 transition hover:text-slate-800 dark:hover:text-white"
              >
                Skip tour
              </button>
              <div className="flex items-center gap-2">
                {stepIndex > 0 && (
                  <button
                    type="button"
                    onClick={() => setStepIndex(index => index - 1)}
                    className="inline-flex items-center gap-1 rounded-xl bg-slate-100 px-3 py-2 text-xs font-bold text-slate-700 transition hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"
                  >
                    <ChevronLeft className="h-4 w-4" />
                    Back
                  </button>
                )}
                {stepIndex < steps.length - 1 ? (
                  <button
                    type="button"
                    onClick={() => setStepIndex(index => index + 1)}
                    className="inline-flex items-center gap-1 rounded-xl bg-brand-600 px-4 py-2 text-xs font-bold text-white transition hover:bg-brand-700"
                  >
                    Next
                    <ChevronRight className="h-4 w-4" />
                  </button>
                ) : (
                  <button
                    type="button"
                    onClick={closeGuide}
                    className="rounded-xl bg-brand-600 px-4 py-2 text-xs font-bold text-white transition hover:bg-brand-700"
                  >
                    Finish
                  </button>
                )}
              </div>
            </div>
          </section>
        </div>
      )}
    </>
  );
}
