package dev.pfaff.jacksoning.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrays;

public final class OpenArrayList<T> extends ObjectArrayList<T> {
	protected OpenArrayList(T[] a, boolean wrapped) {
		super(a, wrapped);
	}

	public static <T> OpenArrayList<T> wrap(T[] a) {
		return new OpenArrayList<>(a, true);
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
}
