package io.metaloom.poc;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.List;

public class VectorAccess {

	// Offsets inside std::vector<Detection>
	private static final long VECTOR_DATA_OFFSET = 0; // First field is data pointer
	private static final long VECTOR_SIZE_OFFSET = 8; // Second field is size_t
	
	public static List<Detection> readVector(MemorySegment vectorSegment, Arena arena) {
		// Read the pointer to data

		
		// MemorySegment dataSegment = vectorSegment.get(ADDRESS, VECTOR_DATA_OFFSET);
		//
		// // Read size of vector
		// long vectorSize = vectorSegment.get(JAVA_LONG, VECTOR_SIZE_OFFSET);
		//
		// // Array to store results
		// Detection[] detections = new Detection[(int) vectorSize];
		//
		// for (int i = 0; i < vectorSize; i++) {
		// MemorySegment elementSegment = dataSegment.asSlice(i * DetectionMemoryLayout.DETECTION_LAYOUT.byteSize());
		// detections[i] = extractDetection(elementSegment);
		// }

		return List.of(extractDetection(vectorSegment));
	}

	// Extract a single Detection from a MemorySegment
	private static Detection extractDetection(MemorySegment detectionSegment) {

		//MemorySegment boxSegment = (MemorySegment)DetectionMemoryLayout.DETECTION_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("box")).get(detectionSegment, 0);
		//.dereferenceElement()).set(segment,0);
		detectionSegment = detectionSegment.reinterpret(DetectionMemoryLayout.DETECTION_LAYOUT.byteSize());
		
//		MemorySegment loadedSegment = (MemorySegment) LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("addr")).get(segment2, 0);
//		loadedSegment = loadedSegment.reinterpret(LAYOUT.byteSize());

		//int value = boxSegment.get(ValueLayout.JAVA_INT, 0);
		//System.out.println(value);
		// int x = BoundingBoxMemoryLayout.getX(segment);
		// int y = BoundingBoxMemoryLayout.getY(segment);
		// int width = BoundingBoxMemoryLayout.getWidth(segment);
		// int height = BoundingBoxMemoryLayout.getHeight(segment);
		// float conf = DetectionMemoryLayout.getConf(segment);
		// int classId = DetectionMemoryLayout.getClassId(segment);
		int x = detectionSegment.get(ValueLayout.JAVA_INT, 0);
		int y =  detectionSegment.get(ValueLayout.JAVA_INT, 4);
		int width =  detectionSegment.get(ValueLayout.JAVA_INT, 8);
		int height =  detectionSegment.get(ValueLayout.JAVA_INT, 12);
		//float conf = DetectionMemoryLayout.getConf(detectionSegment);
		//int classId = DetectionMemoryLayout.getClassId(detectionSegment);
		float conf=0;
		int classId =0;
		return new Detection(x, y, width, height, conf, classId);
	}
}
