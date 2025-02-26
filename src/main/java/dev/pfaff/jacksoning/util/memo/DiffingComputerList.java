package dev.pfaff.jacksoning.util.memo;

import dev.pfaff.jacksoning.util.OpenArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public final class DiffingComputerList<R> {
	private final OpenArrayList<GenericDynamicMemoize> entries = OpenArrayList.wrap(GenericDynamicMemoize.EMPTY_GENERIC_DIFFING_COMPUTERS);

	private GenericDynamicMemoize getEntry(int i) {
		if (i == entries.size()) {
			var entry = new GenericDynamicMemoize();
			entries.add(entry);
			return entry;
		}
		return entries.get(i);
	}

	public R get(int i, Computer.By1<Integer, R> func) {
		return getEntry(i).get(func, i);
	}

	public <A> R get(int i, Computer.By2<A, Integer, R> func, A a) {
		return getEntry(i).get(func, a, i);
	}

	public <A> R get(int i, A a, Computer.By2<A, Integer, R> func) {
		return getEntry(i).get(a, func, i);
	}

	public <A, B> R get(int i, Computer.By3<A, B, Integer, R> func, A a, B b) {
		return getEntry(i).get(func, a, b, i);
	}

	public <A, B> R get(int i, A a, Computer.By3<A, B, Integer, R> func, B b) {
		return getEntry(i).get(a, func, b, i);
	}

	public <A, B> R get(int i, A a, B b, Computer.By3<A, B, Integer, R> func) {
		return getEntry(i).get(a, b, func, i);
	}

	public void truncate(int length) {
		var arr = entries.a();
		for (int i = length; i < arr.length; i++) {
			var entry = arr[i];
			if (entry != null) entry.reset();
		}
	}
}
