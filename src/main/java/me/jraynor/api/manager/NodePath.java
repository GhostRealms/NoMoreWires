package me.jraynor.api.manager;

import lombok.extern.log4j.Log4j2;
import me.jraynor.api.link.ILink;
import me.jraynor.api.node.INode;
import me.jraynor.api.serialize.ITaggable;
import me.jraynor.common.util.TagUtils;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * This class will store a path of execution.
 * It starts with some kinds of root and then there
 * multiple
 */
@Log4j2
public class NodePath implements ITaggable {
    private ILink headNode;
    private final NodeManager manager;


    /**
     * Requires the head to execute
     *
     * @param head the head node
     */
    public NodePath(ILink head, NodeManager manager) {
        this.headNode = head;
        this.manager = manager;
        if (head.getUuid().isEmpty())
            head.setUuid(Optional.of(UUID.randomUUID()));
        manager.allNodes.put(headNode.getUuid().get(), headNode);
    }


    /**
     * This will insert the toInsert node into the afterNode, and sete the afterNode to toInsert's after
     *
     * @param afterNode the after node
     * @param toInsert  the insert node
     * @return false if the afterNode doesn't exist
     */
    public UUID insertAfter(INode afterNode, INode toInsert) {
        var uuid = toInsert.getUuid();
        if (uuid.isEmpty()) {
            uuid = Optional.of(UUID.randomUUID());
            toInsert.setUuid(uuid);
        }
        var toReplace = afterNode.getTo();
        if (toReplace.isEmpty()) {
            afterNode.setTo(Optional.of(toInsert.getUuid().get()));
            toInsert.setFrom(Optional.of(afterNode.getUuid().get()));
        } else {
            var replace = toReplace.get();
            toInsert.setFrom(Optional.of(afterNode.getUuid().get()));
            manager.allNodes.get(replace).setFrom(Optional.of(toInsert.getUuid().get()));
        }
        manager.allNodes.put(toInsert.getUuid().get(), toInsert);
        return uuid.get();
    }

    /**
     * This will remeove the given node
     *
     * @param node the node to remove
     */
    public boolean remove(INode node) {
        if (node.equals(headNode)) return false;
        var from = node.getFrom();
        var to = node.getTo();
        //If there a front and back, then we simply link them
        if (from.isPresent() && to.isPresent()) {
            manager.allNodes.get(from.get()).setTo(to);
            manager.allNodes.get(to.get()).setFrom(from);
        } else if (from.isPresent()) {
            manager.allNodes.get(node.getFrom().get()).setTo(Optional.empty());
        }
        node.setFrom(Optional.empty());
        node.setTo(Optional.empty());
        manager.allNodes.remove(node.getUuid().get());
        return true;
    }

    /**
     * This will add the node on to the end
     *
     * @param node the node to add
     */
    public void add(INode node) {
        if (node.getUuid().isEmpty()) {
            node.setUuid(Optional.of(UUID.randomUUID()));
            log.debug("Set the uuid for new node");
        }
        var last = getLastNode();
        last.setTo(Optional.of(node.getUuid().get()));
        node.setFrom(Optional.of(last.getUuid().get()));
        this.manager.allNodes.put(node.getUuid().get(), node);
    }

    /**
     * this will recursively get the last node
     *
     * @return the last node
     */
    public INode getLastNode() {
        if (headNode.getTo().isEmpty()) return headNode;
        var current = headNode.getTo();
        while (current.isPresent() && manager.allNodes.containsKey(current.get())) {
            current = manager.allNodes.get(current.get()).getTo();
        }
        return manager.allNodes.get(current.get());
    }

    /**
     * This allows us to iterate over each of the nodes
     *
     * @param consumer the consumer for the nodes
     */
    public void forEach(Consumer<INode> consumer) {
        var current = headNode.getUuid();
        while (current.isPresent() && manager.allNodes.containsKey(current.get())) {
            consumer.accept(manager.allNodes.get(current.get()));
            current = manager.allNodes.get(current.get()).getTo();
        }
    }

    /**
     * This will allow for reading of things from a compound tag
     *
     * @param tag the mod tag
     */
    @Override public void read(CompoundNBT tag) {
        var headId = UUID.fromString(tag.getString("head"));
        var headNode = manager.readNode(tag, headId);
        if (headNode instanceof ILink) {
            this.headNode = (ILink) headNode;
            var current = Optional.of(headNode);
            while (current.isPresent() && current.get().getTo().isPresent()) {
                var nodeId = current.get().getTo().get();
                var node = manager.readNode(tag, nodeId);
                if (node != null) {
                    manager.allNodes.put(nodeId, node);
                    log.debug("Read and appended new node: " + nodeId.toString());
                }
            }
        }
    }

    /**
     * THis will write a mod tag. the type will be written
     *
     * @param tag the mod tag
     * @return the inputted mod tag
     */
    @Override public CompoundNBT write(CompoundNBT tag) {
        tag.putString("head", headNode.getUuid().toString());
        var types = new CompoundNBT();
        forEach(node -> {
            TagUtils.writeEnumValue(types, node.getUuid().toString(), node.getNodeType());
            var nodeTag = node.write(new CompoundNBT());
            tag.put(node.getUuid().toString(), nodeTag);
            log.debug("Wrote new tag with uuid: " + node.getUuid().toString());
        });
        tag.put("types", types);
        return tag;
    }


    /**
     * @return the total size
     */
    public int size() {
        var size = new AtomicInteger(0);
        forEach(iNode -> size.getAndIncrement());
        return size.get();
    }


}
