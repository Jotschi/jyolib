package io.metaloom.poc;

public record Detection(BoundingBox box, float conf, int classId) {

}
