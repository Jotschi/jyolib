#include <string>
#include "det/YOLO12.hpp"
#include "yolib.hpp"

// For Test code
#include <iostream>
#include <string>
#include <memory>

const std::string labelsPath = "YOLOs-CPP/models/coco.names";
const std::string modelPath = "YOLOs-CPP/models/yolo8n.onnx"; // YOLOv12

static std::unique_ptr<YOLO12Detector> globalDetector;

static bool initialized = false;

extern "C" void initialize() {
    bool isGPU = true;
    if (!initialized) {
        globalDetector = std::make_unique<YOLO12Detector>(modelPath, labelsPath, isGPU);
        initialized = true;
    }
}

extern "C" void free_boxes(BoundingBoxArray* arr) {
    if (arr) {
        delete[] arr->data;
        delete arr;
    }
}

extern "C" BoundingBoxArray* box() {
    std::vector<BoundingBox> tempVec;
    tempVec.push_back({42, 41, 40, 39});
    tempVec.push_back({1, 2, 3, 4});
    tempVec.push_back({5, 2, 3, 4});

    // Allocate memory for BoundingBoxArray
    BoundingBoxArray* result = new BoundingBoxArray;

    // Allocate memory for the BoundingBox array and copy data
    result->count = static_cast<int>(tempVec.size());
    result->data = new BoundingBox[result->count];
    std::copy(tempVec.begin(), tempVec.end(), result->data);

    return result;
}

    //BoundingBox* bbox =new BoundingBox(42,41,40,39);;
    //printf("Lib: box addr is %p\n", (void*)bbox);
    //printf("Lib: box size is %i\n", sizeof(*bbox));
    //printf("Lib: box val is %i\n", bbox->height);
    //fflush(stdout);
// std::vector<Detection>*

extern "C" BoundingBox detect(cv::Mat *imagePtr) {

    initialize();

    YOLO12Detector *detector = globalDetector.get();
    cv::Mat image = *imagePtr;
    // printf("detectGlobal of x is %p\n", globalDetector);

    // Detect objects in the image and measure execution time
    auto start = std::chrono::high_resolution_clock::now();
    std::cerr << "OK: start detection\n";
    std::vector<Detection> results = detector->detect(image);
    std::cerr << "OK: Run detection\n";
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::high_resolution_clock::now() - start);
    std::cerr << "Detection completed in: " << duration.count() << " ms" << std::endl;

    // Draw bounding boxes on the image
    // detector.drawBoundingBoxMask(image, results); // Uncomment for mask drawing

    // Display the image
    // cv::imshow("Detections", image);
    // cv::waitKey(0); // Wait for a key press to close the window
    // std::vector<BoundingBox> boxes;
    if (results.empty()) {
        //return nullptr;
        // return boxes.dat
        return BoundingBox();
    } else {
        detector->drawBoundingBox(image, results);
        std::cerr << "Lib: Size:" << results.size() << " " << sizeof(results.front().box) << " ADDR " << std::endl;
        printf("Lib: box addr is %p\n", results.front().box);
        //fflush(stdout);
        BoundingBox box = results.front().box;
        printf("Lib: box: %i:%i - %ix%i\n", box.x, box.y, box.width, box.height);
        //fflush(stdout);
        std::vector<BoundingBox> boxes;
        boxes.push_back(box);
        printf("Lib: data addr is %p\n", boxes.data());
        printf("Lib: data size is %p\n", sizeof(boxes.data()));
        fflush(stdout);
        //return boxes.data();
         return box;
    }
}
