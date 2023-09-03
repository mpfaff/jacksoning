package dev.pfaff.jacksoning.util;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class StreamUtil {
	public static <T> Stream<IntObjectImmutablePair<T>> withIndex(List<T> list) {
		return IntStream.range(0, list.size()).mapToObj(i -> new IntObjectImmutablePair<>(i, list.get(i)));
	}

	public static <T> Stream<IntIntImmutablePair> intsWithIndex(List<Integer> list) {
		return IntStream.range(0, list.size()).mapToObj(i -> new IntIntImmutablePair(i, list.get(i)));
	}
}
