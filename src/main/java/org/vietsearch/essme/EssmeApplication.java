package org.vietsearch.essme;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.vietsearch.essme.service.BackupService;

import javax.annotation.Resource;

@EnableMongoAuditing
@SpringBootApplication
public class EssmeApplication implements CommandLineRunner {

    @Resource
    BackupService backupService;

    public static void main(String[] args) {
        SpringApplication.run(EssmeApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... args) {
        backupService.init();
    }
}
