package me.jraynor.common.util;

import lombok.extern.log4j.Log4j2;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * This will allow for easy nbt reading/writing.
 */
@Log4j2
public final class TagUtils {
    private TagUtils() {}

    /**
     * Write a child compound to this compound with the specified name
     *
     * @param name the name of the child compound
     * @param pos  the position of the block
     */
    public static void writeBlockPos(net.minecraft.nbt.CompoundNBT tag, String name, BlockPos pos) {
        if (pos != null) {
            var posTag = new net.minecraft.nbt.CompoundNBT();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            tag.put(name, posTag);
            log.info("wrote block position");
        }
    }

    /**
     * return true if there is a block pos for the given name
     *
     * @param tag  the tag to check against
     * @param name the name of the tag
     * @return true if block pos is present
     */
    public static boolean hasBlockPos(CompoundNBT tag, String name) {
        return tag.contains(name);
    }


    /**
     * Gets the specified block position from the child compound tag
     * can return null if not found so you should check it.
     *
     * @param name the name of the block to read
     * @return the read block
     */
    @Nullable public static BlockPos readBlockPos(net.minecraft.nbt.CompoundNBT tag, String name) {
        var posTag = (net.minecraft.nbt.CompoundNBT) tag.get(name);
        if (posTag != null) {
            var x = posTag.getInt("x");
            var y = posTag.getInt("y");
            var z = posTag.getInt("z");
            return new BlockPos(x, y, z);
        }
        return null;
    }

    /**
     * This will write a uuid to the tag with the given name
     *
     * @param tag  the tag to write to
     * @param name the uuid name
     * @param uuid the uuid to write
     */
    public static void writeUniqueId(CompoundNBT tag, String name, UUID uuid) {
        tag.putLong(name + "_most_bits", uuid.getMostSignificantBits());
        tag.putLong(name + "_least_bits", uuid.getLeastSignificantBits());
    }

    /**
     * Checks to see if the tag has the given unique by the name
     *
     * @param tag  the tag to check
     * @param name the name to check
     * @return true if the unique tag is present
     */
    public static boolean hasUniqueId(CompoundNBT tag, String name) {
        return tag.contains(name + "_least_bits") && tag.contains(name + "_most_bits");
    }

    /**
     * This will read a unique id from the compound tag
     *
     * @param tag  the tag to read from
     * @param name the name of the unique id
     * @return the uuid
     */
    public static UUID readUniqueId(CompoundNBT tag, String name) {
        if (!hasUniqueId(tag, name)) return null;
        var most = tag.getLong(name + "_most_bits");
        var least = tag.getLong(name + "_least_bits");
        return new UUID(most, least);
    }


    /**
     * Writes a enum based upon the ordinal to this current tag.
     *
     * @param name  the name of the enum to write
     * @param value the ordinal value to write
     */
    public static void writeEnumValue(net.minecraft.nbt.CompoundNBT tag, String name, Enum<?> value) {
        tag.putInt(name, value.ordinal());
    }

    /**
     * This will read the enum value from the name and cast it to the given class.
     *
     * @param name      the name of the enum
     * @param enumClass the class of the neum
     * @param <T>       the generic enum type
     * @return the return type of the enum
     */
    @Nullable public static <T extends Enum<T>> T readEnumValue(net.minecraft.nbt.CompoundNBT tag, String name, Class<T> enumClass) {
        return (enumClass.getEnumConstants())[tag.getInt(name)];
    }

}
