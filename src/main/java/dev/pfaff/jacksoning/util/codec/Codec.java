package dev.pfaff.jacksoning.util.codec;

import java.util.function.Function;

/**
 * Codec for value-based types.
 * <p>
 * In essence, a codec is responsible for mapping a value of one type to another. It might do this by making a copy of
 * the data in the original {@code R}, but this is not guaranteed. It might provide a view of the original, possibly
 * mutable!
 * <p>
 * A key aspect of a codec is that it is a narrowing mapping. Hence, the mapping can only fail in one direction, that
 * being from {@code R} to {@code T}.
 */
public interface Codec<T, R> extends FromR<T, R> {
	public T fromR(R r) throws CodecException;
	public R toR(T t);

	@Override
	default Codec<T, R> or(T value) {
		return orElse((e, r) -> value);
	}

	@Override
	public default Codec<T, R> orElse(OrElseFunction<T, R> f) {
		var self = this;
		return new Codec<>() {
			@Override
			public T fromR(R r) throws CodecException {
				try {
					return self.fromR(r);
				} catch (CodecException e) {
					return f.fromR(e, r);
				}
			}

			@Override
			public R toR(T t) {
				return self.toR(t);
			}
		};
	}

	public default <U> Codec<U, R> then(Codec<U, T> codecUT) {
		var self = this;
		return new Codec<>() {
			@Override
			public U fromR(R r) throws CodecException {
				return codecUT.fromR(self.fromR(r));
			}

			@Override
			public R toR(U u) {
				return self.toR(codecUT.toR(u));
			}
		};
	}

	static <T, R> Codec<T, R> by(Function<T, R> toR, FromR<T, R> fromR) {
		return new Codec<>() {
			@Override
			public T fromR(R r) throws CodecException {
				return fromR.fromR(r);
			}

			@Override
			public R toR(T t) {
				return toR.apply(t);
			}
		};
	}

	static <T extends R, R> Codec<T, R> downcast(Class<? extends T> clazz) {
		return by(t -> t, r -> {
			if (clazz.isInstance(r)) {
				return (T) r;
			} else {
				throw new CodecException("R was not an instanceof " + clazz.getName());
			}
		});
	}

	static <T> Codec<T, T> identity() {
		return (Codec<T, T>) IDENTITY;
	}

	static Codec<Object, Object> IDENTITY = new Codec<>() {
		@Override
		public Object fromR(Object o) throws CodecException {
			return o;
		}

		@Override
		public Object toR(Object o) {
			return o;
		}

		@Override
		public Codec<Object, Object> or(Object value) {
			return this;
		}

		@Override
		public Codec<Object, Object> orElse(OrElseFunction<Object, Object> f) {
			return this;
		}

		@Override
		public <U> Codec<U, Object> then(Codec<U, Object> codecUT) {
			return codecUT;
		}
	};
}
