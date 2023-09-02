package dev.pfaff.jacksoning.util;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public abstract class ChangeNotifier<T> {
	private boolean handled = false;
	private @Nullable T input = null;

	protected abstract boolean inputEquals(@Nullable T a, T b);

	public void update(T input) {
		handled &= inputEquals(this.input, input);
		this.input = input;
	}

	public boolean get() {
		if (handled) {
			return false;
		}
		handled = true;
		return true;
	}

	public boolean updateAndGet(T input) {
		update(input);
		return get();
	}

	public T input() {
		return input;
	}

	public static <T> ChangeNotifier<T> identity() {
		return new ChangeNotifier<>() {
			@Override
			protected boolean inputEquals(@Nullable T a, T b) {
				return a == b;
			}
		};
	}

	public static <T> ChangeNotifier<T> equality() {
		return new ChangeNotifier<>() {
			@Override
			protected boolean inputEquals(@Nullable T a, T b) {
				return Objects.equals(a, b);
			}
		};
	}

	// more optimization-friendly implementation of this would involve generating a new class that has the equality
	// function stored in the constant pool.
	public static <T> Supplier<ChangeNotifier<T>> custom(BiPredicate<@Nullable T, T> equality) {
		return () -> new ChangeNotifier<>() {
			@Override
			protected boolean inputEquals(@Nullable T a, T b) {
				return equality.test(a, b);
			}
		};
	}
}
