package dev.pfaff.jacksoning.client;

public record EdgeInsets(int left, int top, int right, int bottom) {
	public static EdgeInsets symmetrical(int horizontal, int vertical) {
		return new EdgeInsets(horizontal, vertical, horizontal, vertical);
	}

	public static EdgeInsets all(int value) {
		return new EdgeInsets(value, value, value, value);
	}

	public int horizontal() {
		return left() + right();
	}

	public int vertical() {
		return top() + bottom();
	}
}
