#ifndef YOLO12_HPP
#define YOLO12_HPP

#include <vector>
#include <opencv2/highgui/highgui.hpp>

typedef struct {
    BoundingBox* data;  // pointer to dynamically allocated BoundingBox array
    int count;          // number of BoundingBoxes
} BoundingBoxArray;

#endif // YOLO12_HPP