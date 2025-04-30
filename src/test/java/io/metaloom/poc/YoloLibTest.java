package io.metaloom.poc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import io.metaloom.video4j.Video4j;
import io.metaloom.video4j.VideoFile;
import io.metaloom.video4j.impl.MatProvider;
import io.metaloom.video4j.opencv.CVUtils;
import io.metaloom.video4j.utils.ImageUtils;
import io.metaloom.video4j.utils.SimpleImageViewer;

public class YoloLibTest {

	private static String imagePath = "YOLOs-CPP/data/kitchen.jpg";

	static {
		Video4j.init();
		YoloLib.init();
	}

	@Test
	public void testBox() throws Throwable {
		List<Detection> detections = YoloLib.box();
		assertNotNull(detections);
		assertEquals(4, detections.size());
	}

	@Test
	public void testImage() throws Throwable {
		// System.setProperty("java.library.path", onnxLibPath);
		BufferedImage img = ImageUtils.load(new File(imagePath));
		Mat imageMat = MatProvider.mat(img, Imgproc.COLOR_BGRA2BGR565);
		CVUtils.bufferedImageToMat(img, imageMat);
		YoloLib.detect(imageMat);
		System.in.read();
	}

	@Test
	public void testVideo() throws Throwable {
		SimpleImageViewer viewer = new SimpleImageViewer();

		try (VideoFile video = VideoFile.open("/extra/vid/1.avi")) {
			video.seekToFrameRatio(0.6);
			// BufferedImage img = ImageUtils.load(new File(imagePath));
			// Mat imageMat = MatProvider.mat(img, Imgproc.COLOR_BGRA2BGR565);
			// CVUtils.bufferedImageToMat(img, imageMat);

			long start = System.currentTimeMillis();
			for (int i = 0; i < 3000; i++) {

				Mat imageMat = video.frame().mat();
				YoloLib.detect(imageMat);
				System.out.println(imageMat.type());
				// MemorySegment imageSeg = MemorySegment.ofAddress(imageMat.nativeObj);
				// MemorySegment vector = (MemorySegment) showHandler.invoke(imageSeg);
				// if (vector != null && vector != MemorySegment.NULL && vector.address() != 0) {
				// // printDetections(vector);
				// }
				YoloLib.detect(imageMat);
				viewer.show(imageMat);
				// System.in.read();
			}
			long dur = System.currentTimeMillis() - start;
			System.out.println("Took " + dur);
			Thread.sleep(100);
		}

	}
}
