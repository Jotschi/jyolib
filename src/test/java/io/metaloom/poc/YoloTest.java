package io.metaloom.poc;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import io.metaloom.video4j.Video4j;

public class YoloTest {

	// private static String onnxLibFileName = "libonnxruntime.so.1.20.1";
	// private static String onnxLibName = "onnxruntime";
	// private static String onnxLibPath = "YOLOs-CPP/onnxruntime-linux-x64-1.20.1/lib/";
	// private static String onnxLib = "libonnxruntime.so.1.20.1";




//	@Test
//	public void testBox() throws Throwable {
//		MethodHandle boxHandler = linker
//			.downcallHandle(
//				yoloLibrary.findOrThrow("box"),
//				FunctionDescriptor.of(ValueLayout.ADDRESS));
//
//		MemorySegment vectorMem = (MemorySegment) boxHandler.invoke();
//		vectorMem = vectorMem.reinterpret(ValueLayout.ADDRESS.byteSize() + ValueLayout.JAVA_LONG.byteSize());
//		System.out.println("FFM@" + vectorMem);
//		// System.out.println(ValueLayout.ADDRESS.byteSize());
//
//		printVectorData(vectorMem);
//
//		// NEXT
//		System.out.println();
//		System.out.println("Next");
//		MemorySegment nextVectorMem = vectorMem.get(ValueLayout.ADDRESS, 0);
//		nextVectorMem = nextVectorMem.reinterpret(ValueLayout.ADDRESS.byteSize() + ValueLayout.JAVA_LONG.byteSize());
//		printVectorData(nextVectorMem);
//		// System.out.println("FFM@" + vectorMem + " data#2: " + nextVectorMem.get(ValueLayout.JAVA_INT, 0));
//	}

	private void printVectorData(MemorySegment vectorMem) {
		MemorySegment firstData = vectorMem.get(ValueLayout.ADDRESS, 0);
		System.out.println("Data:" + firstData);
		firstData = firstData.reinterpret(ValueLayout.JAVA_INT.byteSize() * 4);
		// System.out.println("FFM@" + mem + " vecDataSize: " + mem.get(ValueLayout.ADDRESS,8));
		System.out.println("FFM@" + vectorMem + " data#1: " + firstData.get(ValueLayout.JAVA_INT, 0));
	}




//	private void printDetections(MemorySegment vector) {
//		System.out.println("FFM@" + vector);
//		List<Detection> detections = VectorAccess.readVector(vector, arena);
//		if (detections != null) {
//			for (Detection d : detections) {
//				System.out.println("FFM: d" + d.classId() + " " + d.x() + "x" + d.y() + " " + d.width() + "x" + d.height() + " conf:" + d.conf());
//			}
//		}
//	}

}
