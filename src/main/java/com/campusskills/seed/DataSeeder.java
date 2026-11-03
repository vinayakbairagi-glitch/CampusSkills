package com.campusskills.seed;

import com.campusskills.entity.*;
import com.campusskills.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@Profile("!mysql & !vercel")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final AvailabilityRepository availabilityRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final SessionAttendanceRepository sessionAttendanceRepository;
    private final GroupResourceRepository groupResourceRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final DoubtRepository doubtRepository;
    private final DoubtAnswerRepository doubtAnswerRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final PlacementProfileRepository placementProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(UserRepository userRepository,
                      SkillRepository skillRepository,
                      UserSkillRepository userSkillRepository,
                      AvailabilityRepository availabilityRepository,
                      StudyGroupRepository studyGroupRepository,
                      GroupMemberRepository groupMemberRepository,
                      GroupSessionRepository groupSessionRepository,
                      SessionAttendanceRepository sessionAttendanceRepository,
                      GroupResourceRepository groupResourceRepository,
                      GroupMessageRepository groupMessageRepository,
                      DoubtRepository doubtRepository,
                      DoubtAnswerRepository doubtAnswerRepository,
                      ProjectRepository projectRepository,
                      ProjectMemberRepository projectMemberRepository,
                      ProjectTaskRepository projectTaskRepository,
                      QuestionBankRepository questionBankRepository,
                      QuestionRepository questionRepository,
                      TestRepository testRepository,
                      TestAttemptRepository testAttemptRepository,
                      StudentAnswerRepository studentAnswerRepository,
                      PlacementProfileRepository placementProfileRepository,
                      PasswordEncoder passwordEncoder,
                      ObjectMapper objectMapper,
                      @Value("${campusskills.admin.email:Campus_admin@campus.edu.in}") String adminEmail,
                      @Value("${campusskills.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.availabilityRepository = availabilityRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupSessionRepository = groupSessionRepository;
        this.sessionAttendanceRepository = sessionAttendanceRepository;
        this.groupResourceRepository = groupResourceRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.doubtRepository = doubtRepository;
        this.doubtAnswerRepository = doubtAnswerRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.questionBankRepository = questionBankRepository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.placementProfileRepository = placementProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            seedSwapCandidates(updateAdminAccount());
            seedAdditionalQuizSets();
            return; // Seed demo data on first start only
        }

        // 1. Create Users
        String initialAdminPassword = adminPassword.isBlank() ? "admin123" : adminPassword;
        User admin = createUser(normalizedAdminEmail(), "ADM001", initialAdminPassword, "Super Admin", Role.SUPER_ADMIN, UserStatus.VERIFIED, "Administration", 4, "System Administrator for CampusSkills", "BOTH", 10, 1200, "Campus Expert");
        User tpo = createUser("tpo@campus.edu", "TPO001", "tpo123", "Prof. Rajesh Sharma (TPO)", Role.TPO, UserStatus.VERIFIED, "Training & Placement", 4, "Head of Training & Placement Cell. Guiding students towards tech careers.", "BOTH", 15, 950, "Campus Expert");

        User ravi = createUser("ravi@campus.edu", "21CS042", "student123", "Ravi Kumar", Role.TUTOR, UserStatus.VERIFIED, "Computer Science", 3, "Passionate Java & Spring Boot developer. Solved 300+ LeetCode problems.", "BOTH", 5, 520, "Mentor");
        User priya = createUser("priya@campus.edu", "21CS089", "student123", "Priya Nair", Role.TUTOR, UserStatus.VERIFIED, "Computer Science", 3, "UI/UX enthusiast, Figma designer and React frontend builder.", "ONLINE", 4, 480, "Mentor");
        User arjun = createUser("arjun@campus.edu", "22IT015", "student123", "Arjun Verma", Role.STUDENT, UserStatus.VERIFIED, "Information Technology", 2, "Python scripting, data pipelines & SQL enthusiast.", "BOTH", 3, 210, "Contributor");
        User sneha = createUser("sneha@campus.edu", "22EC033", "student123", "Sneha Rao", Role.STUDENT, UserStatus.VERIFIED, "Electronics", 2, "Aptitude wizard & resume mentor. Preparing for software placements.", "ONLINE", 3, 190, "Contributor");
        User karan = createUser("karan@campus.edu", "20CS008", "student123", "Karan Malhotra", Role.TUTOR, UserStatus.VERIFIED, "Computer Science", 4, "Incoming SDE at top fintech. Conducting mock interviews & system design reviews.", "BOTH", 7, 850, "Campus Expert");
        User ananya = createUser("ananya@campus.edu", "23CS102", "student123", "Ananya Sen", Role.STUDENT, UserStatus.VERIFIED, "Computer Science", 1, "Eager first-year coder looking to learn full stack development.", "ONLINE", 3, 75, "Beginner");

        // Pending user for admin verification demo
        User rahul = createUser("rahul@campus.edu", "23CS155", "student123", "Rahul Das", Role.STUDENT, UserStatus.PENDING, "Computer Science", 1, "Newly registered first-year student awaiting college ID check.", "BOTH", 3, 0, "Beginner");

        // 2. Skills
        String[] skillNames = {"Java", "DSA", "Spring Boot", "React", "Python", "Figma", "UI/UX", "SQL", "Aptitude", "Git", "Mock Interview", "Resume Review"};
        List<Skill> skills = Arrays.stream(skillNames).map(name ->
                skillRepository.save(Skill.builder().name(name).category("TECH").createdBy(admin.getId()).createdAt(LocalDateTime.now()).build())
        ).toList();

        Skill javaSkill = skills.get(0);
        Skill dsaSkill = skills.get(1);
        Skill springSkill = skills.get(2);
        Skill reactSkill = skills.get(3);
        Skill pythonSkill = skills.get(4);
        Skill figmaSkill = skills.get(5);
        Skill uiuxSkill = skills.get(6);
        Skill sqlSkill = skills.get(7);
        Skill aptitudeSkill = skills.get(8);
        Skill gitSkill = skills.get(9);
        Skill mockIntSkill = skills.get(10);
        Skill resumeSkill = skills.get(11);

        // 3. User Skills (Teachable & Wish-list)
        // Ravi teaches Java (verified), DSA (verified), Spring Boot; wants Figma, React
        addUserSkill(ravi.getId(), javaSkill, "CAN_TEACH", "EXPERT", true);
        addUserSkill(ravi.getId(), dsaSkill, "CAN_TEACH", "ADVANCED", true);
        addUserSkill(ravi.getId(), springSkill, "CAN_TEACH", "ADVANCED", false);
        addUserSkill(ravi.getId(), figmaSkill, "WANT_TO_LEARN", "BEGINNER", false);
        addUserSkill(ravi.getId(), reactSkill, "WANT_TO_LEARN", "INTERMEDIATE", false);

        // Priya teaches Figma (verified), UI/UX (verified), React; wants Java, Python
        addUserSkill(priya.getId(), figmaSkill, "CAN_TEACH", "EXPERT", true);
        addUserSkill(priya.getId(), uiuxSkill, "CAN_TEACH", "EXPERT", true);
        addUserSkill(priya.getId(), reactSkill, "CAN_TEACH", "ADVANCED", false);
        addUserSkill(priya.getId(), javaSkill, "WANT_TO_LEARN", "BEGINNER", false);
        addUserSkill(priya.getId(), pythonSkill, "WANT_TO_LEARN", "BEGINNER", false);

        // Arjun teaches Python, SQL, Git; wants Spring Boot, DSA
        addUserSkill(arjun.getId(), pythonSkill, "CAN_TEACH", "ADVANCED", true);
        addUserSkill(arjun.getId(), sqlSkill, "CAN_TEACH", "INTERMEDIATE", false);
        addUserSkill(arjun.getId(), gitSkill, "CAN_TEACH", "ADVANCED", true);
        addUserSkill(arjun.getId(), springSkill, "WANT_TO_LEARN", "BEGINNER", false);
        addUserSkill(arjun.getId(), dsaSkill, "WANT_TO_LEARN", "INTERMEDIATE", false);

        // Sneha teaches Aptitude (verified), Resume Review; wants Mock Interview, SQL
        addUserSkill(sneha.getId(), aptitudeSkill, "CAN_TEACH", "EXPERT", true);
        addUserSkill(sneha.getId(), resumeSkill, "CAN_TEACH", "ADVANCED", true);
        addUserSkill(sneha.getId(), mockIntSkill, "WANT_TO_LEARN", "INTERMEDIATE", false);
        addUserSkill(sneha.getId(), sqlSkill, "WANT_TO_LEARN", "BEGINNER", false);

        // Karan teaches Mock Interview (verified), DSA; wants UI/UX, Java
        addUserSkill(karan.getId(), mockIntSkill, "CAN_TEACH", "EXPERT", true);
        addUserSkill(karan.getId(), dsaSkill, "CAN_TEACH", "EXPERT", true);
        addUserSkill(karan.getId(), uiuxSkill, "WANT_TO_LEARN", "BEGINNER", false);

        // Ananya teaches React; wants Aptitude, Java, Figma
        addUserSkill(ananya.getId(), reactSkill, "CAN_TEACH", "INTERMEDIATE", false);
        addUserSkill(ananya.getId(), aptitudeSkill, "WANT_TO_LEARN", "BEGINNER", false);
        addUserSkill(ananya.getId(), javaSkill, "WANT_TO_LEARN", "BEGINNER", false);
        addUserSkill(ananya.getId(), figmaSkill, "WANT_TO_LEARN", "BEGINNER", false);

        // 4. Availability slots
        addAvailability(ravi.getId(), "MONDAY", "18:00", "20:00", "BOTH");
        addAvailability(ravi.getId(), "WEDNESDAY", "18:00", "20:00", "ONLINE");
        addAvailability(ravi.getId(), "SATURDAY", "10:00", "13:00", "OFFLINE");

        addAvailability(priya.getId(), "TUESDAY", "17:00", "19:00", "ONLINE");
        addAvailability(priya.getId(), "THURSDAY", "18:00", "20:00", "ONLINE");
        addAvailability(priya.getId(), "SATURDAY", "11:00", "14:00", "BOTH");

        addAvailability(karan.getId(), "FRIDAY", "19:00", "21:00", "ONLINE");
        addAvailability(karan.getId(), "SUNDAY", "10:00", "12:00", "ONLINE");

        // 5. Study Groups (3 groups: 1 PLACEMENT, 1 PRIVATE, 1 SKILL)
        // Group 1: Seed group "DSA Placement Prep – Daily 7 PM" with 15 members
        StudyGroup dsaGroup = studyGroupRepository.save(StudyGroup.builder()
                .name("DSA Placement Prep – Daily 7 PM")
                .subject("DSA")
                .category("PLACEMENT")
                .branch("Computer Science")
                .studyYear(3)
                .timing("Daily 7:00 PM - 8:30 PM")
                .mode("HYBRID")
                .memberLimit(30)
                .visibility("PUBLIC")
                .createdBy(karan.getId())
                .description("Intensive daily DSA problem solving targeting Tier-1 product companies. Daily live coding, editorial discussion & interview patterns.")
                .createdAt(LocalDateTime.now().minusDays(20))
                .build());

        // Add 15 members to DSA group
        addMember(dsaGroup.getId(), karan.getId(), "ADMIN");
        addMember(dsaGroup.getId(), ravi.getId(), "MEMBER");
        addMember(dsaGroup.getId(), priya.getId(), "MEMBER");
        addMember(dsaGroup.getId(), arjun.getId(), "MEMBER");
        addMember(dsaGroup.getId(), sneha.getId(), "MEMBER");
        addMember(dsaGroup.getId(), ananya.getId(), "MEMBER");
        addMember(dsaGroup.getId(), tpo.getId(), "ADMIN");

        // Add varied peer profiles with real skills to swap.
        for (User candidate : seedSwapCandidates(admin.getId())) {
            addMember(dsaGroup.getId(), candidate.getId(), "MEMBER");
        }

        // Sessions for DSA group
        GroupSession dsaSession1 = groupSessionRepository.save(GroupSession.builder()
                .groupId(dsaGroup.getId())
                .title("Dynamic Programming: Knapsack & Grid Paths")
                .description("Mastering 1D and 2D DP state transitions with 5 LeetCode Medium/Hard problems.")
                .scheduledTime(LocalDateTime.now().plusDays(1).withHour(19).withMinute(0))
                .durationMinutes(90)
                .mode("ONLINE")
                .meetingLink("https://meet.google.com/dsa-daily-prep")
                .createdBy(karan.getId())
                .createdAt(LocalDateTime.now().minusDays(2))
                .build());

        // Attendance check-ins
        sessionAttendanceRepository.save(SessionAttendance.builder().groupSessionId(dsaSession1.getId()).userId(ravi.getId()).checkedIn(true).checkedInAt(LocalDateTime.now().minusHours(1)).build());
        sessionAttendanceRepository.save(SessionAttendance.builder().groupSessionId(dsaSession1.getId()).userId(priya.getId()).checkedIn(true).checkedInAt(LocalDateTime.now().minusHours(1)).build());

        // Resources
        groupResourceRepository.save(GroupResource.builder()
                .groupId(dsaGroup.getId())
                .title("Striver SDE Sheet Curated Solutions (PDF)")
                .resourceType("PDF")
                .urlOrPath("/uploads/striver_sde_sheet_curated.pdf")
                .uploadedBy(karan.getId())
                .createdAt(LocalDateTime.now().minusDays(5))
                .build());

        groupResourceRepository.save(GroupResource.builder()
                .groupId(dsaGroup.getId())
                .title("Graph BFS & DFS Visualization Video")
                .resourceType("YOUTUBE")
                .urlOrPath("https://youtube.com/watch?v=pcKY4hjDrxk")
                .uploadedBy(ravi.getId())
                .createdAt(LocalDateTime.now().minusDays(3))
                .build());

        // Chat messages for DSA group
        groupMessageRepository.save(GroupMessage.builder().groupId(dsaGroup.getId()).senderId(karan.getId()).senderName("Karan Malhotra").content("Welcome everyone! Tonight at 7 PM we will solve Coin Change 2 and Minimum Path Sum.").createdAt(LocalDateTime.now().minusHours(4)).build());
        groupMessageRepository.save(GroupMessage.builder().groupId(dsaGroup.getId()).senderId(ravi.getId()).senderName("Ravi Kumar").content("Awesome Karan! I have already pushed my Java solution to the shared repo.").createdAt(LocalDateTime.now().minusHours(3)).build());
        groupMessageRepository.save(GroupMessage.builder().groupId(dsaGroup.getId()).senderId(priya.getId()).senderName("Priya Nair").content("Joining via Google Meet! Looking forward to the recursion tree breakdown.").createdAt(LocalDateTime.now().minusHours(2)).build());

        // Group 2: "Full Stack Web Dev & Spring Boot" (PUBLIC, SKILL)
        StudyGroup webGroup = studyGroupRepository.save(StudyGroup.builder()
                .name("Full Stack Web Dev & Spring Boot")
                .subject("Spring Boot")
                .category("SKILL")
                .branch("Computer Science")
                .studyYear(2)
                .timing("Every Saturday 4:00 PM")
                .mode("ONLINE")
                .memberLimit(25)
                .visibility("PUBLIC")
                .createdBy(ravi.getId())
                .description("Hands-on microservices and React SPA building. Learn Spring Data JPA, JWT authentication and Vite.")
                .createdAt(LocalDateTime.now().minusDays(15))
                .build());
        addMember(webGroup.getId(), ravi.getId(), "ADMIN");
        addMember(webGroup.getId(), priya.getId(), "MEMBER");
        addMember(webGroup.getId(), arjun.getId(), "MEMBER");

        // Group 3: "Advanced Algorithms & Competitive Coding" (PRIVATE, EXAM)
        StudyGroup algoGroup = studyGroupRepository.save(StudyGroup.builder()
                .name("Advanced Algorithms & Competitive Coding")
                .subject("Algorithms")
                .category("EXAM")
                .branch("Computer Science")
                .studyYear(3)
                .timing("Sundays 6:00 PM")
                .mode("ONLINE")
                .memberLimit(12)
                .visibility("PRIVATE")
                .createdBy(karan.getId())
                .description("Private invite-only group for Codeforces Div 2 / ICPC regionals contestants. Focus on Segment Trees and Fenwick Trees.")
                .createdAt(LocalDateTime.now().minusDays(10))
                .build());
        addMember(algoGroup.getId(), karan.getId(), "ADMIN");
        addMember(algoGroup.getId(), ravi.getId(), "MEMBER");

        // 6. Doubts and Answers
        Doubt doubt1 = doubtRepository.save(Doubt.builder()
                .askerId(arjun.getId())
                .subject("Java")
                .topic("Collections")
                .title("Difference between HashMap and ConcurrentHashMap in Java?")
                .description("Why shouldn't we use Collections.synchronizedMap(hashMap) in multi-threaded microservices instead of ConcurrentHashMap? How does segment locking work?")
                .status("SOLVED")
                .createdAt(LocalDateTime.now().minusDays(3))
                .build());

        DoubtAnswer ans1 = doubtAnswerRepository.save(DoubtAnswer.builder()
                .doubtId(doubt1.getId())
                .authorId(ravi.getId())
                .authorName("Ravi Kumar")
                .authorYear(3)
                .content("In Java 8+, ConcurrentHashMap uses CAS (Compare-And-Swap) and synchronized blocks on bucket heads rather than locking the whole map! synchronizedMap locks the entire collection on every read/write, ruining concurrency throughput. ConcurrentHashMap allows lock-free volatile reads!")
                .isBest(true)
                .isVerified(true)
                .verifiedBy(karan.getId())
                .upvoteCount(6)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build());
        doubt1.setBestAnswerId(ans1.getId());
        doubtRepository.save(doubt1);

        Doubt doubt2 = doubtRepository.save(Doubt.builder()
                .askerId(ananya.getId())
                .subject("Spring Boot")
                .topic("Security")
                .title("How does Spring Security filter chain handle JWT validation before UsernamePasswordAuthenticationFilter?")
                .description("I am confused about whether JwtFilter should extend OncePerRequestFilter and how SecurityContextHolder receives the authenticated principal.")
                .status("OPEN")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build());

        doubtAnswerRepository.save(DoubtAnswer.builder()
                .doubtId(doubt2.getId())
                .authorId(ravi.getId())
                .authorName("Ravi Kumar")
                .authorYear(3)
                .content("Yes! OncePerRequestFilter guarantees single execution per request dispatch. In doFilterInternal, extract Bearer token, validate with jjwt parser, construct UsernamePasswordAuthenticationToken, and call SecurityContextHolder.getContext().setAuthentication(auth)!")
                .isBest(false)
                .isVerified(true)
                .verifiedBy(tpo.getId())
                .upvoteCount(4)
                .createdAt(LocalDateTime.now().minusHours(12))
                .build());

        // 7. Projects with Tasks
        Project project = projectRepository.save(Project.builder()
                .leaderId(ravi.getId())
                .title("CampusSkills Mobile Companion App")
                .description("Cross-platform React Native / PWA app providing push notifications for peer tutoring sessions, instant doubts alerts, and live study room audio.")
                .requiredSkills("React, TypeScript, Tailwind, REST API")
                .rolesNeeded("Frontend Lead, UI Designer, QA Engineer")
                .teamSizeCap(4)
                .deadline(LocalDate.now().plusDays(45))
                .status("IN_PROGRESS")
                .createdAt(LocalDateTime.now().minusDays(7))
                .build());

        projectMemberRepository.save(ProjectMember.builder().projectId(project.getId()).userId(ravi.getId()).roleApplied("Backend & Lead").status("ACCEPTED").joinedAt(LocalDateTime.now().minusDays(7)).build());
        projectMemberRepository.save(ProjectMember.builder().projectId(project.getId()).userId(priya.getId()).roleApplied("UI Designer").status("ACCEPTED").joinedAt(LocalDateTime.now().minusDays(6)).build());
        projectMemberRepository.save(ProjectMember.builder().projectId(project.getId()).userId(arjun.getId()).roleApplied("Frontend Dev").status("ACCEPTED").joinedAt(LocalDateTime.now().minusDays(5)).build());

        projectTaskRepository.save(ProjectTask.builder().projectId(project.getId()).title("Design Figma high-fidelity prototypes").assigneeId(priya.getId()).isCompleted(true).completedAt(LocalDateTime.now().minusDays(2)).createdAt(LocalDateTime.now().minusDays(6)).build());
        projectTaskRepository.save(ProjectTask.builder().projectId(project.getId()).title("Implement JWT Auth and Profile Screen").assigneeId(arjun.getId()).isCompleted(true).completedAt(LocalDateTime.now().minusDays(1)).createdAt(LocalDateTime.now().minusDays(5)).build());
        projectTaskRepository.save(ProjectTask.builder().projectId(project.getId()).title("Integrate STOMP WebSocket Live Chat").assigneeId(ravi.getId()).isCompleted(false).deadline(LocalDate.now().plusDays(10)).createdAt(LocalDateTime.now().minusDays(4)).build());
        projectTaskRepository.save(ProjectTask.builder().projectId(project.getId()).title("Write End-to-End Tests for Skill Swap").assigneeId(arjun.getId()).isCompleted(false).deadline(LocalDate.now().plusDays(15)).createdAt(LocalDateTime.now().minusDays(4)).build());

        // 8. Question Banks & 20+ Questions (Every type: MCQ, TRUE_FALSE, FILL_BLANK, NUMERIC, MATCH, SHORT)
        QuestionBank javaBank = questionBankRepository.save(QuestionBank.builder().title("Core Java & OOPs Mastery").subject("Java").topic("OOPs").createdBy(tpo.getId()).createdAt(LocalDateTime.now().minusDays(10)).build());
        QuestionBank collBank = questionBankRepository.save(QuestionBank.builder().title("Java Collections & Concurrency").subject("Java").topic("Collections").createdBy(tpo.getId()).createdAt(LocalDateTime.now().minusDays(10)).build());
        QuestionBank dsaBank = questionBankRepository.save(QuestionBank.builder().title("Data Structures & Algorithms").subject("DSA").topic("Algorithms").createdBy(karan.getId()).createdAt(LocalDateTime.now().minusDays(10)).build());
        QuestionBank aptBank = questionBankRepository.save(QuestionBank.builder().title("Quantitative Aptitude & Logic").subject("Aptitude").topic("Quantitative").createdBy(sneha.getId()).createdAt(LocalDateTime.now().minusDays(10)).build());

        // Java OOPs Questions
        createQuestion(javaBank.getId(), "Java", "OOPs", "MCQ", "EASY",
                "Which concept of Java is achieved when a subclass provides a specific implementation of a method already provided by its parent class?",
                Arrays.asList("Method Overloading", "Method Overriding", "Data Encapsulation", "Abstraction"),
                "Method Overriding", "Overriding allows runtime polymorphism where the subclass overrides parent method behavior.", 1.0, 0.25, null);

        createQuestion(javaBank.getId(), "Java", "OOPs", "TRUE_FALSE", "EASY",
                "Can an abstract class in Java have constructor methods?",
                Arrays.asList("True", "False"),
                "True", "Yes, abstract classes can have constructors invoked via super() by subclass constructors.", 1.0, 0.25, null);

        createQuestion(javaBank.getId(), "Java", "OOPs", "FILL_BLANK", "MEDIUM",
                "The Java keyword used to prevent a class from being inherited is _____.",
                null, "final|FINAL", "Marking a class final prevents other classes from extending it (e.g., java.lang.String).", 1.0, 0.0, null);

        createQuestion(javaBank.getId(), "Java", "OOPs", "MATCH", "HARD",
                "Match the OOP principle with its fundamental design intent:",
                null, "A-1,B-2,C-3", "Encapsulation binds data with code, Polymorphism allows multiple forms, Inheritance enables reuse.", 2.0, 0.5,
                "[{\"left\":\"Encapsulation\",\"right\":\"Data hiding & bundling\"},{\"left\":\"Polymorphism\",\"right\":\"Many forms via overriding\"},{\"left\":\"Inheritance\",\"right\":\"Code reusability\"}]");

        createQuestion(javaBank.getId(), "Java", "OOPs", "SHORT", "MEDIUM",
                "What is the default value of an uninitialized boolean instance variable in Java?",
                null, "false", "In Java, instance boolean variables default to false.", 1.0, 0.0, null);

        // Java Collections Questions
        createQuestion(collBank.getId(), "Java", "Collections", "MCQ", "MEDIUM",
                "Which Collection interface does NOT allow duplicate elements and guarantees elements are sorted in ascending natural order?",
                Arrays.asList("HashSet", "TreeSet", "LinkedHashSet", "ArrayList"),
                "TreeSet", "TreeSet is backed by a Red-Black tree and implements NavigableSet, guaranteeing sorted unique elements.", 1.0, 0.25, null);

        createQuestion(collBank.getId(), "Java", "Collections", "NUMERIC", "MEDIUM",
                "What is the default initial capacity of a standard java.util.HashMap in Java 8+?",
                null, "16", "The default initial capacity is 16, with a default load factor of 0.75.", 1.0, 0.0, null);

        createQuestion(collBank.getId(), "Java", "Collections", "TRUE_FALSE", "EASY",
                "ArrayList is synchronized by default in Java.",
                Arrays.asList("True", "False"),
                "False", "ArrayList is unsynchronized. For concurrent access, use CopyOnWriteArrayList or Collections.synchronizedList.", 1.0, 0.25, null);

        createQuestion(collBank.getId(), "Java", "Collections", "FILL_BLANK", "HARD",
                "In Java 8, when a HashMap bucket reaches a threshold of 8 elements and the array capacity is >= 64, the linked list is transformed into a _____.",
                null, "Red-Black Tree|Red Black Tree|tree|TreeNode", "Treeifying bucket elements reduces worst-case collision lookup time from O(n) to O(log n).", 1.0, 0.0, null);

        // DSA Questions
        createQuestion(dsaBank.getId(), "DSA", "Algorithms", "MCQ", "MEDIUM",
                "What is the average time complexity of QuickSort?",
                Arrays.asList("O(n)", "O(n log n)", "O(n^2)", "O(log n)"),
                "O(n log n)", "QuickSort exhibits O(n log n) average time complexity using divide-and-conquer partitioning.", 1.0, 0.25, null);

        createQuestion(dsaBank.getId(), "DSA", "Algorithms", "NUMERIC", "EASY",
                "If a binary tree has 7 nodes in a complete full binary tree configuration, what is its height (levels count)?",
                null, "3", "A full binary tree with 2^h - 1 = 7 nodes has height 3.", 1.0, 0.0, null);

        createQuestion(dsaBank.getId(), "DSA", "Algorithms", "MATCH", "HARD",
                "Match the data structure with its optimal worst-case search complexity:",
                null, "A-1,B-2,C-3", "Unsorted array search is O(n), AVL tree search is O(log n), and an ideal hash table lookup is O(1).", 2.0, 0.5,
                "[{\"left\":\"Unsorted Array\",\"right\":\"O(n)\"},{\"left\":\"AVL Tree\",\"right\":\"O(log n)\"},{\"left\":\"Hash Table (ideal)\",\"right\":\"O(1)\"}]");

        createQuestion(dsaBank.getId(), "DSA", "Algorithms", "SHORT", "EASY",
                "Which standard data structure operates on a LIFO (Last-In, First-Out) order?",
                null, "Stack", "A stack operates on Last-In, First-Out principle.", 1.0, 0.0, null);

        createQuestion(dsaBank.getId(), "DSA", "Algorithms", "TRUE_FALSE", "MEDIUM",
                "Dijkstra's algorithm functions correctly on graphs with negative weight edges.",
                Arrays.asList("True", "False"),
                "False", "Dijkstra assumes non-negative edge weights; Bellman-Ford should be used for graphs with negative weights.", 1.0, 0.25, null);

        // Aptitude Questions
        createQuestion(aptBank.getId(), "Aptitude", "Quantitative", "NUMERIC", "EASY",
                "A train running at 54 km/hr crosses a pole in 20 seconds. What is the length of the train in meters?",
                null, "300", "Speed = 54 * (5/18) = 15 m/s. Length = Speed * Time = 15 * 20 = 300 meters.", 1.0, 0.0, null);

        createQuestion(aptBank.getId(), "Aptitude", "Quantitative", "MCQ", "EASY",
                "If 12 men can finish a task in 8 days, how many men are required to complete the same task in 6 days?",
                Arrays.asList("14", "16", "18", "20"),
                "16", "Total man-days = 12 * 8 = 96. Men needed for 6 days = 96 / 6 = 16.", 1.0, 0.25, null);

        createQuestion(aptBank.getId(), "Aptitude", "Quantitative", "FILL_BLANK", "MEDIUM",
                "The next number in the sequence 2, 6, 12, 20, 30, _____ is:",
                null, "42", "Differences between consecutive terms are +4, +6, +8, +10, +12. 30 + 12 = 42.", 1.0, 0.0, null);

        createQuestion(aptBank.getId(), "Aptitude", "Quantitative", "NUMERIC", "MEDIUM",
                "A shopkeeper marks an article at 20% above cost price and allows a discount of 10%. What is his profit percentage?",
                null, "8", "CP = 100, MP = 120, SP = 120 * 0.9 = 108. Profit = 8%.", 1.0, 0.0, null);

        createQuestion(aptBank.getId(), "Aptitude", "Quantitative", "SHORT", "EASY",
                "What is the probability of rolling a prime number on a standard fair six-sided die? (Format as fraction like 1/2)",
                null, "1/2", "Prime numbers are {2, 3, 5} out of 6 possible outcomes = 3/6 = 1/2.", 1.0, 0.0, null);

        createQuestion(aptBank.getId(), "Aptitude", "Quantitative", "TRUE_FALSE", "EASY",
                "Every square is a rhombus.",
                Arrays.asList("True", "False"),
                "True", "A rhombus is a quadrilateral with four equal sides; a square meets this definition with four right angles.", 1.0, 0.25, null);

        // 9. Create Tests
        // 1. Java practice quiz
        Test javaPractice = testRepository.save(Test.builder()
                .title("Java OOPs Basics Practice Quiz")
                .description("Quick 15-minute practice quiz covering inheritance, polymorphism and abstract classes.")
                .subject("Java")
                .topic("OOPs")
                .testType("PRACTICE")
                .durationMinutes(15)
                .passingMarks(40.0)
                .maxAttempts(5)
                .negativeMarking(false)
                .isOfficial(false)
                .bankId(javaBank.getId())
                .createdBy(ravi.getId())
                .createdAt(LocalDateTime.now().minusDays(5))
                .build());

        // 2. Java placement mock (official)
        Test javaMock = testRepository.save(Test.builder()
                .title("Campus Java Placement Mock Exam 2026")
                .description("Official College Placement Assessment for Java Developer recruitment drive.")
                .subject("Java")
                .topic("OOPs")
                .testType("MOCK")
                .durationMinutes(30)
                .passingMarks(50.0)
                .maxAttempts(2)
                .negativeMarking(true)
                .isOfficial(true)
                .bankId(javaBank.getId())
                .createdBy(tpo.getId())
                .createdAt(LocalDateTime.now().minusDays(4))
                .build());

        // 3. DSA group quiz
        Test dsaGroupQuiz = testRepository.save(Test.builder()
                .title("DSA Daily Challenge: Complexity & Binary Trees")
                .description("Exclusive weekly quiz for DSA Placement Prep group members.")
                .subject("DSA")
                .topic("Algorithms")
                .testType("GROUP")
                .groupId(dsaGroup.getId())
                .durationMinutes(20)
                .passingMarks(40.0)
                .maxAttempts(3)
                .negativeMarking(false)
                .isOfficial(false)
                .bankId(dsaBank.getId())
                .createdBy(karan.getId())
                .createdAt(LocalDateTime.now().minusDays(3))
                .build());

        // 4. Java skill-verification test
        Test javaVerifyTest = testRepository.save(Test.builder()
                .title("Official Java Tutor Skill Verification Test")
                .description("Passing this exam grants the verified tutor badge and unlocks peer-tutoring credentials.")
                .subject("Java")
                .topic("Collections")
                .testType("SKILL_VERIFICATION")
                .skillId(javaSkill.getId())
                .durationMinutes(25)
                .passingMarks(60.0)
                .maxAttempts(2)
                .negativeMarking(true)
                .isOfficial(true)
                .bankId(collBank.getId())
                .createdBy(tpo.getId())
                .createdAt(LocalDateTime.now().minusDays(3))
                .build());

        // 5. Adaptive Java quiz
        Test adaptiveTest = testRepository.save(Test.builder()
                .title("Adaptive Java Skill Profiler")
                .description("Dynamic difficulty adjusts up after correct answers and down after mistakes (10 questions).")
                .subject("Java")
                .topic("OOPs")
                .testType("ADAPTIVE")
                .durationMinutes(20)
                .passingMarks(50.0)
                .maxAttempts(3)
                .negativeMarking(false)
                .isOfficial(false)
                .bankId(javaBank.getId())
                .createdBy(tpo.getId())
                .createdAt(LocalDateTime.now().minusDays(2))
                .build());

        // 6. Aptitude mock (official)
        Test aptMock = testRepository.save(Test.builder()
                .title("College Placement Aptitude & Logical Mock")
                .description("Official general aptitude test simulating TCS, Infosys, and Cognizant screening rounds.")
                .subject("Aptitude")
                .topic("Quantitative")
                .testType("MOCK")
                .durationMinutes(30)
                .passingMarks(50.0)
                .maxAttempts(2)
                .negativeMarking(true)
                .isOfficial(true)
                .bankId(aptBank.getId())
                .createdBy(tpo.getId())
                .createdAt(LocalDateTime.now().minusDays(2))
                .build());

        // 10. Sample Completed Test Attempt for Ravi (for recommendations demo)
        TestAttempt attempt = testAttemptRepository.save(TestAttempt.builder()
                .testId(javaMock.getId())
                .userId(ravi.getId())
                .attemptNumber(1)
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(LocalDateTime.now().minusHours(1))
                .submittedAt(LocalDateTime.now().minusMinutes(75))
                .timeTakenSeconds(1240)
                .score(5.5)
                .totalMarks(10.0)
                .percentage(55.0)
                .passed(true)
                .rankInTest(1)
                .status("SUBMITTED")
                .questionOrderJson("[]")
                .build());

        // 11. Initial placement profile for students
        placementProfileRepository.save(PlacementProfile.builder().userId(ravi.getId()).dsaSolvedCount(110).aptitudeScore(75.0).mockInterviewRating(4.5).resumeScore(88.0).placementReadiness(82.4).streakDays(12).lastChallengeDate(LocalDate.now()).updatedAt(LocalDateTime.now()).build());
        placementProfileRepository.save(PlacementProfile.builder().userId(priya.getId()).dsaSolvedCount(65).aptitudeScore(68.0).mockInterviewRating(4.0).resumeScore(80.0).placementReadiness(72.5).streakDays(8).lastChallengeDate(LocalDate.now()).updatedAt(LocalDateTime.now()).build());
        placementProfileRepository.save(PlacementProfile.builder().userId(arjun.getId()).dsaSolvedCount(45).aptitudeScore(60.0).mockInterviewRating(3.5).resumeScore(70.0).placementReadiness(61.0).streakDays(5).lastChallengeDate(LocalDate.now()).updatedAt(LocalDateTime.now()).build());
        placementProfileRepository.save(PlacementProfile.builder().userId(sneha.getId()).dsaSolvedCount(30).aptitudeScore(92.0).mockInterviewRating(4.2).resumeScore(85.0).placementReadiness(71.8).streakDays(7).lastChallengeDate(LocalDate.now()).updatedAt(LocalDateTime.now()).build());
        seedAdditionalQuizSets();
    }

    private void seedAdditionalQuizSets() {
        User creator = userRepository.findByRole(Role.TPO).stream().findFirst()
                .or(() -> userRepository.findByRole(Role.SUPER_ADMIN).stream().findFirst())
                .orElseThrow(() -> new IllegalStateException("Cannot seed quizzes without an administrator account."));
        correctKnownQuestionExplanations();

        QuestionBank fractionsBank = getOrCreateQuestionBank(
                "Fractions, Ratios & Percentages - Set 2", "Aptitude", "Fractions", creator.getId());
        if (questionRepository.findByBankId(fractionsBank.getId()).isEmpty()) {
            createQuestion(fractionsBank.getId(), "Aptitude", "Fractions", "NUMERIC", "EASY",
                    "Simplify the fraction 18/24.", null, "3/4",
                    "Divide the numerator and denominator by their greatest common divisor, 6: 18/24 = 3/4.",
                    1.0, 0.0, null);
            createQuestion(fractionsBank.getId(), "Aptitude", "Fractions", "NUMERIC", "EASY",
                    "What is 2/5 of 60?", null, "24",
                    "60 x 2/5 = 12 x 2 = 24.",
                    1.0, 0.0, null);
            createQuestion(fractionsBank.getId(), "Aptitude", "Fractions", "NUMERIC", "MEDIUM",
                    "Half of a 3/4-cup measure is used. How many cups is that? Give a fraction.",
                    null, "3/8", "3/4 x 1/2 = 3/8 cup.",
                    1.0, 0.0, null);
            createQuestion(fractionsBank.getId(), "Aptitude", "Fractions", "NUMERIC", "EASY",
                    "What is the probability of rolling an even number on a fair six-sided die? Give a fraction.",
                    null, "1/2", "There are three even results (2, 4, 6) out of six: 3/6 = 1/2.",
                    1.0, 0.0, null);
            createQuestion(fractionsBank.getId(), "Aptitude", "Fractions", "NUMERIC", "EASY",
                    "What is 15% of 200?", null, "30",
                    "10% of 200 is 20 and 5% is 10, so 15% is 30.",
                    1.0, 0.0, null);
        }
        ensurePracticeTest("Aptitude Fractions & Ratios Practice Set 2",
                "Practice fractions, ratios, and percentages. Fraction answers may be entered as a/b or an equivalent decimal.",
                "Aptitude", "Fractions", fractionsBank, creator.getId());

        QuestionBank javaBank = getOrCreateQuestionBank(
                "Java Fundamentals - Set 2", "Java", "Fundamentals", creator.getId());
        if (questionRepository.findByBankId(javaBank.getId()).isEmpty()) {
            createQuestion(javaBank.getId(), "Java", "Fundamentals", "MCQ", "EASY",
                    "What is the main purpose of encapsulation in Java?",
                    Arrays.asList("To restrict direct access to an object's data", "To run code on multiple processors",
                            "To convert source code into bytecode", "To sort objects automatically"),
                    "To restrict direct access to an object's data",
                    "Encapsulation hides internal state and exposes controlled access through methods.", 1.0, 0.0, null);
            createQuestion(javaBank.getId(), "Java", "Fundamentals", "MCQ", "MEDIUM",
                    "When two methods in the same class have the same name but different parameter lists, this is called:",
                    Arrays.asList("Method overriding", "Method overloading", "Encapsulation", "Inheritance"),
                    "Method overloading",
                    "Overloaded methods share a name and differ in their parameter list.", 1.0, 0.0, null);
            createQuestion(javaBank.getId(), "Java", "Fundamentals", "TRUE_FALSE", "EASY",
                    "An instance field of type boolean defaults to false when it is not explicitly initialized.",
                    Arrays.asList("True", "False"), "True",
                    "Java initializes boolean instance fields to false by default.", 1.0, 0.0, null);
            createQuestion(javaBank.getId(), "Java", "Fundamentals", "FILL_BLANK", "EASY",
                    "The keyword used to declare that one Java class inherits from another is _____.",
                    null, "extends|EXTENDS",
                    "A class declares inheritance using the extends keyword.", 1.0, 0.0, null);
            createQuestion(javaBank.getId(), "Java", "Fundamentals", "MCQ", "MEDIUM",
                    "Which method should generally be used to compare the contents of two Java String objects?",
                    Arrays.asList("==", "equals()", "compareToIdentity()", "sameReference()"),
                    "equals()",
                    "String.equals compares character content; == compares object references.", 1.0, 0.0, null);
        }
        ensurePracticeTest("Java Fundamentals Practice Quiz - Set 2",
                "A fresh practice set on Java encapsulation, methods, inheritance, and strings.",
                "Java", "Fundamentals", javaBank, creator.getId());

        QuestionBank dsaBank = getOrCreateQuestionBank(
                "Data Structures & Complexity - Set 2", "DSA", "Fundamentals", creator.getId());
        if (questionRepository.findByBankId(dsaBank.getId()).isEmpty()) {
            createQuestion(dsaBank.getId(), "DSA", "Fundamentals", "MCQ", "EASY",
                    "What is the worst-case time complexity of binary search on a sorted array?",
                    Arrays.asList("O(1)", "O(log n)", "O(n)", "O(n log n)"), "O(log n)",
                    "Each comparison halves the remaining search interval.", 1.0, 0.0, null);
            createQuestion(dsaBank.getId(), "DSA", "Fundamentals", "MCQ", "EASY",
                    "Which data structure is typically used to implement a first-in, first-out queue?",
                    Arrays.asList("Stack", "Queue", "Heap", "Tree"), "Queue",
                    "A queue removes elements in the same order they were added.", 1.0, 0.0, null);
            createQuestion(dsaBank.getId(), "DSA", "Fundamentals", "TRUE_FALSE", "EASY",
                    "Breadth-first search commonly uses a queue to visit graph vertices level by level.",
                    Arrays.asList("True", "False"), "True",
                    "A FIFO queue processes discovered vertices in breadth-first order.", 1.0, 0.0, null);
            createQuestion(dsaBank.getId(), "DSA", "Fundamentals", "MCQ", "MEDIUM",
                    "What is the worst-case time complexity of merge sort?",
                    Arrays.asList("O(n)", "O(log n)", "O(n log n)", "O(n^2)"), "O(n log n)",
                    "Merge sort splits the input into logarithmically many levels and merges n elements per level.", 1.0, 0.0, null);
            createQuestion(dsaBank.getId(), "DSA", "Fundamentals", "FILL_BLANK", "EASY",
                    "In a zero-indexed array, what is the time complexity of accessing an element by index?",
                    null, "O(1)|constant time|constant",
                    "Array indexing takes constant time, written O(1).",
                    1.0, 0.0, null);
        }
        ensurePracticeTest("DSA Fundamentals Practice Quiz - Set 2",
                "A new practice set covering search, queues, graph traversal, and sorting complexity.",
                "DSA", "Fundamentals", dsaBank, creator.getId());
    }

    private void correctKnownQuestionExplanations() {
        questionBankRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(bank -> "Data Structures & Algorithms".equalsIgnoreCase(bank.getTitle()))
                .findFirst()
                .ifPresent(bank -> questionRepository.findByBankId(bank.getId()).stream()
                        .filter(question -> question.getQuestionText().startsWith(
                                "Match the data structure with its optimal worst-case search complexity:"))
                        .forEach(question -> {
                            question.setExplanation(
                                    "Unsorted array search is O(n), AVL tree search is O(log n), and an ideal hash table lookup is O(1).");
                            questionRepository.save(question);
                        }));
    }

    private QuestionBank getOrCreateQuestionBank(String title, String subject, String topic, Long creatorId) {
        return questionBankRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(bank -> bank.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseGet(() -> questionBankRepository.save(QuestionBank.builder()
                        .title(title)
                        .subject(subject)
                        .topic(topic)
                        .createdBy(creatorId)
                        .createdAt(LocalDateTime.now())
                        .build()));
    }

    private void ensurePracticeTest(String title, String description, String subject, String topic,
                                    QuestionBank bank, Long creatorId) {
        boolean exists = testRepository.findAllByOrderByCreatedAtDesc().stream()
                .anyMatch(test -> test.getTitle().equalsIgnoreCase(title));
        if (!exists) {
            testRepository.save(Test.builder()
                    .title(title)
                    .description(description)
                    .subject(subject)
                    .topic(topic)
                    .testType("PRACTICE")
                    .durationMinutes(15)
                    .passingMarks(60.0)
                    .maxAttempts(10)
                    .negativeMarking(false)
                    .isOfficial(false)
                    .bankId(bank.getId())
                    .createdBy(creatorId)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }

    private Long updateAdminAccount() {
        String normalizedEmail = normalizedAdminEmail();
        Optional<User> adminAccount = userRepository.findByEmail("admin@campus.edu");
        if (adminAccount.isEmpty()) {
            adminAccount = userRepository.findByEmail(normalizedEmail);
        }
        if (adminAccount.isEmpty()) {
            return null;
        }

        User admin = adminAccount.get();
        if (admin.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalStateException("Configured administrator account does not have the SUPER_ADMIN role.");
        }

        Optional<User> emailOwner = userRepository.findByEmail(normalizedEmail);
        if (emailOwner.isPresent() && !emailOwner.get().getId().equals(admin.getId())) {
            throw new IllegalStateException("Configured administrator email is already used by another account.");
        }

        admin.setEmail(normalizedEmail);
        if (!adminPassword.isBlank()) {
            admin.setPassword(passwordEncoder.encode(adminPassword));
        }
        return userRepository.save(admin).getId();
    }

    private String normalizedAdminEmail() {
        return adminEmail.trim().toLowerCase(Locale.ROOT);
    }

    private User createUser(String email, String rollNumber, String plainPassword, String fullName, Role role, UserStatus status, String branch, int year, String bio, String mode, int credits, int xp, String level) {
        User u = User.builder()
                .email(email)
                .rollNumber(rollNumber)
                .password(passwordEncoder.encode(plainPassword))
                .fullName(fullName)
                .role(role)
                .status(status)
                .branch(branch)
                .studyYear(year)
                .bio(bio)
                .preferredMode(mode)
                .skillCredits(credits)
                .xp(xp)
                .level(level)
                .dailyStreak(Math.max(1, xp / 50))
                .lastActiveDate(LocalDate.now())
                .createdAt(LocalDateTime.now().minusDays(15))
                .updatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(u);
    }

    private List<User> seedSwapCandidates(Long createdBy) {
        List<User> candidates = new ArrayList<>();
        candidates.add(seedSwapCandidate("student1@campus.edu", "21CS101", "Aisha Rahman", "Computer Science", 3,
                "Python and SQL enthusiast who enjoys making data useful and easy to understand.", "ONLINE",
                List.of("Python", "SQL", "Data Visualization"), List.of("Power BI", "Public Speaking"), createdBy));
        candidates.add(seedSwapCandidate("student2@campus.edu", "21IT102", "Dev Patel", "Information Technology", 3,
                "Building reliable cloud deployments with Linux, Docker, and practical DevOps workflows.", "BOTH",
                List.of("Linux", "Docker", "Cloud Computing"), List.of("Java", "System Design"), createdBy));
        candidates.add(seedSwapCandidate("student3@campus.edu", "22CS103", "Meera Iyer", "Computer Science", 2,
                "UI designer focused on clear Figma prototypes, accessible interfaces, and thoughtful user flows.", "ONLINE",
                List.of("Figma", "UI/UX", "Web Accessibility"), List.of("React", "TypeScript"), createdBy));
        candidates.add(seedSwapCandidate("student4@campus.edu", "21CY104", "Kabir Shah", "Cybersecurity", 3,
                "Enjoys security fundamentals, Python automation, and explaining networking concepts to beginners.", "BOTH",
                List.of("Cybersecurity", "Networking", "Python"), List.of("Cloud Computing", "Linux"), createdBy));
        candidates.add(seedSwapCandidate("student5@campus.edu", "22BA105", "Isha Menon", "Business Analytics", 2,
                "Turns spreadsheets into useful reports and dashboards for student clubs and small projects.", "ONLINE",
                List.of("Excel", "Power BI", "Data Visualization"), List.of("SQL", "Python"), createdBy));
        candidates.add(seedSwapCandidate("student6@campus.edu", "21EC106", "Nikhil Rao", "Electronics", 3,
                "Android developer learning by building Kotlin apps, integrating APIs, and testing on real devices.", "BOTH",
                List.of("Kotlin", "Android Development", "Git"), List.of("Java", "UI/UX"), createdBy));
        candidates.add(seedSwapCandidate("student7@campus.edu", "22ME107", "Farah Khan", "Mechanical Engineering", 2,
                "Peer mentor for resumes and interview communication, now building confidence with technical skills.", "BOTH",
                List.of("Resume Review", "Mock Interview", "Public Speaking"), List.of("Python", "SQL"), createdBy));
        candidates.add(seedSwapCandidate("student8@campus.edu", "21CS108", "Aditya Bose", "Computer Science", 3,
                "Frontend developer working with React and TypeScript, with a strong interest in testing and UX.", "ONLINE",
                List.of("React", "TypeScript", "Git"), List.of("Figma", "Web Accessibility"), createdBy));
        return candidates;
    }

    private User seedSwapCandidate(String email, String rollNumber, String fullName, String branch, int year,
                                   String bio, String mode, List<String> teachSkills, List<String> learnSkills,
                                   Long createdBy) {
        Optional<User> existing = userRepository.findByEmail(email);
        User candidate;
        if (existing.isPresent()) {
            candidate = existing.get();
            if (!candidate.getFullName().startsWith("Demo Peer ")) {
                return candidate;
            }
            candidate.setRollNumber(rollNumber);
            candidate.setFullName(fullName);
            candidate.setRole(Role.STUDENT);
            candidate.setStatus(UserStatus.VERIFIED);
            candidate.setBranch(branch);
            candidate.setStudyYear(year);
            candidate.setBio(bio);
            candidate.setPreferredMode(mode);
            userRepository.save(candidate);
            userSkillRepository.deleteAll(userSkillRepository.findByUserId(candidate.getId()));
        } else {
            candidate = createUser(email, rollNumber, "student123", fullName, Role.STUDENT, UserStatus.VERIFIED,
                    branch, year, bio, mode, 3, 100, "Beginner");
        }

        for (String skillName : teachSkills) {
            addCandidateSkill(candidate.getId(), skillName, "CAN_TEACH", createdBy);
        }
        for (String skillName : learnSkills) {
            addCandidateSkill(candidate.getId(), skillName, "WANT_TO_LEARN", createdBy);
        }

        if (availabilityRepository.findByUserId(candidate.getId()).isEmpty()) {
            addAvailability(candidate.getId(), "TUESDAY", "17:00", "19:00", mode);
            addAvailability(candidate.getId(), "SATURDAY", "10:00", "12:00", mode);
        }
        return candidate;
    }

    private void addCandidateSkill(Long userId, String name, String type, Long createdBy) {
        Skill skill = skillRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> skillRepository.save(Skill.builder()
                        .name(name)
                        .category("TECH")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .build()));
        addUserSkill(userId, skill, type, type.equals("CAN_TEACH") ? "ADVANCED" : "BEGINNER", false);
    }

    private void addUserSkill(Long userId, Skill skill, String type, String proficiency, boolean verified) {
        userSkillRepository.save(UserSkill.builder()
                .userId(userId)
                .skillId(skill.getId())
                .skillName(skill.getName())
                .skillType(type)
                .proficiency(proficiency)
                .verified(verified)
                .build());
    }

    private void addAvailability(Long userId, String day, String start, String end, String mode) {
        availabilityRepository.save(Availability.builder()
                .userId(userId)
                .dayOfWeek(day)
                .startTime(start)
                .endTime(end)
                .mode(mode)
                .build());
    }

    private void addMember(Long groupId, Long userId, String role) {
        groupMemberRepository.save(GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role(role)
                .status("ACTIVE")
                .joinedAt(LocalDateTime.now().minusDays(10))
                .build());
    }

    private void createQuestion(Long bankId, String subject, String topic, String type, String difficulty,
                                String text, List<String> options, String correct, String explanation,
                                double marks, double neg, String matchPairs) {
        String optJson = null;
        if (options != null) {
            try {
                optJson = objectMapper.writeValueAsString(options);
            } catch (Exception ignored) {}
        }
        questionRepository.save(Question.builder()
                .bankId(bankId)
                .subject(subject)
                .topic(topic)
                .questionType(type)
                .difficulty(difficulty)
                .questionText(text)
                .optionsJson(optJson)
                .correctAnswer(correct)
                .explanation(explanation)
                .marks(marks)
                .negativeMarks(neg)
                .matchPairsJson(matchPairs)
                .build());
    }
}
