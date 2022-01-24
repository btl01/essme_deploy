package org.vietsearch.essme.model.event;

import lombok.Data;

@Data
public class Geojson {
    private String type;
    private Geometry geometry;
}
