package me.jraynor.api.node;

import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.serialize.ITaggable;
import me.jraynor.api.util.NodeType;
import me.jraynor.common.util.TagUtils;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.UUID;


/**
 * This is the base for each of the linkable elements. They all need to be able to be written to nbt data.
 */
public interface INode extends ITaggable {
    /**
     * Sets the parent if not set this will be considered a root
     *
     * @param from the from
     */
    void setFrom(Optional<UUID> from);

    void setManager(NodeManager manager);

    /**
     * This is the parent. If it's not present
     */
    Optional<UUID> getFrom();

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
        tag.putBoolean("has_uuid", getUuid().isPresent());
        tag.putBoolean("has_from", getFrom().isPresent());
        tag.putBoolean("has_to", getTo().isPresent());
        if (getUuid().isPresent())
            tag.putString("node_uuid", getUuid().get().toString());
        if (getFrom().isPresent())
            tag.putString("from_uuid", getFrom().get().toString());
        if (getTo().isPresent())
            tag.putString("to_uuid", getTo().get().toString());
        return tag;
    }

    /**
     * This will read the node data from the tag
     *
     * @param tag the mod tag
     */
    @Override default void read(CompoundNBT tag) {
        if (tag.getBoolean("has_uuid"))
            setUuid(Optional.of(UUID.fromString(tag.getString("node_uuid"))));
        if (tag.getBoolean("has_from"))
            setFrom(Optional.of(UUID.fromString(tag.getString("from_uuid"))));
        if (tag.getBoolean("has_to"))
            setTo(Optional.of(UUID.fromString(tag.getString("to_uuid"))));
    }
}
