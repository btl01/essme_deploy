package org.vietsearch.essme.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class LocationControllerTest {

    @Test
    void getLocationByAddress() throws NoSuchFieldException {
        LocationController locationController = new LocationController();
        System.out.println(locationController.getLocationByAddress(null).getClass());
        System.out.println(locationController.getLocationByAddress("hanoi").getClass());
    }
}