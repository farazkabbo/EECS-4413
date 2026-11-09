# Cloud deployment (Supabase + Render)

```
Browser --HTTPS--> Render static site (React, frontend/)
        --HTTPS--> Render web service (Docker, Spring Boot, backend/) --JDBC/SSL--> Supabase PostgreSQL
```

## 1. Database: Supabase

1. Create a project at supabase.com (pick a Canadian region if one is offered) and save the database password.
2. **SQL Editor**: paste `backend/src/main/resources/schema.sql` and run it, then do the same with `data.sql`.
   (Alternatively, start the API once with `DB_INIT_MODE=always`, then set it back to `never`. If you leave it on `always`, every restart wipes the data.)
3. **Project Settings > Database > Connection string > JDBC**, and pick the **Session pooler**. It works over IPv4, which Render needs. The direct connection is IPv6-only.
   * `SUPABASE_DB_URL` = `jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres?sslmode=require`
   * `SUPABASE_DB_USER` = `postgres.<project-ref>`
   * `SUPABASE_DB_PASSWORD` = the database password

Test it locally against Supabase:

```bash
cd backend
export SPRING_PROFILES_ACTIVE=supabase SUPABASE_DB_URL=... SUPABASE_DB_USER=... SUPABASE_DB_PASSWORD=...
./mvnw spring-boot:run
```

Never commit these values. `.env` is git-ignored, and `backend/.env.example` is the template.

## 2. API and front end: Render

1. render.com > **New > Blueprint** > select this GitHub repo. Render reads `render.yaml` and creates:
   * `fritz-ev-api`: Docker web service from `backend/Dockerfile`
   * `fritz-ev-web`: static site built from `frontend/`
2. Fill in the secret environment variables in the dashboard:
   * API: `SUPABASE_DB_URL`, `SUPABASE_DB_USER`, `SUPABASE_DB_PASSWORD`, `CORS_ALLOWED_ORIGINS=https://fritz-ev-web.onrender.com`
   * Web: `VITE_API_BASE_URL=https://fritz-ev-api.onrender.com`
3. Both get HTTPS automatically. Check `https://fritz-ev-api.onrender.com/actuator/health`, then run
   `BASE_URL=https://fritz-ev-api.onrender.com bash tests/curl/api-tests.sh`.

Notes:
* Free Render services sleep after about 15 minutes idle, so the first request after that takes around 30 to 60 seconds. Open the site before a demo.
* The front end and the API are on different domains, so the `supabase` profile sets the session cookie to `SameSite=None; Secure`, and CORS allows credentials only for `CORS_ALLOWED_ORIGINS`.

## Run the container locally (optional)

```bash
cd backend
docker build -t fritz-ev-api .
docker run -p 8080:8080 fritz-ev-api                # H2 demo data
docker run -p 8080:8080 --env-file .env fritz-ev-api  # Supabase
```
