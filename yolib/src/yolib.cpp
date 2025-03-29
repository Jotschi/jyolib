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
        initialized= true;
    }
}


extern "C" void detectGlobal(cv::Mat* imagePtr) {

     initialize();

     YOLO12Detector* detector =  globalDetector.get();
    cv::Mat image    = *imagePtr;
    //printf("detectGlobal of x is %p\n", globalDetector);  


    // Detect objects in the image and measure execution time
    auto start = std::chrono::high_resolution_clock::now();
        std::cerr << "OK: start detection\n";
    std::vector<Detection> results = detector->detect(image);
    std::cerr << "OK: Run detection\n";
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(
                        std::chrono::high_resolution_clock::now() - start);

    std::cerr << "Detection completed in: " << duration.count() << " ms" << std::endl;

    // Draw bounding boxes on the image
    detector->drawBoundingBox(image, results); // simple bbox drawing
    // detector.drawBoundingBoxMask(image, results); // Uncomment for mask drawing

    // Display the image
    //cv::imshow("Detections", image);
    //cv::waitKey(0); // Wait for a key press to close the window
}




extern "C" void detect() {

    initialize();

    //printf("Address of x is %p\n", detector);  
YOLO12Detector* detector =  globalDetector.get();

// , cv::Mat image
    const std::string imagePath = "YOLOs-CPP/data/kitchen.jpg";
    cv::Mat image = cv::imread(imagePath);
    if (image.empty())
    {
        std::cerr << "Error: Could not open or find the image!\n";
        return;
    } else {
        std::cerr << "OK: Image Loaded\n";
    }


    // Detect objects in the image and measure execution time
    auto start = std::chrono::high_resolution_clock::now();
    std::vector<Detection> results = detector->detect(image);
    std::cerr << "OK: Run detection\n";
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(
                        std::chrono::high_resolution_clock::now() - start);

    std::cerr << "Detection completed in: " << duration.count() << " ms" << std::endl;

    // Draw bounding boxes on the image
    detector->drawBoundingBox(image, results); // simple bbox drawing
    // detector.drawBoundingBoxMask(image, results); // Uncomment for mask drawing

    // Display the image
    cv::imshow("Detections", image);
    cv::waitKey(0); // Wait for a key press to close the window
}
