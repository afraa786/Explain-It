package io.explainit.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipExtractor {
    
    public static Path extractZip(InputStream inputStream) throws IOException {
        Path tempDir = Files.createTempDirectory("explainit-analysis-");
        
        try (ZipArchiveInputStream zipInput = new ZipArchiveInputStream(inputStream)) {
            ZipArchiveEntry entry;
            while ((entry = zipInput.getNextEntry()) != null) {
                Path filePath = tempDir.resolve(entry.getName());
                
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipInput, filePath);
                }
            }
        }
        
        return tempDir;
    }
    
    public static void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        Files.walk(directory)
            .sorted(java.util.Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    // Ignore
                }
            });
    }
}
