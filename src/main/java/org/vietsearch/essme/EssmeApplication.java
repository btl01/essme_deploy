package org.vietsearch.essme;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.vietsearch.essme.repository.EventRepository;
import org.vietsearch.essme.service.BackupService;

import javax.annotation.Resource;

@EnableMongoAuditing
@SpringBootApplication
public class EssmeApplication implements CommandLineRunner {

    @Resource
    BackupService backupService;

    @Autowired
    EventRepository eventRepository;

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
//        List<Event> events = eventRepository.findAll();
//        events.forEach(event -> {
//            String location = event.getLocation();
//            System.out.println(location);
//            Map<String, Double> coor = OpenStreetMapUtils.getInstance().getCoordinates(location);
//            System.out.println(coor);
//
//            Geojson geojson = new Geojson();
//            geojson.setType("Feature");
//            Geometry geometry = new Geometry();
//            geometry.setType("Point");
//            geometry.setCoordinates(new ArrayList<>());
//            geometry.getCoordinates().add(coor.get("lon"));
//            geometry.getCoordinates().add(coor.get("lat"));
//            geojson.setGeometry(geometry);
//            System.out.println(geojson);
//            event.setGeojson(geojson);
//            eventRepository.save(event);
//        });
    }
}
