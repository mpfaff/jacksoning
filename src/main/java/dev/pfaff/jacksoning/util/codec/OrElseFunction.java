package dev.pfaff.jacksoning.util.codec;

@FunctionalInterface
public interface OrElseFunction<T, R> {
	public T fromR(CodecException e, R r) throws CodecException;
}
