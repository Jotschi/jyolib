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

extern "C" void initialize()
{
    bool isGPU = true;
    if (!initialized)
    {
        globalDetector = std::make_unique<YOLO12Detector>(modelPath, labelsPath, isGPU);
        initialized = true;
    }
}

// std::vector<Detection>*
extern "C" BoundingBox *detect(cv::Mat *imagePtr)
{

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
    if (results.empty())
    {
        return nullptr;
        // return boxes.dat
    }
    else
    {
        detector->drawBoundingBox(image, results);
        std::cerr << "Size:" << sizeof(results.front()) << std::endl;
        BoundingBox box = results.front().box;
        printf("box: %i:%i - %ix%i\n", box.x, box.y, box.width, box.height);
        std::vector<BoundingBox> boxes;
        boxes.push_back(box);
        printf("box addr is %p\n", boxes.data());
        return boxes.data();
        // return &box;
    }
}
