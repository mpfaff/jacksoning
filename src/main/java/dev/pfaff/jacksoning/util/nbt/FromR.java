package dev.pfaff.jacksoning.util.nbt;

@FunctionalInterface
public interface FromR<T, R> {
	public T fromR(R r) throws CodecException;

	public default FromR<T, R> or(T value) {
		return orElse((e, r) -> value);
	}

	public default FromR<T, R> orElse(OrElseFunction<T, R> f) {
		var self = this;
		return r -> {
			try {
				return self.fromR(r);
			} catch (CodecException e) {
				return f.fromR(e, r);
			}
		};
	}

	public default <U> FromR<U, R> then(FromR<U, T> fromT) {
		return r -> fromT.fromR(fromR(r));
	}
}
