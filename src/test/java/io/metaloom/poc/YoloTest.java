package io.metaloom.poc;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

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
	public void testYolo() throws Throwable {
		// System.setProperty("java.library.path", onnxLibPath);
		// -Djava.library.path=/usr/java/packages/lib:/usr/lib64:/lib64:/lib:/usr/lib:YOLOs-CPP/onnxruntime-linux-x64-1.20.1/lib/

		// Load the native library
		Arena session = Arena.ofAuto();
		// SymbolLookup library = SymbolLookup.loaderLookup().orElseThrow();
		SymbolLookup yoloLibrary = SymbolLookup.libraryLookup(libPath, session);

		// Lookup the function
		MethodHandle testHandler = Linker.nativeLinker()
			.downcallHandle(
				yoloLibrary.findOrThrow("detect"),
				FunctionDescriptor.ofVoid());

		testHandler.invoke();
	}

	@Test
	public void testYoloStepByStep() throws Throwable {
		// System.setProperty("java.library.path", onnxLibPath);

		BufferedImage img = ImageUtils.load(new File(imagePath));
		Mat imageMat = MatProvider.mat(img, Imgproc.COLOR_BGRA2BGR565);
		CVUtils.bufferedImageToMat(img, imageMat);
		// ImageUtils.show(mat);

		// -Djava.library.path=/usr/java/packages/lib:/usr/lib64:/lib64:/lib:/usr/lib:YOLOs-CPP/onnxruntime-linux-x64-1.20.1/lib/

		// Load the native library
		// SymbolLookup library = SymbolLookup.loaderLookup().orElseThrow();

		detect(imageMat);

	}

	@Test
	public void testYoloImage() throws Throwable {
		// System.setProperty("java.library.path", onnxLibPath);

		BufferedImage img = ImageUtils.load(new File(imagePath));
		Mat imageMat = MatProvider.mat(img, Imgproc.COLOR_BGRA2BGR565);
		CVUtils.bufferedImageToMat(img, imageMat);

		MethodHandle detectHandler = linker
			.downcallHandle(
				yoloLibrary.findOrThrow("detectGlobal"),
				FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

		MemorySegment imageSeg = MemorySegment.ofAddress(imageMat.getNativeObjAddr());
		MemorySegment addr = (MemorySegment) detectHandler.invoke(imageSeg);
		System.out.println(addr);

	}

	// Memory Layout for BoundingBox
	public static final GroupLayout BOUNDING_BOX_LAYOUT = MemoryLayout.structLayout(
		JAVA_INT.withName("x"),
		JAVA_INT.withName("y"),
		JAVA_INT.withName("width"),
		JAVA_INT.withName("height"));

	// Memory Layout for Detection
	public static final GroupLayout DETECTION_LAYOUT = MemoryLayout.structLayout(
		BOUNDING_BOX_LAYOUT.withName("box"),
		JAVA_FLOAT.withName("conf"),
		JAVA_INT.withName("classId"));

	// VarHandles for BoundingBox
	public static final VarHandle X_HANDLE = BOUNDING_BOX_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("x"));
	public static final VarHandle Y_HANDLE = BOUNDING_BOX_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("y"));
	public static final VarHandle WIDTH_HANDLE = BOUNDING_BOX_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("width"));
	public static final VarHandle HEIGHT_HANDLE = BOUNDING_BOX_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("height"));

	// VarHandles for Detection
	public static final VarHandle CONF_HANDLE = DETECTION_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("conf"));
	public static final VarHandle CLASS_ID_HANDLE = DETECTION_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("classId"));

	// Set BoundingBox fields
	public static void setBoundingBox(MemorySegment segment, int x, int y, int width, int height) {
		X_HANDLE.set(segment, x);
		Y_HANDLE.set(segment, y);
		WIDTH_HANDLE.set(segment, width);
		HEIGHT_HANDLE.set(segment, height);
	}

	// Set Detection fields
	public static void setDetection(MemorySegment segment, int x, int y, int width, int height, float conf, int classId) {
		setBoundingBox(segment, x, y, width, height);
		CONF_HANDLE.set(segment, conf);
		CLASS_ID_HANDLE.set(segment, classId);
	}

	// Get Detection confidence
	public static float getConf(MemorySegment segment) {
		return (float) CONF_HANDLE.get(segment);
	}

	public static int getClassId(MemorySegment segment) {
		return (int) CLASS_ID_HANDLE.get(segment);
	}

	@Test
	public void testShowMat() throws Throwable {
		SimpleImageViewer viewer = new SimpleImageViewer();

		try (VideoFile video = VideoFile.open("/extra/vid/1.avi")) {
			video.seekToFrameRatio(0.4);
			// BufferedImage img = ImageUtils.load(new File(imagePath));
			// Mat imageMat = MatProvider.mat(img, Imgproc.COLOR_BGRA2BGR565);
			// CVUtils.bufferedImageToMat(img, imageMat);

			long start = System.currentTimeMillis();
			for (int i = 0; i < 1000; i++) {
				video.frame();
				video.frame();
				MethodHandle showHandler = linker
					.downcallHandle(
						yoloLibrary.findOrThrow("detect"),
						//FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS));
						FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
				Mat imageMat = video.frame().mat();
				System.out.println(imageMat.type());
				MemorySegment imageSeg = MemorySegment.ofAddress(imageMat.nativeObj);
				MemorySegment vector = (MemorySegment) showHandler.invoke(imageSeg);
				viewer.show(imageMat);
				// System.in.read();
			}
			long dur = System.currentTimeMillis() - start;
			System.out.println("Took " + dur);

		}

	}

	private void detect(Mat imageMat) throws Throwable {
		System.out.println("Invoke detect");
		MethodHandle detectHandler = linker
			.downcallHandle(
				yoloLibrary.findOrThrow("detectGlobal"),
				FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

		imageMat.dataAddr();
		detectHandler.invoke(imageMat);

	}

	private MemorySegment initYolo(Linker linker, SymbolLookup yoloLibrary) throws Throwable {
		// Lookup the function
		MethodHandle initHandler = linker
			.downcallHandle(
				yoloLibrary.findOrThrow("initialize"),
				FunctionDescriptor.of(ValueLayout.ADDRESS));

		MemorySegment addr = (MemorySegment) initHandler.invoke();
		System.out.println(addr);

		if (addr == null) {
			throw new RuntimeException("Failed to initialize YOLO12Detector");
		}
		// addr = addr.reinterpret(ValueLayout..byteSize());

		return addr;

	}
}
