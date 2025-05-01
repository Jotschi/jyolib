package io.metaloom.jyolib.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

import io.metaloom.jyolib.Detection;
import io.metaloom.jyolib.YoloLib;
import io.metaloom.video4j.Video4j;
import io.metaloom.video4j.VideoFile;
import io.metaloom.video4j.VideoFrame;
import io.metaloom.video4j.opencv.CVUtils;
import io.metaloom.video4j.utils.ImageUtils;
import io.metaloom.video4j.utils.SimpleImageViewer;

public class UsageExampleTest {

	@Test
	public void testImageUsageExample() throws IOException {
		// SNIPPET START image-usage.example
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
		// SNIPPET END image-usage.example
	}

	@Test
	public void testVideoUsageExample() throws IOException {
		// SNIPPET START video-usage.example
		boolean useGPU = true;

		// Initialize video4j and YoloLib (Video4j is used to handle OpenCV Mat)
		Video4j.init();
		YoloLib.init("YOLOs-CPP/models/yolo8n.onnx", "YOLOs-CPP/models/coco.names", useGPU);
		SimpleImageViewer viewer = new SimpleImageViewer();

		boolean run = false;
		// Open the video using Video4j
		try (VideoFile video = VideoFile.open("src/test/resources/3769953-hd_1920_1080_25fps.mp4")) {

			// Process each frame
			VideoFrame frame;
			while ((frame = video.frame()) != null) {
				video.frame();
				CVUtils.resize(frame, 1024);
				// Run the detection on the mat reference
				List<Detection> detections = YoloLib.detect(frame.mat(), true);

				// Print the detections
				for (Detection detection : detections) {
					System.out
						.println("Frame[" + video.currentFrame() + "] " + detection.label() + " = " + detection.conf() + " @ " + detection.box());
				}

				viewer.show(frame.mat());
				if(!run) {
					System.in.read();
					run = true;
				}
			}
		}
		// SNIPPET END video-usage.example
	}

}
