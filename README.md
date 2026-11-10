# Fritz EV Online Store (EECS 4413, Fall 2026)

Web store for new and pre-owned electric vehicles.
Team: Tamjeed, Zaaid, Mohammed, Ronak.

| Layer | Tech |
|---|---|
| Back end | Java 21, Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation), Maven wrapper |
| Database | Supabase (PostgreSQL) in the cloud, H2 in-memory (PostgreSQL mode) locally, no setup needed |
| Front end | React 19 + Vite + Tailwind CSS 4 (`frontend/`). D2's no-CSS pages are in `backend/src/main/resources/static/` |
| Deployment | Docker + Render (API and static site), Supabase for the database. See [docs/deployment.md](docs/deployment.md) |

## Run it locally

Prerequisites: **JDK 17+** and **Node 20+**. Maven doesn't need to be installed, because the wrapper is included.

```bash
# 1. Back end  ->  http://localhost:8080
cd backend
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run

# 2. Front end ->  http://localhost:5173   (second terminal)
cd frontend
npm install
npm run dev
```

The back end starts an in-memory database and loads `schema.sql` and `data.sql` on every start, so each run begins with fresh demo data.

* D2 plain-HTML catalogue and calculator pages: http://localhost:8080/catalog
* The supplementary `backend/src/main/resources/catalog-seed.sql` is a merge fragment for Mohammed; it is not loaded automatically. The existing `data.sql` already seeds 14 vehicles and their options for local development.
* Basic D2 pages (plain HTML): http://localhost:8080/
* H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:fritzev`, user `sa`, no password)
* Health check: http://localhost:8080/actuator/health

Demo accounts:

| User | Password | Notes |
|---|---|---|
| `admin` | `Admin123!` | ADMIN, can run the reports |
| `alice` | `Password123!` | verified EV owner, can write reviews |
| `bob` | `Password123!` | **not** verified, used for the review rejection test |
| `carol` | `Password123!` | verified EV owner |

To run against Supabase instead of H2, see [docs/deployment.md](docs/deployment.md).

## Run the tests

```bash
cd backend && ./mvnw test                 # JUnit + MockMvc (T09, T10, T12, hot deals, validation)
bash tests/curl/api-tests.sh              # curl script, needs the back end running
BASE_URL=https://<deployed-api> bash tests/curl/api-tests.sh
```

## Repository layout

```
backend/                                  Spring Boot REST API
  src/main/java/ca/yorku/eecs4413/fritzev/
    controller/        M3  REST controllers, one per component. SessionAuth = login/admin guard
      dto/                 request/response records with validation annotations
    service/           M4-M14 business logic, one sub-package per component (interface + Impl)
      account/ catalog/ configurator/ cart/ order/ finance/
      recommendation/ testdrive/ review/ chatbot/ analytics/
    integration/       M15 payment adapter, M16 LLM adapter
    dao/               M17 Data Access Layer (Spring Data JPA repositories)
    domain/            JPA entities + enums (shared domain model)
    exception/         error types + GlobalExceptionHandler (every error -> JSON)
    config/            CORS, BCrypt password encoder
  src/main/resources/
    schema.sql         M18 tables (portable: PostgreSQL + H2)
    data.sql           seed data
    application*.yml   dev (H2) and supabase profiles
    static/            D2 plain-HTML pages (no CSS)
  src/test/java/       JUnit tests
  Dockerfile
frontend/                                 React + Tailwind (D3 UI)
  src/api/             one file per back-end area; all fetch calls go through client.js
  src/pages/           one page per route (stubs show the owner)
  src/components/      layout/, ui/, vehicles/
  src/context/         AuthContext (signed-in user)
tests/curl/            curl test script       tests/postman/  Postman collection
docs/                  deployment guide, AI-use log
render.yaml            one-click cloud deployment (Render Blueprint)
```

## How to add a feature (everyone)

1. Find your stub. Every unfinished method throws `NotImplementedYetException`, which the API returns as **HTTP 501** with your name in the message. Search the code for `TODO(<your name>)`.
2. Write the logic in `service/<component>/<Name>ServiceImpl.java`. The DAOs you need are already injected.
3. Controllers and DTOs already exist. Only change them if the API contract has to change, and tell the team if you do.
4. Report errors by throwing from `exception/` (`ResourceNotFoundException`, `BadRequestException`, `ConflictException`, `ForbiddenException`, `UnauthorizedException`). Don't build error responses by hand.
5. Never return JPA entities from a controller. Map them to a DTO, because lazy relations break JSON serialisation.
6. Schema changes go in `schema.sql` **and** the entity (Hibernate checks they match on start-up). Seed data goes in `data.sql`.
7. Add your curl checks to `tests/curl/api-tests.sh` and JUnit tests to `backend/src/test/java`.
8. Work on a branch (`feature/<name>-<thing>`) and open a PR. Run `./mvnw test` before you push.
9. If AI wrote any code, log it in [docs/ai-usage-log.md](docs/ai-usage-log.md). The repository records the written 30% course rule and the separate instructor exception reported by the student, pending authentic evidence.

## Who owns what (D2)

| Owner | Back end | Front end |
|---|---|---|
| **Mohammed** | Stack, repo, schema + seed data, DAOs, REST controllers + validation, hot deals, deployment. LLM chatbot in D3 | Hot deals pages |
| **Zaaid** | `AccountServiceImpl`, `CartServiceImpl`, `CheckoutServiceImpl`, `MockPaymentGateway`, `ReviewServiceImpl.submitReview` | Login, register, cart, checkout pages, `AuthContext` |
| **Tamjeed** | `ConfiguratorServiceImpl`, `FinanceServiceImpl` (loan + UC-20), `CatalogServiceImpl.compareVehicles`. Recommendations in D3 | Catalogue, details, compare, calculators pages |
| **Ronak** | `TestDriveServiceImpl`, `AnalyticsServiceImpl` (reports + dashboard), curl/Postman tests, README test section | Test-drive and admin pages |

## API overview

| Method | Path | UC | Status |
|---|---|---|---|
| GET | `/api/vehicles?brand&shape&category&minYear&maxYear&noAccidents&sort&dir` | 04, 05 | done |
| GET | `/api/vehicles/{id}` | 06 | done |
| GET | `/api/vehicles/hot-deals?limit` | hot deals | done |
| GET | `/api/vehicles/compare?ids=1,2`, `/brands`, `/shapes` | 07,  filter UI | done |
| GET | `/api/vehicles/{id}/options`, POST `/api/vehicles/{id}/configuration` | 08 | done |
| POST | `/api/calculators/loan` | 09 | done |
| POST | `/api/vehicles/{id}/savings` | 20 | done |
| POST | `/api/recommendations` | 10 | stub (D3) |
| GET | `/api/vehicles/{id}/test-drive-slots?type&date`, POST/DELETE `/api/test-drives` | 11 | stub |
| GET / POST / PATCH / DELETE | `/api/cart`, `/api/cart/items/{itemId}` | 12, 13 | implemented |
| POST | `/api/checkout/start`, PUT `/{id}/shipping`, POST `/{id}/place-order` | 14 | implemented |
| GET / POST | `/api/vehicles/{id}/reviews` | 15 | implemented |
| POST | `/api/auth/register`, `/login`, `/logout`, GET `/api/auth/me`, GET `/api/auth/csrf` | 01-03 | implemented |
| POST | `/api/chat` | 16 | stub (D3) |
| POST | `/api/events` | 18 | stub |
| GET | `/api/admin/reports/sales`, `/usage`, `/api/admin/dashboard` | 17-19 | stub (admin guard done) |

Errors always come back as `{"status","error","message","path","fieldErrors"}`.
