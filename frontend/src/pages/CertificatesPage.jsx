import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import CertificateModal from '../components/CertificateModal';
import { FiAward, FiPrinter, FiShield, FiCheckCircle, FiClock, FiFileText } from 'react-icons/fi';

export default function CertificatesPage() {
  const [certificates, setCertificates] = useState([]);
  const [selectedCert, setSelectedCert] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/api/certificates')
      .then(res => setCertificates(res.data))
      .catch(() => setCertificates([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="max-w-5xl mx-auto space-y-6 pb-12">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-amber-600 via-amber-700 to-indigo-800 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <span className="px-3 py-1 bg-white/20 backdrop-blur rounded-full text-xs font-semibold tracking-wide uppercase">
            Credentials & Honors
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold mt-2">Verified Campus Credentials</h1>
          <p className="text-amber-100 text-sm mt-1 max-w-xl">
            Authenticated certificates awarded for milestone tutoring sessions, competitive campus mock tests, and verified skill achievements.
          </p>
        </div>

        <div className="p-4 rounded-2xl bg-white/10 backdrop-blur-md border border-white/20 flex items-center space-x-3">
          <FiAward className="text-3xl text-amber-300" />
          <div>
            <span className="text-2xl font-black">{certificates.length}</span>
            <p className="text-xs text-amber-200 uppercase font-semibold">Earned Certificates</p>
          </div>
        </div>
      </div>

      {/* Session Milestone Info Cards */}
      <div className="bg-white dark:bg-slate-800 rounded-2xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm space-y-4">
        <h2 className="text-base font-bold text-slate-900 dark:text-white flex items-center">
          <FiShield className="mr-2 text-indigo-600" /> Milestone Tutoring Tiers
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-700/40 border border-slate-200 dark:border-slate-700">
            <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase">Tier 1</span>
            <h3 className="font-bold text-slate-900 dark:text-white text-sm mt-1">10 Sessions Milestone</h3>
            <p className="text-xs text-slate-500 mt-1">Earn the Peer Mentor credential and digital badge.</p>
          </div>

          <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-700/40 border border-slate-200 dark:border-slate-700">
            <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase">Tier 2</span>
            <h3 className="font-bold text-slate-900 dark:text-white text-sm mt-1">25 Sessions Milestone</h3>
            <p className="text-xs text-slate-500 mt-1">Official college distinction with recommendation rights.</p>
          </div>

          <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-700/40 border border-slate-200 dark:border-slate-700">
            <span className="text-xs font-bold text-indigo-600 dark:text-indigo-400 uppercase">Tier 3</span>
            <h3 className="font-bold text-slate-900 dark:text-white text-sm mt-1">50 Sessions Master</h3>
            <p className="text-xs text-slate-500 mt-1">College Master Tutor credential signed by Head of TPO.</p>
          </div>
        </div>
      </div>

      {/* Certificates Grid */}
      {loading ? (
        <div className="flex justify-center py-16">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        </div>
      ) : certificates.length === 0 ? (
        <div className="bg-white dark:bg-slate-800 rounded-3xl p-12 text-center border border-slate-200 dark:border-slate-700 space-y-4">
          <FiAward className="mx-auto text-5xl text-slate-300 dark:text-slate-600" />
          <h3 className="text-base font-bold text-slate-800 dark:text-slate-200">No Certificates Earned Yet</h3>
          <p className="text-sm text-slate-500 max-w-md mx-auto">
            Complete 10 peer tutoring sessions or pass an official MOCK / Skill Verification test to unlock printable authenticated credentials.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {certificates.map((cert, idx) => (
            <div
              key={cert.id || idx}
              className="bg-white dark:bg-slate-800 rounded-3xl p-6 border border-slate-200 dark:border-slate-700 shadow-sm flex flex-col justify-between hover:shadow-md transition space-y-4"
            >
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-amber-50 dark:bg-amber-900/30 text-amber-700 dark:text-amber-300 border border-amber-200 dark:border-amber-800/40">
                    {cert.type || 'CERTIFIED'}
                  </span>
                  <span className="text-xs text-slate-400">{cert.date}</span>
                </div>

                <div className="flex items-start space-x-3 pt-1">
                  <div className="p-2.5 rounded-xl bg-amber-500/10 text-amber-600 dark:text-amber-400 flex-shrink-0">
                    <FiAward className="text-2xl" />
                  </div>
                  <div>
                    <h3 className="font-bold text-slate-900 dark:text-white text-base">
                      {cert.title}
                    </h3>
                    <p className="text-xs text-slate-500 dark:text-slate-400 mt-1 line-clamp-2">
                      {cert.description}
                    </p>
                  </div>
                </div>

                {cert.score && (
                  <div className="text-xs font-bold text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/30 px-3 py-1 rounded-lg inline-block">
                    Score: {cert.score}
                  </div>
                )}
              </div>

              <div className="pt-4 border-t border-slate-100 dark:border-slate-700/60 flex items-center justify-between">
                <span className="text-xs text-slate-400 font-mono">ID: {cert.id}</span>
                <button
                  onClick={() => setSelectedCert(cert)}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold transition flex items-center shadow-sm"
                >
                  <FiPrinter className="mr-1.5" /> View & Print
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedCert && (
        <CertificateModal
          certificate={selectedCert}
          onClose={() => setSelectedCert(null)}
        />
      )}
    </div>
  );
}
