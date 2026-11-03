import React from 'react';

export default function Avatar({ src, name = 'U', size = 'md', className = '' }) {
  const sizeClasses = {
    sm: 'w-7 h-7 text-xs',
    md: 'w-9 h-9 text-sm',
    lg: 'w-12 h-12 text-base font-semibold',
    xl: 'w-16 h-16 text-lg font-bold',
  };

  const getInitials = (n) => {
    if (!n) return 'U';
    const parts = n.trim().split(' ');
    if (parts.length >= 2) return `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase();
    return n.slice(0, 2).toUpperCase();
  };

  const colors = [
    'bg-emerald-500 text-white',
    'bg-indigo-500 text-white',
    'bg-sky-500 text-white',
    'bg-amber-500 text-white',
    'bg-rose-500 text-white',
    'bg-violet-500 text-white',
  ];

  const charCode = (name || 'U').charCodeAt(0);
  const colorClass = colors[charCode % colors.length];

  if (src) {
    return (
      <img
        src={src}
        alt={name}
        className={`rounded-full object-cover ring-2 ring-white dark:ring-slate-800 ${sizeClasses[size] || sizeClasses.md} ${className}`}
      />
    );
  }

  return (
    <div
      className={`rounded-full flex items-center justify-center font-medium shadow-sm ring-2 ring-white dark:ring-slate-800 select-none ${colorClass} ${sizeClasses[size] || sizeClasses.md} ${className}`}
    >
      {getInitials(name)}
    </div>
  );
}
