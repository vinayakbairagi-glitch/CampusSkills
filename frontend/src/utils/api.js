const configuredApiUrl = import.meta.env.VITE_API_URL || '';
export const API_BASE = configuredApiUrl.replace(/\/$/, '');

export function apiUrl(path) {
  if (/^https?:\/\//i.test(path)) return path;
  return `${API_BASE}${path.startsWith('/') ? path : `/${path}`}`;
}

export async function apiRequest(endpoint, options = {}) {
  const token = localStorage.getItem('campusskills_token');
  const headers = { ...options.headers };
  if (token) headers.Authorization = `Bearer ${token}`;
  if (!(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  const normalizedEndpoint = endpoint.startsWith('http')
    ? endpoint
    : endpoint.startsWith('/api')
      ? endpoint
      : `/api${endpoint.startsWith('/') ? endpoint : '/' + endpoint}`;

  const res = await fetch(apiUrl(normalizedEndpoint), { ...options, headers });
  const contentType = res.headers.get('content-type');
  if (contentType?.includes('text/csv') || options.responseType === 'blob') {
    if (!res.ok) throw new Error('Failed to download file');
    return options.responseType === 'blob'
      ? { data: await res.blob() }
      : { data: await res.text() };
  }

  let data = null;
  try { data = await res.json(); } catch { data = null; }
  if (!res.ok) {
    const error = new Error(data?.message || `Request failed with status ${res.status}`);
    error.status = res.status;
    error.code = data?.code;
    error.response = { status: res.status, data };
    error.data = data;
    throw error;
  }
  return data;
}

export const api = {
  get: async (url, config = {}) => {
    const data = await apiRequest(url, { method: 'GET', ...config });
    return data && data.data !== undefined ? data : { data };
  },
  post: async (url, body, config = {}) => {
    const data = await apiRequest(url, {
      method: 'POST', body: body instanceof FormData ? body : JSON.stringify(body), ...config,
    });
    return data && data.data !== undefined ? data : { data };
  },
  put: async (url, body, config = {}) => {
    const data = await apiRequest(url, {
      method: 'PUT',
      body: body instanceof FormData ? body : body ? JSON.stringify(body) : undefined,
      ...config,
    });
    return data && data.data !== undefined ? data : { data };
  },
  delete: async (url, config = {}) => {
    const data = await apiRequest(url, { method: 'DELETE', ...config });
    return data && data.data !== undefined ? data : { data };
  },
};

export async function uploadFile(file) {
  const token = localStorage.getItem('campusskills_token');
  const formData = new FormData();
  formData.append('file', file);
  const res = await fetch(apiUrl('/api/files'), {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.message || 'File upload failed');
  return data.url;
}

export default api;
