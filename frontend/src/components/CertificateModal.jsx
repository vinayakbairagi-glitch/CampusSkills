import React from 'react';
import { X, Printer, Award, ShieldCheck } from 'lucide-react';

export default function CertificateModal({ certificate, onClose }) {
  if (!certificate) return null;

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/70 backdrop-blur-xs animate-in fade-in duration-200">
      <div className="bg-white dark:bg-slate-900 rounded-2xl shadow-2xl max-w-3xl w-full border border-slate-200 dark:border-slate-800 overflow-hidden flex flex-col">
        {/* Header Bar */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-900/50">
          <div className="flex items-center gap-2">
            <Award className="w-5 h-5 text-brand-600" />
            <h3 className="font-semibold text-slate-800 dark:text-slate-100">Official Campus Certificate</h3>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-white bg-brand-600 hover:bg-brand-700 rounded-lg shadow-xs transition-colors"
            >
              <Printer className="w-4 h-4" />
              Print / Save PDF
            </button>
            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Certificate Body (Styled for printing and display) */}
        <div className="p-8 sm:p-12 overflow-y-auto">
          <div className="printable-certificate border-8 border-double border-amber-600/40 p-8 sm:p-12 rounded-xl text-center bg-gradient-to-b from-amber-50/30 via-white to-amber-50/20 dark:from-slate-900 dark:via-slate-900 dark:to-slate-900 relative shadow-inner">
            {/* Corner ornaments */}
            <div className="absolute top-2 left-2 text-amber-500/60 text-xl font-serif">✦</div>
            <div className="absolute top-2 right-2 text-amber-500/60 text-xl font-serif">✦</div>
            <div className="absolute bottom-2 left-2 text-amber-500/60 text-xl font-serif">✦</div>
            <div className="absolute bottom-2 right-2 text-amber-500/60 text-xl font-serif">✦</div>

            {/* Emblem */}
            <div className="flex justify-center mb-4">
              <div className="w-16 h-16 rounded-full bg-amber-500/10 border-2 border-amber-500/30 flex items-center justify-center text-amber-600 dark:text-amber-400">
                <ShieldCheck className="w-10 h-10" />
              </div>
            </div>

            <p className="text-xs tracking-widest font-bold text-amber-700 dark:text-amber-400 uppercase mb-1">
              CampusSkills Peer-Learning Academy
            </p>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white font-serif mb-6 tracking-wide">
              {certificate.title || 'Certificate of Excellence'}
            </h1>

            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 italic mb-2">This is proudly presented to</p>

            <div className="inline-block border-b-2 border-slate-400 dark:border-slate-600 px-8 pb-1 mb-4">
              <h2 className="text-xl sm:text-2xl font-bold text-slate-900 dark:text-white">
                {certificate.recipientName}
              </h2>
            </div>

            {certificate.recipientRoll && (
              <p className="text-xs font-mono text-slate-500 dark:text-slate-400 mb-4">
                Roll No: {certificate.recipientRoll}
              </p>
            )}

            <p className="text-sm sm:text-base text-slate-700 dark:text-slate-300 max-w-lg mx-auto leading-relaxed mb-8">
              {certificate.description ||
                'For demonstrating outstanding peer-tutoring leadership, technical proficiency, and active collaborative growth.'}
            </p>

            {certificate.score && (
              <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-emerald-50 dark:bg-emerald-950/40 text-emerald-700 dark:text-emerald-300 border border-emerald-200 dark:border-emerald-800 text-xs font-bold mb-8">
                Official Score: {certificate.score}
              </div>
            )}

            {/* Signature & Date Footer */}
            <div className="grid grid-cols-2 gap-8 pt-8 border-t border-slate-200 dark:border-slate-800 mt-6 text-xs text-slate-600 dark:text-slate-400">
              <div className="text-left">
                <p className="font-semibold text-slate-900 dark:text-white">Date of Issue</p>
                <p>{certificate.date || new Date().toISOString().split('T')[0]}</p>
                <p className="font-mono text-[10px] text-slate-400 mt-1">ID: {certificate.id || 'CS-CERT-2026'}</p>
              </div>
              <div className="text-right flex flex-col items-end">
                <div className="font-serif italic text-lg text-slate-800 dark:text-slate-200 font-bold mb-1">
                  Prof. Rajesh Sharma
                </div>
                <p className="font-semibold text-slate-900 dark:text-white">Head of Training & Placement</p>
                <p className="text-[10px] text-slate-400">CampusSkills Verified Authority</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
