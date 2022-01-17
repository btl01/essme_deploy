package org.vietsearch.essme.utils;

import org.junit.jupiter.api.Test;
import org.modelmapper.internal.util.Assert;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OpenStreetMapUtilsTest {

    @Test
    void getCoordinates() {
        String address = "Ha noi";
        Map<String, Double> coords;
        coords = OpenStreetMapUtils.getInstance().getCoordinates(address);
        assertNotNull(coords, "coord");
        assertNotNull(coords.get("lat"), "lat atribute");
        assertNotNull(coords.get("lon"), "lon atribute");
    }
}