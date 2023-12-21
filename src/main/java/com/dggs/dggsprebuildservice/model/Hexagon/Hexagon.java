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


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<List<Double>> getBoundary() {
        return boundary;
    }

    public void setBoundary(List<List<Double>> boundary) {
        this.boundary = boundary;
    }

    public List<Double> getCenter() {
        return center;
    }

    public void setCenter(List<Double> center) {
        this.center = center;
    }

    public List<Hexagon> getChildren() {
        return children;
    }

    public void setChildren(List<Hexagon> children) {
        this.children = children;
    }

    public BoundingSphere getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingSphere boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Integer getIntersection() {
        return intersection;
    }

    public void setIntersection(Integer intersection) {
        this.intersection = intersection;
    }

    public List<Integer> getColor() {
        return color;
    }

    public void setColor(List<Integer> color) {
        this.color = color;
    }
}
