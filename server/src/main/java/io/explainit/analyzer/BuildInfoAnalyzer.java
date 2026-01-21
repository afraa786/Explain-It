package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.dto.BuildAnalysisResult;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import io.explainit.util.ProjectSizeCalculator;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyzes build configuration, project size, dependencies, and multi-module setup.
 */
public class BuildInfoAnalyzer implements IProjectAnalyzer {
    
    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile(
        "<artifactId>\\s*([^<]+)\\s*</artifactId>"
    );
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        BuildAnalysisResult result = new BuildAnalysisResult();
        
        // Detect build tool
        detectBuildTool(projectRoot, result);
        
        // Calculate project size
        long[] sizeData = ProjectSizeCalculator.calculateProjectSize(projectRoot);
        result.setProjectSizeKB(sizeData[0] / 1024);
        result.setProjectSizeMB(ProjectSizeCalculator.bytesToMB(sizeData[0]));
        result.setTotalFileCount(sizeData[1]);
        
        // Count dependencies
        result.setDependencyCount(countDependencies(projectRoot));
        
        // Detect multi-module
        result.setMultiModule(isMultiModule(projectRoot));
        if (result.isMultiModule()) {
            result.setModuleCount(countModules(projectRoot));
        }
        
        result.setSuccess(true);
        return result;
    }
    
    private void detectBuildTool(Path projectRoot, BuildAnalysisResult result) throws Exception {
        // Check for Maven
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            result.setBuildTool("Maven");
            
            String javaVersion = PomParser.parsePomProperties(pomPath.get())
                .getOrDefault("java.version", 
                    PomParser.parsePomProperties(pomPath.get())
                        .getOrDefault("maven.compiler.source", "Unknown"));
            result.setJavaVersion(javaVersion);
            return;
        }
        
        // Check for Gradle
        Optional<Path> gradlePath = FileScanner.findFile(projectRoot, "build.gradle");
        if (gradlePath.isPresent()) {
            result.setBuildTool("Gradle");
            
            String gradleContent = FileScanner.readFileAsString(gradlePath.get());
            if (gradleContent.contains("sourceCompatibility")) {
                String[] lines = gradleContent.split("\n");
                for (String line : lines) {
                    if (line.contains("sourceCompatibility")) {
                        String[] parts = line.split("=");
                        if (parts.length > 1) {
                            result.setJavaVersion(parts[1].trim().replaceAll("['\"]", ""));
                        }
                    }
                }
            }
            return;
        }
        
        // Check for npm/yarn
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            result.setBuildTool("npm");
            result.setLanguageVersion("Node.js");
            return;
        }
        
        result.setBuildTool("Unknown");
    }
    
    private int countDependencies(Path projectRoot) throws Exception {
        Set<String> dependencies = new HashSet<>();
        
        // Maven dependencies
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            String pomContent = FileScanner.readFileAsString(pomPath.get());
            Matcher matcher = DEPENDENCY_PATTERN.matcher(pomContent);
            while (matcher.find()) {
                dependencies.add(matcher.group(1).trim());
            }
        }
        
        // Gradle dependencies
        Optional<Path> gradlePath = FileScanner.findFile(projectRoot, "build.gradle");
        if (gradlePath.isPresent()) {
            String gradleContent = FileScanner.readFileAsString(gradlePath.get());
            Pattern depPattern = Pattern.compile("(implementation|compile|compileOnly|testImplementation)\\s+['\\\"]([^'\\\"]+)['\\\"]");
            Matcher matcher = depPattern.matcher(gradleContent);
            while (matcher.find()) {
                dependencies.add(matcher.group(2).trim());
            }
        }
        
        // npm dependencies
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String pkgContent = FileScanner.readFileAsString(packageJsonPath.get());
            Pattern depPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");
            Matcher matcher = depPattern.matcher(pkgContent);
            while (matcher.find()) {
                if (!matcher.group(1).equals("version") && !matcher.group(1).equals("name")) {
                    dependencies.add(matcher.group(1));
                }
            }
        }

        Optional<Path> yarnLockPath = FileScanner.findFile(projectRoot, "yarn.lock");
        if (yarnLockPath.isPresent()) {
            String yarnContent = FileScanner.readFileAsString(yarnLockPath.get());
            Pattern depPattern = Pattern.compile("^([^@\\s]+)@");
            Matcher matcher = depPattern.matcher(yarnContent);
            while (matcher.find()) {
                dependencies.add(matcher.group(1).trim());
            }
        }

        Optional<Path> pipfilePath = FileScanner.findFile(projectRoot, "Pipfile");
        if (pipfilePath.isPresent()) {
            String pipfileContent = FileScanner.readFileAsString(pipfilePath.get());
            Pattern depPattern = Pattern.compile("^(\\S+)\\s*=\\s*\"([^\"]+)\"", Pattern.MULTILINE);
            Matcher matcher = depPattern.matcher(pipfileContent);
            while (matcher.find()) {
                dependencies.add(matcher.group(1).trim());
            }
        }

        Optional<Path> requirementsPath = FileScanner.findFile(projectRoot, "requirements.txt");
        if (requirementsPath.isPresent()) {
            String requirementsContent = FileScanner.readFileAsString(requirementsPath.get());
            String[] lines = requirementsContent.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    dependencies.add(line.split("==")[0].trim());
                }
            }
        }

        Optional<Path> setupPyPath = FileScanner.findFile(projectRoot, "setup.py");
        if (setupPyPath.isPresent()) {
            String setupContent = FileScanner.readFileAsString(setupPyPath.get());
            Pattern depPattern = Pattern.compile("install_requires\\s*=\\s*\\[(.*?)\\]", Pattern.DOTALL);
            Matcher matcher = depPattern.matcher(setupContent);
            if (matcher.find()) {
                String depsBlock = matcher.group(1);
                Pattern singleDepPattern = Pattern.compile("'([^']+)'");
                Matcher singleMatcher = singleDepPattern.matcher(depsBlock);
                while (singleMatcher.find()) {
                    dependencies.add(singleMatcher.group(1).trim());
                }
            }
        }

        Optional<Path> cargoTomlPath = FileScanner.findFile(projectRoot, "Cargo.toml");
        if (cargoTomlPath.isPresent()) {
            String cargoContent = FileScanner.readFileAsString(cargoTomlPath.get());
            Pattern depPattern = Pattern.compile("^([^=\\s]+)\\s*=\\s*\"([^\"]+)\"", Pattern.MULTILINE);
            Matcher matcher = depPattern.matcher(cargoContent);
            while (matcher.find()) {
                dependencies.add(matcher.group(1).trim());
            }
        }

        Optional<Path> buildGradleKtsPath = FileScanner.findFile(projectRoot, "build.gradle.kts");

        if (buildGradleKtsPath.isPresent()) {
            String gradleKtsContent = FileScanner.readFileAsString(buildGradleKtsPath.get());
            Pattern depPattern = Pattern.compile("(implementation|compile|compileOnly|testImplementation)\\s*\\(\\s*['\\\"]([^'\\\"]+)['\\\"]\\s*\\)");
            Matcher matcher = depPattern.matcher(gradleKtsContent);
            while (matcher.find()) {
                dependencies.add(matcher.group(2).trim());
            }
        }

        Optional<Path> buildSbtPath = FileScanner.findFile(projectRoot, "build.sbt");   
        if (buildSbtPath.isPresent()) {
            String buildSbtContent = FileScanner.readFileAsString(buildSbtPath.get());
            Pattern depPattern = Pattern.compile("libraryDependencies\\s*\\+?=\\s*\\([^)]*\\)");
            Matcher matcher = depPattern.matcher(buildSbtContent);
            while (matcher.find()) {
                String depLine = matcher.group(0);
                Pattern singleDepPattern = Pattern.compile("'([^']+)'");
                Matcher singleMatcher = singleDepPattern.matcher(depLine);
                while (singleMatcher.find()) {
                    dependencies.add(singleMatcher.group(1).trim());
                }
            }
        }

        Optional<Path> gemfilePath = FileScanner.findFile(projectRoot, "Gemfile");
        if (gemfilePath.isPresent()) {
            String gemfileContent = FileScanner.readFileAsString(gemfilePath.get());
            Pattern depPattern = Pattern.compile("gem\\s+['\\\"]([^'\\\"]+)['\\\"]");
            Matcher matcher = depPattern.matcher(gemfileContent);
            while (matcher.find()) {
                dependencies.add(matcher.group(1).trim());
            }
        }

        return dependencies.size();
    }
    
    private boolean isMultiModule(Path projectRoot) throws Exception {
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            String pomContent = FileScanner.readFileAsString(pomPath.get());
            return pomContent.contains("<modules>") && pomContent.contains("<module>");
        }
        return false;
    }
    
    private int countModules(Path projectRoot) throws Exception {
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            String pomContent = FileScanner.readFileAsString(pomPath.get());
            Pattern modulePattern = Pattern.compile("<module>([^<]+)</module>");
            Matcher matcher = modulePattern.matcher(pomContent);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            return count;
        }
        return 0;
    }
}
