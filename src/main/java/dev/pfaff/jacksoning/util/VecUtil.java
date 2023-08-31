package dev.pfaff.jacksoning.util;

import net.minecraft.util.math.Vec3i;

import java.util.function.IntPredicate;

public final class VecUtil {
	public static boolean all(Vec3i vec, IntPredicate predicate) {
		return predicate.test(vec.getX()) && predicate.test(vec.getY()) && predicate.test(vec.getZ());
	}

	public static boolean any(Vec3i vec, IntPredicate predicate) {
		return predicate.test(vec.getX()) || predicate.test(vec.getY()) || predicate.test(vec.getZ());
	}
}
