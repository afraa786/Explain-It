# ExplainIt Architecture - Visual Overview

## System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                      REST API Layer                             │
│              (/analyze endpoint)                                │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│              ProjectAnalysisService                             │
│                                                                  │
│  1. Run FrameworkDetector → FrameworkDetectionResult           │
│  2. Calculate ProjectSize → ProjectSizeInfo                    │
│  3. Run all analyzers → Populate metadata                      │
│  4. Generate summary → Intelligent text                        │
└──────────────────────────┬──────────────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Framework    │  │ Project Size │  │ Analyzer     │
│ Detector     │  │ Calculator   │  │ Pipeline     │
└──────────────┘  └──────────────┘  └──────────────┘
        │                  │                  │
        │                  │     ┌────────────┼────────────┬─────────┐
        │                  │     │            │            │         │
        ▼                  ▼     ▼            ▼            ▼         ▼
┌──────────────┐  ┌──────────────┐  ┌───────────────┐ ┌─────────┐ ┌──────┐
│ Language     │  │ Recursive    │  │ Data Layer    │ │Security │ │API   │
│ Detection    │  │ Traversal    │  │ Analyzer      │ │Analyzer │ │&     │
│              │  │              │  │ (REFACTORED)  │ │(REF)    │ │Other │
│ - Weighted   │  │ - Smart      │  │               │ │         │ │      │
│   scoring    │  │   exclusion  │  │ ✓ Database    │ │✓ Auth   │ │      │
│ - Version    │  │ - MB + count │  │ ✓ ORM         │ │✓ Encrypt│ │      │
│   detection  │  │ - File count │  │ ✓ Migrations  │ │✓ CORS   │ │      │
└──────────────┘  └──────────────┘  │ ✓ Pooling    │ │✓ Config │ │      │
                                     └───────────────┘ └─────────┘ └──────┘
```

## Multi-Framework Detection Flow

```
                    Project Root Path
                          │
                          ▼
            ┌─────────────────────────────┐
            │   FrameworkDetector         │
            └──────────────┬──────────────┘
                           │
        ┌──────────────────┼──────────────────┬─────────────┐
        │                  │                  │             │
        ▼                  ▼                  ▼             ▼
   ┌─────────┐      ┌──────────┐      ┌──────────┐   ┌───────────┐
   │Language │      │Framework │      │Build     │   │Versions   │
   │Detection│      │Detection │      │Systems   │   │Extraction │
   └─────────┘      └──────────┘      └──────────┘   └───────────┘
        │                  │                  │             │
        └──────────────────┼──────────────────┴─────────────┘
                           │
                           ▼
            ┌─────────────────────────────┐
            │FrameworkDetectionResult     │
            │                             │
            │ - Primary Language          │
            │ - Language Version          │
            │ - All Languages             │
            │ - Frameworks (HIGH conf)    │
            │ - Build System (HIGH conf)  │
            └─────────────────────────────┘
```

## Database & ORM Detection Across Languages

```
        DataLayerAnalyzer
              │
    ┌─────────┼─────────┐
    │         │         │
    ▼         ▼         ▼
 [Java]    [Python]  [Node.js]
    │         │         │
    ├─────┬───┴─┬───┬───┴────┐
    │     │     │   │        │
    ▼     ▼     ▼   ▼        ▼
  Java  Gradle Pip PyProject NPM
  pom.xml       Requirements      package.json
    │     │     │   │        │
    └─────┴─────┴───┴────────┘
            │
    ┌───────┴───────────────────┐
    │                           │
    ▼                           ▼
┌─────────────┐         ┌───────────────┐
│ Databases   │         │ ORMs & Tools  │
│             │         │               │
│✓ PostgreSQL │         │✓ JPA          │
│✓ MySQL      │         │✓ Hibernate    │
│✓ MongoDB    │         │✓ Django ORM   │
│✓ Redis      │         │✓ SQLAlchemy   │
│✓ SQLite     │         │✓ Prisma       │
│✓ Oracle     │         │✓ TypeORM      │
│✓ SQL Server │         │✓ Sequelize    │
│✓ H2         │         │✓ Mongoose     │
│             │         │               │
└─────────────┘         └───────────────┘
```

## Security Detection Across Frameworks

```
    SecurityAnalyzer
          │
    ┌─────┼─────┐
    │     │     │
    ▼     ▼     ▼
[Spring] [Python] [Node.js]
    │     │       │
    │  ┌──┴──┐    │
    │  │     │    │
    ▼  ▼     ▼    ▼
    │ Auth  REST  │
    │ Libs  Auth  │
    │       │     │
┌───┴────┬──┴─────┴────┐
│        │             │
▼        ▼             ▼
┌─────────┐    ┌──────────┐    ┌──────────┐
│Crypto   │    │Auth      │    │Network   │
│         │    │          │    │Security  │
│✓BCrypt  │    │✓Spring   │    │✓CORS     │
│✓Bounc.  │    │✓OAuth2   │    │✓Helmet   │
│✓Crypt   │    │✓JWT      │    │✓Headers  │
│         │    │✓Passport │    │✓.env     │
└─────────┘    │✓Django   │    └──────────┘
               │✓Auth0    │
               └──────────┘
```

## Request -> Response Flow

```
┌─────────────────────────────┐
│  POST /analyze              │
│  Project ZIP File           │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ Extract & Validate Project  │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ 1. FrameworkDetector        │
│    → Framework Detection    │
│       Result                │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ 2. ProjectSizeCalculator    │
│    → Project Size Info      │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ 3. Run All Analyzers        │
│    → Layer Detections       │
│    → Security Detections    │
│    → API Routes             │
│    → Config Files           │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ Generate Summary            │
│ Combine all findings        │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│ ProjectMetadata Response            │
│                                     │
│ {                                   │
│   projectType,                      │
│   frameworkDetection {...},         │
│   projectSize {...},                │
│   dataLayerDetections [...],        │
│   securityDetections [...],         │
│   apiRoutes [...],                  │
│   summary: "...",                   │
│   ... (other fields)                │
│ }                                   │
└─────────────────────────────────────┘
```

## DetectionResult Structure

```
DetectionResult
│
├─ name: String
│  Example: "Spring Boot", "PostgreSQL", "JWT"
│
├─ type: String
│  Examples: "Framework", "Database", "ORM", "Authentication", "Encryption"
│
├─ confidence: Enum
│  Values: HIGH | MEDIUM | LOW
│
├─ reason: String (Human-readable explanation)
│  Example: "spring-boot-starter-web dependency found in pom.xml"
│
├─ evidence: List<String>
│  Examples:
│    - "Dependency: org.springframework.boot:spring-boot-starter-web:3.0.0"
│    - "File: manage.py"
│    - "Package: django"
│
└─ version: String (Optional)
   Example: "3.0.0", "3.10", "16.13.0"
```

## Confidence Scoring Rules

```
┌─────────────────────────────────────────────────┐
│         Confidence Level Determination          │
├─────────────────────────────────────────────────┤
│                                                 │
│  HIGH Confidence:                               │
│  ✓ Explicit dependency found                    │
│  ✓ Multiple signals present                     │
│  ✓ Config file with settings                    │
│  ✓ Primary detection method used                │
│                                                 │
│  MEDIUM Confidence:                             │
│  ✓ Single signal present                        │
│  ✓ Likely pattern (e.g., app.py = Flask)       │
│  ✓ Transitive dependency                       │
│                                                 │
│  LOW Confidence:                                │
│  ✓ Weak signal only                             │
│  ✓ Inference from naming                        │
│  ✓ Requires verification                        │
│                                                 │
└─────────────────────────────────────────────────┘
```

## Excluded Directories (Size Calculation)

```
ProjectRoot/
├─ .git/                    ← EXCLUDED
├─ node_modules/            ← EXCLUDED
├─ target/                  ← EXCLUDED
├─ build/                   ← EXCLUDED
├─ dist/                    ← EXCLUDED
├─ .venv/                   ← EXCLUDED
├─ venv/                    ← EXCLUDED
├─ .next/                   ← EXCLUDED
├─ __pycache__/             ← EXCLUDED
├─ .gradle/                 ← EXCLUDED
├─ .maven/                  ← EXCLUDED
├─ .m2/                     ← EXCLUDED
├─ .idea/                   ← EXCLUDED
├─ .vscode/                 ← EXCLUDED
│
├─ src/                     ← INCLUDED ✓
├─ lib/                     ← INCLUDED ✓
├─ public/                  ← INCLUDED ✓
├─ migrations/              ← INCLUDED ✓
├─ pom.xml                  ← INCLUDED ✓
├─ package.json             ← INCLUDED ✓
└─ requirements.txt         ← INCLUDED ✓
```

## Technology Support Matrix

```
┌──────────────┬──────────┬──────────┬──────────────┐
│   Feature    │   Java   │ Python   │ JavaScript/TS │
├──────────────┼──────────┼──────────┼──────────────┤
│Framework     │ Spring   │ Django   │ Express      │
│Detection     │ Quarkus  │ Flask    │ NestJS       │
│              │ Micronaut│ FastAPI  │ React        │
├──────────────┼──────────┼──────────┼──────────────┤
│Build System  │ Maven    │ Poetry   │ NPM          │
│              │ Gradle   │ Pip      │ Yarn         │
├──────────────┼──────────┼──────────┼──────────────┤
│Databases     │ All 8    │ All 8    │ All 8        │
│              │ (via dep)│ (via dep)│ (via dep)    │
├──────────────┼──────────┼──────────┼──────────────┤
│ORMs          │JPA, HIB. │Django ORM│ Prisma       │
│              │MyBatis   │SQLAlch.  │ TypeORM      │
│              │          │Tortoise  │ Sequelize    │
├──────────────┼──────────┼──────────┼──────────────┤
│Migration     │Flyway    │Alembic   │Prisma Migr.  │
│Tools         │Liquibase │Django    │TypeORM       │
├──────────────┼──────────┼──────────┼──────────────┤
│Authentication│Spring    │Django    │ Passport     │
│              │Security  │REST Auth │ JWT          │
│              │OAuth2    │PyJWT     │ Auth0        │
├──────────────┼──────────┼──────────┼──────────────┤
│Encryption    │BCrypt    │BCrypt    │ BCryptjs     │
│              │Bouncy    │Crypto    │ Helmet       │
│              │Castle    │          │              │
└──────────────┴──────────┴──────────┴──────────────┘
```

## File Size Calculation Logic

```
FOR EACH file in project:
  │
  ├─ Is file in EXCLUDED_DIR? 
  │  ├─ YES → Skip file
  │  └─ NO → Add file size to total
  │
  └─ Increment file count

CALCULATION:
  total_size_mb = (total_size_bytes / (1024 * 1024)) 
                  rounded to 2 decimal places

BENEFITS:
  ✓ No node_modules/target bloat
  ✓ Realistic source code size
  ✓ 50-70% smaller than raw directory
  ✓ Comparable across projects
```

## Summary Generation Logic

```
BuildSummary(metadata):
  │
  ├─ Add Project Type
  │   "Backend (Java)" / "Backend (Python)" / etc.
  │
  ├─ Add Frameworks (from detection)
  │   "Spring Boot, Spring Data JPA"
  │
  ├─ Add Build System
  │   "Maven" / "Pip" / "NPM"
  │
  ├─ Add Data Layer (top 2)
  │   "PostgreSQL, JPA"
  │
  ├─ Add Size Info
  │   "42.37 MB (1284 files)"
  │
  └─ Add Security (top 2)
      "Spring Security, JWT"

OUTPUT: Single, human-readable line
EXAMPLE: "Backend (Java) | Frameworks: Spring Boot, Spring Data JPA 
          | Build: Maven | Data Layer: PostgreSQL, JPA | 
          Size: 42.37 MB (1284 files) | Security: Spring Security, JWT"
```

---

## Quick Navigation

- **Detailed Architecture** → ARCHITECTURE_REFACTOR.md
- **API Changes** → API_MIGRATION_GUIDE.md
- **Testing Guide** → TESTING_AND_VALIDATION.md
- **Implementation Summary** → REFACTOR_SUMMARY.md
- **File Reference** → FILE_REFERENCE.md
