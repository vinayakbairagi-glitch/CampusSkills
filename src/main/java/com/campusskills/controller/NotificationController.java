package com.campusskills.controller;

import com.campusskills.entity.Notification;
import com.campusskills.entity.User;
import com.campusskills.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(@AuthenticationPrincipal User user) {
        List<Notification> list = notificationService.getUserNotifications(user.getId());
        long unread = notificationService.getUnreadCount(user.getId());
        Map<String, Object> resp = new HashMap<>();
        resp.put("notifications", list);
        resp.put("unreadCount", unread);
        return ResponseEntity.ok(resp);
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> markRead(@AuthenticationPrincipal User user,
                                                        @RequestParam(required = false) Long id) {
        if (id != null) {
            notificationService.markAsRead(id, user.getId());
        } else {
            notificationService.markAllAsRead(user.getId());
        }
        return ResponseEntity.ok(Map.of("message", "Notifications marked as read"));
    }
}
