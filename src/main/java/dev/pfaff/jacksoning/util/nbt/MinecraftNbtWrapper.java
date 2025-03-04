package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.CodecException;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_COMPOUND;

public record MinecraftNbtWrapper(@NotNull net.minecraft.nbt.NbtElement nbt) implements NbtElement, NbtList, NbtCompound {
	@Nullable
	static MinecraftNbtWrapper of(@Nullable net.minecraft.nbt.NbtElement nbt) {
		if (nbt == null) return null;
		return new MinecraftNbtWrapper(nbt);
	}

	@Override
	public NbtType type() {
		return NbtType.VALUES.get(nbt.getType());
	}

	private <T extends net.minecraft.nbt.NbtElement> T cast(Class<T> clazz, NbtType type) throws CodecException {
		if (nbt.getClass() != clazz) {
			throw new CodecException("The element is a " + type() + ", not a " + type);
		}
		return (T) nbt;
	}

	@Override
	public byte asByte() throws CodecException {
		return cast(NbtByte.class, NbtType.BYTE).byteValue();
	}

	@Override
	public short asShort() throws CodecException {
		return cast(NbtShort.class, NbtType.SHORT).shortValue();
	}

	@Override
	public int asInt() throws CodecException {
		return cast(NbtInt.class, NbtType.INT).intValue();
	}

	@Override
	public long asLong() throws CodecException {
		return cast(NbtLong.class, NbtType.LONG).longValue();
	}

	@Override
	public float asFloat() throws CodecException {
		return cast(NbtFloat.class, NbtType.FLOAT).floatValue();
	}

	@Override
	public double asDouble() throws CodecException {
		return cast(NbtDouble.class, NbtType.DOUBLE).doubleValue();
	}

	@Override
	public byte[] asByteArray() throws CodecException {
		return cast(NbtByteArray.class, NbtType.BYTE_ARRAY).getByteArray();
	}

	@Override
	public String asString() throws CodecException {
		return cast(NbtString.class, NbtType.STRING).asString();
	}

	@Override
	public NbtList asList() throws CodecException {
		if (nbt instanceof net.minecraft.nbt.NbtList) return this;
		throw new CodecException("The element is a " + type() + ", not a " + NbtType.LIST);
	}

	@Override
	public NbtCompound asCompound() throws CodecException {
		if (nbt instanceof net.minecraft.nbt.NbtCompound) return this;
		throw new CodecException("The element is a " + type() + ", not a " + NbtType.COMPOUND);
	}

	@Override
	public int[] asIntArray() {
		return ((NbtIntArray) nbt).getIntArray();
	}

	@Override
	public long[] asLongArray() {
		return ((NbtLongArray) nbt).getLongArray();
	}

	private net.minecraft.nbt.NbtCompound asMcCompound() {
		return ((net.minecraft.nbt.NbtCompound) nbt);
	}

	private net.minecraft.nbt.NbtList asMcList() {
		return ((net.minecraft.nbt.NbtList) nbt);
	}

	@Override
	public NbtElement get(String key) {
		return of(asMcCompound().get(key));
	}

	@Override
	public void put(String key, NbtElement value) {
		asMcCompound().put(key, ((MinecraftNbtWrapper) value).nbt);
	}

	@Override
	public NbtCompound getOrPutCompound(String key) throws CodecException {
		var compound = get(key);
		if (compound != null) return NBT_COMPOUND.fromR(compound);
		var newCompound = new MinecraftNbtWrapper(new net.minecraft.nbt.NbtCompound());
		put(key, newCompound);
		return newCompound;
	}

	@Override
	public Iterable<String> keys() {
		return asMcCompound().getKeys();
	}

	@Override
	public int size() {
		return asMcList().size();
	}

	@Override
	public NbtElement get(int index) {
		return of(asMcList().get(index));
	}

	@Override
	public void set(int index, NbtElement value) {
		asMcList().set(index, ((MinecraftNbtWrapper) value).nbt);
	}

	@Override
	public void add(NbtElement value) {
		asMcList().add(((MinecraftNbtWrapper) value).nbt);
	}
}
