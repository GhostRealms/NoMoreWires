package me.jraynor.old;

import lombok.SneakyThrows;
import me.jraynor.common.data.IOMode;
import me.jraynor.common.data.TransferMode;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * This is a generic node that can be represented on the server.
 */
public interface INode2 extends IPacket {
    BlockPos getPos();

    void setPos(BlockPos pos);

    Direction getDir();

    void setDir(Direction dir);

    TransferMode getMode();

    void setMode(TransferMode mode);

    /**
     * @return the client/server sided node instance to be instaniated. There must be an empty construtor!
     */
    Class<? extends INode2> getNodeType();

    /**
     * Allows for linking of the linked nodes across client and server
     *
     * @return the linked nodes
     */
    Map<IOMode, Collection<INode2>> getLinkedNodes();

    /**
     * Used for the ability to absorb other nodes
     *
     * @param linkedNodes the nodes to set to
     */
    void setLinkedNodes(Map<IOMode, Collection<INode2>> linkedNodes);

    /**
     * This will add a link to the linked nodes
     *
     * @param mode the mode to add
     * @param node the ndoe
     */
    default void addLink(IOMode mode, INode2 node) {
        getLinkedNodes().computeIfAbsent(mode, (m) -> new ArrayList<>()).add(node);
    }

    /**
     * This will remove the given link from the linked nodes
     */
    default void removeLink(IOMode mode, INode2 node) {
        if (getLinkedNodes().containsKey(mode))
            getLinkedNodes().get(mode).removeIf(iNode -> iNode.matches(node));
    }

    /**
     * @return the child node some where in the nodes
     */
    default INode2 findNode(INode2 toCheck) {
        if (toCheck.matches(this)) return this;
        for (var nodes : getLinkedNodes().values()) {
            for (var node : nodes) {
                if (node.matches(toCheck))
                    return node;
                var childSearch = node.findNode(toCheck);
                if (childSearch != null)
                    return childSearch;
            }
        }
        return null;
    }

    /**
     * This will update this nodes data with the other data. It's used for networking purposes.
     *
     * @param other the other to absorb
     */
    default void absorb(INode2 other) {
        setDir(other.getDir());
        setMode(other.getMode());
        setPos(other.getPos());
        setLinkedNodes(other.getLinkedNodes());
    }

    /**
     * This will construct the packet from the buffer.
     *
     * @param buf the buffer to construct the packet from
     */
    @SneakyThrows @Override default void readBuffer(PacketBuffer buf) {
        setPos(buf.readBlockPos());
        setDir(buf.readEnumValue(Direction.class));
        setMode(buf.readEnumValue(TransferMode.class));
        var ioCount = buf.readInt();
        for (var i = 0; i < ioCount; i++) {
            var mode = buf.readEnumValue(IOMode.class);
            var nodeCount = buf.readInt();
            var nodes = getLinkedNodes().computeIfAbsent(mode, (m) -> new ArrayList<>(nodeCount));
            for (var j = 0; j < nodeCount; j++) {
                /*This is either {@link ClientNode} or {@link ServerNode */
                INode2 node = null;
                if (ClientNode.class.isAssignableFrom(getNodeType())) {
                    var random = new Random();
                    node = new ClientNode((int) Math.round(Math.random() * 50), (int) Math.round(Math.random() * 50));
                } else if (ServerNode.class.isAssignableFrom(getNodeType()))
                    node = new ServerNode();
                if (node != null) {
                    node.readBuffer(buf);
                    nodes.add(node);
                }
            }
        }
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    @Override default void writeBuffer(PacketBuffer buf) {
        buf.writeBlockPos(getPos());
        buf.writeEnumValue(getDir());
        buf.writeEnumValue(getMode());
        buf.writeInt(getLinkedNodes().size());
    }


    /**
     * Checks another node against this one.
     * *doesnt check for matching connections.*
     *
     * @param other the other node to check against
     * @return true if the nodes match
     */
    default boolean matches(INode2 other) {
        if (other == null) return false;
        return other.getPos().equals(this.getPos()) && other.getDir() == this.getDir() && other.getMode() == this.getMode();
    }

    /**
     * @return the tostring
     */
    default String getString() {
        return getNodeType().getSimpleName() + "{" +
                "pos=" + getPos() +
                ", dir=" + getDir() +
                ", mode=" + getMode() +
                ", linkedNodes=" + getLinkedNodes() +
                '}';
    }
}
