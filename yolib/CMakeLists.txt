cmake_minimum_required(VERSION 3.0.0)
project(yolo_ort)

option(ONNXRUNTIME_DIR "Path to built ONNX Runtime directory." STRING)
message(STATUS "ONNXRUNTIME_DIR: ${ONNXRUNTIME_DIR}")

find_package(OpenCV REQUIRED)

include_directories("../YOLOs-CPP/include/")

add_library(yolib SHARED
    src/yolib.cpp)

set_target_properties(yolib PROPERTIES VERSION "1.0.0")
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

target_include_directories(yolib PUBLIC "${ONNXRUNTIME_DIR}/include")
target_compile_features(yolib PRIVATE cxx_std_14)
target_link_libraries(yolib ${OpenCV_LIBS})
target_link_libraries(yolib "${ONNXRUNTIME_DIR}/lib/libonnxruntime.so")
