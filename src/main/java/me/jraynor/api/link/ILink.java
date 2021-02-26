package me.jraynor.api.link;

import me.jraynor.api.node.INode;
import me.jraynor.common.util.TagUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * The roots are linkable that don't have source links. They are roots.
 * The roots are what are synchronized to and from the client/server.
 * They contain all of the links and operations.
 * <p>
 * They have to be linked to a tile entity that has either an item handler
 * fluid handler, gas handler, energy handler etc.
 * </p>
 */
public interface ILink extends INode {
    BlockPos getPos();

    void setPos(BlockPos pos);

    void setFace(Direction dir);

    Direction getFace();

    /**
     * This will allow you to read your packet to a compound
     *
     * @param tag the compound to read from
     */
    @Override default void read(CompoundNBT tag) {
        INode.super.read(tag);
        setPos(TagUtils.readBlockPos(tag, "link_block"));
        setFace(TagUtils.readEnumValue(tag, "link_face", Direction.class));
    }

    /**
     * This will allow you to write your packet to a compound
     *
     * @param tag the compound to write to
     * @return the passed compound instance
     */
    @Override default CompoundNBT write(CompoundNBT tag) {
        INode.super.write(tag);
        TagUtils.writeBlockPos(tag, "link_block", getPos());
        TagUtils.writeEnumValue(tag, "link_face", getFace());
        return tag;
    }
}
