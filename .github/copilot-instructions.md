# Deep-Dive Project - Copilot Instructions

## Project Overview

Deep-Dive is a Spring Boot 4.0.1 application (Java 21) for RSS feed ingestion with heuristic-based article filtering and machine learning dataset management. The application runs in two distinct modes controlled by Spring profiles.

## Build, Test, and Run Commands

### Build
```bash
./mvnw clean install
./mvnw clean package  # Skip tests
./mvnw compile        # Compile only
```

### Run Tests
```bash
./mvnw test                                    # Run all tests
./mvnw test -Dtest=ClassName                   # Run specific test class
./mvnw test -Dtest=ClassName#methodName        # Run specific test method
```

### Run Application

**Ingestion Mode** (RSS feed processing):
```bash
SPRING_PROFILES_ACTIVE=ingestion ./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="https://example.com/feed.rss"
```

**Classifier Mode** (CSV dataset loading):
```bash
SPRING_PROFILES_ACTIVE=classifier ./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="./src/main/resources/dataset/dataset.csv ,|;|\\t"
```

Or use the convenience script:
```bash
./run.sh  # Runs classifier mode with default dataset
```

### Database
```bash
docker compose up -d    # Start PostgreSQL
docker compose down     # Stop PostgreSQL
```

## Architecture

### Hexagonal Architecture (Ports & Adapters)

The codebase follows Domain-Driven Design with strict layer separation:

- **Domain Layer** (`ingestion/domain/`): Pure business logic
  - `domain/model/`: Immutable records (Article, Source)
  - `domain/port/`: Interfaces defining contracts (FeedFetcher, ArticleRepository, SourceRepository)
  - `domain/service/`: Business logic (FeedRefinery orchestrates filtering)
  
- **Adapter Layer** (`ingestion/adapter/`): Infrastructure implementations
  - `adapter/rss/`: RSS feed parsing (RomeRssFetcher implements FeedFetcher)
  - `adapter/persistence/`: JPA repositories implementing domain ports
  
- **Application Layer**: Orchestration (DeepDiveRunner, ClassifierRunner)

**Key Principle**: Domain layer has NO dependencies on frameworks or infrastructure. Adapters depend on domain ports, never vice versa.

### Package Structure

```
com.ktotopawel.deepdive/
├── application/         # Application orchestration (CommandLineRunners)
├── ingestion/
│   ├── domain/
│   │   ├── model/       # Domain records (immutable)
│   │   ├── port/        # Domain interfaces
│   │   └── service/     # Business logic & filters
│   └── adapter/
│       ├── rss/         # External feed fetching
│       └── persistence/ # Database access
└── classifier/          # ML dataset management (separate bounded context)
```

## Key Conventions

### Naming Conventions

1. **Entity Classes**: Suffix with `Entity` (ArticleEntity, SourceEntity) to distinguish from domain models
2. **Domain Models**: Use simple names (Article, Source) - they are immutable records
3. **Port Interfaces**: Named after domain capabilities (FeedFetcher, ArticleRepository)
4. **Spring Data Repositories**: Prefix with `SpringData` (SpringDataArticleRepository) to distinguish from domain port interfaces
5. **Filters**: Descriptive domain names + `Filter` suffix (ReleaseNoteFilter, TutorialFilter)

### Domain Model Patterns

- **Immutable Records**: All domain models (Article, Source) use Java records for immutability
- **Constructor Injection**: Services use Lombok's `@RequiredArgsConstructor` with final fields
- **No Framework in Domain**: Domain layer has no Spring, JPA, or Hibernate annotations

### Filter Architecture

All filters extend `ingestion/domain/service/filter/Filter.java`:
- Calculate numeric scores based on multiple heuristics
- Return `score >= 50` to filter out articles
- Each filter focuses on a specific article type (release notes, repositories, tutorials)

When adding new filters:
1. Extend the `Filter` abstract class
2. Implement `calculateScore(Article)` method
3. Register as `@Component` to be auto-detected by `FeedRefinery`
4. Use scoring approach: individual checks return points, sum must reach 50 to filter

### Database Conventions

- **Hibernate DDL**: Set to `update` mode - schema auto-migrates
- **Batch Size**: Configure batch operations at 50 records (`hibernate.jdbc.batch_size=50`)
- **Ordered Operations**: Always enable `hibernate.order_inserts=true` and `hibernate.order_updates=true`
- **Primary Keys**: 
  - Natural keys for deduplicated entities (Article.title, Source.url)
  - Generated sequences for training data (TrainingArticle.id)

### Spring Profile Usage

The application uses profiles to select operational modes (mutually exclusive):
- `ingestion`: Activates RSS feed processing with article filtering
- `classifier`: Activates CSV dataset loading for ML training data

Components should use `@Profile("profile-name")` for mode-specific beans (e.g., `DeepDiveRunner`, `ClassifierRunner`).

## Domain-Specific Logic

### Article Filtering Pipeline

1. **Fetch**: `RomeRssFetcher` parses RSS/Atom feeds using Rome library
2. **Refine**: `FeedRefinery` applies all registered `Filter` implementations
3. **Score**: Each filter calculates a score (0-100+); score ≥ 50 means "filter out"
4. **Persist**: Refined articles saved via `JpaArticleRepository`

### Classifier Data Processing

1. **Parse**: `DataSetLoader` uses univocity-parsers for CSV handling
2. **Validate**: Filter blank entries, normalize categories (uppercase, trim)
3. **Batch**: Save in batches of 50 for optimal Hibernate performance
4. **Schema**: Expects CSV with `category,title,body` columns

## Configuration

### Environment Variables

Required in `.env` (not committed):
```
POSTGRES_USER=postgres
POSTGRES_PASSWORD=<password>
POSTGRES_DB=deepdive_db
```

### Application Properties

Located in `src/main/resources/application.properties`:
- Database connection configured via `spring.datasource.*`
- Hibernate settings in `spring.jpa.properties.hibernate.*`
- Profile-specific properties can be added in `application-{profile}.properties`

## Dependencies & Libraries

- **rome** (2.1.0): RSS/Atom feed parsing - use for all feed operations
- **univocity-parsers** (2.9.1): CSV parsing - preferred over manual parsing
- **jsoup** (1.17.2): HTML parsing capability (included but currently unused)
- **lombok**: Use for reducing boilerplate (`@RequiredArgsConstructor`, `@Slf4j`, etc.)
- **JetBrains annotations**: Use `@NotNull` for null-safety hints

## Testing

### Test Structure
- Tests located in `src/test/java/com/ktotopawel/deepdive/`
- Currently minimal coverage (only context load test exists)
- Use `@SpringBootTest` for integration tests

### Testing Domain Logic
- Domain models and services should be unit-testable without Spring context
- Mock port interfaces for isolated testing
- Adapters require integration tests with testcontainers or embedded databases

## Common Tasks

### Adding a New Filter

1. Create class in `ingestion/domain/service/filter/` extending `Filter`
2. Implement `calculateScore(Article article)` with heuristics
3. Add `@Component` annotation for auto-registration
4. Return score ≥ 50 to filter article, < 50 to keep

### Adding New RSS Sources

No code changes needed - pass RSS URL as command-line argument when running in ingestion profile.

### Modifying Database Schema

Edit JPA entity classes (ArticleEntity, SourceEntity, TrainingArticle). Hibernate will auto-update schema on next run (DDL mode: `update`).

### Adding Profile-Specific Configuration

1. Create `application-{profile}.properties` in `src/main/resources/`
2. Use `@Profile("{profile}")` on beans that should only load for that profile
3. Set `SPRING_PROFILES_ACTIVE={profile}` when running
