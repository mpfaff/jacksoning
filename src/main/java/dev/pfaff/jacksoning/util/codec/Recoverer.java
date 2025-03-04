package dev.pfaff.jacksoning.util.codec;

@FunctionalInterface
public interface Recoverer<T, R> {
	public R recover(CodecException e, T r) throws CodecException;
}
