#include <string>
#include "det/YOLO12.hpp"
#include "yolib.hpp"
#include <string.h>

// For Test code
#include <iostream>
#include <string>
#include <memory>

//const std::string labelsPath = "YOLOs-CPP/models/coco.names";
//const std::string modelPath = "YOLOs-CPP/models/yolo8n.onnx"; // YOLOv12

static std::unique_ptr<YOLO12Detector> globalDetector;

static bool initialized = false;

extern "C" void test_str(const char* name, const char* name2) {
    printf("Test %s - %s\n", name, name2);
    fflush(stdout);
}

extern "C" void initialize(const char* labelsPath, const char* modelPath, bool useGPU)
{
    if (!initialized)
    {
        printf("Initializing YoloLib using model %s - labels %s - Using GPU: %s\n", modelPath, labelsPath, useGPU ? "true" : "false");
        fflush(stdout);
        globalDetector = std::make_unique<YOLO12Detector>(modelPath, labelsPath, useGPU);
        initialized = true;
    }
}

extern "C" void free_detection(DetectionArray *arr)
{
    if (arr)
    {
        delete[] arr->data;
        delete arr;
    }
}

extern "C" DetectionArray *detect_test()
{
    std::vector<Detection> tempVec;

    for (int i = 0; i < 4; i++)
    {
        BoundingBox bbox = {42 + i, 41, 40, 39 - i};
        Detection *d = new Detection;
        d->box = bbox;
        d->conf = 2.0f + i;
        d->classId = 11 + i;
        tempVec.push_back(*d);
    }

    DetectionArray *result = new DetectionArray;
    result->count = static_cast<int>(tempVec.size());
    result->data = new Detection[result->count];
    std::copy(tempVec.begin(), tempVec.end(), result->data);

    return result;
}

extern "C" DetectionArray *detect(cv::Mat *imagePtr, bool drawBoundingBox)
{

    //initialize();

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
    DetectionArray *result = new DetectionArray;
    if (results.empty())
    {
        result->count = 0;
        return result;
    }
    else
    {
        if (drawBoundingBox)
        {
            detector->drawBoundingBox(image, results);
        }

        result->count = static_cast<int>(results.size());
        result->data = new Detection[result->count];
        std::copy(results.begin(), results.end(), result->data);

        std::cerr << "Lib: Size:" << results.size() << " " << sizeof(results.front().box) << " ADDR " << std::endl;
        printf("Lib: box addr is %p\n", results.front().box);
        // fflush(stdout);
        // fflush(stdout);
        // std::vector<BoundingBox> boxes;
        // boxes.push_back(box);
        // BoundingBox box = results.front().box;
        // printf("Lib: box: %i:%i - %ix%i\n", box.x, box.y, box.width, box.height);
        // printf("Lib: data addr is %p\n", boxes.data());
        // printf("Lib: data size is %p\n", sizeof(boxes.data()));
        fflush(stdout);
        // return boxes.data();
        return result;
    }
}
