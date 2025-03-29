


extern "C" void showMat(cv::Mat* imagePtr) {

    cv::Mat image    = *imagePtr;
    cv::imshow("test", image);
    cv::waitKey(0); 
}


extern "C" void detectGlobal(cv::Mat* imagePtr) {

    YOLO12Detector detector = initialize();
    cv::Mat image    = *imagePtr;
    //printf("detectGlobal of x is %p\n", globalDetector);  


    // Detect objects in the image and measure execution time
    auto start = std::chrono::high_resolution_clock::now();
        std::cerr << "OK: start detection\n";
    std::vector<Detection> results = detector.detect(image);
    std::cerr << "OK: Run detection\n";
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(
                        std::chrono::high_resolution_clock::now() - start);

    std::cerr << "Detection completed in: " << duration.count() << " ms" << std::endl;

    // Draw bounding boxes on the image
    detector.drawBoundingBox(image, results); // simple bbox drawing
    // detector.drawBoundingBoxMask(image, results); // Uncomment for mask drawing

    // Display the image
    cv::imshow("Detections", image);
    cv::waitKey(0); // Wait for a key press to close the window
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



extern "C" void detect() {

   YOLO12Detector detector = initialize();

    //printf("Address of x is %p\n", detector);  

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
    std::vector<Detection> results = detector.detect(image);
    std::cerr << "OK: Run detection\n";
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(
                        std::chrono::high_resolution_clock::now() - start);

    std::cerr << "Detection completed in: " << duration.count() << " ms" << std::endl;

    // Draw bounding boxes on the image
    detector.drawBoundingBox(image, results); // simple bbox drawing
    // detector.drawBoundingBoxMask(image, results); // Uncomment for mask drawing

    // Display the image
    cv::imshow("Detections", image);
    cv::waitKey(0); // Wait for a key press to close the window
}




extern "C" void  testInvoke() {
    const std::string imagePath = "YOLOs-CPP/data/kitchen.jpg";
    cv::Mat image = cv::imread(imagePath);
    if (image.empty())
    {
        std::cerr << "Error: Could not open or find the image!\n";
        return;
    }
    std::cerr << image.type();
    //YOLO12Detector detector = initialize();

    //if(!globalDetector) {
    //initialize();
    //}
    detect();
    //std::vector<Detection> results = detect(image);

}


/*
extern "C" void  testInvokeImage(cv::Mat image) {
    YOLO12Detector detector = initialize();
    detect(globalDetector);
    //std::vector<Detection> results = detect(image);

}

*/