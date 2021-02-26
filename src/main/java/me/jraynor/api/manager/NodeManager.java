package me.jraynor.api.manager;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.link.ILink;
import me.jraynor.api.link.LinkClient;
import me.jraynor.api.link.LinkServer;
import me.jraynor.api.node.INode;
import me.jraynor.api.operation.OperationClient;
import me.jraynor.api.operation.OperationServer;
import me.jraynor.api.serialize.ITaggable;
import me.jraynor.api.util.NodeType;
import me.jraynor.common.util.TagUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is where the core the node system happens.
 * It stores all of the {@link NodePath} and allow for iteration over each,
 * or it allows you to add a new path, or remove a path. It managaes all nodes for a given
 * controller.
 */
@Log4j2
public final class NodeManager implements ITaggable {
    @Getter final Map<UUID, INode> allNodes = Maps.newHashMap();
    private final Map<UUID, NodePath> nodePaths = Maps.newHashMap();
    private Dist dist;

    public NodeManager(Dist dist) {
        this.dist = dist;
    }

    /**
     * This will simply add a node.
     *
     * @param node the node to add
     */
    public UUID add(INode node) {
        if (node.getUuid().isEmpty())
            node.setUuid(Optional.of(UUID.randomUUID()));
        allNodes.put(node.getUuid().get(), node);
        return node.getUuid().get();
    }

    /**
     * This will add a new link/nodepath
     *
     * @param link the link to add
     * @return the uuid the link
     */
    public Optional<UUID> addLink(ILink link) {
        if (link.getUuid().isEmpty())
            link.setUuid(Optional.of(UUID.randomUUID()));
        if (!nodePaths.containsKey(link.getUuid().get())) {
            nodePaths.put(link.getUuid().get(), new NodePath(link, this));
            log.debug("Added new link: " + link.getUuid().toString());
            return link.getUuid();
        }
        log.debug("Link already exists: " + link.getUuid().toString());
        return Optional.empty();
    }

    /**
     * THis will write a mod tag
     *
     * @param tag the mod tag
     * @return the inputted mod tag
     */
    @Override public CompoundNBT write(CompoundNBT tag) {
        //We want to write all of the nodes.
        var types = new CompoundNBT();
        allNodes.forEach((uuid, iNode) -> {
            TagUtils.writeEnumValue(types, uuid.toString(), iNode.getNodeType());
            var nodeTag = iNode.write(new CompoundNBT());
            tag.put(uuid.toString(), nodeTag);
        });
        tag.put("types", types);
        //Then we want to write each of the heads for our links
        var heads = allNodes.keySet();
        tag.putInt("node_head_size", heads.size());
        var index = new AtomicInteger(0);
        heads.forEach(uuid -> {
            tag.putString("node_head_" + index.getAndIncrement(), uuid.toString());
            log.debug("Wrote node head uuid: " + uuid.toString());
        });
        return tag;
    }

    /**
     * This will allow for reading of things from a compound tag
     *
     * @param tag the mod tag
     */
    @Override public void read(CompoundNBT tag) {
        System.out.println("Starting to read tag: " + tag);
        var headsSize = tag.getInt("node_head_size");
        var nodeIds = new HashSet<UUID>();
        for (var i = 0; i < headsSize; i++) {
            var uuid = UUID.fromString(tag.getString("node_head_" + i));
            nodeIds.add(uuid);
        }
        nodeIds.forEach(uuid -> {
            if (!allNodes.containsKey(uuid)) {
                var node = readNode(tag, uuid);
                allNodes.put(uuid, node);
                System.out.println("Put node " + uuid.toString());
            }
        });

    }

    /**
     * @return a node with the given uuid
     */
    INode readNode(CompoundNBT tag, UUID uuid) {
        var types = (CompoundNBT) tag.get("types");
        INode node = null;
        switch (TagUtils.readEnumValue(types, uuid.toString(), NodeType.class)) {
            case LINK -> {
                if (dist.isClient())
                    node = new LinkClient();
                else
                    node = new LinkServer();
            }
            case OPERATION -> {
                if (dist.isClient())
                    node = new OperationClient();
                else
                    node = new OperationServer();
            }
        }
        var nodeTag = (CompoundNBT) tag.get(uuid.toString());
        if (node == null || nodeTag == null) return null;
        node.read(nodeTag);
        return node;
    }

}