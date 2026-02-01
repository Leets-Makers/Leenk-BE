# CLAUDE.md

Project guidance for Claude Code (claude.ai/code).

## Project Overview

**Leenk** - Social feed platform backend

| Stack | Details |
|-------|---------|
| Language | Java 21 (Kotlin migration in progress) |
| Framework | Spring Boot 3.5.9 |
| Build | Gradle Kotlin DSL |
| Database | MySQL (relational), MongoDB (notifications) |
| Cloud | AWS S3 (media), SQS (messaging) |

## Quick Start

```bash
./gradlew build          # Build project
./gradlew bootRun        # Run on port 8081
./gradlew test           # Run all tests
./gradlew ktlintCheck    # Check code style
./gradlew ktlintFormat   # Auto-format code
```

## Architecture

**Domain-Driven Design** with layered architecture:

```
Controller → UseCase → Domain Service → Repository
```

```
leets.leenk/
├── domain/{name}/
│   ├── application/     # usecase/, dto/, mapper/, exception/
│   ├── domain/          # entity/, repository/, service/
│   └── presentation/    # controllers
└── global/              # auth/, config/, common/, sqs/
```

### Key Domains

- **feed** - Feed management with media and reactions
- **leenk** - Scheduled meetups with participants
- **notification** - Real-time notifications (MongoDB)
- **user** - User profiles, settings, blocking
- **media** - S3 media management

### Service Naming Convention

Split services by operation type:
- `{Domain}GetService` - Read operations
- `{Domain}SaveService` - Create operations
- `{Domain}UpdateService` - Update operations
- `{Domain}DeleteService` - Delete operations

## Code Style

- **ktlint** enforced (v1.8.0) - run `./gradlew ktlintFormat` before committing
- Exceptions extend `BaseException` with domain-specific `ErrorCode`
- Use `@CurrentUserId` annotation for authenticated user in controllers
- Soft deletes via `deletedAt` field

## Commit Convention

Format: `type: 본문` (type in English, body in Korean)

| Type | Description |
|------|-------------|
| feat | New feature |
| fix | Bug fix |
| refactor | Code refactoring |
| test | Test code changes |
| docs | Documentation |
| style | Code formatting |
| chore | Maintenance tasks |

Examples:
```
feat: 피드 상세 조회 API 추가
fix: 공감하기 중복 처리 버그 수정
refactor: dto validation 수정
```

## Testing

- **Kotest** + **MockK** for unit tests
- **springmockk** for Spring bean mocking - use `@MockkBean` instead of `@MockBean`
- **Testcontainers** for integration tests (MySQL, MongoDB)
- Test both success and failure scenarios
- **Kotest Test Styles:**
  - `StringSpec` - Simple tests with minimal boilerplate
  - `BehaviorSpec` - BDD-style tests (Given/When/Then)
  - `DescribeSpec` - Technical specs requiring detailed structure and readability
- Controller tests: Use `@WebMvcTest` with `@Import` for Security config testing

## Notes

- Swagger UI: `/swagger-ui.html`
- Profiles: `local` (default), `dev`
- Auth: OAuth2 Resource Server with JWT (Apple via Kakao)
