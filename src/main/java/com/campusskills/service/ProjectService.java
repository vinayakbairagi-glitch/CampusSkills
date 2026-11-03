package com.campusskills.service;

import com.campusskills.dto.*;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;
    private final TextModerationService textModerationService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          ProjectTaskRepository projectTaskRepository,
                          RatingRepository ratingRepository,
                          UserRepository userRepository,
                          GamificationService gamificationService,
                          NotificationService notificationService,
                          TextModerationService textModerationService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
        this.textModerationService = textModerationService;
    }

    @Transactional
    public Project createProject(Long userId, ProjectCreateRequest req) {
        textModerationService.check(userId, "PROJECT", req.getTitle(), req.getDescription(),
                req.getRequiredSkills(), req.getRolesNeeded());
        Project project = Project.builder()
                .leaderId(userId)
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .requiredSkills(req.getRequiredSkills())
                .rolesNeeded(req.getRolesNeeded())
                .teamSizeCap(req.getTeamSizeCap() != null ? req.getTeamSizeCap() : 4)
                .deadline(req.getDeadline())
                .status("RECRUITING")
                .createdAt(LocalDateTime.now())
                .build();

        Project saved = projectRepository.save(project);

        // Leader is the first accepted member
        ProjectMember leaderMember = ProjectMember.builder()
                .projectId(saved.getId())
                .userId(userId)
                .roleApplied("Project Leader")
                .message("Creator & Project Lead")
                .status("ACCEPTED")
                .joinedAt(LocalDateTime.now())
                .build();
        projectMemberRepository.save(leaderMember);

        return saved;
    }

    public List<Map<String, Object>> getAllProjects(Long currentUserId) {
        List<Project> list = projectRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Project p : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("leaderId", p.getLeaderId());
            map.put("title", p.getTitle());
            map.put("description", p.getDescription());
            map.put("requiredSkills", p.getRequiredSkills());
            map.put("rolesNeeded", p.getRolesNeeded());
            map.put("teamSizeCap", p.getTeamSizeCap());
            map.put("deadline", p.getDeadline());
            map.put("status", p.getStatus());
            map.put("createdAt", p.getCreatedAt());

            long acceptedCount = projectMemberRepository.countByProjectIdAndStatus(p.getId(), "ACCEPTED");
            map.put("acceptedMembersCount", acceptedCount);

            Optional<ProjectMember> myApp = projectMemberRepository.findByProjectIdAndUserId(p.getId(), currentUserId);
            map.put("isLeader", p.getLeaderId().equals(currentUserId));
            map.put("isMember", myApp.isPresent() && "ACCEPTED".equalsIgnoreCase(myApp.get().getStatus()));
            map.put("isPending", myApp.isPresent() && "PENDING".equalsIgnoreCase(myApp.get().getStatus()));

            userRepository.findById(p.getLeaderId()).ifPresent(u -> {
                map.put("leaderName", u.getFullName());
                map.put("leaderPhoto", u.getPhotoUrl());
            });

            // Progress %
            long totalTasks = projectTaskRepository.countByProjectId(p.getId());
            long completedTasks = projectTaskRepository.countByProjectIdAndIsCompletedTrue(p.getId());
            double progress = totalTasks > 0 ? Math.round(((double) completedTasks / totalTasks) * 100.0) : 0.0;
            map.put("progressPercent", progress);

            result.add(map);
        }
        return result;
    }

    public Map<String, Object> getProjectDetail(Long projectId, Long currentUserId) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        Map<String, Object> map = new HashMap<>();
        map.put("project", p);
        map.put("isLeader", p.getLeaderId().equals(currentUserId));

        userRepository.findById(p.getLeaderId()).ifPresent(u -> {
            map.put("leaderName", u.getFullName());
            map.put("leaderPhoto", u.getPhotoUrl());
        });

        // Members list
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        List<Map<String, Object>> memberDetails = new ArrayList<>();
        for (ProjectMember m : members) {
            Map<String, Object> mmap = new HashMap<>();
            mmap.put("id", m.getId());
            mmap.put("userId", m.getUserId());
            mmap.put("roleApplied", m.getRoleApplied());
            mmap.put("message", m.getMessage());
            mmap.put("status", m.getStatus());
            mmap.put("joinedAt", m.getJoinedAt());
            userRepository.findById(m.getUserId()).ifPresent(u -> {
                mmap.put("fullName", u.getFullName());
                mmap.put("email", u.getEmail());
                mmap.put("photoUrl", u.getPhotoUrl());
                mmap.put("branch", u.getBranch());
                mmap.put("studyYear", u.getStudyYear());
            });
            memberDetails.add(mmap);
        }
        map.put("members", memberDetails);

        // Tasks
        List<ProjectTask> tasks = projectTaskRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
        map.put("tasks", tasks);

        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> Boolean.TRUE.equals(t.getIsCompleted())).count();
        double progress = totalTasks > 0 ? Math.round(((double) completedTasks / totalTasks) * 100.0) : 0.0;
        map.put("progressPercent", progress);

        return map;
    }

    @Transactional
    public ProjectMember applyToProject(Long projectId, Long userId, ProjectApplyRequest req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (project.getLeaderId().equals(userId)) {
            throw new ApiException("You are already the leader of this project", HttpStatus.BAD_REQUEST);
        }

        Optional<ProjectMember> existing = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        if (existing.isPresent()) {
            throw new ApiException("You have already applied to this project", HttpStatus.BAD_REQUEST);
        }

        long acceptedCount = projectMemberRepository.countByProjectIdAndStatus(projectId, "ACCEPTED");
        if (acceptedCount >= project.getTeamSizeCap()) {
            throw new ApiException("This project team is already full", HttpStatus.BAD_REQUEST);
        }
        textModerationService.check(userId, "PROJECT_APPLICATION", req.getRoleApplied(), req.getMessage());

        ProjectMember member = ProjectMember.builder()
                .projectId(projectId)
                .userId(userId)
                .roleApplied(req.getRoleApplied())
                .message(req.getMessage())
                .status("PENDING")
                .joinedAt(LocalDateTime.now())
                .build();

        ProjectMember saved = projectMemberRepository.save(member);

        // Notify project leader
        User applicant = userRepository.findById(userId).orElse(null);
        notificationService.sendNotification(
                project.getLeaderId(),
                "New Project Application",
                (applicant != null ? applicant.getFullName() : "A student") + " applied for " + req.getRoleApplied() + " on " + project.getTitle(),
                "GROUP",
                project.getId()
        );

        return saved;
    }

    @Transactional
    public ProjectMember actionApplication(Long projectId, Long leaderUserId, Long memberId, String action) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (!project.getLeaderId().equals(leaderUserId)) {
            throw new ApiException("Only the project leader can manage applications", HttpStatus.FORBIDDEN);
        }

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException("Application not found", HttpStatus.NOT_FOUND));

        if ("ACCEPT".equalsIgnoreCase(action)) {
            long acceptedCount = projectMemberRepository.countByProjectIdAndStatus(projectId, "ACCEPTED");
            if (acceptedCount >= project.getTeamSizeCap()) {
                throw new ApiException("Team size cap (" + project.getTeamSizeCap() + ") reached", HttpStatus.BAD_REQUEST);
            }
            member.setStatus("ACCEPTED");

            if (acceptedCount + 1 >= project.getTeamSizeCap()) {
                project.setStatus("IN_PROGRESS");
                projectRepository.save(project);
            }

            notificationService.sendNotification(
                    member.getUserId(),
                    "Project Application Accepted!",
                    "Your application for " + project.getTitle() + " has been accepted!",
                    "GROUP",
                    projectId
            );
        } else if ("REJECT".equalsIgnoreCase(action)) {
            member.setStatus("REJECTED");
        } else {
            throw new ApiException("Invalid action. Must be ACCEPT or REJECT", HttpStatus.BAD_REQUEST);
        }

        return projectMemberRepository.save(member);
    }

    @Transactional
    public ProjectTask createTask(Long projectId, Long leaderUserId, ProjectTaskCreateRequest req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (!project.getLeaderId().equals(leaderUserId)) {
            throw new ApiException("Only the project leader can assign tasks", HttpStatus.FORBIDDEN);
        }
        textModerationService.check(leaderUserId, "PROJECT_TASK", req.getTitle(), req.getDescription());

        ProjectTask task = ProjectTask.builder()
                .projectId(projectId)
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .assigneeId(req.getAssigneeId())
                .deadline(req.getDeadline())
                .isCompleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        return projectTaskRepository.save(task);
    }

    @Transactional
    public ProjectTask toggleTask(Long taskId, Long userId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException("Task not found", HttpStatus.NOT_FOUND));

        boolean wasCompleted = Boolean.TRUE.equals(task.getIsCompleted());
        task.setIsCompleted(!wasCompleted);
        task.setCompletedAt(!wasCompleted ? LocalDateTime.now() : null);

        return projectTaskRepository.save(task);
    }

    @Transactional
    public Project completeProject(Long projectId, Long leaderUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (!project.getLeaderId().equals(leaderUserId)) {
            throw new ApiException("Only the project leader can complete the project", HttpStatus.FORBIDDEN);
        }

        project.setStatus("COMPLETED");
        Project saved = projectRepository.save(project);

        // XP rules: project completed +40 for everyone
        List<ProjectMember> acceptedMembers = projectMemberRepository.findByProjectId(projectId).stream()
                .filter(m -> "ACCEPTED".equalsIgnoreCase(m.getStatus()))
                .collect(Collectors.toList());

        for (ProjectMember m : acceptedMembers) {
            gamificationService.awardXp(m.getUserId(), 40, "Completed project: " + project.getTitle());
            notificationService.sendNotification(
                    m.getUserId(),
                    "Project Completed!",
                    "The project " + project.getTitle() + " has been completed! You earned +40 XP. Don't forget to rate your teammates!",
                    "BADGE",
                    projectId
            );
        }

        return saved;
    }

    @Transactional
    public Rating rateTeammate(Long projectId, Long currentUserId, RatingRequest req) {
        textModerationService.check(currentUserId, "PROJECT_REVIEW", req.getComment());
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException("Project not found", HttpStatus.NOT_FOUND));

        if (!"COMPLETED".equalsIgnoreCase(project.getStatus())) {
            throw new ApiException("You can only rate teammates on completed projects", HttpStatus.BAD_REQUEST);
        }

        if (req.getRatedUserId() == null) {
            throw new ApiException("Target teammate userId is required", HttpStatus.BAD_REQUEST);
        }

        if (req.getRatedUserId().equals(currentUserId)) {
            throw new ApiException("You cannot rate yourself", HttpStatus.BAD_REQUEST);
        }

        boolean isMember1 = projectMemberRepository.existsByProjectIdAndUserIdAndStatus(projectId, currentUserId, "ACCEPTED");
        boolean isMember2 = projectMemberRepository.existsByProjectIdAndUserIdAndStatus(projectId, req.getRatedUserId(), "ACCEPTED");

        if (!isMember1 || !isMember2) {
            throw new ApiException("Both users must be members of the project", HttpStatus.FORBIDDEN);
        }

        // Teammates rate each other once
        boolean alreadyRated = ratingRepository.existsByProjectIdAndRaterId(projectId, currentUserId);
        if (alreadyRated) {
            throw new ApiException("You have already submitted a rating for this project", HttpStatus.CONFLICT);
        }

        Rating rating = Rating.builder()
                .projectId(projectId)
                .raterId(currentUserId)
                .ratedUserId(req.getRatedUserId())
                .rating(req.getRating())
                .comment(req.getComment())
                .context("PROJECT")
                .createdAt(LocalDateTime.now())
                .build();

        return ratingRepository.save(rating);
    }
}
