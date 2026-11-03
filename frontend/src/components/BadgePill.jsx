import React from 'react';
import { Award, CheckCircle2, ShieldCheck, Flame, Star, Sparkles } from 'lucide-react';

export default function BadgePill({ badge, size = 'sm' }) {
  const isVerified = badge.toLowerCase().includes('verified');
  const isMentor = badge.toLowerCase().includes('mentor') || badge.toLowerCase().includes('master');
  const isStreak = badge.toLowerCase().includes('streak');

  let icon = <Award className="w-3.5 h-3.5 text-amber-500" />;
  let color = 'bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-950/40 dark:text-amber-300 dark:border-amber-800';

  if (isVerified) {
    icon = <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />;
    color = 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/40 dark:text-emerald-300 dark:border-emerald-800';
  } else if (isStreak) {
    icon = <Flame className="w-3.5 h-3.5 text-orange-500" />;
    color = 'bg-orange-50 text-orange-700 border-orange-200 dark:bg-orange-950/40 dark:text-orange-300 dark:border-orange-800';
  } else if (isMentor) {
    icon = <Sparkles className="w-3.5 h-3.5 text-indigo-500" />;
    color = 'bg-indigo-50 text-indigo-700 border-indigo-200 dark:bg-indigo-950/40 dark:text-indigo-300 dark:border-indigo-800';
  }

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold border ${color} shadow-xs`}>
      {icon}
      <span>{badge}</span>
    </span>
  );
}
