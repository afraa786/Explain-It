package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.dto.DataLayerAnalysisResult;
import io.explainit.dto.DetectionResult;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Detects databases, ORMs, migrations, and connection pooling.
 * Focused on data layer technologies across all languages.
 */
public class DataLayerAnalyzer implements IProjectAnalyzer {
    
    // Database detection patterns
    private static final Map<String, DatabaseSignature> DATABASES = Map.ofEntries(
        Map.entry("postgresql", new DatabaseSignature("PostgreSQL", Arrays.asList("postgresql", "postgres", "pgsql"))),
        Map.entry("mysql", new DatabaseSignature("MySQL", Arrays.asList("mysql", "mariadb"))),
        Map.entry("mongodb", new DatabaseSignature("MongoDB", Arrays.asList("mongodb", "mongo"))),
        Map.entry("redis", new DatabaseSignature("Redis", Arrays.asList("redis", "lettuce", "jedis"))),
        Map.entry("h2", new DatabaseSignature("H2", Arrays.asList("h2", "h2database"))),
        Map.entry("sqlite", new DatabaseSignature("SQLite", Arrays.asList("sqlite"))),
        Map.entry("oracle", new DatabaseSignature("Oracle", Arrays.asList("oracle", "ojdbc"))),
        Map.entry("mssql", new DatabaseSignature("SQL Server", Arrays.asList("sqlserver", "mssql")))
    );
    
    // ORM detection patterns
    private static final Map<String, ORMSignature> JAVA_ORMS = Map.ofEntries(
        Map.entry("jpa", new ORMSignature("JPA", Arrays.asList("spring-data-jpa", "jakarta.persistence", "javax.persistence"))),
        Map.entry("hibernate", new ORMSignature("Hibernate", Arrays.asList("hibernate-core", "hibernate-jpa"))),
        Map.entry("mybatis", new ORMSignature("MyBatis", Arrays.asList("mybatis", "mybatis-spring")))
    );
    
    private static final Map<String, ORMSignature> PYTHON_ORMS = Map.ofEntries(
        Map.entry("django_orm", new ORMSignature("Django ORM", Arrays.asList("django"))),
        Map.entry("sqlalchemy", new ORMSignature("SQLAlchemy", Arrays.asList("sqlalchemy"))),
        Map.entry("tortoise_orm", new ORMSignature("Tortoise ORM", Arrays.asList("tortoise-orm")))
    );
    
    private static final Map<String, ORMSignature> NODE_ORMS = Map.ofEntries(
        Map.entry("prisma", new ORMSignature("Prisma", Arrays.asList("@prisma/client"))),
        Map.entry("typeorm", new ORMSignature("TypeORM", Arrays.asList("typeorm"))),
        Map.entry("sequelize", new ORMSignature("Sequelize", Arrays.asList("sequelize"))),
        Map.entry("knex", new ORMSignature("Knex.js", Arrays.asList("knex"))),
        Map.entry("mongoose", new ORMSignature("Mongoose", Arrays.asList("mongoose")))
    );
    
    // Migration tool patterns
    private static final Map<String, String> MIGRATION_TOOLS = Map.ofEntries(
        Map.entry("flyway", "Flyway"),
        Map.entry("liquibase", "Liquibase"),
        Map.entry("alembic", "Alembic"),
        Map.entry("prisma", "Prisma Migrate"),
        Map.entry("django", "Django Migrations"),
        Map.entry("typeorm", "TypeORM Migrations"),
        Map.entry("sequelize", "Sequelize CLI")
    );
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        DataLayerAnalysisResult result = new DataLayerAnalysisResult();
        List<DetectionResult> detections = new ArrayList<>();
        
        // Detect databases
        detections.addAll(detectDatabases(projectRoot));
        
        // Detect ORMs
        detections.addAll(detectORMs(projectRoot));
        
        // Detect connection pooling
        detections.addAll(detectConnectionPooling(projectRoot));
        
        // Detect migrations
        detections.addAll(detectMigrations(projectRoot));
        
        // Check application properties
        detections.addAll(detectFromConfig(projectRoot));
        
        // Set primary database and ORM
        for (DetectionResult d : detections) {
            if (d.getCategory().equals("Database")) {
                result.setDatabase(d.getName());
                break;
            }
        }
        
        for (DetectionResult d : detections) {
            if (d.getCategory().contains("ORM")) {
                result.setOrm(d.getName());
                break;
            }
        }
        
        // Extract migration tools
        List<String> migrations = new ArrayList<>();
        for (DetectionResult d : detections) {
            if (d.getCategory().equals("Migration")) {
                migrations.add(d.getName());
            }
        }
        result.setMigrationToolsDetected(migrations);
        
        // Detect entity and repository counts (simplified)
        result.setEntityCount(countEntities(projectRoot));
        result.setRepositoryCount(countRepositories(projectRoot));
        
        result.setDetections(detections);
        result.setSuccess(true);
        return result;
    }
    
    private int countEntities(Path projectRoot) throws Exception {
        try {
            // Look for @Entity annotated classes in Java
            List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
            int count = 0;
            for (Path file : javaFiles) {
                String content = FileScanner.readFileAsString(file);
                if (content.contains("@Entity") || content.contains("@Document")) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private int countRepositories(Path projectRoot) throws Exception {
        try {
            // Look for @Repository annotated classes or interface extending Repository
            List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
            int count = 0;
            for (Path file : javaFiles) {
                String content = FileScanner.readFileAsString(file);
                if (content.contains("@Repository") || content.contains("extends Repository")) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private List<DetectionResult> detectDatabases(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check Maven dependencies
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            for (PomParser.Dependency dep : deps) {
                for (Map.Entry<String, DatabaseSignature> entry : DATABASES.entrySet()) {
                    for (String keyword : entry.getValue().keywords) {
                        if (dep.artifactId.toLowerCase().contains(keyword)) {
                            results.add(new DetectionResult(
                                entry.getValue().name,
                                "Database",
                                DetectionResult.Confidence.HIGH,
                                "Database driver dependency detected in pom.xml",
                                Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId),
                                dep.version
                            ));
                            break;
                        }
                    }
                }
            }
        }
        
        // Check npm dependencies
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            for (String key : DATABASES.keySet()) {
                if (content.contains("\"" + key)) {
                    DatabaseSignature sig = DATABASES.get(key);
                    results.add(new DetectionResult(
                        sig.name,
                        "Database",
                        DetectionResult.Confidence.HIGH,
                        "Database driver package found in package.json",
                        Arrays.asList("NPM package: " + key)
                    ));
                }
            }
        }
        
        // Check Python requirements
        Optional<Path> requirementsPath = FileScanner.findFile(projectRoot, "requirements.txt");
        if (requirementsPath.isPresent()) {
            String content = FileScanner.readFileAsString(requirementsPath.get());
            for (String key : DATABASES.keySet()) {
                if (content.contains(key)) {
                    DatabaseSignature sig = DATABASES.get(key);
                    results.add(new DetectionResult(
                        sig.name,
                        "Database",
                        DetectionResult.Confidence.HIGH,
                        "Database driver package found in requirements.txt",
                        Arrays.asList("Python package: " + key)
                    ));
                }
            }
        }
        
        // Check config files
        Optional<Path> appPropsPath = FileScanner.findFile(projectRoot, "application.properties");
        if (appPropsPath.isPresent()) {
            String content = FileScanner.readFileAsString(appPropsPath.get());
            results.addAll(detectDatabaseFromConfig(content, "application.properties"));
        }
        
        Optional<Path> appYmlPath = FileScanner.findFile(projectRoot, "application.yml");
        if (appYmlPath.isPresent()) {
            String content = FileScanner.readFileAsString(appYmlPath.get());
            results.addAll(detectDatabaseFromConfig(content, "application.yml"));
        }
        
        Optional<Path> appEnvPath = FileScanner.findFile(projectRoot, ".env");
        if (appEnvPath.isPresent()) {
            String content = FileScanner.readFileAsString(appEnvPath.get());
            results.addAll(detectDatabaseFromConfig(content, ".env"));
        }
        
        return results;
    }
    
    private List<DetectionResult> detectDatabaseFromConfig(String content, String source) {
        List<DetectionResult> results = new ArrayList<>();
        
        for (Map.Entry<String, DatabaseSignature> entry : DATABASES.entrySet()) {
            for (String keyword : entry.getValue().keywords) {
                if (content.toLowerCase().contains(keyword)) {
                    results.add(new DetectionResult(
                        entry.getValue().name,
                        "Database",
                        DetectionResult.Confidence.HIGH,
                        "Database URL/config found in " + source,
                        Arrays.asList("Config: " + source)
                    ));
                    break;
                }
            }
        }
        
        return results;
    }
    
    private List<DetectionResult> detectORMs(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check Maven for Java ORMs
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            for (PomParser.Dependency dep : deps) {
                for (ORMSignature orm : JAVA_ORMS.values()) {
                    for (String keyword : orm.dependencies) {
                        if (dep.artifactId.contains(keyword) || dep.groupId.contains(keyword)) {
                            results.add(new DetectionResult(
                                orm.name,
                                "ORM",
                                DetectionResult.Confidence.HIGH,
                                "ORM dependency detected in pom.xml",
                                Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId),
                                dep.version
                            ));
                            break;
                        }
                    }
                }
            }
        }
        
        // Check npm for Node ORMs
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            for (ORMSignature orm : NODE_ORMS.values()) {
                for (String keyword : orm.dependencies) {
                    if (content.contains(keyword)) {
                        results.add(new DetectionResult(
                            orm.name,
                            "ORM",
                            DetectionResult.Confidence.HIGH,
                            "ORM package found in package.json",
                            Arrays.asList("NPM package: " + keyword)
                        ));
                    }
                }
            }
        }
        
        // Check Python requirements for ORMs
        Optional<Path> requirementsPath = FileScanner.findFile(projectRoot, "requirements.txt");
        if (requirementsPath.isPresent()) {
            String content = FileScanner.readFileAsString(requirementsPath.get());
            for (ORMSignature orm : PYTHON_ORMS.values()) {
                for (String keyword : orm.dependencies) {
                    if (content.contains(keyword)) {
                        results.add(new DetectionResult(
                            orm.name,
                            "ORM",
                            DetectionResult.Confidence.HIGH,
                            "ORM package found in requirements.txt",
                            Arrays.asList("Python package: " + keyword)
                        ));
                    }
                }
            }
        }
        
        // Check for Prisma schema file
        Optional<Path> prismaSchemaPath = FileScanner.findFile(projectRoot, "schema.prisma");
        if (prismaSchemaPath.isPresent()) {
            results.add(new DetectionResult(
                "Prisma",
                "ORM",
                DetectionResult.Confidence.HIGH,
                "Prisma schema.prisma file found",
                Arrays.asList("File: schema.prisma")
            ));
        }
        
        return results;
    }
    
    private List<DetectionResult> detectConnectionPooling(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check for HikariCP, Tomcat Connection Pool, etc.
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            String content = FileScanner.readFileAsString(pomPath.get());
            
            if (content.contains("hikari")) {
                results.add(new DetectionResult(
                    "HikariCP",
                    "Connection Pool",
                    DetectionResult.Confidence.HIGH,
                    "HikariCP dependency detected",
                    Arrays.asList("Dependency: HikariCP")
                ));
            }
            
            if (content.contains("commons-dbcp")) {
                results.add(new DetectionResult(
                    "Apache DBCP",
                    "Connection Pool",
                    DetectionResult.Confidence.HIGH,
                    "Apache Commons DBCP dependency detected",
                    Arrays.asList("Dependency: commons-dbcp")
                ));
            }
        }
        
        // Check application properties for connection pool settings
        Optional<Path> appPropsPath = FileScanner.findFile(projectRoot, "application.properties");
        if (appPropsPath.isPresent()) {
            String content = FileScanner.readFileAsString(appPropsPath.get());
            if (content.contains("hikari") || content.contains("pool")) {
                results.add(new DetectionResult(
                    "Connection Pool",
                    "Connection Pool",
                    DetectionResult.Confidence.HIGH,
                    "Connection pool configuration found",
                    Arrays.asList("Config: application.properties")
                ));
            }
        }
        
        return results;
    }
    
    private List<DetectionResult> detectMigrations(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check for migration files and tools
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            String content = FileScanner.readFileAsString(pomPath.get());
            
            for (Map.Entry<String, String> tool : MIGRATION_TOOLS.entrySet()) {
                if (content.contains(tool.getKey())) {
                    results.add(new DetectionResult(
                        tool.getValue(),
                        "Migration Tool",
                        DetectionResult.Confidence.HIGH,
                        "Migration tool dependency detected",
                        Arrays.asList("Dependency: " + tool.getKey())
                    ));
                }
            }
        }
        
        // Check for migration directories
        Optional<Path> migrationDir = FileScanner.findFile(projectRoot, "db");
        if (migrationDir.isPresent()) {
            results.add(new DetectionResult(
                "Migrations",
                "Migration Tool",
                DetectionResult.Confidence.MEDIUM,
                "Migration directory detected",
                Arrays.asList("Directory: db/")
            ));
        }
        
        // Check for Alembic (Python)
        Optional<Path> alembicPath = FileScanner.findFile(projectRoot, "alembic.ini");
        if (alembicPath.isPresent()) {
            results.add(new DetectionResult(
                "Alembic",
                "Migration Tool",
                DetectionResult.Confidence.HIGH,
                "Alembic migration tool detected",
                Arrays.asList("File: alembic.ini")
            ));
        }
        
        // Check for Django migrations
        Optional<Path> djangoMigPath = FileScanner.findFile(projectRoot, "migrations");
        if (djangoMigPath.isPresent()) {
            results.add(new DetectionResult(
                "Django Migrations",
                "Migration Tool",
                DetectionResult.Confidence.HIGH,
                "Django migration directory detected",
                Arrays.asList("Directory: migrations/")
            ));
        }
        
        // Check package.json for migration tools
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            
            for (Map.Entry<String, String> tool : MIGRATION_TOOLS.entrySet()) {
                if (content.contains(tool.getKey())) {
                    results.add(new DetectionResult(
                        tool.getValue(),
                        "Migration Tool",
                        DetectionResult.Confidence.HIGH,
                        "Migration tool package detected",
                        Arrays.asList("NPM package: " + tool.getKey())
                    ));
                }
            }
        }
        
        return results;
    }
    
    private List<DetectionResult> detectFromConfig(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check configuration files for datasource info
        Optional<Path> appPropsPath = FileScanner.findFile(projectRoot, "application.properties");
        if (appPropsPath.isPresent()) {
            String content = FileScanner.readFileAsString(appPropsPath.get());
            
            if (content.contains("spring.datasource") || content.contains("datasource")) {
                results.add(new DetectionResult(
                    "Datasource Configuration",
                    "Data Layer Config",
                    DetectionResult.Confidence.HIGH,
                    "Datasource configuration detected",
                    Arrays.asList("Config: application.properties")
                ));
            }
            
            if (content.contains("spring.jpa")) {
                results.add(new DetectionResult(
                    "JPA Configuration",
                    "Data Layer Config",
                    DetectionResult.Confidence.HIGH,
                    "JPA configuration detected",
                    Arrays.asList("Config: application.properties")
                ));
            }
        }
        
        return results;
    }
    

    private static class DatabaseSignature {
        String name;
        List<String> keywords;
        
        DatabaseSignature(String name, List<String> keywords) {
            this.name = name;
            this.keywords = keywords;
        }
    }
    
   
    private static class ORMSignature {
        String name;
        List<String> dependencies;
        
        ORMSignature(String name, List<String> dependencies) {
            this.name = name;
            this.dependencies = dependencies;
        }
    }
}

