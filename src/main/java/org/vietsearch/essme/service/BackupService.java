package org.vietsearch.essme.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface BackupService {
    void init();

    String dump();

    boolean restore(String archiveName);

    List<String> list();

    boolean delete(String filename);

    File load(String filename);

    String save(MultipartFile file) throws IOException;
}
