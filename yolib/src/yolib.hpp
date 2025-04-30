#ifndef YOLO12_HPP
#define YOLO12_HPP

#include <vector>
#include <opencv2/highgui/highgui.hpp>

typedef struct {
    Detection* data;  // pointer to dynamically allocated Detection array
    int count;          // number of Detections
} DetectionArray;


#endif // YOLO12_HPP