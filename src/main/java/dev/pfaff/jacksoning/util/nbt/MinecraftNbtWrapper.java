package dev.pfaff.jacksoning.util.nbt;

import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record MinecraftNbtWrapper(@NotNull net.minecraft.nbt.NbtElement nbt) implements NbtElement, NbtList, NbtCompound {
	@Nullable
	static MinecraftNbtWrapper of(@Nullable net.minecraft.nbt.NbtElement nbt) {
		if (nbt == null) return null;
		return new MinecraftNbtWrapper(nbt);
	}

	@Override
	public byte type() {
		return nbt.getType();
	}

	@Override
	public byte asByte() {
		return ((NbtByte) nbt).byteValue();
	}

	@Override
	public short asShort() {
		return ((NbtShort) nbt).shortValue();
	}

	@Override
	public int asInt() {
		return ((NbtInt) nbt).intValue();
	}

	@Override
	public long asLong() {
		return ((NbtLong) nbt).longValue();
	}

	@Override
	public float asFloat() {
		return ((NbtFloat) nbt).floatValue();
	}

	@Override
	public double asDouble() {
		return ((NbtDouble) nbt).doubleValue();
	}

	@Override
	public byte[] asByteArray() {
		return ((NbtByteArray) nbt).getByteArray();
	}

	@Override
	public String asString() {
		return ((NbtString) nbt).asString();
	}

	@Override
	public NbtList asList() {
		return this;
	}

	@Override
	public NbtCompound asCompound() {
		return this;
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
	public NbtElement get(String key) throws CodecException {
		return of(asMcCompound().get(key));
	}

	@Override
	public void put(String key, NbtElement value) {
		asMcCompound().put(key, ((MinecraftNbtWrapper) value).nbt);
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
	public NbtElement get(int index) throws CodecException {
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
