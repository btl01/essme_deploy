package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.service.BackupService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BackupController {

    @Autowired
    BackupService backupService;

    @GetMapping("/backup")
    public List<String> listArchive(@RequestParam(value = "asc", required = false) boolean asc) {
        if (asc)
            return backupService.list();
        else {
            List<String> list = backupService.list();
            Collections.reverse(list);
            return list;
        }
    }

    @GetMapping("/backup/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) throws IOException {
        File file = backupService.load(filename);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        return ResponseEntity
                .ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/backup/create")
    public String createBackup() {
        String name = backupService.dump();
        if (!"".equals(name))
            return name;
        else
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create backup");
    }

    @PostMapping("/backup")
    public String uploadFile(@RequestParam MultipartFile file) {
        try {
            return backupService.save(file);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/backup/{filename}")
    public boolean delete(@PathVariable String filename) {
        return backupService.delete(filename);
    }

    @GetMapping("/restore/{filename}")
    public boolean restore(@PathVariable String filename) {
        return backupService.restore(filename);
    }

}
