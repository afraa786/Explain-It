package io.explainit.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Calculates total project size with intelligent exclusion of common directories.
 * This gives developers a realistic sense of their codebase footprint.
 */
public class ProjectSizeCalculator {

    private static final Set<String> EXCLUDED_DIRECTORIES = Set.of(
        ".git",
        "node_modules",
        "target",
        "build",
        ".venv",
        "venv",
        "dist",
        ".next",
        ".nuxt",
        "__pycache__",
        ".pytest_cache",
        ".gradle",
        ".maven",
        ".m2",
        "bin",
        "obj",
        ".DS_Store",
        ".idea",
        ".vscode"
    );

    /**
     * @return [totalSizeBytes, totalFileCount]
     */
    public static long[] calculateProjectSize(Path projectRoot) throws IOException {
        try (Stream<Path> stream = Files.walk(projectRoot)) {

            var stats = stream
                .filter(Files::isRegularFile)
                .filter(path -> !isInExcludedDirectory(path))
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        return 0L; // ignore unreadable files
                    }
                })
                .summaryStatistics();

            return new long[]{
                stats.getSum(),
                stats.getCount()
            };
        }
    }

    private static boolean isInExcludedDirectory(Path path) {
        Path current = path;
        while (current != null) {
            Path fileName = current.getFileName();
            if (fileName != null && EXCLUDED_DIRECTORIES.contains(fileName.toString())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    public static double bytesToMB(long bytes) {
        return Math.round((bytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }
}
