package dev.pfaff.jacksoning.util.memo;

public sealed interface Computer<R> {
	@FunctionalInterface
	public non-sealed interface By0<R> extends Computer<R> {
		R compute();
	}

	@FunctionalInterface
	public non-sealed interface By1<A, R> extends Computer<R> {
		R compute(A a);
	}

	@FunctionalInterface
	public non-sealed interface By2<A, B, R> extends Computer<R> {
		R compute(A a, B b);
	}

	@FunctionalInterface
	public non-sealed interface By3<A, B, C, R> extends Computer<R> {
		R compute(A a, B b, C c);
	}

	@FunctionalInterface
	public non-sealed interface By4<A, B, C, D, R> extends Computer<R> {
		R compute(A a, B b, C c, D d);
	}

	@FunctionalInterface
	public non-sealed interface By5<A, B, C, D, E, R> extends Computer<R> {
		R compute(A a, B b, C c, D d, E e);
	}

	@FunctionalInterface
	public non-sealed interface By6<A, B, C, D, E, F, R> extends Computer<R> {
		R compute(A a, B b, C c, D d, E e, F f);
	}

	@FunctionalInterface
	public non-sealed interface By7<A, B, C, D, E, F, G, R> extends Computer<R> {
		R compute(A a, B b, C c, D d, E e, F f, G g);
	}
}