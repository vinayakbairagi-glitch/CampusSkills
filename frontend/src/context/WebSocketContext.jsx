import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from './AuthContext';
import { apiRequest, apiUrl } from '../utils/api';

const WebSocketContext = createContext();

export function WebSocketProvider({ children }) {
  const { token, user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const stompClientRef = useRef(null);

  const fetchNotifications = async () => {
    try {
      if (!token) return;
      const res = await apiRequest('/notifications');
      setNotifications(res.notifications || []);
      setUnreadCount(res.unreadCount || 0);
    } catch (e) {
      console.error('Error fetching notifications:', e);
    }
  };

  useEffect(() => {
    if (token) fetchNotifications();
    else {
      setNotifications([]);
      setUnreadCount(0);
    }
  }, [token]);

  useEffect(() => {
    if (!token || !user) {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        stompClientRef.current = null;
      }
      return;
    }
    // Use the same Vercel deployment origin when API_BASE is empty. This keeps
    // REST and WebSocket traffic on the shared frontend/backend domain.
    const socketUrl = new URL(apiUrl('/ws'), window.location.origin).toString();
    const client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      connectHeaders: { Authorization: `Bearer ${token}` },
      debug: () => {},
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });
    client.onConnect = () => {
      client.subscribe('/user/queue/notifications', (message) => {
        try {
          const notification = JSON.parse(message.body);
          setNotifications(prev => [notification, ...prev]);
          setUnreadCount(prev => prev + 1);
        } catch (e) {
          console.error('Notification parse error:', e);
        }
      });
    };
    client.activate();
    stompClientRef.current = client;
    return () => {
      client.deactivate();
      stompClientRef.current = null;
    };
  }, [token, user?.id]);

  const markAllRead = async () => {
    try {
      await apiRequest('/notifications', { method: 'PUT' });
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch (e) {
      console.error('Failed to mark all read:', e);
    }
  };

  return (
    <WebSocketContext.Provider value={{
      stompClient: stompClientRef.current, notifications, unreadCount,
      markAllRead, refreshNotifications: fetchNotifications,
    }}>
      {children}
    </WebSocketContext.Provider>
  );
}

export function useWebSocket() { return useContext(WebSocketContext); }
