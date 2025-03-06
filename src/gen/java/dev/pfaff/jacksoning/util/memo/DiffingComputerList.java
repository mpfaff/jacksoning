package dev.pfaff.jacksoning.util.memo;

import dev.pfaff.jacksoning.util.OpenArrayList;

public final class DiffingComputerList {
	private final OpenArrayList<GenericDynamicMemoize> entries = OpenArrayList.wrap(GenericDynamicMemoize.EMPTY_GENERIC_DIFFING_COMPUTERS);

	private GenericDynamicMemoize getEntry(int i) {
		if (i == entries.size()) {
			var entry = new GenericDynamicMemoize();
			entries.add(entry);
			return entry;
		}
		return entries.get(i);
	}

	public <R> R get(int i, Computer.By1<Integer, R> func) {
		return getEntry(i).get(func, i);
	}

	public <A, R> R get(int i, Computer.By2<A, Integer, R> func, A a) {
		return getEntry(i).get(func, a, i);
	}

	public <A, R> R get(int i, A a, Computer.By2<A, Integer, R> func) {
		return getEntry(i).get(a, func, i);
	}

	public <A, B, R> R get(int i, Computer.By3<A, B, Integer, R> func, A a, B b) {
		return getEntry(i).get(func, a, b, i);
	}

	public <A, B, R> R get(int i, A a, Computer.By3<A, B, Integer, R> func, B b) {
		return getEntry(i).get(a, func, b, i);
	}

	public <A, B, R> R get(int i, A a, B b, Computer.By3<A, B, Integer, R> func) {
		return getEntry(i).get(a, b, func, i);
	}

	public <A, B, C, R> R get(int i, Computer.By4<A, B, C, Integer, R> func, A a, B b, C c) {
		return getEntry(i).get(func, a, b, c, i);
	}

	public <A, B, C, R> R get(int i, A a, Computer.By4<A, B, C, Integer, R> func, B b, C c) {
		return getEntry(i).get(a, func, b, c, i);
	}

	public <A, B, C, R> R get(int i, A a, B b, Computer.By4<A, B, C, Integer, R> func, C c) {
		return getEntry(i).get(a, b, func, c, i);
	}

	public <A, B, C, R> R get(int i, A a, B b, C c, Computer.By4<A, B, C, Integer, R> func) {
		return getEntry(i).get(a, b, c, func, i);
	}

	public <A, B, C, D, R> R get(int i, Computer.By5<A, B, C, D, Integer, R> func, A a, B b, C c, D d) {
		return getEntry(i).get(func, a, b, c, d, i);
	}

	public <A, B, C, D, R> R get(int i, A a, Computer.By5<A, B, C, D, Integer, R> func, B b, C c, D d) {
		return getEntry(i).get(a, func, b, c, d, i);
	}

	public <A, B, C, D, R> R get(int i, A a, B b, Computer.By5<A, B, C, D, Integer, R> func, C c, D d) {
		return getEntry(i).get(a, b, func, c, d, i);
	}

	public <A, B, C, D, R> R get(int i, A a, B b, C c, Computer.By5<A, B, C, D, Integer, R> func, D d) {
		return getEntry(i).get(a, b, c, func, d, i);
	}

	public <A, B, C, D, R> R get(int i, A a, B b, C c, D d, Computer.By5<A, B, C, D, Integer, R> func) {
		return getEntry(i).get(a, b, c, d, func, i);
	}

	public <A, B, C, D, E, R> R get(int i, Computer.By6<A, B, C, D, E, Integer, R> func, A a, B b, C c, D d, E e) {
		return getEntry(i).get(func, a, b, c, d, e, i);
	}

	public <A, B, C, D, E, R> R get(int i, A a, Computer.By6<A, B, C, D, E, Integer, R> func, B b, C c, D d, E e) {
		return getEntry(i).get(a, func, b, c, d, e, i);
	}

	public <A, B, C, D, E, R> R get(int i, A a, B b, Computer.By6<A, B, C, D, E, Integer, R> func, C c, D d, E e) {
		return getEntry(i).get(a, b, func, c, d, e, i);
	}

	public <A, B, C, D, E, R> R get(int i, A a, B b, C c, Computer.By6<A, B, C, D, E, Integer, R> func, D d, E e) {
		return getEntry(i).get(a, b, c, func, d, e, i);
	}

	public <A, B, C, D, E, R> R get(int i, A a, B b, C c, D d, Computer.By6<A, B, C, D, E, Integer, R> func, E e) {
		return getEntry(i).get(a, b, c, d, func, e, i);
	}

	public <A, B, C, D, E, R> R get(int i, A a, B b, C c, D d, E e, Computer.By6<A, B, C, D, E, Integer, R> func) {
		return getEntry(i).get(a, b, c, d, e, func, i);
	}

	public <A, B, C, D, E, F, R> R get(int i, Computer.By7<A, B, C, D, E, F, Integer, R> func, A a, B b, C c, D d, E e, F f) {
		return getEntry(i).get(func, a, b, c, d, e, f, i);
	}

	public <A, B, C, D, E, F, R> R get(int i, A a, Computer.By7<A, B, C, D, E, F, Integer, R> func, B b, C c, D d, E e, F f) {
		return getEntry(i).get(a, func, b, c, d, e, f, i);
	}

	public <A, B, C, D, E, F, R> R get(int i, A a, B b, Computer.By7<A, B, C, D, E, F, Integer, R> func, C c, D d, E e, F f) {
		return getEntry(i).get(a, b, func, c, d, e, f, i);
	}

	public <A, B, C, D, E, F, R> R get(int i, A a, B b, C c, Computer.By7<A, B, C, D, E, F, Integer, R> func, D d, E e, F f) {
		return getEntry(i).get(a, b, c, func, d, e, f, i);
	}

	public <A, B, C, D, E, F, R> R get(int i, A a, B b, C c, D d, Computer.By7<A, B, C, D, E, F, Integer, R> func, E e, F f) {
		return getEntry(i).get(a, b, c, d, func, e, f, i);
	}

	public <A, B, C, D, E, F, R> R get(int i, A a, B b, C c, D d, E e, Computer.By7<A, B, C, D, E, F, Integer, R> func, F f) {
		return getEntry(i).get(a, b, c, d, e, func, f, i);
	}

	public <A, B, C, D, E, F, R> R get(int i, A a, B b, C c, D d, E e, F f, Computer.By7<A, B, C, D, E, F, Integer, R> func) {
		return getEntry(i).get(a, b, c, d, e, f, func, i);
	}

	public void truncate(int length) {
		var arr = entries.a();
		for (int i = length; i < arr.length; i++) {
			var entry = arr[i];
			if (entry != null) entry.reset();
		}
	}
}
