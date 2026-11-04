# CampusSkills deployment on Vercel

This branch prepares the existing React + Java Spring Boot application for a **single Vercel project** using Vercel Services and the Container runtime. The existing frontend remains React/Vite; the backend remains Java 17 / Spring Boot. No Supabase or backend-language conversion is involved.

**This is deployment preparation only.** It has not been merged into `main`, deployed to Vercel, connected to a production database, or supplied with real secrets.

## Architecture

- `frontend/`: existing React/Vite user interface.
- `src/`: existing Java 17 / Spring Boot REST API, authentication, JWT, WebSocket and JPA code.
- `Dockerfile.vercel`: builds the Java backend for Vercel's container runtime.
- `vercel.json`: defines a Vite frontend service and Java container backend service on one public domain.
- Database: persistent MySQL-compatible service. The existing backend uses MySQL Connector/J and Hibernate's MySQL dialect.

The Vercel top-level routes send `/api`, `/api/*`, `/ws*`, and `/uploads*` to the Java backend. All other paths go to the React frontend, which retains its SPA fallback for client-side routes. The frontend uses same-origin API paths by default, so **do not set `VITE_API_URL`** for this combined Vercel Services deployment. An obsolete external URL override should be removed.

## 1. Vercel project settings

The current Vercel project screenshot showed Framework Preset = Vite and Root Directory = `frontend`. Those settings are for the existing frontend-only deployment and will not activate the root-level multi-service configuration.

For this branch, Vercel project settings need to be:

1. Framework Preset: **Services**.
2. Root Directory: repository root (clear the `frontend` subdirectory setting).
3. Keep the project connected to the CampusSkills Git repository.
4. Select the `feat/vercel-database-integration` branch for a preview test.

**Do not change the current production project settings until ready to test.** The safest first test is a separate Vercel project connected to this feature branch. This leaves the current production frontend untouched. Vercel Services and Container runtime are currently beta features; the Vercel account must have access to Services.

## 2. Persistent MySQL-compatible database

Vercel's containers are not durable database storage. Use a persistent managed database; do not put production user records in H2 or in the container's local filesystem.

A simple option that keeps database setup within the Vercel ecosystem is **TiDB Cloud**, available as a Vercel Marketplace integration. TiDB Cloud is MySQL-compatible and provides the connection variables expected below. See:

- Vercel Marketplace: https://vercel.com/marketplace/tidb-cloud
- TiDB setup guide: https://docs.pingcap.com/tidbcloud/integrate-tidbcloud-with-vercel/

Connect a TiDB Cloud cluster to the Vercel project using the integration. The integration can add these variables to the Vercel project:

| Variable | Meaning |
| --- | --- |
| `TIDB_HOST` | Database hostname |
| `TIDB_PORT` | Database port (TiDB Cloud Serverless commonly uses `4000`) |
| `TIDB_DATABASE` | Database name |
| `TIDB_USER` | Database username |
| `TIDB_PASSWORD` | Database password |

The Java MySQL profile now recognizes these `TIDB_*` variables directly, uses TLS with hostname verification, and also retains compatibility with the existing `MYSQLHOST` / `MYSQLUSER` environment variable names. If using another MySQL-compatible service, provide the corresponding `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, and `MYSQLPASSWORD` variables instead.

## 3. Backend environment variables

Set these as Vercel project environment variables for the preview and production environments as appropriate. Keep all credentials private; never add them to source control or Vite-prefixed variables.

| Variable | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `mysql` |
| `TIDB_HOST` | From TiDB Cloud integration |
| `TIDB_PORT` | From TiDB Cloud integration |
| `TIDB_DATABASE` | From TiDB Cloud integration |
| `TIDB_USER` | From TiDB Cloud integration |
| `TIDB_PASSWORD` | From TiDB Cloud integration |
| `JWT_SECRET` | Private random secret, at least 32 characters |
| `CORS_ALLOWED_ORIGINS` | Exact public Vercel origin, e.g. `https://your-project.vercel.app` |
| `CAMPUS_ADMIN_EMAIL` | Email address for the first platform administrator |
| `CAMPUS_ADMIN_PASSWORD` | Private initial administrator password, at least 12 characters |
| `PASSWORD_RESET_FRONTEND_URL` | Public CampusSkills frontend URL |

If using a different SQL provider, use its MySQL-compatible connection values through the `MYSQL*` variables or set `DATABASE_URL` to a full JDBC MySQL URL with `DATABASE_USERNAME` and `DATABASE_PASSWORD`.

The first MySQL-profile startup creates the configured administrator if that email does not already exist. Production does not seed demo student accounts. New student registrations are saved in the persistent SQL database and retain the existing PENDING verification workflow.

## 4. Health check and sign-in test

After the preview deployment and database connection are configured, open:

```
https://YOUR-VERCEL-DOMAIN/api/health
```

A healthy response should report application and database status as UP. Then register a new student, confirm the account is saved in the persistent database, and test sign-in with the same email or roll number and password. The first administrator uses the configured `CAMPUS_ADMIN_EMAIL` and `CAMPUS_ADMIN_PASSWORD`; the existing seeder assigns roll number `ADM001`.

## Production limitations to resolve before relying on every feature

1. **Uploads:** the current Java file-storage service writes to local `./uploads`. Vercel container filesystems are not persistent and may differ between instances. The `/uploads/*` route is wired to Java, but reliable file storage still needs an object-storage integration before relying on uploads in production.
2. **Scheduled reminders:** Spring `@Scheduled` work runs inside container instances. Vercel containers scale down when idle and can scale out, so the current every-minute reminder scheduler is not a reliable single scheduler. Production reminders need a durable scheduler/queue or an authenticated scheduled invocation. Confirm plan-specific Vercel Cron frequency before using it.
3. **Database backups:** the existing local backup implementation is H2-specific. It is excluded from the MySQL profile so it does not run H2 backup SQL against MySQL. Use the managed database provider's backup/restore facilities or add a MySQL-aware backup process.
4. **WebSockets:** the current SockJS/STOMP endpoint is routed to Java. Vercel container instances can scale down, connections need reconnect handling, and the Spring simple in-memory message broker does not synchronize notifications across multiple instances. A shared messaging broker is needed for reliable multi-instance real-time delivery.

## Build verification

The GitHub Actions workflow runs:

```bash
mvn -B verify
docker build -f Dockerfile.vercel -t campusskills-backend:ci .
cd frontend
npm ci
npm run build
```

Do not change production Vercel settings, merge the branch, or promote to production until the full CI run and a preview deployment have been verified.
