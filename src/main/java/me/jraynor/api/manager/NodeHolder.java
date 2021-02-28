package me.jraynor.api.manager;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.node.INode;
import me.jraynor.api.serialize.ITaggable;
import me.jraynor.api.util.NodeType;
import me.jraynor.common.util.TagUtils;
import me.jraynor.core.Side;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is where the core the node system happens.
 * It stores all of the and allow for iteration over each,
 * or it allows you to add a new path, or remove a path. It managaes all nodes for a given
 * controller.
 */
@Log4j2
public final class NodeHolder implements ITaggable {
    @Getter final Map<UUID, INode> allNodes = Maps.newHashMap();
    @Getter private BlockPos pos;

    public NodeHolder(BlockPos pos) {
        this.pos = pos;
    }

    /**
     * This will simply add a node.
     *
     * @param nodeIn the node to add
     */
    public boolean add(INode nodeIn) {
        var node = nodeIn.getNodeType().newNodeFor(Side.SERVER);
        node.read(nodeIn.write(new CompoundNBT()));
        if (node.getUuid().isEmpty()) {
            log.error("Attempted to add node that doesn't have a uuid");
            return false;
        }
        allNodes.put(node.getUuid().get(), node);
        return true;
    }


    /**
     * This will add a new link from the from to the to
     */
    public boolean addLink(UUID from, UUID to) {
        var fromNode = allNodes.get(from);
        var toNode = allNodes.get(to);
        if (fromNode != null && toNode != null) {
            fromNode.setTo(Optional.of(to));
            return true;
        }
        return false;
    }

    /**
     * Removes the given node also will removes links if possible
     *
     * @param uuid the uuid to remove
     */
    public boolean remove(UUID uuid) {
        if (allNodes.containsKey(uuid)) {
            for (var node : allNodes.values())
                if (node.getTo().isPresent() && node.getTo().get().equals(uuid))
                    node.setTo(Optional.empty());
            return allNodes.remove(uuid) != null;
        }
        return false;
    }

    /**
     * THis will write a mod tag
     *
     * @param tag the mod tag
     * @return the inputted mod tag
     */
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        //We want to write all of the nodes.
        var types = new CompoundNBT();
        for (var entry : allNodes.entrySet()) {
            var uuid = entry.getKey();
            var node = entry.getValue();
            TagUtils.writeEnumValue(types, uuid.toString(), node.getNodeType());
            var nodeTag = node.write(new CompoundNBT());
            tag.put(uuid.toString(), nodeTag);
        }
        tag.put("types", types);
        //Then we want to write each of the heads for our links
        var heads = allNodes.keySet();
        tag.putInt("node_head_size", heads.size());
        var index = 0;
        for (var uuid : heads) {
            tag.putString("node_head_" + index++, uuid.toString());
            log.debug("Wrote node head uuid: " + uuid.toString());
        }
        return tag;
    }

    /**
     * This will allow for reading of things from a compound tag
     *
     * @param tag the mod tag
     */
    @Override
    public void read(CompoundNBT tag) {
        allNodes.clear();
        var headsSize = tag.getInt("node_head_size");
        var nodeIds = new HashSet<UUID>();
        for (var i = 0; i < headsSize; i++) {
            var uuid = UUID.fromString(tag.getString("node_head_" + i));
            nodeIds.add(uuid);
        }
        for (var uuid : nodeIds) {
            if (!allNodes.containsKey(uuid)) {
                var node = readNode(tag, uuid);
                add(node);
            }
        }
    }

    /**
     * @return a node with the given uuid
     */
    INode readNode(CompoundNBT tag, UUID uuid) {
        var types = (CompoundNBT) tag.get("types");
        var type = TagUtils.readEnumValue(types, uuid.toString(), NodeType.class);
        var node = type.newNodeFor(Side.SERVER);
        var nodeTag = (CompoundNBT) tag.get(uuid.toString());
        if (node == null || nodeTag == null) return null;
        node.read(nodeTag);
        node.setUuid(Optional.of(uuid));
        return node;
    }

    /**
     * Gets the node with the given uuid. Null if the node with the given uuid is not found.
     *
     * @param uuid the uuid of the node to get
     * @return the node with the given uuid. Will return null if not present
     */
    @Nullable
    public INode getNode(UUID uuid) {
        if (!allNodes.containsKey(uuid)) return null;
        return allNodes.get(uuid);
    }

    /**
     * Gets the node with the given uuid. Null if the node with the given uuid is not found.
     * This will cas it to the given type if possible, if the node is not found or is not
     * assingable of the given type it will return null;
     *
     * @param uuid the uuid of the node to get
     * @return the node with the given uuid. Will return null if not present
     */
    @Nullable
    public <T extends INode> T getNodeAs(UUID uuid, Class<T> type) {
        if (!allNodes.containsKey(uuid)) return null;
        var node = allNodes.get(uuid);
        if (type.isAssignableFrom(node.getClass()))
            return (T) node;
        return null;
    }

}
