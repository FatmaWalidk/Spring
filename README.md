# Task Manager - Spring Boot Reference Project

Reconstructed from your Spring Boot course transcript. This is meant as a
**reference you can copy patterns from** when starting new backend
projects, not just a one-off app. See `docs/spring-cheatsheet.md` for the
generic "what almost every Spring Boot project needs" guide.

> Note: the base package is `com.fatma.taskmanger` (missing the second
> "a" in "manager") to match your original project exactly. Rename the
> package in your IDE (Refactor -> Rename) if you'd rather fix the typo
> going forward - IntelliJ will update every file automatically.

## Running it

1. Start Postgres: `docker compose up -d`
2. Set the JWT secret (must be Base64, 256+ bits):
   ```
   export JWT_SECRET=$(openssl rand -base64 32)
   ```
3. Run: `./mvnw spring-boot:run` (defaults to the `dev` profile)

## Module map (mirrors the course, in the order you learned them)

| Package                          | Course module                              |
|-----------------------------------|---------------------------------------------|
| `user/`                           | Fundamentals, REST APIs, DTOs, Validation    |
| `common/exception/`               | Exception Handling                           |
| `task/`                           | JPA Relationships (@ManyToOne, @ManyToMany)  |
| `auth/security/`, `common/config` `SecurityConfig` | Spring Security + JWT           |
| `auth/refresh/`                   | Refresh Tokens                               |
| `common/config` `CacheConfig`     | Caching (Caffeine)                           |
| `storage/`                        | File Uploads                                 |
| `src/main/resources/application-*.properties`, `docker-compose.yml` | Profiles / Docker (course was cut short here) |

## Endpoints

- `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`
- `GET/PATCH/DELETE /users/{id}`, `GET /users`
- `POST/GET/PUT/DELETE /tasks`, `/tasks/{id}` (JWT required, ownership enforced)
- `POST /files`, `GET /files/{filename}`, `DELETE /files/{filename}`

## What was reconstructed vs. what came verbatim from your transcript

Everything in `auth/`, `common/`, `user/`, `storage/`, and `CacheConfig`
reflects the exact final design you and the assistant arrived at in the
course (including every revision - e.g. AuthenticationManager-based login,
refresh token rotation with hashed storage, the production-hardened
LocalFileStorageService).

The `task/` package (Task, Tag, TaskController, TaskService) follows the
package structure your course explicitly planned out, but the CRUD
implementation itself wasn't pasted in the transcript beyond the entity
relationships teaching example - I built it using the exact same
Controller -> Service -> Repository -> Mapper pattern used for `user/`,
plus the ownership check pattern (a task can only be read/edited/deleted
by its owner), so it's consistent with everything else but is new code,
not recovered code.
