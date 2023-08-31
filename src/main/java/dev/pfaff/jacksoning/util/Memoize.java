package dev.pfaff.jacksoning.util;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Memoize<T, R> {
	private boolean init = false;
	private T input;
	private R output;

	protected abstract boolean inputEquals(T a, T b);

	protected abstract R compute(T input);

	public void update(T input) {
		if (!init || !inputEquals(this.input, input)) {
			this.input = input;
			output = compute(input);
		}
		if (!init) {
			init = true;
		}
	}

	public Memoize<T, R> init(T input) {
		assert !init;
		this.input = input;
		output = compute(input);
		init = true;
		return this;
	}

	public R get() {
		assert init;
		return output;
	}

	public static <T, R> Supplier<Memoize<T, R>> identity(Function<T, R> compute) {
		return () -> new Memoize<>() {
			@Override
			protected boolean inputEquals(T a, T b) {
				return a == b;
			}

			@Override
			protected R compute(T input) {
				return compute.apply(input);
			}
		};
	}

	public static <T, R> Supplier<Memoize<T, R>> equality(Function<T, R> compute) {
		return () -> new Memoize<>() {
			@Override
			protected boolean inputEquals(T a, T b) {
				return a.equals(b);
			}

			@Override
			protected R compute(T input) {
				return compute.apply(input);
			}
		};
	}

	// more optimization-friendly implementation of this would involve generating a new class that has the compute and
	// equality functions stored in the constant pool.
	public static <T, R> Supplier<Memoize<T, R>> custom(Function<T, R> compute, BiPredicate<T, T> equality) {
		return () -> new Memoize<>() {
			@Override
			protected boolean inputEquals(T a, T b) {
				return equality.test(a, b);
			}

			@Override
			protected R compute(T input) {
				return compute.apply(input);
			}
		};
	}
}
