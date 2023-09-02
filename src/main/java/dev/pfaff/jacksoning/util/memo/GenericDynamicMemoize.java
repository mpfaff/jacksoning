package dev.pfaff.jacksoning.util.memo;

import dev.pfaff.jacksoning.Jacksoning;
import org.slf4j.event.Level;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public final class GenericDynamicMemoize implements DynamicMemoize {
	public static final GenericDynamicMemoize[] EMPTY_GENERIC_DIFFING_COMPUTERS = new GenericDynamicMemoize[0];

	private static final Level LOG_LEVEL_DIRTY = Level.DEBUG;

	private static final List<String> ARG_FIELDS = IntStream.rangeClosed('a', 'g')
															.mapToObj(i -> String.valueOf((char) i))
															.toList();
	private static final List<MethodHandle> GETTERS = ARG_FIELDS.stream().map(name -> {
		try {
			return MethodHandles.lookup().findGetter(GenericDynamicMemoize.class, name, Object.class);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
	}).toList();
	private static final List<MethodHandle> SETTERS = ARG_FIELDS.stream().map(name -> {
		try {
			return MethodHandles.lookup().findSetter(GenericDynamicMemoize.class, name, Object.class);
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
	}).toList();

	@SuppressWarnings("unused")
	private Object a, b, c, d, e, f, g;
	private Computer<?> func;
	private Object result;

	private boolean funcDirty(Computer func) {
		if (this.func != func) {
			Jacksoning.LOGGER.log(LOG_LEVEL_DIRTY, () -> "Computer is dirty: " + this.func + " != " + func);
			this.func = func;
			return true;
		} else {
			return false;
		}
	}

	private boolean argDirty(char argName, Object arg) {
		Object existing;
		int argI = (int) argName - (int) 'a';
		var getter = GETTERS.get(argI);
		try {
			existing = getter.invokeExact(this);
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
		if (!Objects.equals(existing, arg)) {
			Jacksoning.LOGGER.log(LOG_LEVEL_DIRTY,
								  () -> "Argument ." + argName + " is dirty: " + existing + " != " + arg);
			var setter = SETTERS.get(argI);
			try {
				setter.invokeExact(this, arg);
			} catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
			return true;
		} else {
			return false;
		}
	}

	// this *should* be inlined by the JVM into each of the get methods, if not further. From there, the argN checks
	// will be constant-folded.
	private <A, B, C, D, E, F, G, R> R update(Computer<R> func, int argN, A a, B b, C c, D d, E e, F f, G g) {
		boolean dirty = funcDirty(func);
		if (argN >= 1) dirty |= argDirty('a', a);
		if (argN >= 2) dirty |= argDirty('b', b);
		if (argN >= 3) dirty |= argDirty('c', c);
		if (argN >= 4) dirty |= argDirty('d', d);
		if (argN >= 5) dirty |= argDirty('e', e);
		if (argN >= 6) dirty |= argDirty('f', f);
		if (argN >= 7) dirty |= argDirty('g', g);
		if (dirty) {
			// dispatch to the appropriate compute function
			// this *should* be constant-folded too.
			R result = switch (func) {
				case Computer.By0<R> by -> by.compute();
				case Computer.By1 by -> (R) by.compute(a);
				case Computer.By2 by -> (R) by.compute(a, b);
				case Computer.By3 by -> (R) by.compute(a, b, c);
				case Computer.By4 by -> (R) by.compute(a, b, c, d);
				case Computer.By5 by -> (R) by.compute(a, b, c, d, e);
				case Computer.By6 by -> (R) by.compute(a, b, c, d, e, f);
				case Computer.By7 by -> (R) by.compute(a, b, c, d, e, f, g);
			};
			this.result = result;
			return result;
		}
		return (R) result;
	}

	// --- generated code ---
	@Override
	public <R> R get(Computer.By0<R> func) {
		return update(func, 0, null, null, null, null, null, null, null);
	}

	@Override
	public <A, R> R get(Computer.By1<A, R> func, A a) {
		return update(func, 0, a, null, null, null, null, null, null);
	}

	@Override
	public <A, R> R get(A a, Computer.By1<A, R> func) {
		return update(func, 1, a, null, null, null, null, null, null);
	}

	@Override
	public <A, B, R> R get(Computer.By2<A, B, R> func, A a, B b) {
		return update(func, 0, a, b, null, null, null, null, null);
	}

	@Override
	public <A, B, R> R get(A a, Computer.By2<A, B, R> func, B b) {
		return update(func, 1, a, b, null, null, null, null, null);
	}

	@Override
	public <A, B, R> R get(A a, B b, Computer.By2<A, B, R> func) {
		return update(func, 2, a, b, null, null, null, null, null);
	}

	@Override
	public <A, B, C, R> R get(Computer.By3<A, B, C, R> func, A a, B b, C c) {
		return update(func, 0, a, b, c, null, null, null, null);
	}

	@Override
	public <A, B, C, R> R get(A a, Computer.By3<A, B, C, R> func, B b, C c) {
		return update(func, 1, a, b, c, null, null, null, null);
	}

	@Override
	public <A, B, C, R> R get(A a, B b, Computer.By3<A, B, C, R> func, C c) {
		return update(func, 2, a, b, c, null, null, null, null);
	}

	@Override
	public <A, B, C, R> R get(A a, B b, C c, Computer.By3<A, B, C, R> func) {
		return update(func, 3, a, b, c, null, null, null, null);
	}

	@Override
	public <A, B, C, D, R> R get(Computer.By4<A, B, C, D, R> func, A a, B b, C c, D d) {
		return update(func, 0, a, b, c, d, null, null, null);
	}

	@Override
	public <A, B, C, D, R> R get(A a, Computer.By4<A, B, C, D, R> func, B b, C c, D d) {
		return update(func, 1, a, b, c, d, null, null, null);
	}

	@Override
	public <A, B, C, D, R> R get(A a, B b, Computer.By4<A, B, C, D, R> func, C c, D d) {
		return update(func, 2, a, b, c, d, null, null, null);
	}

	@Override
	public <A, B, C, D, R> R get(A a, B b, C c, Computer.By4<A, B, C, D, R> func, D d) {
		return update(func, 3, a, b, c, d, null, null, null);
	}

	@Override
	public <A, B, C, D, R> R get(A a, B b, C c, D d, Computer.By4<A, B, C, D, R> func) {
		return update(func, 4, a, b, c, d, null, null, null);
	}

	@Override
	public <A, B, C, D, E, R> R get(Computer.By5<A, B, C, D, E, R> func, A a, B b, C c, D d, E e) {
		return update(func, 0, a, b, c, d, e, null, null);
	}

	@Override
	public <A, B, C, D, E, R> R get(A a, Computer.By5<A, B, C, D, E, R> func, B b, C c, D d, E e) {
		return update(func, 1, a, b, c, d, e, null, null);
	}

	@Override
	public <A, B, C, D, E, R> R get(A a, B b, Computer.By5<A, B, C, D, E, R> func, C c, D d, E e) {
		return update(func, 2, a, b, c, d, e, null, null);
	}

	@Override
	public <A, B, C, D, E, R> R get(A a, B b, C c, Computer.By5<A, B, C, D, E, R> func, D d, E e) {
		return update(func, 3, a, b, c, d, e, null, null);
	}

	@Override
	public <A, B, C, D, E, R> R get(A a, B b, C c, D d, Computer.By5<A, B, C, D, E, R> func, E e) {
		return update(func, 4, a, b, c, d, e, null, null);
	}

	@Override
	public <A, B, C, D, E, R> R get(A a, B b, C c, D d, E e, Computer.By5<A, B, C, D, E, R> func) {
		return update(func, 5, a, b, c, d, e, null, null);
	}

	@Override
	public <A, B, C, D, E, F, R> R get(Computer.By6<A, B, C, D, E, F, R> func, A a, B b, C c, D d, E e, F f) {
		return update(func, 0, a, b, c, d, e, f, null);
	}

	@Override
	public <A, B, C, D, E, F, R> R get(A a, Computer.By6<A, B, C, D, E, F, R> func, B b, C c, D d, E e, F f) {
		return update(func, 1, a, b, c, d, e, f, null);
	}

	@Override
	public <A, B, C, D, E, F, R> R get(A a, B b, Computer.By6<A, B, C, D, E, F, R> func, C c, D d, E e, F f) {
		return update(func, 2, a, b, c, d, e, f, null);
	}

	@Override
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, Computer.By6<A, B, C, D, E, F, R> func, D d, E e, F f) {
		return update(func, 3, a, b, c, d, e, f, null);
	}

	@Override
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, D d, Computer.By6<A, B, C, D, E, F, R> func, E e, F f) {
		return update(func, 4, a, b, c, d, e, f, null);
	}

	@Override
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, D d, E e, Computer.By6<A, B, C, D, E, F, R> func, F f) {
		return update(func, 5, a, b, c, d, e, f, null);
	}

	@Override
	public <A, B, C, D, E, F, R> R get(A a, B b, C c, D d, E e, F f, Computer.By6<A, B, C, D, E, F, R> func) {
		return update(func, 6, a, b, c, d, e, f, null);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(Computer.By7<A, B, C, D, E, F, G, R> func, A a, B b, C c, D d, E e, F f, G g) {
		return update(func, 0, a, b, c, d, e, f, g);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(A a, Computer.By7<A, B, C, D, E, F, G, R> func, B b, C c, D d, E e, F f, G g) {
		return update(func, 1, a, b, c, d, e, f, g);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(A a, B b, Computer.By7<A, B, C, D, E, F, G, R> func, C c, D d, E e, F f, G g) {
		return update(func, 2, a, b, c, d, e, f, g);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, Computer.By7<A, B, C, D, E, F, G, R> func, D d, E e, F f, G g) {
		return update(func, 3, a, b, c, d, e, f, g);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, Computer.By7<A, B, C, D, E, F, G, R> func, E e, F f, G g) {
		return update(func, 4, a, b, c, d, e, f, g);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, Computer.By7<A, B, C, D, E, F, G, R> func, F f, G g) {
		return update(func, 5, a, b, c, d, e, f, g);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, F f, Computer.By7<A, B, C, D, E, F, G, R> func, G g) {
		return update(func, 6, a, b, c, d, e, f, g);
	}

	@Override
	public <A, B, C, D, E, F, G, R> R get(A a, B b, C c, D d, E e, F f, G g, Computer.By7<A, B, C, D, E, F, G, R> func) {
		return update(func, 7, a, b, c, d, e, f, g);
	}
	// --- generated code ---
}
