package org.vietsearch.essme.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BackupServiceImpl implements BackupService {

    private final Path root = Paths.get("backup");

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
            System.out.println(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder");
        }
    }

    @Override
    public Optional<String> dump() {
        String archiveName = String.valueOf(new Date());
        List<String> command = Arrays.asList(
                "mongodump",
                "--db", "essme_test",
                "--uri", mongoUri,
                "--gzip",
                "--archive=" + archiveName
        );
        try {
            ProcessBuilder pb = new ProcessBuilder(command).directory(new File(root.toUri()));
            Process process = pb.start();

            //WAITING FOR A RETURN FROM THE PROCESS WE STARTED
            int exitCode = process.waitFor();
            return exitCode == 0 ? Optional.of(archiveName) : Optional.empty();
        } catch (InterruptedException | IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean restore(String archiveName) {
        List<String> command = Arrays.asList(
                "mongorestore",
                "--uri", mongoUri,
                "--drop",
                "--gzip",
                "--archive=" + archiveName
        );
        ProcessBuilder pb = new ProcessBuilder(command).directory(new File(root.toUri()));
        try {
            Process process = pb.start();
            //WAITING FOR A RETURN FROM THE PROCESS WE STARTED
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException exception) {
            return false;
        }
    }

    @Override
    public boolean delete(String filename) {
        try {
            Path path = root.resolve(filename);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<String> list() {
        File[] files = root.toFile().listFiles();
        assert files != null;
        return Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    @Override
    public File load(String filename) {
        return root.resolve(filename).toFile();
    }

    @Override
    public Optional<String> save(MultipartFile file) {
        if (file == null) return Optional.empty();
        try {
            file.transferTo(root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
            return Optional.ofNullable(file.getOriginalFilename());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
