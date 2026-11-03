import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiRequest } from '../utils/api';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem('campusskills_token'));
  const [loading, setLoading] = useState(true);

  const fetchCurrentUser = async () => {
    try {
      if (!token) {
        setUser(null);
        setLoading(false);
        return;
      }
      const userData = await apiRequest('/students/me');
      setUser(userData);
    } catch (err) {
      console.error('Failed to fetch user:', err);
      if (err.status === 401) {
        logout();
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCurrentUser();
  }, [token]);

  const login = (authToken, userData) => {
    localStorage.setItem('campusskills_token', authToken);
    setToken(authToken);
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem('campusskills_token');
    setToken(null);
    setUser(null);
  };

  const isPending = user?.status === 'PENDING';
  const isAdmin = user?.role === 'SUPER_ADMIN';
  const isTpo = user?.role === 'TPO' || user?.role === 'SUPER_ADMIN';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        logout,
        fetchCurrentUser,
        refreshUser: fetchCurrentUser,
        isPending,
        isAdmin,
        isTpo,
        credits: user?.skillCredits || 0,
        xp: user?.xp || 0,
        level: user?.level || 'Beginner',
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
