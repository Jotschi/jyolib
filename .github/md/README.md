# jYoLib


This jYoLib is a library which enables native access to YOLO object detection in Java.

Under the hood this library uses the Foreign Function and Memory API to hook into a custom library which uses YOLOs-CPP to run inference on OpenCV Mats which can be provided by Video4j.

## Limitations

Currently only AMD64 Linux is supported. Support for other platforms is not planned.

## Usage

```xml
<dependency>
  <groupId>io.metaloom.jyolib</groupId>
  <artifactId>jyolib</artifactId>
  <version>${project.version}</version>
</dependency>
```

Example
```java
%{snippet|id=usage.example|file=src/test/java/io/metaloom/jyolib/example/UsageExampleTest.java}
```


## Build 

### Requirements:

- YOLO-CPP [Using YOLO-CPP from C++](https://github.com/Geekgineer/YOLOs-CPP)
- JDK 23 or newer
- Maven
- GCC 13

### Building native code

```bash
git clone git@github.com:Geekgineer/YOLOs-CPP.git (Head Rev: 363930885855b0441ba672d5ead7c6363cc34edb)
cd yolib
./build.sh 1.20.1 1
```

## Releasing

```bash
# Set release version and commit changes
mvn versions:set -DgenerateBackupPoms=false
git add pom.xml ; git commit -m "Prepare release"

# Invoke release
mvn clean deploy -Drelease
```

