package dev.pfaff.jacksoning.util.codec;

import java.util.function.Function;

@FunctionalInterface
public interface Coder<T, R> {
	static <T, R> Coder<T, R> adapt(Function<T, R> f) {
		return f::apply;
	}

	public R apply(T r) throws CodecException;

	public default Coder<T, R> or(R value) {
		var self = this;
		return t -> {
			try {
				return self.apply(t);
			} catch (CodecException e) {
				return value;
			}
		};
	}

	public default Coder<T, R> orElse(Recoverer<T, R> f) {
		var self = this;
		return t -> {
			try {
				return self.apply(t);
			} catch (CodecException e) {
				return f.recover(e, t);
			}
		};
	}

	public default <S> Coder<T, S> then(Coder<R, S> coder) {
		return t -> coder.apply(apply(t));
	}
}
