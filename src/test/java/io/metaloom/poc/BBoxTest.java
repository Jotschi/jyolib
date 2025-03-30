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

		MemorySegment vectorMem = (MemorySegment) boxHandler.invoke();
		vectorMem = vectorMem.reinterpret(32);
		System.out.println("Got " + vectorMem + " from function.");

		// Extract vector fields
		MemorySegment dataPtr = vectorMem.get(ADDR, 0); // T* data
		dataPtr = dataPtr.reinterpret(4* ValueLayout.JAVA_INT.byteSize());
		long size = vectorMem.get(JSIZE_T, 0); // size
		// capacity is not needed for reading

		System.out.println("Vector size: " + size);
		if (size == 0 || dataPtr.address() == 0) {
			System.out.println("Vector is empty.");
			return;
		}

		// Read BoundingBox elements
		List<BoundingBox> boundingBoxes = new ArrayList<>();
		long structSize = 4 * JINT.byteSize(); // 4 int fields

		for (long i = 0; i < size; i++) {
			MemorySegment element = dataPtr.asSlice(i * structSize, structSize);
			int x = element.get(JINT, 0);
			int y = element.get(JINT, JINT.byteSize());
			int width = element.get(JINT, 2 * JINT.byteSize());
			int height = element.get(JINT, 3 * JINT.byteSize());

			boundingBoxes.add(new BoundingBox(x, y, width, height));
		}

		// Print results
		boundingBoxes.forEach(System.out::println);

	}
}
