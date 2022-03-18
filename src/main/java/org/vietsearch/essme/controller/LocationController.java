package org.vietsearch.essme.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vietsearch.essme.model.expert.Geometry;
import org.vietsearch.essme.utils.OpenStreetMapUtils;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @GetMapping
    public ResponseEntity<Geometry> getLocationByAddress(@RequestParam(value = "address", defaultValue = "vietnam") String address) {
        Geometry geometry;

        try {
            geometry = OpenStreetMapUtils.getInstance().addressToLocation(address).getFeatures().get(0).getGeometry();
        } catch (Exception ex) {
            geometry = new Geometry();
            ArrayList<Double> coordinates = new ArrayList<>();
            coordinates.add(0d);
            coordinates.add(0d);
            geometry.setCoordinates(coordinates);
            geometry.setType("Point");
        }
        return new ResponseEntity<>(geometry, HttpStatus.OK);
    }
}
