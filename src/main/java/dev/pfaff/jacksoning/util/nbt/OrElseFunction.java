package dev.pfaff.jacksoning.util.nbt;

@FunctionalInterface
public interface OrElseFunction<T, R> {
	public T fromR(CodecException e, R r) throws CodecException;
}
