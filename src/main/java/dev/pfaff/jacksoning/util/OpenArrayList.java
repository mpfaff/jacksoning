package dev.pfaff.jacksoning.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrays;

import java.util.function.Consumer;

public final class OpenArrayList<T> extends ObjectArrayList<T> {
	private OpenArrayList() {
	}

	private OpenArrayList(int capacity) {
		super(capacity);
	}

	private OpenArrayList(T[] a, boolean wrapped) {
		super(a, wrapped);
	}

	public static OpenArrayList<Object> create() {
		return createGeneric();
	}

	public static OpenArrayList<Object> create(int capacity) {
		return createGeneric(capacity);
	}

	public static <T> OpenArrayList<T> wrap(T[] a) {
		return new OpenArrayList<>(a, true);
	}

	public static <T> OpenArrayList<T> createGeneric() {
		return new OpenArrayList<>();
	}

	public static <T> OpenArrayList<T> createGeneric(int capacity) {
		return new OpenArrayList<>(capacity);
	}

	public T[] a() {
		return a;
	}

	public void setSizeAssumeCapacityAndInitialized(int size) {
		assert size <= this.a.length;
		this.size = size;
	}

	@Override
	public void ensureCapacity(int capacity) {
		ensureCapacityAndReturnArray(capacity);
	}

	public T[] ensureCapacityAndReturnArray(int capacity) {
		var a = this.a;
		if (capacity > a.length) {
			if (a != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
				capacity = Math.max((int) Math.min((long) a.length + (long) (a.length >> 1), 2147483639),
									capacity);
			} else if (capacity < 10) {
				capacity = 10;
			}

			assert this.wrapped;
			a = ObjectArrays.forceCapacity(a, capacity, this.size);
			assert this.size <= a.length;
			return this.a = a;
		}
		return a;
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		var a = this.a;
		int upr = this.size;
		if (upr <= a.length) {
			for (int i = 0; i < upr;) {
				action.accept(a[i++]);
			}
		}
	}

	public void forEachReversed(Consumer<? super T> action) {
		var a = this.a;
		int upr = this.size;
		if (upr <= a.length) {
			for (int i = upr; i > 0; ) {
				action.accept(a[--i]);
			}
		}
	}
}
