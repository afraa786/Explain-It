# ExplainIt - Backend Project Analysis Engine

## Overview

ExplainIt is a Spring Boot 4.0.1 REST API backend that accepts a ZIP file containing any backend project (Java, Python, Node.js, etc.) and returns a structured JSON analysis of the project's architecture, frameworks, entry points, and API structure.

The service performs **static code analysis** without executing any code, making it safe for analyzing untrusted projects.

---

## Technology Stack

- **Java 17**
- **Spring Boot 4.0.1**
- **Spring MVC (REST)**
- **Maven**
- **Apache Commons Compress** (ZIP handling)
- **Jackson** (JSON serialization)

---

## Architecture

### Layered Architecture

```
Controller Layer (REST Endpoints)
    ↓
Service Layer (Orchestration)
    ↓
Analyzer Layer (9 Specialized Analyzers)
    ↓
Utility Layer (File I/O, Parsing)
```

### Components

#### 1. **Controllers** (`io.explainit.controller`)
- `AnalysisController`: Handles POST `/api/explain/analyze` endpoint
- Multipart file upload validation
- Error handling and response formatting

#### 2. **Services** (`io.explainit.service`)
- `ProjectAnalysisService`: Orchestrates all analyzers
- Coordinates analysis workflow
- Generates project summary
- Determines project type

#### 3. **Analyzers** (`io.explainit.analyzer`)

Each analyzer implements `IProjectAnalyzer` and targets specific aspects:

| Analyzer | Responsibility |
|----------|-----------------|
| `LanguageAnalyzer` | Detects programming languages (.java, .py, .js, etc.) |
| `FrameworkAnalyzer` | Identifies frameworks (Spring Boot, Django, Express, etc.) |
| `ConfigFileAnalyzer` | Locates configuration files (pom.xml, package.json, etc.) |
| `EntryPointAnalyzer` | Finds main entry points (@SpringBootApplication, main methods) |
| `BuildInfoAnalyzer` | Extracts build tool info, Java version, Spring Boot version |
| `ApiAnalyzer` | Detects REST controllers and routes (@RestController, @GetMapping, etc.) |
| `DataLayerAnalyzer` | Identifies database dependencies (JPA, Hibernate, PostgreSQL, etc.) |
| `SecurityAnalyzer` | Detects security configurations (Spring Security, OAuth2, etc.) |
| `ProjectStructureAnalyzer` | Analyzes directory structure and class counts |

#### 4. **Utilities** (`io.explainit.util`)

- `ZipExtractor`: Extracts ZIP files to temp directory, cleans up after analysis
- `FileScanner`: Recursively scans directories, counts files, finds specific files
- `PomParser`: Parses Maven pom.xml using DOM parser, extracts dependencies and versions

#### 5. **DTOs** (`io.explainit.dto`)

- `ProjectMetadata`: Main response object
- `EntryPoint`: Represents application entry points
- `ConfigFile`: Represents configuration files
- `ApiRoute`: Represents discovered API routes
- `BuildInfo`: Build tool and version information
- `ProjectStructure`: Directory structure and file counts

---

## API Endpoint

### POST `/api/explain/analyze`

**Description**: Analyzes a ZIP-compressed backend project and returns structured metadata.

#### Request
```
Content-Type: multipart/form-data

Parameter: file (MultipartFile)
- Must be a valid ZIP file
- Maximum size: 100MB
```

#### Response (200 OK)
```json
{
  "projectType": "Backend (REST API)",
  "languages": ["Java"],
  "frameworks": ["Spring Boot", "Spring MVC", "Spring Data JPA", "Spring Security"],
  "entryPoints": [
    {
      "file": "src/main/java/io/ExplainItApplication.java",
      "class": "io.ExplainItApplication",
      "method": "main(String[] args)",
      "type": "Spring Boot Application"
    }
  ],
  "configFiles": [
    {
      "file": "pom.xml",
      "type": "Maven Build Configuration",
      "purpose": "Dependency management and build automation"
    }
  ],
  "apiDetected": true,
  "apiRoutes": [
    {
      "method": "POST",
      "path": "/api/explain/analyze",
      "handler": "AnalysisController"
    }
  ],
  "dataLayerHints": [
    "spring-boot-starter-data-jpa (ORM framework)",
    "PostgreSQL driver detected"
  ],
  "securityHints": [
    "spring-boot-starter-security detected"
  ],
  "buildInfo": {
    "buildTool": "Maven",
    "javaVersion": "17",
    "springBootVersion": "4.0.1"
  },
  "projectStructure": {
    "sourceDirectory": "src/main/java",
    "resourcesDirectory": "src/main/resources",
    "testDirectory": "src/test/java",
    "currentClasses": 25
  },
  "summary": "This backend project uses Spring Boot with REST APIs, Spring Data JPA for data access, and Spring Security. It is structured using Maven and follows standard Spring conventions."
}
```

#### Error Response (400)
```json
{
  "error": "Only ZIP files are accepted"
}
```

#### Error Response (500)
```json
{
  "error": "Analysis failed",
  "message": "Error details..."
}
```

---

## Health Check

### GET `/api/explain/health`

**Description**: Checks if the service is running.

#### Response (200 OK)
```json
{
  "status": "UP"
}
```

---

## Building & Running

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

The service will start on `http://localhost:8080`

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=ExplainIt

# Server Configuration
server.port=8080

# File Upload Configuration
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# Logging
logging.level.io.explainit=DEBUG
```

---

## Supported Ecosystems

### Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Hibernate
- Maven / Gradle

### Python
- Django
- Flask
- FastAPI

### JavaScript / TypeScript
- Node.js
- Express
- NestJS
- React
- Next.js

### Other
- Go (net/http, Gin)
- Rust (Actix, Rocket)
- .NET / C#

---

## Project Type Detection

The analyzer automatically determines project type:

- **Backend (REST API)**: Maven/Gradle + Java with controllers
- **Backend / Full-Stack (Node.js)**: package.json + JavaScript/TypeScript
- **Backend (Python)**: Python with Django/Flask/FastAPI
- **Unknown**: Falls back when no clear indicators

---

## Scanning Logic Details

### 1. Language Detection
Scans all file extensions and maps to programming languages:
- `.java` → Java
- `.kt` → Kotlin
- `.py` → Python
- `.js` / `.ts` → JavaScript / TypeScript
- `.go` → Go
- `.rs` → Rust

### 2. Framework Detection
- Parses `pom.xml` for Maven dependencies
- Parses `build.gradle` for Gradle dependencies
- Scans `package.json` for Node.js packages
- Scans `requirements.txt` for Python packages

### 3. Entry Point Detection
- Finds classes with `@SpringBootApplication`
- Locates `main(String[] args)` methods
- Extracts fully qualified class names from package declarations

### 4. API Route Detection
- Scans for `@RestController` / `@Controller`
- Extracts routes from:
  - `@RequestMapping`
  - `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping`
  - `@PatchMapping`

### 5. Configuration File Detection
- Searches for standard config files
- pom.xml, build.gradle, application.properties, application.yml
- package.json, requirements.txt, docker-compose.yml, etc.

### 6. Build Tool Detection
- Identifies Maven or Gradle
- Extracts Java version from `<java.version>` or `sourceCompatibility`
- Extracts Spring Boot version from parent POM

### 7. Data Layer Detection
- Scans dependencies for JPA, Hibernate, JDBC
- Detects database drivers (PostgreSQL, MySQL, MongoDB, H2)
- Checks application properties for datasource config

### 8. Security Detection
- Detects Spring Security dependency
- Looks for OAuth2, JWT dependencies
- Finds SecurityConfig classes
- Detects WebSecurityConfigurerAdapter usage

---

## File Handling & Security

1. **ZIP Extraction**: Files extracted to temporary directory with unique name
2. **Cleanup**: Temporary directory is deleted immediately after analysis
3. **File Size Limits**: Maximum 100MB per file, configurable
4. **No Execution**: Only static analysis, no code execution
5. **Timeout**: Analysis runs in-memory without external process spawning

---

## Extensibility

To add support for new frameworks:

1. Create analyzer class implementing `IProjectAnalyzer`
2. Add to `ProjectAnalysisService.analyzers` list
3. Implement `analyze()` method
4. Update project type detection if needed

Example:
```java
public class CustomAnalyzer implements IProjectAnalyzer {
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        // Custom logic here
    }
}
```

---

## Performance Considerations

- **Temp Directory**: Uses system temp directory (`java.io.tmpdir`)
- **Memory**: Loads entire files into memory (suitable for typical project sizes)
- **Parallelization**: Could be enhanced with parallel stream processing for large projects
- **Caching**: Could cache analysis results by project hash

---

## Future Enhancements

- [ ] Support for more languages (Ruby, PHP, Go, Rust)
- [ ] Detailed service layer detection
- [ ] Database schema analysis
- [ ] Dependency vulnerability scanning
- [ ] Code complexity metrics
- [ ] Frontend framework detection
- [ ] Docker/Kubernetes configuration parsing
- [ ] CI/CD pipeline detection (GitHub Actions, Jenkins, etc.)
- [ ] Code documentation extraction
- [ ] Test coverage detection

---

## Error Handling

The service implements comprehensive error handling:

1. **Empty File**: Returns 400 with error message
2. **Invalid ZIP**: Returns 500 with error details
3. **Parsing Errors**: Gracefully handled, safe defaults used
4. **Temp Dir Cleanup**: Always executed in finally block
5. **XML Parse Errors**: Caught and logged, doesn't break analysis

---

## Example Usage

### cURL
```bash
curl -X POST -F "file=@project.zip" http://localhost:8080/api/explain/analyze
```

### JavaScript / Fetch
```javascript
const formData = new FormData();
formData.append('file', zipFile);

const response = await fetch('http://localhost:8080/api/explain/analyze', {
  method: 'POST',
  body: formData
});

const metadata = await response.json();
console.log(metadata);
```

### Python
```python
import requests

with open('project.zip', 'rb') as f:
    files = {'file': f}
    response = requests.post('http://localhost:8080/api/explain/analyze', files=files)
    metadata = response.json()
    print(metadata)
```

---

## License

This project is provided as-is for educational and commercial use.

---

## Author

ExplainIt Backend Analysis Engine
Spring Boot 4.0.1 | Java 17 | Maven
