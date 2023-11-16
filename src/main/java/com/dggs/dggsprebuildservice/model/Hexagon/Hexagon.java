package com.dggs.dggsprebuildservice.model.Hexagon;

import com.dggs.dggsprebuildservice.model.Camera;
import com.dggs.dggsprebuildservice.model.SpatialData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Hexagon {
    @JsonProperty("code")
    private String code;
    @JsonProperty("level")
    private int level;
    @JsonProperty("boundary")
    private List<List<Double>> boundary;
    @JsonProperty("center")
    private List<Double> center;
    @JsonProperty("children")
    private List<Hexagon> children;
    @JsonProperty("boundingBox")
    private BoundingSphere boundingBox;
    @JsonProperty("intersection")
    private Integer intersection;
    @JsonProperty("color")
    private List<Integer> color;

}
