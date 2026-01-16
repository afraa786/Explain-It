# ExplainIt - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### 1. Build the Project
```bash
cd c:\Users\Afraa\OneDrive\Desktop\expo
mvn clean package
```

### 2. Run the Application
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

You should see:
```
Started ExplainItApplication in X seconds
```

### 3. Test the API

**Check Health:**
```bash
curl http://localhost:8080/api/explain/health
```

Response:
```json
{"status":"UP"}
```

**Analyze a Project:**
```bash
curl -X POST -F "file=@project.zip" http://localhost:8080/api/explain/analyze
```

---

## 📋 What ExplainIt Does

ExplainIt analyzes any backend project ZIP file and extracts:

- 📝 **Programming Languages** - Java, Python, JavaScript, etc.
- 🎯 **Frameworks** - Spring Boot, Django, Express, NestJS, etc.
- 🚪 **Entry Points** - Main classes and bootstrap methods
- 📂 **Configuration Files** - pom.xml, package.json, settings.gradle, etc.
- 🔌 **API Routes** - REST endpoints with HTTP methods
- 💾 **Data Layers** - Database frameworks (JPA, Hibernate, MongoDB, etc.)
- 🔒 **Security** - Spring Security, OAuth2, JWT, etc.
- 🔨 **Build Info** - Build tools, Java version, Spring Boot version
- 📊 **Project Structure** - Source dirs, test dirs, class counts

---

## 🎯 Example Workflow

### Step 1: Create a Test ZIP
```bash
# Create a test directory
mkdir test-project
cd test-project

# Copy your project
cp -r /path/to/your/backend-project/* .

# Create ZIP
cd ..
Compress-Archive -Path test-project -DestinationPath test-project.zip
```

### Step 2: Upload and Analyze
```bash
curl -X POST -F "file=@test-project.zip" http://localhost:8080/api/explain/analyze | jq .
```

### Step 3: View Results
```json
{
  "projectType": "Backend (REST API)",
  "languages": ["Java"],
  "frameworks": ["Spring Boot", "Spring MVC"],
  "entryPoints": [...],
  "configFiles": [...],
  "apiDetected": true,
  "apiRoutes": [...],
  "buildInfo": {...},
  "summary": "..."
}
```

---

## 🛠️ Project Structure

```
expo/
├── pom.xml                           (Maven configuration)
├── src/
│   ├── main/
│   │   ├── java/io/
│   │   │   ├── ExplainItApplication.java
│   │   │   └── explainit/
│   │   │       ├── analyzer/         (9 analyzers)
│   │   │       ├── controller/       (REST endpoint)
│   │   │       ├── service/          (Orchestration)
│   │   │       ├── util/             (File handling)
│   │   │       └── dto/              (Data models)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── README_EXPLAINIT.md               (Full documentation)
├── IMPLEMENTATION_SUMMARY.md         (Architecture details)
└── QUICKSTART.md                     (This file)
```

---

## 📚 Documentation

- **README_EXPLAINIT.md** - Complete API documentation and architecture
- **IMPLEMENTATION_SUMMARY.md** - Implementation details and design decisions
- **QUICKSTART.md** - This quick start guide

---

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Max file size for upload
spring.servlet.multipart.max-file-size=100MB

# Logging level for analyzers
logging.level.io.explainit=DEBUG
```

---

## 🔍 Supported Ecosystems

### ✅ Java
- Spring Boot / Spring MVC
- Spring Data JPA / Hibernate
- Spring Security
- Maven / Gradle

### ✅ Python
- Django / Flask / FastAPI

### ✅ JavaScript
- Node.js / Express / NestJS
- React / Next.js

### ✅ Other
- Go / Rust / .NET / Kotlin

---

## 🚨 Error Handling

### Empty File
```json
{"error": "File is empty"}
```

### Invalid ZIP Format
```json
{"error": "Only ZIP files are accepted"}
```

### Analysis Error
```json
{
  "error": "Analysis failed",
  "message": "Details about what went wrong"
}
```

---

## 📦 Dependencies

Main dependencies added for ExplainIt:

- **Spring Boot 4.0.1** - REST framework
- **Apache Commons Compress 1.26.0** - ZIP handling
- **Jackson** - JSON serialization
- **Spring MVC** - HTTP handling

---

## 🧪 Testing the Analyzers

Each analyzer runs independently:

```java
// In ProjectAnalysisService
for (IProjectAnalyzer analyzer : analyzers) {
    analyzer.analyze(projectRoot, metadata);
}
```

Analyzers:
1. LanguageAnalyzer - Detects .java, .py, .js, etc.
2. FrameworkAnalyzer - Parses pom.xml, package.json, etc.
3. ConfigFileAnalyzer - Finds config files
4. EntryPointAnalyzer - Finds main() methods
5. BuildInfoAnalyzer - Extracts versions
6. ApiAnalyzer - Scans @Controller classes
7. DataLayerAnalyzer - Finds database config
8. SecurityAnalyzer - Detects security frameworks
9. ProjectStructureAnalyzer - Counts files and classes

---

## 🔧 Adding New Analyzer

To support a new framework:

### 1. Create Analyzer Class
```java
package io.explainit.analyzer;

public class MyFrameworkAnalyzer implements IProjectAnalyzer {
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        // Your analysis logic
    }
}
```

### 2. Register in Service
```java
// In ProjectAnalysisService.java
private final List<IProjectAnalyzer> analyzers = Arrays.asList(
    new LanguageAnalyzer(),
    // ... existing analyzers ...
    new MyFrameworkAnalyzer()  // Add here
);
```

---

## 🎯 Common Use Cases

### Analyze a Spring Boot Project
```bash
cd spring-boot-project
zip -r project.zip .
curl -X POST -F "file=@project.zip" http://localhost:8080/api/explain/analyze
```

### Analyze a Node.js Project
```bash
cd nodejs-project
zip -r project.zip .
curl -X POST -F "file=@project.zip" http://localhost:8080/api/explain/analyze
```

### Analyze a Python Project
```bash
cd python-project
zip -r project.zip .
curl -X POST -F "file=@project.zip" http://localhost:8080/api/explain/analyze
```

---

## 📱 Integration Examples

### JavaScript / Fetch
```javascript
const form = new FormData();
form.append('file', zipFile);

const response = await fetch('http://localhost:8080/api/explain/analyze', {
  method: 'POST',
  body: form
});

const analysis = await response.json();
console.log(analysis.frameworks); // ["Spring Boot", "Spring MVC"]
```

### Python
```python
import requests

files = {'file': open('project.zip', 'rb')}
response = requests.post('http://localhost:8080/api/explain/analyze', files=files)
data = response.json()

print(f"Languages: {data['languages']}")
print(f"Frameworks: {data['frameworks']}")
```

### Java / HttpClient
```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .POST(HttpRequest.BodyPublishers.ofFile(Path.of("project.zip")))
    .uri(URI.create("http://localhost:8080/api/explain/analyze"))
    .header("Content-Type", "multipart/form-data")
    .build();

HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

---

## 🐳 Docker Support (Optional)

### Create Dockerfile
```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

### Build and Run
```bash
docker build -t explainit .
docker run -p 8080:8080 explainit
```

---

## 📊 Performance Notes

- **Extraction Speed**: ~1-2 seconds for typical projects
- **Analysis Speed**: ~1-3 seconds depending on project size
- **Memory Usage**: ~100-200MB heap typical
- **Max File Size**: 100MB configurable
- **Cleanup**: Automatic temporary file deletion

---

## 🔗 API Endpoints Summary

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/explain/analyze` | POST | Analyze project ZIP |
| `/api/explain/health` | GET | Health check |

---

## 💡 Pro Tips

1. **JSON Pretty Print**: Use `jq` to format output
   ```bash
   curl ... | jq .
   ```

2. **Save Results**: Save analysis to file
   ```bash
   curl ... > analysis.json
   ```

3. **Integrate with CI/CD**: Use in build pipelines
   ```bash
   curl -X POST -F "file=@project.zip" http://server:8080/api/explain/analyze > metadata.json
   ```

4. **Monitor Health**: Check before sending analysis
   ```bash
   curl http://localhost:8080/api/explain/health
   ```

---

## 🐛 Troubleshooting

### Port 8080 Already in Use
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=8081
```

### ZIP Upload Size Too Large
Edit `application.properties`:
```properties
spring.servlet.multipart.max-file-size=500MB
```

### OutOfMemory Error
Increase heap size:
```bash
java -Xmx1024m -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Temp Directory Permission Issues
Set custom temp dir:
```bash
java -Djava.io.tmpdir=/tmp -jar target/demo-0.0.1-SNAPSHOT.jar
```

---

## 📞 Support

For detailed architecture information, see:
- **README_EXPLAINIT.md** - Full API documentation
- **IMPLEMENTATION_SUMMARY.md** - Design decisions

---

**Ready to analyze projects!** 🚀
