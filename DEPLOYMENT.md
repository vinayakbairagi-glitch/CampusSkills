# CampusSkills deployment on Vercel

This branch prepares the existing React + Java Spring Boot application for a **single Vercel project** using Vercel Services and the Container runtime. The existing frontend remains React/Vite; the backend remains Java 17 / Spring Boot. No Supabase or backend-language conversion is involved.

**This is deployment preparation only.** It has not been merged into `main`, deployed to Vercel, connected to a production database, or supplied with real secrets.

## Architecture

- `frontend/`: existing React/Vite user interface.
- `src/`: existing Java 17 / Spring Boot REST API, authentication, JWT, WebSocket and JPA code.
- `Dockerfile.vercel`: builds the Java backend for Vercel's container runtime.
- `vercel.json`: defines a Vite frontend service and Java container backend service on one public domain.
- MySQL: a separate persistent MySQL provider is still required. Vercel container filesystems are ephemeral; do not store production user data in H2 or local files.

The top-level routes send `/api/*`, `/ws*`, and `/uploads/*` to the Java backend. All other paths go to the React frontend, which retains its SPA fallback for client-side routes. The frontend now uses same-origin API paths by default, so `VITE_API_URL` is not required for this combined deployment. Remove any old `VITE_API_URL` override if it points at a different or obsolete backend.

## Important Vercel project settings

The existing Vercel project screenshot showed Framework Preset = Vite and Root Directory = `frontend`. Those settings are for the old frontend-only deployment and will not activate the root-level multi-service configuration.

For a deployment using this branch, Vercel project settings must be changed to:

1. Framework Preset: **Services**.
2. Root Directory: repository root (clear the `frontend` subdirectory setting).
3. Keep the project connected to the CampusSkills Git repository.
4. Deploy the feature branch for testing before promoting anything to production.

**Do not change the current production project's settings until you are ready to test this branch.** A safer first test is a separate Vercel project connected to this feature branch, with Framework Preset = Services and Root Directory set to the repository root. This avoids changing the existing live Vite deployment. Vercel Services and the Container runtime are currently beta features; confirm they are available to the Vercel account before proceeding.

## MySQL environment variables

Create a persistent MySQL database with a provider that permits connections from the deployed Vercel backend. Then add these variables to the backend service/project environment (never put database secrets in Vite frontend variables):

| Variable | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `mysql` |
| `MYSQLHOST` | Database host |
| `MYSQLPORT` | Database port (usually `3306`) |
| `MYSQLDATABASE` | Database name |
| `MYSQLUSER` | Database username |
| `MYSQLPASSWORD` | Database password |
| `JWT_SECRET` | Private random secret, at least 32 characters |
| `CORS_ALLOWED_ORIGINS` | Comma-separated exact frontend origins used for the deployment |
| `CAMPUS_ADMIN_EMAIL` | Email address for the first administrator |
| `CAMPUS_ADMIN_PASSWORD` | Private initial administrator password, at least 12 characters |
| `PASSWORD_RESET_FRONTEND_URL` | Public CampusSkills frontend URL |

Alternatively, the application accepts `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` where the provider supplies a JDBC MySQL URL. Use provider-specific connection instructions; do not guess credentials or commit them to Git.

The first MySQL startup creates the configured administrator if the email does not already exist. Production does not seed demo student accounts. New student registrations are saved in MySQL and retain the existing PENDING verification workflow.

## Health check and sign-in test

After deployment, open:

```
https://YOUR-VERCEL-DOMAIN/api/health
```

A healthy response should report application and database status as UP. Then register a new student, confirm the account is saved in the production MySQL database, and test sign-in with the same email or roll number and password.

## Production limitations to resolve before calling the whole application production-ready

1. **Uploads:** the current Java file-storage service writes to local `./uploads`. Vercel container filesystems are not persistent and may differ between instances. The `/uploads/*` route is wired to Java, but reliable uploaded-file storage still needs an object-storage integration (for example, an S3-compatible bucket or Vercel Blob) before relying on uploads in production.
2. **Scheduled reminders:** Spring `@Scheduled` work runs inside container instances. Vercel containers can scale down and scale out, so the current every-minute reminder scheduler is not a reliable single scheduler. Production reminders need a durable external scheduler/queue or an authenticated scheduled invocation. Vercel Cron on Hobby is limited to daily schedules; a per-minute reminder cadence needs an eligible plan or an external scheduler.
3. **Database backups:** the existing local backup implementation is H2-specific. It is excluded from the MySQL profile so it does not try to run H2 backup SQL against MySQL. Use the managed MySQL provider's backup/restore facilities or add a MySQL-aware backup process.
4. **WebSockets:** the current SockJS/STOMP route is included. Vercel supports WebSocket connections, but connections are tied to function/container execution limits; reconnect behavior remains important. The current simple in-memory Spring message broker is not a cross-instance message bus, so high-scale real-time delivery would need shared messaging infrastructure.

## Local checks

Run the backend build from the repository root:

```bash
mvn -B verify
```

Run the frontend build:

```bash
cd frontend
npm ci
npm run build
```

Do not promote the feature branch or change production Vercel settings until both builds and a preview deployment have been verified.
