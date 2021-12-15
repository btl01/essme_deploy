package org.vietsearch.essme.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface BackupService {
    void init();

    Optional<String> dump();

    boolean restore(String archiveName);

    List<String> list();

    boolean delete(String filename);

    File load(String filename);

    Optional<String> save(MultipartFile file);
}
