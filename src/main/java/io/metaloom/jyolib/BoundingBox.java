package io.metaloom.jyolib;

public class BoundingBox {

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
