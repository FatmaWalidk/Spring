# Spring Boot Backend Cheatsheet

The classes/interfaces/config that show up in almost every Spring Boot
REST backend, regardless of domain. Use `taskmanager/` as the worked
example for each one.

## 1. Package structure

Package by **feature**, not by layer. Each feature folder owns its full
vertical slice (controller, service, repository, dto, mapper):

```
com.yourcompany.yourapp/
├── YourAppApplication.java
├── <feature>/                  e.g. user/, order/, product/
│   ├── <Entity>.java
│   ├── <Entity>Repository.java
│   ├── <Entity>Service.java
│   ├── <Entity>Controller.java
│   ├── <Entity>Mapper.java
│   └── dto/
├── common/
│   ├── config/                 SecurityConfig, CacheConfig, etc.
│   └── exception/               GlobalExceptionHandler + custom exceptions
└── auth/ (if you have auth)     security/, refresh/, dto/
```

Avoid `controller/`, `service/`, `repository/` as top-level packages -
finding everything related to "orders" shouldn't require jumping between
five unrelated folders.

## 2. Every layer, per feature

- **Entity** (`@Entity`) - maps to a table. Keep persistence concerns
  only; no business logic, no validation annotations (those belong on
  DTOs).
- **Repository** (`interface X extends JpaRepository<Entity, Id>`) -
  never write an implementation; Spring generates it. Add derived query
  methods (`findByEmail`, `existsByEmail`) as needed.
- **DTOs** (`record`) - one for input (`CreateXRequest`/`UpdateXRequest`
  with `@Valid` annotations), one for output (`XResponse`). Never expose
  the entity directly - it leaks fields you don't want public (password,
  internal flags) and couples your API to your database schema.
- **Mapper** (MapStruct `@Mapper(componentModel = "spring")`) - declare
  the contract, let MapStruct generate the implementation. Use
  `@Mapping(target = "...", ignore = true)` for fields that are business
  rules, not straight copies (password hashing, default role, computed
  fields).
- **Service** (`@Service`) - owns business logic, transactions, caching
  annotations, and authorization rules like ownership checks. Depends on
  the Repository interface, never a concrete implementation.
- **Controller** (`@RestController`) - thin. Receives the request,
  validates it (`@Valid`), delegates to the Service, returns a DTO. No
  business logic here.

## 3. Config classes almost every project needs

- **`SecurityConfig`** (`@Configuration @EnableWebSecurity`) - four
  beans: `PasswordEncoder`, `AuthenticationProvider`,
  `AuthenticationManager`, `SecurityFilterChain`. Stateless JWT setups
  disable CSRF and set `SessionCreationPolicy.STATELESS`.
- **`GlobalExceptionHandler`** (`@RestControllerAdvice`) - one
  `@ExceptionHandler` per custom exception, mapped to the right HTTP
  status. Always return a consistent `ErrorResponse` shape (timestamp,
  status, error, message, path).
- **`CacheConfig`** (`@Configuration`, if caching) - returns
  `CacheManager` (the interface, not `CaffeineCacheManager` directly) so
  swapping to Redis later only touches this one bean.
- **`*Properties`** records (`@ConfigurationProperties(prefix = "...")`)
  - one per logical group of settings (`JwtProperties`,
  `FileStorageProperties`, `MailProperties`...) instead of scattering
  `@Value("${...}")` everywhere. Register with
  `@ConfigurationPropertiesScan` on the main application class.

## 4. Auth module, if your app needs users/login

- `User` entity + `Role` enum (`@Enumerated(EnumType.STRING)`)
- `ApplicationUserDetails implements UserDetails` - wraps your entity,
  keeps persistence and security concerns separate
- `ApplicationUserDetailsService implements UserDetailsService` - the
  bridge Spring Security uses to load a user by username/email
- `JwtService` - generate/parse/validate tokens; depends on
  `UserDetails`, never your entity directly
- `JwtAuthenticationFilter extends OncePerRequestFilter` - reads the
  `Authorization` header once per request, populates the
  `SecurityContext`
- `AuthController` / `AuthService` - register/login/refresh; the service
  never compares passwords itself, it delegates to
  `AuthenticationManager`

## 5. The "program to an interface" habit

Shows up everywhere in a well-structured Spring app:
- inject `UserDetailsService`, not `ApplicationUserDetailsService`
- inject `CacheManager`, not `CaffeineCacheManager`
- inject `FileStorageService`, not `LocalFileStorageService`
- depend on `JpaRepository<Entity, Id>`, never a hand-written
  implementation

The concrete class can change (local disk -> S3, Caffeine -> Redis)
without touching anything that depends on the interface.

## 6. Config files

- `application.properties` - defaults + `spring.profiles.active`
- `application-dev.properties` - local DB, verbose logging,
  `ddl-auto=update`
- `application-prod.properties` - everything from env vars,
  `ddl-auto=validate` (schema managed by migrations, not Hibernate)
- `application-test.properties` - H2 in-memory DB, fast, isolated
- `docker-compose.yml` - at minimum, spin up the database locally so
  `dev` doesn't require installing Postgres/MySQL by hand

## 7. Standard dependency set (pom.xml)

`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, your JDBC
driver, `spring-boot-starter-validation`, `spring-boot-starter-security`
+ a JWT library (JJWT) if you're rolling your own auth, `lombok`,
`mapstruct` (+ `mapstruct-processor` in the compiler plugin), and
`spring-boot-starter-test` + `spring-security-test` for testing. Add
`spring-boot-starter-cache` + `caffeine` only once you actually need
caching - don't add it preemptively.

## 8. Checklist when starting a new feature

1. Entity + Repository
2. Request/Response DTOs with validation annotations
3. Mapper interface
4. Service (business logic, transactions, any caching/ownership rules)
5. Controller (thin, delegates only)
6. Custom exceptions + a case in `GlobalExceptionHandler`
7. Wire it into `SecurityConfig`'s `authorizeHttpRequests` if it needs a
   non-default access rule
