<<<<<<< HEAD
# CampusSkills 🎓
> *"Learn. Teach. Practice. Grow Together."*

**CampusSkills** is a verified college peer-learning and skill-swap platform designed for university campuses. It brings together peer tutoring (with SkillCredits), collaborative study groups, real-time STOMP group chat, interactive doubt solving, project team finding with task boards, question banks with 6 assessment question types, personalized AI recommendations, placement readiness scoring across 5 recruitment pillars, gamified XP leaderboards, and a dedicated Training & Placement Officer (TPO) / Admin moderation console.

---

## 🏛️ System Architecture

```
                  ┌──────────────────────────────────────────────┐
                  │                 Web Browser                  │
                  │   React 18 + Vite + Tailwind CSS + Lucide   │
                  └──────────────┬───────────────────────────────┘
                                 │ HTTP REST (JWT) & STOMP /ws
                                 ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       Spring Boot 3.3.x Backend                             │
│                         (Bound to 127.0.0.1:8080)                          │
│                                                                             │
│  ┌───────────────────────┐  ┌───────────────────────┐  ┌─────────────────┐ │
│  │   Spring Security     │  │   WebSocket STOMP     │  │  File Storage   │ │
│  │   Stateless JWT Filter│  │   /topic/groups/{id}  │  │  Safe Exts Only │ │
│  │   Role & Verification │  │   /user/queue/notifs  │  │  uploads/       │ │
│  └───────────────────────┘  └───────────────────────┘  └─────────────────┘ │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                       Application Services                            │ │
│  │  • Swap & Matching Service (70% coverage + 20% rating + 10% overlap)  │ │
│  │  • Study Groups & Attendance Engine                                   │ │
│  │  • Doubt-Solving Board & Best-Answer Rewards                          │ │
│  │  • Project Team Finder & Task Board                                   │ │
│  │  • Quiz Assessment & Auto-Grader (MCQ, True/False, Blank, Numeric)    │ │
│  │  • Smart Weak-Topic Recommendation Engine (<60% accuracy)            │ │
│  │  • Placement 5-Pillar Readiness Tracker (0-100 radar/ring)            │ │
│  │  • Gamification, Trust Score (60% rating + 40% attendance), XP        │ │
│  │  • Admin Moderation, CSV Exporters & Print PDF Reports                │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                       Spring Data JPA / Hibernate                     │ │
│  │               H2 Database (file:./data/campusskills)                  │ │
│  │               (Optional MySQL Profile: -Dspring.profiles.active=mysql)│ │
│  └───────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

The frontend React application is compiled directly into `src/main/resources/static`, enabling a single `mvn spring-boot:run` or standalone `java -jar` to serve both the entire REST API, WebSocket endpoints, and the single-page application on port `8080`.

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17+** (Compatible with JDK 17 through JDK 26)
- **Maven 3.8+** (or bundled `mvn`)
- **Node.js 18+** (only if editing and re-bundling the frontend)

### Running the Application
From the project root:

```bash
# Option 1: Run via Maven
mvn spring-boot:run

# Option 2: Run the pre-packaged Standalone Fat JAR
java -jar target/campusskills-1.0.0.jar
```

Open your browser and navigate to:
👉 **`http://localhost:8080`**

The application binds to `127.0.0.1` so it is reachable only from this computer, not from other devices on your network or the internet. This also means a Vercel deployment will not be available to other students; keep the API and frontend local for this setup.

The local Super Admin account email defaults to `Campus_admin@campus.edu.in`. To set or change its password, set `CAMPUS_ADMIN_PASSWORD` in the same terminal before starting the application. The password is saved as a hash in the local database; do not put it in source control.

### Local backups and session reminders

- The application creates an H2 database backup at startup and every day at 2:00 AM in `./backups`. It keeps the seven newest backup archives. Backups contain private account and learning data; keep this folder on a trusted device and do not share or commit it.
- Super Admins can open **Admin Console → Local Backups** to create a backup immediately or download an existing archive.
- In-app notifications are generated about 15 minutes before scheduled tutoring and study-group sessions. They appear in the app's notification center; they are not browser or email push notifications.
- The dashboard's **Quick guide** opens the student tour. It appears automatically on first use in each browser profile and can be reopened any time.

### Password reset email

The login page's **Forgot password?** link sends a single-use reset link to the registered college email. Configure SMTP before requesting a link:

```powershell
$env:MAIL_HOST = "smtp.gmail.com"
$env:MAIL_PORT = "587"
$env:MAIL_USERNAME = "your-sender@gmail.com"
$env:MAIL_PASSWORD = "your-mail-provider-app-password"
$env:MAIL_SMTP_AUTH = "true"
$env:MAIL_SMTP_STARTTLS = "true"
$env:PASSWORD_RESET_FRONTEND_URL = "http://localhost:8080"
java -jar target/campusskills-1.0.0.jar
```

For Vite development, set `PASSWORD_RESET_FRONTEND_URL` to `http://localhost:5173`. Reset links expire after 30 minutes and can only be used once. The request response does not disclose whether the email address has an account.

---

## 🔑 Pre-Seeded Demo Accounts

All demo accounts are pre-loaded with skills, study groups, tutoring sessions, question banks, quizzes, placement scores, and badges.

| Role | Email / Roll Number | Password | Notes |
| :--- | :--- | :--- | :--- |
| **Super Admin** | Local administrator account | Not published | Full administrative control; sign-in details are kept off the public-facing site |
| **TPO Officer** | Local staff account | Not published | Training & Placement management; sign-in details are kept off the public-facing site |
| **Tutor (Verified)** | `ravi@campus.edu` / `CS2021001` | `student123` | Java, Spring Boot, DSA Tutor • 5 Credits • Level: Mentor • 4.8★ Rating |
| **Student (Verified)** | `priya@campus.edu` / `CS2021002` | `student123` | UI/UX, Figma, React Tutor • 3 Credits • Level: Contributor |
| **Student (Verified)** | `arjun@campus.edu` / `CS2021003` | `student123` | Python, Machine Learning Learner • 3 Credits |
| **Student (Verified)** | `sneha@campus.edu` / `CS2021004` | `student123` | Cloud & DevOps Tutor • 3 Credits |
| **Student (Verified)** | `karan@campus.edu` / `CS2021005` | `student123` | Competitive Programmer • 3 Credits |
| **Student (Verified)** | `ananya@campus.edu` / `CS2021006` | `student123` | Web Development & System Design • 3 Credits |
| **Pending Student** | `rahul@campus.edu` / `CS2024099` | `student123` | **Demonstrates Verification Flow**: Logs in, sees "Waiting for Verification" screen. All protected APIs return `403 (NOT_VERIFIED)` until approved by Admin/TPO |

### College-first student network

The email domain is used as the campus identifier. Students can register with their college email regardless of its domain; Skill Swap prioritizes verified peers with the same domain, while the **All colleges** filter keeps cross-college peers available. New registrations remain pending and require Admin/TPO verification before students can join the peer network. Admins/TPOs should verify the student's college affiliation before approval.

### Admin access

Sign in with an account whose role is `SUPER_ADMIN` or `TPO` to open the separate **Admin Console** for student verification, user management, reports, analytics, and exports. Student accounts continue to open the learning dashboard. Admins can use **Student Dashboard** in the sidebar to access the learner-facing app.

The admin console is available only after signing in with a `SUPER_ADMIN` or `TPO` account. Admin shortcuts and credentials are not shown on public-facing login or landing pages.

The Skill Swap directory also includes these sample peer accounts (password: `student123`) with different teach/learn portfolios:

| Peer | Login | Can Teach | Wants to Learn |
| :--- | :--- | :--- | :--- |
| Aisha Rahman | `student1@campus.edu` | Python, SQL, Data Visualization | Power BI, Public Speaking |
| Dev Patel | `student2@campus.edu` | Linux, Docker, Cloud Computing | Java, System Design |
| Meera Iyer | `student3@campus.edu` | Figma, UI/UX, Web Accessibility | React, TypeScript |
| Kabir Shah | `student4@campus.edu` | Cybersecurity, Networking, Python | Cloud Computing, Linux |
| Isha Menon | `student5@campus.edu` | Excel, Power BI, Data Visualization | SQL, Python |
| Nikhil Rao | `student6@campus.edu` | Kotlin, Android Development, Git | Java, UI/UX |
| Farah Khan | `student7@campus.edu` | Resume Review, Mock Interview, Public Speaking | Python, SQL |
| Aditya Bose | `student8@campus.edu` | React, TypeScript, Git | Figma, Web Accessibility |

---

## 🧩 Modules & Key Features

### 1. Profiles & Skills Matrix
- Profile information: photo URL, full name, branch, study year, bio, and preferred learning mode (`ONLINE`, `OFFLINE`, `BOTH`).
- Skills I Can Teach vs Skills I Want to Learn (with proficiency levels: `BEGINNER`, `INTERMEDIATE`, `ADVANCED`).
- Weekly availability schedule slots (Day of week, start time, end time, mode).
- Public profile showcases: SkillCredits, XP points, Level tier, Trust Reliability Score, badges, and average star rating.

### 2. Skill Swap & Peer Tutoring (SkillCredits)
- New students receive **3 initial SkillCredits** on onboarding.
- **Smart Matching Algorithm (`GET /api/matches`)**:
  - Score calculated as: `70% Skill Coverage (bidirectional teachable vs wish-list) + 20% Average Rating + 10% Overlapping Availability`.
  - Highlights *"They can teach you"*, *"You can teach them"*, and verified tutor status ticks.
- Swap requests: Send, Accept, Reject, Cancel.
- Scheduling: Schedule 1-on-1 sessions. Upon completion, learner pays `ceil(minutes / 60)` credits, tutor receives credits, and XP is awarded (Tutor +30 XP, Learner +15 XP).
- Reciprocal 1–5 star rating with comments.

### 3. Collaborative Study Groups
- Filter groups by subject, branch, year, timing, mode, and category (`SUBJECT`, `EXAM`, `SKILL`, `PLACEMENT`).
- Seed group: *"DSA Placement Prep – Daily 7 PM"* with 15 active members.
- Instant access to Public groups; admin approval queue for Private groups.
- Real-time STOMP WebSocket chat room (`/topic/groups/{id}`) and REST message history.
- Resources vault: upload lecture notes, cheat sheets, YouTube tutorials, and PDF docs (restricted to safe file extensions).
- Share PDFs, documents, spreadsheets, archives, code files, or web links in a group's Resources Vault; only active group members can add resources.
- Weekly session schedule planner with recurrence up to 12 weeks.
- Check-in button (+10 XP) and administrative group attendance sheet.

### Personal Study Notes
- Open **My Notes** in the sidebar to create, edit, search, and delete notes saved to your account.
- Personal notes are private; use a study group's **Resources Vault** to share study files and links with other members.

### Community content safety
- Public text submissions (including registration/profile details, chats, group content, skill names, swap requests, doubts, answers, project content, and quiz/test questions) are checked for abusive language and sexual/nudity terms on the server.
- A flagged submission is rejected before it is published. The attempted text, content area, and category are saved as evidence in the Admin Console report queue; Super Admins are notified. Admins decide whether to confirm the block, dismiss the report, or suspend an existing account; accounts are not automatically suspended.
- A blocked registration is reported with the attempted email address as context, but no account exists yet to suspend.
- Image uploads are not allowed in study-group resource vaults. Profile pictures remain supported. This local keyword filter is a basic safeguard, not an AI classifier, and cannot detect explicit material hidden in video, audio, or image content linked from an external website.

### 4. Doubt-Solving Board
- Post technical doubts with subject, topic, description, and optional code attachments.
- Live duplicate search on the ask form to prevent repeat questions.
- Answer discussion thread (+5 XP for answering). Upvote toggle.
- Best-Answer selection: Asker selects the best solution; the answerer earns **+1 SkillCredit and +25 XP**, and the doubt is automatically marked `SOLVED`.
- Senior (Year 3+) and faculty verification tick on high-quality solutions.
- Community report button with moderation queue.

### 5. Project Team Finder
- Post project opportunities with role requirements, desired skills, team cap, and deadline.
- Student application workflow (role and message). Leader accepts or rejects.
- Integrated Task Board: leader and members assign tasks with deadlines; interactive check-offs update the live progress bar.
- Project completion awards **+40 XP** to all teammates with peer ratings.

### 6. Tests, Quizzes & Assessments
- Question Banks supporting 6 distinct question types:
  1. `MCQ`: Single-choice options
  2. `TRUE_FALSE`: Boolean questions
  3. `FILL_BLANK`: Fill in the blank (supports multiple alternatives delimited by `|`)
  4. `NUMERIC`: Calculation questions with automatic `±0.01` tolerance grading
  5. `MATCH`: Match the following terms
  6. `SHORT`: Brief descriptive answers
- Assessment categories: `PRACTICE`, `GROUP`, `MOCK`, `SKILL_VERIFICATION`, and `ADAPTIVE`.
- Live test runner with countdown timer (server deadline enforcement), stable randomized question navigator, and real-time answer persistence.
- Immediate auto-grading on submit: score, percentage, rank in test, topic accuracy bars, detailed question review with explanations, and official printable certificate.
- Includes additional Java fundamentals, DSA fundamentals, and fractions/ratios practice sets. Numeric answers accept fractions such as `1/2` and equivalent forms such as `2/4`.
- Assessment start offers local browser focus monitoring. It records page-hidden events in that browser tab's session storage; it does not use a camera or AI, upload monitoring data, or automatically penalize students.

### 7. AI Smart Recommendations (`GET /api/recommendations`)
- Analyzes all test attempts by a student and identifies weak topics (<60% accuracy).
- Generates a tailored 3-part study prescription for each weak area:
  1. Practice / Adaptive quizzes
  2. Study groups to join
  3. Matched peer tutors with match percentage (40% rating, 30% reliability, 20% verified, 10% availability overlap)
- Example output: *"Java Mock 55%. Weak: OOPs 40%, Collections 50%. Practice 'OOPs Basics Quiz', join 'Java Placement Preparation Group', book Ravi (92% match)."*

### 8. Campus Placement Readiness Tracker
- **Placement Readiness Score (0–100)** calculated across 5 balanced recruitment pillars:
  1. **DSA Progress**: `(solved / 150) × 100`
  2. **Aptitude**: Average best `MOCK` test score %
  3. **Mock Interview**: Rating × 20
  4. **Resume Score**: Faculty / senior evaluated score (0–100)
  5. **Project Work**: 50 points per completed project (capped at 100)
- Visual SVG circular progress ring and 5-pillar progress bars.
- Daily coding challenge logger (+5 XP per problem, active streak tracking).
- Senior/faculty evaluation modal.

### 9. Gamification, Badges & Trust Reliability
- **XP Rules**: Teach session (+30), Learn session (+15), Answer doubt (+5), Best answer (+25), Quiz complete (+10, +20 if passed), Group attendance (+10), Project completed (+40), Daily challenge (+5).
- **Levels**: Beginner (<100 XP), Contributor (<300), Mentor (<700), Campus Expert (700+).
- **Trust Reliability Score**: `60% Average Rating + 40% Group Attendance Rate`.
- **Badges**: Skill Sharer, DSA Mentor, Quiz Master, Best Answerer, Team Player, Perfect Score, 7-Day Streak, Verified Tutor.
- **Printable Certificates**: Milestone certificates for 10, 25, 50 sessions and passed official/mock tests.

### 10. Admin & TPO Moderation Portal
- Verification queue for new student registrations.
- Role management (`STUDENT`, `TUTOR`, `TPO`, `SUPER_ADMIN`).
- Community moderation queue (dismiss, remove content, or suspend user).
- Official placement test results table.
- One-click CSV exports:
  - `GET /api/admin/export/students.csv`
  - `GET /api/admin/export/sessions.csv`
  - `GET /api/admin/export/tests.csv`
- "Print / Save PDF" analytics report.

---

## 🛠️ REST API Specification

### Authentication
- `POST /api/auth/register` — Register student (defaults to `PENDING` status)
- `POST /api/auth/login` — Login with email or roll number + password

### Students & Skills
- `GET /api/students/me` — Current user profile and status
- `PUT /api/students/me/profile` — Update bio, year, branch, mode
- `GET /api/students/{id}` — Public student profile
- `GET /api/skills` — All available campus skills
- `GET /api/skills/my` — User's teach & learn skills
- `POST /api/skills/user` — Add teach/learn skill
- `DELETE /api/skills/user/{id}` — Remove skill
- `GET /api/skills/availability` — Get weekly availability slots
- `POST /api/skills/availability` — Save weekly availability slots
- `GET /api/matches` — Smart matching algorithm scores
- `GET /api/tutors?skill=` — Browse tutors by skill

### Swaps & Sessions
- `GET /api/swap-requests` — List user's swap requests
- `POST /api/swap-requests` — Create swap request
- `PUT /api/swap-requests/{id}?action=ACCEPT|REJECT|CANCEL` — Update request
- `GET /api/sessions` — List user's tutoring sessions
- `POST /api/sessions` — Schedule session
- `PUT /api/sessions/{id}/complete` — Mark session completed (transfers credits, awards XP)
- `POST /api/sessions/{id}/rating` — Submit 1-5★ rating and feedback

### Study Groups
- `GET /api/groups` — List and filter groups
- `POST /api/groups` — Create study group
- `GET /api/groups/{id}` — Group details, roster, schedule, resources
- `POST /api/groups/{id}/join` — Join public group / request private
- `POST /api/groups/{id}/sessions` — Schedule group study session
- `POST /api/groups/{id}/resources` — Upload resources
- `GET /api/groups/{id}/messages` — Group chat message history

### Personal Notes
- `GET /api/notes` — List the signed-in user's private notes
- `POST /api/notes` — Create a note
- `PUT /api/notes/{id}` — Update a note owned by the signed-in user
- `DELETE /api/notes/{id}` — Delete a note owned by the signed-in user

### Doubts & Projects
- `GET /api/doubts` — List doubts
- `POST /api/doubts` — Post new doubt
- `GET /api/doubts/{id}` — Doubt details with answers
- `POST /api/doubts/{id}/answers` — Answer a doubt
- `PUT /api/doubts/answers/{answerId}/upvote` — Upvote toggle
- `PUT /api/doubts/answers/{answerId}/best` — Mark as best answer (+1 credit, +25 XP)
- `GET /api/projects` — List team finder projects
- `POST /api/projects` — Create project
- `GET /api/projects/{id}` — Project details, tasks, applicants
- `POST /api/projects/{id}/apply` — Apply for project role
- `POST /api/projects/{id}/tasks` — Add project task
- `PUT /api/projects/tasks/{taskId}` — Toggle task completion
- `PUT /api/projects/{id}/complete` — Mark project complete (+40 XP)

### Tests, Placement & Gamification
- `GET /api/tests` — List tests and quizzes
- `POST /api/tests` — Create test (Tutors/TPO/Admin)
- `POST /api/tests/{id}/start` — Start / resume test attempt
- `POST /api/attempts/{id}/answer` — Save answer in real-time
- `POST /api/attempts/{id}/submit` — Auto-grade test attempt
- `GET /api/attempts/{id}/result` — Detailed question review & certificate
- `GET /api/recommendations` — AI weak-topic diagnostic recommendations
- `GET /api/placement/me` — 5-pillar placement readiness scorecard
- `POST /api/placement/challenge` — Log daily coding problems (+5 XP)
- `GET /api/leaderboard?period=all|week` — Leaderboard rankings
- `GET /api/certificates` — List earned milestone & test certificates

### Admin & TPO
- `GET /api/admin/stats` — Overall campus metrics & charts
- `GET /api/admin/students` — Student roster
- `PUT /api/admin/students/{id}/verify?approve=true|false` — Verify or reject pending student
- `PUT /api/admin/students/{id}/role?role=` — Change user role
- `GET /api/admin/reports` — Community moderation queue
- `PUT /api/admin/reports/{id}?action=` — Moderate report (DISMISS, REMOVE_CONTENT, SUSPEND_USER)
- `GET /api/admin/export/students.csv` — Export students CSV
- `GET /api/admin/export/sessions.csv` — Export sessions CSV
- `GET /api/admin/export/tests.csv` — Export test results CSV

---

## 🔒 Security & Verification Controls

1. **Pending Student Quarantine**:
   - Newly registered students are set to `PENDING` status.
   - Any protected API endpoint returns `403 Forbidden` with `{ "message": "...", "code": "NOT_VERIFIED" }`.
   - The UI intercepts this state and routes the student exclusively to the friendly Waiting for Verification screen until approved by an administrator or TPO.
2. **Stateless JWT Security**:
   - BCrypt encrypted passwords.
   - JJWT `0.12.x` token generation and validation.
   - STOMP WebSocket channel interceptor validates JWT tokens in the `CONNECT` header.
3. **Restricted File Uploads**:
   - File uploads are validated strictly against an allowed whitelist: `pdf`, `png`, `jpg`, `jpeg`, `gif`, `webp`, `zip`, `doc`, `docx`, `txt`, `java`, `py`, `cpp`, `c`, `js`, `ts`.
   - HTML, SVG, executable scripts, and active payloads are explicitly blocked.
4. **Answer Integrity Protection**:
   - In-progress test endpoints NEVER expose correct answers or explanations until the student formally submits the attempt.
=======
# CampusSkills
peer and tutoring
>>>>>>> 15ba72542d3c87ddc935fbd2b260a7fa9618eaf2
