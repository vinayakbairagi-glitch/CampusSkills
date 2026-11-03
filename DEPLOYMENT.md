# CampusSkills deployment

CampusSkills uses its existing Spring Boot API and SQL persistence layer. The Vercel project serves the React frontend; the Java API and SQL database must run on a backend host.

## 1. Deploy the backend

Use a Docker-capable host (for example, Railway or Render) and deploy this repository using the root `Dockerfile`.

Set these backend environment variables in the hosting dashboard:

| Variable | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `mysql` |
| `PORT` | Use the port supplied by the host (usually automatic) |
| `MYSQLHOST` | SQL host from your managed MySQL service |
| `MYSQLPORT` | SQL port, usually `3306` |
| `MYSQLDATABASE` | Database name, e.g. `campusskills` |
| `MYSQLUSER` | Database username |
| `MYSQLPASSWORD` | Database password |
| `JWT_SECRET` | A private random secret of at least 32 characters |
| `CORS_ALLOWED_ORIGINS` | Exact public Vercel URL, e.g. `https://your-project.vercel.app` |
| `CAMPUS_ADMIN_EMAIL` | Email address for the first platform administrator |
| `CAMPUS_ADMIN_PASSWORD` | Private password of at least 12 characters for the first administrator |
| `PASSWORD_RESET_FRONTEND_URL` | Same public Vercel URL |

The application also accepts `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` if your SQL provider supplies a JDBC URL instead of the individual MySQL variables.

Do not commit real passwords, JWT secrets, or database credentials to GitHub. Keep them in the backend host's environment-variable settings.

The first MySQL startup creates the configured administrator if that email does not already exist. It does not insert demo student accounts into production. New student registrations are stored in MySQL and start in the existing PENDING verification state.

After deployment, open `https://YOUR-BACKEND-HOST/api/health`. A healthy response reports both the application and database as UP.

## 2. Connect Vercel to the backend

In the Vercel project settings:

1. Set **Root Directory** to `frontend`.
2. Set the environment variable `VITE_API_URL` to the public backend origin, for example `https://your-backend-host.example.com` (no `/api` suffix).
3. Make sure the Vercel deployment uses the `frontend` folder's `vercel.json` SPA rewrite.
4. Redeploy the frontend after saving the variable.

The frontend uses this URL for REST API requests, file uploads, and the notifications WebSocket connection.

## 3. Verify sign-in

1. Open the backend health URL and confirm the database reports UP.
2. Open the Vercel site and register a new student account.
3. Confirm the registration is saved in the production MySQL database.
4. Sign in with the same email or roll number and password.
5. Verify the account using the existing Admin/TPO workflow before testing protected student features.

The first administrator uses the configured `CAMPUS_ADMIN_EMAIL` and `CAMPUS_ADMIN_PASSWORD`; its roll number is `ADM001`. Keep those credentials private and change them through a secured account-management process after first sign-in.

## Important data note

H2 is retained for local development. A file-based H2 database on an ephemeral hosting filesystem is not a reliable production database. The MySQL profile is intended for deployment with a persistent managed SQL database. Existing users in a previous local/H2 database are not automatically copied into a new MySQL database; migrate those records separately if they need to be retained.
