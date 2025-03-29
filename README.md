# Yolo Java PoC

This PoC uses the Foreign Function and Memory API to hook into a custom library which uses YOLOs-CPP to run inference on OpenCV Mats which can be provided by Video4j.

## Build 

Building native code

```bash
git clone git@github.com:Geekgineer/YOLOs-CPP.git (Head Rev: 363930885855b0441ba672d5ead7c6363cc34edb)
cd yolib
./build.sh
```


