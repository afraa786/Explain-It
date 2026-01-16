package io.explainit.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileScanner {
    
    public static Set<String> getAllFileExtensions(Path rootPath) throws IOException {
        Set<String> extensions = new HashSet<>();
        
        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream.filter(Files::isRegularFile)
                .forEach(path -> {
                    String filename = path.getFileName().toString();
                    int dotIndex = filename.lastIndexOf('.');
                    if (dotIndex > 0) {
                        extensions.add(filename.substring(dotIndex + 1));
                    }
                });
        }
        
        return extensions;
    }
    
    public static List<Path> findFilesByExtension(Path rootPath, String extension) throws IOException {
        List<Path> files = new ArrayList<>();
        
        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith("." + extension))
                .forEach(files::add);
        }
        
        return files;
    }
    
    public static List<String> readFileAsLines(Path filePath) throws IOException {
        return Files.readAllLines(filePath);
    }
    
    public static String readFileAsString(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
    
    public static long countFilesByExtension(Path rootPath, String extension) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith("." + extension))
                .count();
        }
    }
    
    public static boolean fileExists(Path rootPath, String filename) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream.filter(Files::isRegularFile)
                .anyMatch(path -> path.getFileName().toString().equals(filename));
        }
    }
    
    public static Optional<Path> findFile(Path rootPath, String filename) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().equals(filename))
                .findFirst();
        }
    }
    
    public static long calculateTotalSize(Path rootPath) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream.filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        return 0L;
                    }
                })
                .sum();
        }
    }
    
    public static long countDirectories(Path rootPath) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream.filter(Files::isDirectory)
                .count();
        }
    }
    
    public static long countAllFiles(Path rootPath) throws IOException {
        try (Stream<Path> stream = Files.walk(rootPath)) {
            return stream.filter(Files::isRegularFile)
                .count();
        }
    }
}
