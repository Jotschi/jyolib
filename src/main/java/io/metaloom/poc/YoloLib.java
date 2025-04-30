package io.metaloom.poc;

import java.lang.foreign.AddressLayout;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.ValueLayout.OfInt;
import java.lang.foreign.ValueLayout.OfLong;
import java.lang.invoke.MethodHandle;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import io.metaloom.video4j.utils.ImageUtils;

public class YoloLib {

	private static String libPath = "yolib/build/libyolib.so.1.0.0";

	static final Linker linker = Linker.nativeLinker();
	static final Arena arena = Arena.ofAuto();

	static final AddressLayout ADDR = ValueLayout.ADDRESS.withOrder(ByteOrder.nativeOrder());
	static final OfInt JINT = ValueLayout.JAVA_INT.withOrder(ByteOrder.nativeOrder());
	static final OfLong JSIZE_T = ValueLayout.JAVA_LONG.withOrder(ByteOrder.nativeOrder()); // size_t on 64-bit Linux

	private static SymbolLookup yoloLibrary;

	public static List<Detection> box() throws Throwable {
		MethodHandle detectTestHandler = linker.downcallHandle(
			SymbolLookup.libraryLookup(libPath, arena).find("detect_test").orElseThrow(),
			FunctionDescriptor.of(ADDR));

		MethodHandle freeDetectionHandler = linker.downcallHandle(
			SymbolLookup.libraryLookup(libPath, arena).find("free_detection").orElseThrow(),
			FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

		MemorySegment detectionArrayStruct = (MemorySegment) detectTestHandler.invoke();
		detectionArrayStruct = detectionArrayStruct.reinterpret(DetectionArrayMemoryLayout.size());

		MemorySegment dataPtr = detectionArrayStruct.get(ValueLayout.ADDRESS, 0);
		int detectionCount = detectionArrayStruct.get(ValueLayout.JAVA_INT, ValueLayout.ADDRESS.byteSize());
		dataPtr = dataPtr.reinterpret(DetectionMemoryLayout.size() * detectionCount);

		System.out.println("Received " + detectionCount + " detections");
		System.out.println("Got " + detectionArrayStruct + " from function.");

		// Read BoundingBox elements
		List<Detection> detections = new ArrayList<>();
		long structSize = DetectionMemoryLayout.size();
		for (long i = 0; i < detectionCount; i++) {
			MemorySegment detectionMemory = dataPtr.asSlice(i * structSize, structSize);
			float conf = DetectionMemoryLayout.getConf(detectionMemory);
			int clazz = DetectionMemoryLayout.getClassId(detectionMemory);
			int x = detectionMemory.get(JINT, 0);
			int y = detectionMemory.get(JINT, JINT.byteSize());
			int width = detectionMemory.get(JINT, 2 * JINT.byteSize());
			int height = detectionMemory.get(JINT, 3 * JINT.byteSize());
			BoundingBox bbox = new BoundingBox(x, y, width, height);
			detections.add(new Detection(bbox, conf, clazz));
		}

		// Print results
		detections.forEach(System.out::println);

		// Free the native memory
		freeDetectionHandler.invoke(detectionArrayStruct);
		return detections;
	}

	public static void init() {
		yoloLibrary = SymbolLookup.libraryLookup(libPath, arena);

	}

	public static List<BoundingBox> detect(Mat imageMat) throws Throwable {
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
		return null;

	}
}
