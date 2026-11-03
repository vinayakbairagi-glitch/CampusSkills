package com.campusskills.controller;

import com.campusskills.entity.GroupMessage;
import com.campusskills.service.StudyGroupService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    private final StudyGroupService studyGroupService;

    public ChatWebSocketController(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    @MessageMapping("/groups/{id}/chat")
    public GroupMessage handleGroupChat(@DestinationVariable Long id,
                                        @Payload Map<String, String> payload,
                                        Principal principal) {
        Long userId = null;
        if (principal != null) {
            try {
                userId = Long.parseLong(principal.getName());
            } catch (NumberFormatException ignored) {}
        }
        if (userId == null) {
            // Fallback or demo user if not connected with token
            userId = 1L;
        }

        String content = payload.get("content");
        if (content == null || content.isBlank()) {
            return null;
        }

        return studyGroupService.postGroupMessage(id, userId, content.trim());
    }
}
