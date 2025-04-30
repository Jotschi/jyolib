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

import org.junit.jupiter.api.Test;

public class BBoxTest {

	private static String libPath = "yolib/build/libyolib.so.1.0.0";

	static final Linker linker = Linker.nativeLinker();
	static final Arena arena = Arena.ofAuto();

	static final AddressLayout ADDR = ValueLayout.ADDRESS.withOrder(ByteOrder.nativeOrder());
	static final OfInt JINT = ValueLayout.JAVA_INT.withOrder(ByteOrder.nativeOrder());
	static final OfLong JSIZE_T = ValueLayout.JAVA_LONG.withOrder(ByteOrder.nativeOrder()); // size_t on 64-bit Linux

	static class BoundingBox {
		int x, y, width, height;

		BoundingBox(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString() {
			return "BoundingBox{x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "}";
		}
	}

	@Test
	public void testBbox() throws Throwable {
		MethodHandle boxHandler = linker.downcallHandle(
			SymbolLookup.libraryLookup(libPath, arena).find("box").orElseThrow(),
			FunctionDescriptor.of(ADDR));

		MethodHandle freeBoxes = linker.downcallHandle(
			SymbolLookup.libraryLookup(libPath, arena).find("free_boxes").orElseThrow(),
			FunctionDescriptor.ofVoid(ValueLayout.ADDRESS) // accepts BoundingBoxArray*
		);

		MemorySegment resultStruct = (MemorySegment) boxHandler.invoke();
		resultStruct = resultStruct.reinterpret(BoundingBoxArrayMemoryLayout.BOUNDING_BOX_ARRAY_LAYOUT.byteSize());

		MemorySegment dataPtr = resultStruct.get(ValueLayout.ADDRESS, 0);
		int count = resultStruct.get(ValueLayout.JAVA_INT, ValueLayout.ADDRESS.byteSize());
		dataPtr = dataPtr.reinterpret(BoundingBoxMemoryLayout.BOUNDING_BOX_LAYOUT.byteSize() * count);

		System.out.println("Received " + count + " bounding boxes:");

		System.out.println("Got " + resultStruct + " from function.");

		// Read BoundingBox elements
		List<BoundingBox> boundingBoxes = new ArrayList<>();
		long structSize = BoundingBoxMemoryLayout.BOUNDING_BOX_LAYOUT.byteSize();
		for (long i = 0; i < count; i++) {
			MemorySegment boxMemory = dataPtr.asSlice(i * structSize, structSize);
			System.out.println("Read " + i);
			int x = boxMemory.get(JINT, 0);
			int y = boxMemory.get(JINT, JINT.byteSize());
			int width = boxMemory.get(JINT, 2 * JINT.byteSize());
			int height = boxMemory.get(JINT, 3 * JINT.byteSize());
			boundingBoxes.add(new BoundingBox(x, y, width, height));
		}

		// Print results
		boundingBoxes.forEach(System.out::println);

		// Free the native memory
		freeBoxes.invoke(resultStruct);
	}
}
