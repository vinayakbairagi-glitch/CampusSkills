package com.campusskills.seed;

import com.campusskills.entity.GroupMember;
import com.campusskills.entity.Project;
import com.campusskills.entity.ProjectMember;
import com.campusskills.entity.Role;
import com.campusskills.entity.StudyGroup;
import com.campusskills.entity.User;
import com.campusskills.repository.GroupMemberRepository;
import com.campusskills.repository.ProjectMemberRepository;
import com.campusskills.repository.ProjectRepository;
import com.campusskills.repository.StudyGroupRepository;
import com.campusskills.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Seeds public subject-wise study groups and starter project ideas in MySQL.
 * Existing group memberships and project applications are never overwritten.
 */
@Component
@Profile("mysql")
@Order(20)
public class CommunityContentSeeder implements CommandLineRunner {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final String adminEmail;

    public CommunityContentSeeder(
            StudyGroupRepository studyGroupRepository,
            GroupMemberRepository groupMemberRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            @Value("${campusskills.admin.email}") String adminEmail) {
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.adminEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        User admin = userRepository.findByEmail(adminEmail)
                .filter(user -> user.getRole() == Role.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "The CampusSkills administrator must be initialized before community content is seeded."));

        JsonNode content;
        try (InputStream input = new ClassPathResource("seed/community-content.json").getInputStream()) {
            content = objectMapper.readTree(input);
        }

        int createdGroups = 0;
        int createdProjects = 0;

        JsonNode groups = content.path("groups");
        if (!groups.isArray()) {
            throw new IllegalStateException("Community seed file is missing its groups array.");
        }

        for (JsonNode item : groups) {
            String name = requiredText(item, "name");
            String subject = requiredText(item, "subject");
            String category = requiredText(item, "category").toUpperCase(Locale.ROOT);

            StudyGroup group = studyGroupRepository.findFirstByNameAndSubjectAndCategory(name, subject, category)
                    .orElseGet(() -> studyGroupRepository.save(StudyGroup.builder()
                            .name(name)
                            .subject(subject)
                            .category(category)
                            .branch(null)
                            .studyYear(null)
                            .timing(requiredText(item, "timing"))
                            .mode(requiredText(item, "mode").toUpperCase(Locale.ROOT))
                            .memberLimit(item.path("memberLimit").asInt(30))
                            .visibility("PUBLIC")
                            .createdBy(admin.getId())
                            .description(requiredText(item, "description"))
                            .createdAt(LocalDateTime.now())
                            .build()));

            if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(group.getId(), admin.getId(), "ACTIVE")) {
                groupMemberRepository.save(GroupMember.builder()
                        .groupId(group.getId())
                        .userId(admin.getId())
                        .role("ADMIN")
                        .status("ACTIVE")
                        .joinedAt(LocalDateTime.now())
                        .build());
            }
            if (group.getCreatedAt() != null
                    && group.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                createdGroups++;
            }
        }

        JsonNode projects = content.path("projects");
        if (!projects.isArray()) {
            throw new IllegalStateException("Community seed file is missing its projects array.");
        }

        for (JsonNode item : projects) {
            String title = requiredText(item, "title");
            Project project = projectRepository.findFirstByTitleAndLeaderId(title, admin.getId())
                    .orElseGet(() -> projectRepository.save(Project.builder()
                            .leaderId(admin.getId())
                            .title(title)
                            .description(requiredText(item, "description"))
                            .requiredSkills(requiredText(item, "requiredSkills"))
                            .rolesNeeded(requiredText(item, "rolesNeeded"))
                            .teamSizeCap(item.path("teamSizeCap").asInt(4))
                            .status("RECRUITING")
                            .createdAt(LocalDateTime.now())
                            .build()));

            if (!projectMemberRepository.existsByProjectIdAndUserIdAndStatus(
                    project.getId(), admin.getId(), "ACCEPTED")) {
                projectMemberRepository.save(ProjectMember.builder()
                        .projectId(project.getId())
                        .userId(admin.getId())
                        .roleApplied("Project Curator")
                        .message("Starter project idea; students can apply to form a team.")
                        .status("ACCEPTED")
                        .joinedAt(LocalDateTime.now())
                        .build());
            }
            if (project.getCreatedAt() != null
                    && project.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                createdProjects++;
            }
        }

        System.out.println("CampusSkills community content ready: "
                + groups.size() + " study groups and " + projects.size()
                + " project ideas available (" + createdGroups + " groups and "
                + createdProjects + " projects newly added).");
    }

    private String requiredText(JsonNode node, String field) {
        String value = node.path(field).asText("").trim();
        if (value.isEmpty()) {
            throw new IllegalStateException("Missing required community content field: " + field);
        }
        return value;
    }
}
