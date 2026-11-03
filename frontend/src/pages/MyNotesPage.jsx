import React, { useEffect, useMemo, useState } from 'react';
import { BookOpen, FilePlus2, Save, Search, Trash2 } from 'lucide-react';
import { apiRequest } from '../utils/api';

const EMPTY_NOTE = { title: '', content: '' };

export default function MyNotesPage() {
  const [notes, setNotes] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [draft, setDraft] = useState(EMPTY_NOTE);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    let active = true;
    apiRequest('/notes')
      .then(data => {
        if (active) setNotes(data);
      })
      .catch(err => {
        if (active) setError(err.message || 'Could not load your notes.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []);

  const filteredNotes = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) return notes;
    return notes.filter(note =>
      note.title.toLowerCase().includes(query) || note.content.toLowerCase().includes(query)
    );
  }, [notes, search]);

  const startNewNote = () => {
    setSelectedId(null);
    setDraft(EMPTY_NOTE);
    setError('');
    setMessage('');
  };

  const selectNote = note => {
    setSelectedId(note.id);
    setDraft({ title: note.title, content: note.content });
    setError('');
    setMessage('');
  };

  const saveNote = async event => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');
    try {
      const saved = await apiRequest(selectedId ? `/notes/${selectedId}` : '/notes', {
        method: selectedId ? 'PUT' : 'POST',
        body: JSON.stringify(draft),
      });
      setNotes(current => [saved, ...current.filter(note => note.id !== saved.id)]
        .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt)));
      setSelectedId(saved.id);
      setDraft({ title: saved.title, content: saved.content });
      setMessage('Note saved.');
    } catch (err) {
      setError(err.message || 'Could not save your note.');
    } finally {
      setSaving(false);
    }
  };

  const deleteNote = async () => {
    if (!selectedId || !window.confirm('Delete this note? This cannot be undone.')) return;
    setError('');
    try {
      await apiRequest(`/notes/${selectedId}`, { method: 'DELETE' });
      setNotes(current => current.filter(note => note.id !== selectedId));
      startNewNote();
      setMessage('Note deleted.');
    } catch (err) {
      setError(err.message || 'Could not delete your note.');
    }
  };

  return (
    <div className="mx-auto max-w-6xl space-y-5">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="mb-2 inline-flex items-center gap-2 text-xs font-bold uppercase tracking-wide text-brand-600 dark:text-brand-400">
            <BookOpen className="h-4 w-4" /> Personal notebook
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">My Notes</h1>
          <p className="mt-1 text-sm text-slate-500">Write and keep private study notes for your account.</p>
        </div>
        <button
          type="button"
          onClick={startNewNote}
          className="inline-flex items-center justify-center gap-2 rounded-xl bg-brand-600 px-4 py-2.5 text-sm font-bold text-white hover:bg-brand-700"
        >
          <FilePlus2 className="h-4 w-4" /> New note
        </button>
      </header>

      {error && <p role="alert" className="rounded-xl border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}
      {message && <p role="status" className="rounded-xl border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">{message}</p>}

      <div className="grid min-h-[32rem] gap-4 lg:grid-cols-[18rem_minmax(0,1fr)]">
        <aside className="overflow-hidden rounded-2xl border border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-900">
          <label className="flex items-center gap-2 border-b border-slate-100 px-3 py-3 dark:border-slate-800">
            <Search className="h-4 w-4 text-slate-400" />
            <input
              value={search}
              onChange={event => setSearch(event.target.value)}
              placeholder="Search notes"
              aria-label="Search notes"
              className="w-full bg-transparent text-sm outline-none"
            />
          </label>
          {loading ? (
            <p className="p-5 text-sm text-slate-500">Loading notes...</p>
          ) : filteredNotes.length === 0 ? (
            <p className="p-5 text-sm text-slate-500">{search ? 'No matching notes.' : 'No notes yet. Create one to get started.'}</p>
          ) : (
            <ul className="max-h-[28rem] divide-y divide-slate-100 overflow-y-auto dark:divide-slate-800">
              {filteredNotes.map(note => (
                <li key={note.id}>
                  <button
                    type="button"
                    onClick={() => selectNote(note)}
                    className={`w-full px-4 py-3 text-left transition hover:bg-slate-50 dark:hover:bg-slate-800 ${
                      selectedId === note.id ? 'bg-brand-50 dark:bg-brand-950/30' : ''
                    }`}
                  >
                    <span className="block truncate text-sm font-bold text-slate-800 dark:text-slate-100">{note.title}</span>
                    <span className="mt-1 block truncate text-xs text-slate-500">{note.content || 'No note text yet'}</span>
                    <span className="mt-1 block text-[10px] text-slate-400">
                      {new Date(note.updatedAt).toLocaleString()}
                    </span>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </aside>

        <form onSubmit={saveNote} className="flex flex-col rounded-2xl border border-slate-200 bg-white p-4 dark:border-slate-800 dark:bg-slate-900 sm:p-6">
          <label className="mb-3 block">
            <span className="mb-1 block text-xs font-bold text-slate-500">Title</span>
            <input
              required
              maxLength={120}
              value={draft.title}
              onChange={event => setDraft(current => ({ ...current, title: event.target.value }))}
              placeholder="e.g. Data structures - binary trees"
              className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-base font-bold text-slate-900 outline-none focus:border-brand-500 dark:border-slate-700 dark:bg-slate-800 dark:text-white"
            />
          </label>
          <label className="flex min-h-64 flex-1 flex-col">
            <span className="mb-1 block text-xs font-bold text-slate-500">Your notes</span>
            <textarea
              maxLength={20000}
              value={draft.content}
              onChange={event => setDraft(current => ({ ...current, content: event.target.value }))}
              placeholder="Write your study notes here..."
              className="min-h-64 flex-1 resize-y rounded-xl border border-slate-200 bg-white p-3 text-sm leading-6 text-slate-800 outline-none focus:border-brand-500 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100"
            />
            <span className="mt-1 text-right text-[11px] text-slate-400">{draft.content.length}/20,000</span>
          </label>
          <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
            <span className="text-xs text-slate-400">Notes are private to your account.</span>
            <div className="flex gap-2">
              {selectedId && (
                <button
                  type="button"
                  onClick={deleteNote}
                  className="inline-flex items-center gap-2 rounded-xl border border-rose-200 px-3 py-2 text-xs font-bold text-rose-700 hover:bg-rose-50 dark:border-rose-900 dark:text-rose-300"
                >
                  <Trash2 className="h-4 w-4" /> Delete
                </button>
              )}
              <button
                type="submit"
                disabled={saving || !draft.title.trim()}
                className="inline-flex items-center gap-2 rounded-xl bg-brand-600 px-4 py-2 text-xs font-bold text-white hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-60"
              >
                <Save className="h-4 w-4" /> {saving ? 'Saving...' : 'Save note'}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
