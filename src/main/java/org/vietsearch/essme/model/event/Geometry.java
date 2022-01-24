package org.vietsearch.essme.model.event;

import lombok.Data;

import java.util.List;

@Data
public class Geometry {
    private String type;
    private List<Double> coordinates;
}
