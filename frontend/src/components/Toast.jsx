import React from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export default function Toast({ message, type = 'info', onClose }) {
  if (!message) return null;

  const icons = {
    success: <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0" />,
    error: <AlertCircle className="w-4 h-4 text-rose-500 shrink-0" />,
    info: <Info className="w-4 h-4 text-sky-500 shrink-0" />,
  };

  const borders = {
    success: 'border-emerald-200 dark:border-emerald-800 bg-emerald-50/90 dark:bg-emerald-950/90 text-emerald-900 dark:text-emerald-200',
    error: 'border-rose-200 dark:border-rose-800 bg-rose-50/90 dark:bg-rose-950/90 text-rose-900 dark:text-rose-200',
    info: 'border-sky-200 dark:border-sky-800 bg-sky-50/90 dark:bg-sky-950/90 text-sky-900 dark:text-sky-200',
  };

  return (
    <div className={`fixed bottom-5 right-5 z-50 flex items-center gap-3 px-4 py-3 rounded-2xl shadow-xl border backdrop-blur-md text-xs font-semibold animate-in slide-in-from-bottom-3 duration-200 ${borders[type] || borders.info}`}>
      {icons[type] || icons.info}
      <span>{message}</span>
      {onClose && (
        <button onClick={onClose} className="p-0.5 hover:opacity-75 transition-opacity">
          <X className="w-3.5 h-3.5" />
        </button>
      )}
    </div>
  );
}
