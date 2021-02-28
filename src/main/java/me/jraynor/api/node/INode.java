package me.jraynor.api.node;

import me.jraynor.api.manager.NodeHolder;
import me.jraynor.api.serialize.ITaggable;
import me.jraynor.api.util.NodeType;
import me.jraynor.common.util.TagUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;


/**
 * This is the base for each of the linkable elements. They all need to be able to be written to nbt data.
 */
public interface INode extends ITaggable {
    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    BlockPos getTilePos();

    void setTilePos(BlockPos pos);


    /**
     * This will set the link we're linked to
     *
     * @param to this set the location we're linked
     */
    void setTo(Optional<UUID> to);

    /**
     * This can be used to send to other ILinkables of the a given type,
     * it can be used by operations to
     *
     * @return the to operation
     */
    Optional<UUID> getTo();

    /**
     * The uuid for the linkable. This should be different for each instance of the uuid.
     * It should be generated per instance.
     *
     * @return the uuid of the linkable instance
     */
    Optional<UUID> getUuid();

    /**
     * This will update the current uuid.
     *
     * @param uuid the uuid to set
     */
    void setUuid(Optional<UUID> uuid);

    /**
     * Used to creating/serialization of nodes
     *
     * @return the type of node this is
     */
    NodeType getNodeType();

    /**
     * This will write the base data like the uuid of the from node
     *
     * @param tag the mod tag
     * @return the given tag
     */
    @Override default CompoundNBT write(CompoundNBT tag) {
        TagUtils.writeUniqueId(tag, "node_uuid", getUuid().get());
        if (getTo().isPresent())
            TagUtils.writeUniqueId(tag, "to_uuid", getTo().get());
        TagUtils.writeBlockPos(tag, "controller_pos", getTilePos());
        tag.putInt("client_pos_x", getX());
        tag.putInt("client_pos_y", getY());
        return tag;
    }

    /**
     * This will read the node data from the tag
     *
     * @param tag the mod tag
     */
    @Override default void read(CompoundNBT tag) {
        setUuid(Optional.of(TagUtils.readUniqueId(tag, "node_uuid")));
        setTo(Optional.ofNullable(TagUtils.readUniqueId(tag, "to_uuid")));
        setX(tag.getInt("client_pos_x"));
        setY(tag.getInt("client_pos_y"));
        setTilePos(TagUtils.readBlockPos(tag, "controller_pos"));
    }
}
