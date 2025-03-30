package io.metaloom.poc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
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

public class YoloTest {

	// private static String onnxLibFileName = "libonnxruntime.so.1.20.1";
	// private static String onnxLibName = "onnxruntime";
	// private static String onnxLibPath = "YOLOs-CPP/onnxruntime-linux-x64-1.20.1/lib/";
	// private static String onnxLib = "libonnxruntime.so.1.20.1";
	private static String libPath = "yolib/build/libyolib.so.1.0.0";

	private static String imagePath = "YOLOs-CPP/data/kitchen.jpg";
	private static Linker linker = Linker.nativeLinker();
	private static Arena arena = Arena.ofAuto();

	private static SymbolLookup yoloLibrary;
	static {
		Video4j.init();
		yoloLibrary = SymbolLookup.libraryLookup(libPath, arena);
	}

	@Test
	public void testBox() throws Throwable {
		MethodHandle boxHandler = linker
			.downcallHandle(
				yoloLibrary.findOrThrow("box"),
				FunctionDescriptor.of(ValueLayout.ADDRESS));

		MemorySegment vectorMem = (MemorySegment) boxHandler.invoke();
		vectorMem = vectorMem.reinterpret(ValueLayout.ADDRESS.byteSize() + ValueLayout.JAVA_LONG.byteSize());
		System.out.println("FFM@" + vectorMem);
		// System.out.println(ValueLayout.ADDRESS.byteSize());

		printVectorData(vectorMem);

		// NEXT
		System.out.println();
		System.out.println("Next");
		MemorySegment nextVectorMem = vectorMem.get(ValueLayout.ADDRESS, 0);
		nextVectorMem = nextVectorMem.reinterpret(ValueLayout.ADDRESS.byteSize() + ValueLayout.JAVA_LONG.byteSize());
		printVectorData(nextVectorMem);
		//System.out.println("FFM@" + vectorMem + " data#2: " + nextVectorMem.get(ValueLayout.JAVA_INT, 0));

	}

	private void printVectorData(MemorySegment vectorMem) {
		MemorySegment firstData = vectorMem.get(ValueLayout.ADDRESS, 0);
		System.out.println("Data:" + firstData);
		firstData = firstData.reinterpret(ValueLayout.JAVA_INT.byteSize() * 4);
		// System.out.println("FFM@" + mem + " vecDataSize: " + mem.get(ValueLayout.ADDRESS,8));
		System.out.println("FFM@" + vectorMem + " data#1: " + firstData.get(ValueLayout.JAVA_INT, 0));
	}

	@Test
	public void testImage() throws Throwable {
		// System.setProperty("java.library.path", onnxLibPath);

		BufferedImage img = ImageUtils.load(new File(imagePath));
		Mat imageMat = MatProvider.mat(img, Imgproc.COLOR_BGRA2BGR565);
		CVUtils.bufferedImageToMat(img, imageMat);

		MethodHandle detectHandler = linker
			.downcallHandle(
				yoloLibrary.findOrThrow("detect"),
				FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));

		MemorySegment imageSeg = MemorySegment.ofAddress(imageMat.getNativeObjAddr());
		MemorySegment addr = (MemorySegment) detectHandler.invoke(imageSeg);
		addr = addr.reinterpret(8);
		ImageUtils.show(imageMat);
		// System.out.println("FFM@" + addr.getAtIndex(OfInt.JAVA_INT, 0));
		// System.out.println("FFM2@" + addr.get(ValueLayout.JAVA_INT, 0));
		// System.out.println("FFM2@" + addr.get(ValueLayout.JAVA_INT, 4));
		// System.out.println("FFM2@" + addr.get(ValueLayout.JAVA_INT, 8));
		// System.out.println("FFM2@" + addr.get(ValueLayout.JAVA_INT, 12));
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
				MethodHandle showHandler = linker
					.downcallHandle(
						yoloLibrary.findOrThrow("detect"),
						FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
				Mat imageMat = video.frame().mat();
				System.out.println(imageMat.type());
				MemorySegment imageSeg = MemorySegment.ofAddress(imageMat.nativeObj);
				MemorySegment vector = (MemorySegment) showHandler.invoke(imageSeg);
				if (vector != null && vector != MemorySegment.NULL && vector.address() != 0) {
					printDetections(vector);
				}
				viewer.show(imageMat);
				// System.in.read();
			}
			long dur = System.currentTimeMillis() - start;
			System.out.println("Took " + dur);
			Thread.sleep(100);
		}

	}

	private void printDetections(MemorySegment vector) {
		System.out.println("FFM@" + vector);
		List<Detection> detections = VectorAccess.readVector(vector, arena);
		if (detections != null) {
			for (Detection d : detections) {
				System.out.println("FFM: d" + d.classId() + " " + d.x() + "x" + d.y() + " " + d.width() + "x" + d.height() + " conf:" + d.conf());
			}
		}
	}

}
