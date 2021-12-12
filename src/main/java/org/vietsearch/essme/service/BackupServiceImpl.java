package org.vietsearch.essme.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public String dump() {
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
            return exitCode == 0 ? archiveName : "";
        } catch (InterruptedException | IOException e) {
            return "";
        }
    }

    @Override
    public boolean restore(String archiveName) {
        List<String> command = Arrays.asList(
                "mongorestore",
//                "--uri", mongoUri,
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
    public String[] list() {
        File file = root.toFile();
        return file.list();
    }

    @Override
    public File load(String filename) {
        return root.resolve(filename).toFile();
    }

    @Override
    public String save(MultipartFile file) {
        if (file == null) return "";
        try {
            file.transferTo(root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
            return file.getOriginalFilename();
        } catch (IOException e) {
            return "";
        }
    }
}
