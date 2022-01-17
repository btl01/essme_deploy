package org.vietsearch.essme.model.Event;

import lombok.Data;

@Data
public class Geojson {
    private String type;
    private Geometry geometry;
}
