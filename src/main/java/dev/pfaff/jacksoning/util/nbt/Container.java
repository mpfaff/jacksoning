package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;

import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_COMPOUND;

public interface Container {
	void readNbt(NbtCompound nbt) throws CodecException;

	void writeNbt(NbtCompound nbt) throws CodecException;

	default NbtCompound writeNbt() {
		var nbt = NbtElement.compound();
		try {
			writeNbt(nbt);
		} catch (CodecException e) {
			throw new RuntimeException(e);
		}
		return nbt;
	}

	static <T extends Container> Codec<Void, NbtElement> codec(T container) {
		return NBT_COMPOUND.then(Codec.by(_v -> {
			var nbt = NbtElement.compound();
			try {
				container.writeNbt(nbt);
			} catch (CodecException e) {
				throw new RuntimeException(e);
			}
			return nbt;
		}, r -> {
			container.readNbt(r);
			return null;
		}));
	}
}
