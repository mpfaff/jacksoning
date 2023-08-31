package dev.pfaff.jacksoning.util;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public abstract class AndIntChangeNotifier<T> {
	private boolean handled = false;
	private @Nullable T inputA;
	private int inputB;

	protected abstract boolean inputEquals(@Nullable T a, T b);

	public void update(T inputA, int inputB) {
		updateA(inputA);
		updateB(inputB);
	}

	public void updateA(T inputA) {
		handled &= inputEquals(this.inputA, inputA);
		this.inputA = inputA;
	}

	public void updateB(int inputB) {
		handled &= this.inputB == inputB;
		this.inputB = inputB;
	}

	public boolean get() {
		if (handled) {
			return false;
		}
		handled = true;
		return true;
	}

	public boolean updateAAndGet(T inputA) {
		updateA(inputA);
		return get();
	}

	public boolean updateBAndGet(int inputB) {
		updateB(inputB);
		return get();
	}

	public boolean updateAndGet(T inputA, int inputB) {
		update(inputA, inputB);
		return get();
	}

	public T inputA() {
		return inputA;
	}

	public int inputB() {
		return inputB;
	}

	public static <T> AndIntChangeNotifier<T> identity() {
		return new AndIntChangeNotifier<>() {
			@Override
			protected boolean inputEquals(@Nullable T a, T b) {
				return a == b;
			}
		};
	}

	public static <T> AndIntChangeNotifier<T> equality() {
		return new AndIntChangeNotifier<>() {
			@Override
			protected boolean inputEquals(@Nullable T a, T b) {
				return Objects.equals(a, b);
			}
		};
	}

	// more optimization-friendly implementation of this would involve generating a new class that has the equality
	// function stored in the constant pool.
	public static <T> Supplier<AndIntChangeNotifier<T>> custom(BiPredicate<@Nullable T, T> equality) {
		return () -> new AndIntChangeNotifier<>() {
			@Override
			protected boolean inputEquals(@Nullable T a, T b) {
				return equality.test(a, b);
			}
		};
	}
}
