package io.metaloom.poc;

import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class BoundingBoxArrayMemoryLayout {

	public static final GroupLayout BOUNDING_BOX_ARRAY_LAYOUT = MemoryLayout.structLayout(
		ValueLayout.ADDRESS.withName("data"),
		JAVA_INT.withName("count"));

	// VarHandles for BoundingBox
	public static final VarHandle X_HANDLE = BOUNDING_BOX_ARRAY_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("count"));
	public static final VarHandle Y_HANDLE = BOUNDING_BOX_ARRAY_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("data"));

	// Set BoundingBox fields
	public static void setBoundingBox(MemorySegment segment, int x, int y, int width, int height) {
		X_HANDLE.set(segment, x);
		Y_HANDLE.set(segment, y);
	}

	public static int getX(MemorySegment segment) {
		return (int) X_HANDLE.get(segment, 0);
	}

	public static int getY(MemorySegment segment) {
		return (int) Y_HANDLE.get(segment, 0);
	}

}
