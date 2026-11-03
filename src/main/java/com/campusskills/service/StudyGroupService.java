package com.campusskills.service;

import com.campusskills.dto.*;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final SessionAttendanceRepository sessionAttendanceRepository;
    private final GroupResourceRepository groupResourceRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;
    private final TextModerationService textModerationService;

    public StudyGroupService(StudyGroupRepository studyGroupRepository,
                             GroupMemberRepository groupMemberRepository,
                             GroupSessionRepository groupSessionRepository,
                             SessionAttendanceRepository sessionAttendanceRepository,
                             GroupResourceRepository groupResourceRepository,
                             GroupMessageRepository groupMessageRepository,
                             UserRepository userRepository,
                             TestRepository testRepository,
                             TestAttemptRepository testAttemptRepository,
                             SimpMessagingTemplate messagingTemplate,
                             GamificationService gamificationService,
                             NotificationService notificationService,
                             TextModerationService textModerationService) {
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupSessionRepository = groupSessionRepository;
        this.sessionAttendanceRepository = sessionAttendanceRepository;
        this.groupResourceRepository = groupResourceRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.messagingTemplate = messagingTemplate;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
        this.textModerationService = textModerationService;
    }

    @Transactional
    public StudyGroup createGroup(Long userId, StudyGroupCreateRequest req) {
        textModerationService.check(userId, "STUDY_GROUP", req.getName(), req.getSubject(), req.getDescription());
        StudyGroup group = StudyGroup.builder()
                .name(req.getName().trim())
                .subject(req.getSubject().trim())
                .category(req.getCategory().toUpperCase().trim())
                .branch(req.getBranch())
                .studyYear(req.getStudyYear())
                .timing(req.getTiming())
                .mode(req.getMode() != null ? req.getMode().toUpperCase() : "ONLINE")
                .memberLimit(req.getMemberLimit() != null ? req.getMemberLimit() : 30)
                .visibility(req.getVisibility() != null ? req.getVisibility().toUpperCase() : "PUBLIC")
                .createdBy(userId)
                .description(req.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        StudyGroup saved = studyGroupRepository.save(group);

        // Creator becomes the first ADMIN member
        GroupMember creatorMember = GroupMember.builder()
                .groupId(saved.getId())
                .userId(userId)
                .role("ADMIN")
                .status("ACTIVE")
                .joinedAt(LocalDateTime.now())
                .build();
        groupMemberRepository.save(creatorMember);

        return saved;
    }

    public List<Map<String, Object>> searchGroups(Long currentUserId, String query, String subject, String category,
                                                  String branch, Integer studyYear, String mode, Boolean myGroups) {
        List<StudyGroup> list = studyGroupRepository.findAllByOrderByCreatedAtDesc();

        if (Boolean.TRUE.equals(myGroups)) {
            List<GroupMember> myMemberships = groupMemberRepository.findByUserIdAndStatus(currentUserId, "ACTIVE");
            Set<Long> myGroupIds = myMemberships.stream().map(GroupMember::getGroupId).collect(Collectors.toSet());
            list = list.stream().filter(g -> myGroupIds.contains(g.getId())).collect(Collectors.toList());
        }

        if (StringUtils.hasText(query)) {
            String q = query.toLowerCase().trim();
            list = list.stream().filter(g ->
                    (g.getName() != null && g.getName().toLowerCase().contains(q)) ||
                    (g.getDescription() != null && g.getDescription().toLowerCase().contains(q)) ||
                    (g.getSubject() != null && g.getSubject().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }

        if (StringUtils.hasText(subject)) {
            list = list.stream().filter(g -> g.getSubject() != null && g.getSubject().equalsIgnoreCase(subject)).collect(Collectors.toList());
        }

        if (StringUtils.hasText(category)) {
            list = list.stream().filter(g -> g.getCategory() != null && g.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
        }

        if (StringUtils.hasText(branch)) {
            list = list.stream().filter(g -> g.getBranch() != null && g.getBranch().equalsIgnoreCase(branch)).collect(Collectors.toList());
        }

        if (studyYear != null) {
            list = list.stream().filter(g -> g.getStudyYear() != null && g.getStudyYear().equals(studyYear)).collect(Collectors.toList());
        }

        if (StringUtils.hasText(mode)) {
            list = list.stream().filter(g -> g.getMode() != null && g.getMode().equalsIgnoreCase(mode)).collect(Collectors.toList());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (StudyGroup g : list) {
            long memberCount = groupMemberRepository.countByGroupIdAndStatus(g.getId(), "ACTIVE");
            Optional<GroupMember> myMembership = groupMemberRepository.findByGroupIdAndUserId(g.getId(), currentUserId);

            Map<String, Object> map = new HashMap<>();
            map.put("id", g.getId());
            map.put("name", g.getName());
            map.put("subject", g.getSubject());
            map.put("category", g.getCategory());
            map.put("branch", g.getBranch());
            map.put("studyYear", g.getStudyYear());
            map.put("timing", g.getTiming());
            map.put("mode", g.getMode());
            map.put("memberLimit", g.getMemberLimit());
            map.put("visibility", g.getVisibility());
            map.put("description", g.getDescription());
            map.put("createdAt", g.getCreatedAt());
            map.put("memberCount", memberCount);
            map.put("isMember", myMembership.isPresent() && "ACTIVE".equalsIgnoreCase(myMembership.get().getStatus()));
            map.put("isPending", myMembership.isPresent() && "PENDING".equalsIgnoreCase(myMembership.get().getStatus()));
            map.put("isAdmin", myMembership.isPresent() && "ADMIN".equalsIgnoreCase(myMembership.get().getRole()) && "ACTIVE".equalsIgnoreCase(myMembership.get().getStatus()));

            result.add(map);
        }

        return result;
    }

    public Map<String, Object> getGroupDetail(Long groupId, Long currentUserId) {
        StudyGroup g = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException("Study group not found", HttpStatus.NOT_FOUND));

        long memberCount = groupMemberRepository.countByGroupIdAndStatus(g.getId(), "ACTIVE");
        Optional<GroupMember> myMembership = groupMemberRepository.findByGroupIdAndUserId(g.getId(), currentUserId);

        Map<String, Object> map = new HashMap<>();
        map.put("group", g);
        map.put("memberCount", memberCount);
        map.put("isMember", myMembership.isPresent() && "ACTIVE".equalsIgnoreCase(myMembership.get().getStatus()));
        map.put("isPending", myMembership.isPresent() && "PENDING".equalsIgnoreCase(myMembership.get().getStatus()));
        map.put("isAdmin", myMembership.isPresent() && "ADMIN".equalsIgnoreCase(myMembership.get().getRole()) && "ACTIVE".equalsIgnoreCase(myMembership.get().getStatus()));

        // Members list
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        List<Map<String, Object>> memberDetails = new ArrayList<>();
        for (GroupMember m : members) {
            Map<String, Object> mmap = new HashMap<>();
            mmap.put("id", m.getId());
            mmap.put("userId", m.getUserId());
            mmap.put("role", m.getRole());
            mmap.put("status", m.getStatus());
            mmap.put("joinedAt", m.getJoinedAt());
            userRepository.findById(m.getUserId()).ifPresent(u -> {
                mmap.put("fullName", u.getFullName());
                mmap.put("email", u.getEmail());
                mmap.put("photoUrl", u.getPhotoUrl());
                mmap.put("level", u.getLevel());
                mmap.put("studyYear", u.getStudyYear());
                mmap.put("branch", u.getBranch());
            });
            memberDetails.add(mmap);
        }
        map.put("members", memberDetails);

        // Sessions
        map.put("sessions", groupSessionRepository.findByGroupIdOrderByScheduledTimeAsc(groupId));

        // Resources
        map.put("resources", groupResourceRepository.findByGroupIdOrderByCreatedAtDesc(groupId));

        // Progress tracker: group attendance rate & group quiz average
        double attendanceRate = calculateGroupAttendanceRate(groupId);
        double quizAverage = calculateGroupQuizAverage(groupId);
        map.put("attendanceRate", attendanceRate);
        map.put("quizAverage", quizAverage);

        return map;
    }

    @Transactional
    public GroupMember joinGroup(Long groupId, Long userId) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException("Group not found", HttpStatus.NOT_FOUND));

        Optional<GroupMember> existing = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (existing.isPresent()) {
            GroupMember m = existing.get();
            if ("ACTIVE".equalsIgnoreCase(m.getStatus())) {
                throw new ApiException("You are already an active member of this group", HttpStatus.BAD_REQUEST);
            }
            if ("PENDING".equalsIgnoreCase(m.getStatus())) {
                throw new ApiException("Your join request is already pending approval", HttpStatus.BAD_REQUEST);
            }
        }

        long activeCount = groupMemberRepository.countByGroupIdAndStatus(groupId, "ACTIVE");
        if (activeCount >= group.getMemberLimit()) {
            throw new ApiException("Group has reached its member limit (" + group.getMemberLimit() + ")", HttpStatus.BAD_REQUEST);
        }

        boolean isPublic = "PUBLIC".equalsIgnoreCase(group.getVisibility());
        String status = isPublic ? "ACTIVE" : "PENDING";

        GroupMember member = existing.orElse(GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role("MEMBER")
                .joinedAt(LocalDateTime.now())
                .build());

        member.setStatus(status);
        member.setRole("MEMBER");
        GroupMember saved = groupMemberRepository.save(member);

        if (!isPublic) {
            // Notify group admins
            List<GroupMember> admins = groupMemberRepository.findByGroupIdAndStatus(groupId, "ACTIVE")
                    .stream().filter(m -> "ADMIN".equalsIgnoreCase(m.getRole())).collect(Collectors.toList());
            User user = userRepository.findById(userId).orElse(null);
            for (GroupMember admin : admins) {
                notificationService.sendNotification(
                        admin.getUserId(),
                        "Group Join Request",
                        (user != null ? user.getFullName() : "A student") + " requested to join " + group.getName(),
                        "GROUP",
                        group.getId()
                );
            }
        }

        return saved;
    }

    @Transactional
    public void updateMemberRoleOrStatus(Long groupId, Long adminUserId, Long targetUserId, String action) {
        // Verify caller is admin of group or SUPER_ADMIN
        GroupMember caller = groupMemberRepository.findByGroupIdAndUserId(groupId, adminUserId)
                .orElseThrow(() -> new ApiException("You are not a member of this group", HttpStatus.FORBIDDEN));

        User user = userRepository.findById(adminUserId).orElse(null);
        boolean isSuperAdmin = user != null && user.getRole() == Role.SUPER_ADMIN;

        if (!"ADMIN".equalsIgnoreCase(caller.getRole()) && !isSuperAdmin) {
            throw new ApiException("Only group admins can manage group members", HttpStatus.FORBIDDEN);
        }

        GroupMember target = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ApiException("Target member not found in group", HttpStatus.NOT_FOUND));

        if ("APPROVE".equalsIgnoreCase(action)) {
            target.setStatus("ACTIVE");
            groupMemberRepository.save(target);
            notificationService.sendNotification(
                    targetUserId,
                    "Group Join Approved",
                    "Your request to join the group has been approved!",
                    "GROUP",
                    groupId
            );
        } else if ("REJECT".equalsIgnoreCase(action) || "REMOVE".equalsIgnoreCase(action)) {
            groupMemberRepository.delete(target);
        } else if ("PROMOTE".equalsIgnoreCase(action)) {
            target.setRole("ADMIN");
            groupMemberRepository.save(target);
        } else {
            throw new ApiException("Unknown action: " + action, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public GroupSession scheduleGroupSession(Long groupId, Long userId, GroupSessionCreateRequest req) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException("Group not found", HttpStatus.NOT_FOUND));

        GroupMember caller = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ApiException("You are not a member of this group", HttpStatus.FORBIDDEN));

        if (!"ADMIN".equalsIgnoreCase(caller.getRole())) {
            throw new ApiException("Only group admins can schedule sessions", HttpStatus.FORBIDDEN);
        }
        textModerationService.check(userId, "GROUP_SESSION", req.getTitle(), req.getDescription());

        LocalDateTime startTime = req.getScheduledTime() != null ? req.getScheduledTime() : LocalDateTime.now().plusDays(1).withHour(19).withMinute(0);
        int weeks = req.getRecurrenceWeeks() != null ? Math.min(12, Math.max(0, req.getRecurrenceWeeks())) : 0;

        GroupSession firstSession = null;
        for (int i = 0; i <= weeks; i++) {
            LocalDateTime sessionTime = startTime.plusWeeks(i);
            GroupSession s = GroupSession.builder()
                    .groupId(groupId)
                    .title(req.getTitle() + (weeks > 0 ? " (Week " + (i + 1) + ")" : ""))
                    .description(req.getDescription())
                    .scheduledTime(sessionTime)
                    .durationMinutes(req.getDurationMinutes() != null ? req.getDurationMinutes() : 60)
                    .mode(req.getMode() != null ? req.getMode() : "ONLINE")
                    .meetingLink(req.getMeetingLink() != null ? req.getMeetingLink() : "https://meet.google.com/group-" + UUID.randomUUID().toString().substring(0, 8))
                    .location(req.getLocation())
                    .recurrenceWeeks(weeks)
                    .createdBy(userId)
                    .createdAt(LocalDateTime.now())
                    .build();
            GroupSession saved = groupSessionRepository.save(s);
            if (firstSession == null) firstSession = saved;
        }

        // Notify active members
        List<GroupMember> activeMembers = groupMemberRepository.findByGroupIdAndStatus(groupId, "ACTIVE");
        for (GroupMember m : activeMembers) {
            if (!m.getUserId().equals(userId)) {
                notificationService.sendNotification(
                        m.getUserId(),
                        "New Group Session",
                        "New study session scheduled in " + group.getName() + ": " + req.getTitle(),
                        "GROUP",
                        groupId
                );
            }
        }

        return firstSession;
    }

    @Transactional
    public SessionAttendance checkInSession(Long sessionId, Long userId) {
        GroupSession session = groupSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException("Session not found", HttpStatus.NOT_FOUND));

        // Check user is active member of this group
        boolean isMember = groupMemberRepository.existsByGroupIdAndUserIdAndStatus(session.getGroupId(), userId, "ACTIVE");
        if (!isMember) {
            throw new ApiException("You must be an active group member to check into this session", HttpStatus.FORBIDDEN);
        }

        Optional<SessionAttendance> existing = sessionAttendanceRepository.findByGroupSessionIdAndUserId(sessionId, userId);
        SessionAttendance att;
        if (existing.isPresent()) {
            att = existing.get();
            if (Boolean.TRUE.equals(att.getCheckedIn())) {
                return att; // Already checked in
            }
            att.setCheckedIn(true);
            att.setCheckedInAt(LocalDateTime.now());
        } else {
            att = SessionAttendance.builder()
                    .groupSessionId(sessionId)
                    .userId(userId)
                    .checkedIn(true)
                    .checkedInAt(LocalDateTime.now())
                    .markedByAdmin(false)
                    .build();
        }

        SessionAttendance saved = sessionAttendanceRepository.save(att);

        // XP rules: attend a group session +10
        gamificationService.awardXp(userId, 10, "Attended group session: " + session.getTitle());

        return saved;
    }

    @Transactional
    public void markAttendanceSheet(Long sessionId, Long adminUserId, List<Long> attendedUserIds) {
        GroupSession session = groupSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException("Session not found", HttpStatus.NOT_FOUND));

        GroupMember caller = groupMemberRepository.findByGroupIdAndUserId(session.getGroupId(), adminUserId)
                .orElseThrow(() -> new ApiException("You are not in this group", HttpStatus.FORBIDDEN));

        if (!"ADMIN".equalsIgnoreCase(caller.getRole())) {
            throw new ApiException("Only group admins can mark the attendance sheet", HttpStatus.FORBIDDEN);
        }

        List<GroupMember> members = groupMemberRepository.findByGroupIdAndStatus(session.getGroupId(), "ACTIVE");
        for (GroupMember m : members) {
            boolean present = attendedUserIds != null && attendedUserIds.contains(m.getUserId());
            Optional<SessionAttendance> attOpt = sessionAttendanceRepository.findByGroupSessionIdAndUserId(sessionId, m.getUserId());
            SessionAttendance att = attOpt.orElse(SessionAttendance.builder()
                    .groupSessionId(sessionId)
                    .userId(m.getUserId())
                    .build());
            att.setCheckedIn(present);
            att.setMarkedByAdmin(true);
            if (present && att.getCheckedInAt() == null) {
                att.setCheckedInAt(LocalDateTime.now());
                gamificationService.awardXp(m.getUserId(), 10, "Attended group session: " + session.getTitle());
            }
            sessionAttendanceRepository.save(att);
        }
    }

    @Transactional
    public GroupResource addResource(Long groupId, Long userId, GroupResourceCreateRequest req) {
        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(groupId, userId, "ACTIVE")) {
            throw new ApiException("You must be an active group member to share resources", HttpStatus.FORBIDDEN);
        }
        textModerationService.check(userId, "GROUP_RESOURCE", req.getTitle());
        String resourceUrl = req.getUrlOrPath().trim();
        if ("IMAGE".equalsIgnoreCase(req.getResourceType())
                || resourceUrl.matches("(?i).*\\.(?:png|jpe?g|gif|webp|svg)(?:[?#].*)?$")) {
            throw new ApiException("Image files cannot be shared in study group resources.", HttpStatus.BAD_REQUEST);
        }
        GroupResource resource = GroupResource.builder()
                .groupId(groupId)
                .title(req.getTitle().trim())
                .resourceType(req.getResourceType().toUpperCase().trim())
                .urlOrPath(resourceUrl)
                .uploadedBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
        return groupResourceRepository.save(resource);
    }

    public List<GroupMessage> getGroupMessages(Long groupId) {
        return groupMessageRepository.findByGroupIdOrderByCreatedAtAsc(groupId);
    }

    @Transactional
    public GroupMessage postGroupMessage(Long groupId, Long userId, String content) {
        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(groupId, userId, "ACTIVE")) {
            throw new ApiException("You must be an active group member to send messages", HttpStatus.FORBIDDEN);
        }
        textModerationService.check(userId, "GROUP_CHAT", content);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        GroupMessage msg = GroupMessage.builder()
                .groupId(groupId)
                .senderId(userId)
                .senderName(user.getFullName())
                .senderPhoto(user.getPhotoUrl())
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        GroupMessage saved = groupMessageRepository.save(msg);

        // Broadcast to WebSocket /topic/groups/{id}
        try {
            messagingTemplate.convertAndSend("/topic/groups/" + groupId, saved);
        } catch (Exception e) {
            // Ignore if websocket not connected
        }

        return saved;
    }

    public double calculateGroupAttendanceRate(Long groupId) {
        List<GroupSession> sessions = groupSessionRepository.findByGroupIdOrderByScheduledTimeAsc(groupId);
        if (sessions.isEmpty()) return 100.0;
        long totalPossible = 0;
        long totalAttended = 0;
        for (GroupSession s : sessions) {
            List<SessionAttendance> attList = sessionAttendanceRepository.findByGroupSessionId(s.getId());
            totalPossible += Math.max(1, attList.size());
            totalAttended += attList.stream().filter(a -> Boolean.TRUE.equals(a.getCheckedIn())).count();
        }
        if (totalPossible == 0) return 100.0;
        return Math.round(((double) totalAttended / totalPossible * 100.0) * 10.0) / 10.0;
    }

    public double calculateGroupQuizAverage(Long groupId) {
        List<Test> tests = testRepository.findByGroupId(groupId);
        if (tests.isEmpty()) return 85.0; // default benchmark
        double sum = 0.0;
        int count = 0;
        for (Test t : tests) {
            List<TestAttempt> attempts = testAttemptRepository.findByTestIdOrderByScoreDescTimeTakenSecondsAsc(t.getId());
            for (TestAttempt a : attempts) {
                if (a.getPercentage() != null) {
                    sum += a.getPercentage();
                    count++;
                }
            }
        }
        if (count == 0) return 85.0;
        return Math.round((sum / count) * 10.0) / 10.0;
    }
}
