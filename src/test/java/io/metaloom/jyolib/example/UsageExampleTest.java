package io.metaloom.jyolib.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.metaloom.jyolib.Detection;
import io.metaloom.jyolib.YoloLib;
import io.metaloom.video4j.Video4j;
import io.metaloom.video4j.utils.ImageUtils;

public class UsageExampleTest {

	@Test
	public void testUsageExample() throws IOException {
		// SNIPPET START usage.example
		String imagePath = "YOLOs-CPP/data/kitchen.jpg";
		boolean useGPU = true;

		// Initialize video4j and YoloLib (Video4j is used to handle OpenCV Mat)
		Video4j.init();
		YoloLib.init("YOLOs-CPP/models/yolo8n.onnx", "YOLOs-CPP/models/coco.names", useGPU);

		// Load the image and invoke the detection
		BufferedImage img = ImageUtils.load(new File(imagePath));
		List<Detection> detections = YoloLib.detect(img, false);

		// Print the detections
		for (Detection detection : detections) {
			System.out.println(detection.label() + " = " + detection.conf() + " @ " + detection.box());
		}
		// SNIPPET END usage.example
	}

}
