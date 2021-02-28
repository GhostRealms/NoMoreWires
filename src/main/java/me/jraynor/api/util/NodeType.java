package me.jraynor.api.util;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.link.LinkClient;
import me.jraynor.api.link.LinkServer;
import me.jraynor.api.node.INode;
import me.jraynor.api.operation.extract.ExtractOperationClient;
import me.jraynor.api.operation.extract.ExtractOperationServer;
import me.jraynor.api.operation.insert.InsertOperationClient;
import me.jraynor.api.operation.insert.InsertOperationServer;
import me.jraynor.core.Side;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;

/**
 * The node type is used to have a hard storage copy of all types of nodes.
 * It can create a new instance of a node for a given distribution
 */
@Log4j2
public enum NodeType {
    LINK(LinkClient.class, LinkServer.class),
    EXTRACT_OP(ExtractOperationClient.class, ExtractOperationServer.class),
    INSERT_OP(InsertOperationClient.class, InsertOperationServer.class);

    private Class<? extends INode> clientClass, serverClass;

    NodeType(Class<? extends INode> clientClass, Class<? extends INode> serverClass) {
        this.clientClass = clientClass;
        this.serverClass = serverClass;
    }

    /**
     * This will create a new node instance for the given distribution
     *
     * @param dist the current distrubtion
     */
    @SneakyThrows public INode newNodeFor(Side dist) {
        switch (dist) {
            case CLIENT -> { return clientClass.getConstructor().newInstance(); }
            case SERVER -> { return serverClass.getConstructor().newInstance(); }
        }
        return null;
    }

    /**
     * @param dist the distribution
     * @return the class for the given distribution
     */
    public Class<? extends INode> getClassFor(Side dist) {
        if (dist == Side.CLIENT)
            return clientClass;
        return serverClass;
    }

    /**
     * Checks to see if the node is valid for the given destruction
     *
     * @param dist the current distribution
     * @return true if it's valid
     */
    public static boolean isValidFor(INode node, Side dist) {
        if (node == null) return false;
        var type = node.getClass();
        switch (dist) {
            case CLIENT -> { return node.getNodeType().clientClass.equals(type); }
            case SERVER -> { return node.getNodeType().serverClass.equals(type); }
        }
        return false;
    }

    /**
     * This will convert the node to the correct type. It does this by first
     * checking to make sure it's not already the correct type, then if its not
     * it will create a new node of teh correct type. Finally it write the nodeIn
     * node to the newNode and return the newNode.
     *
     * @param nodeIn the node to convert
     * @param dist   the current distribution
     * @return the node on the correct distribution.
     */
    public static INode convertNode(INode nodeIn, Side dist) {
        if (isValidFor(nodeIn, dist)) return nodeIn; //It's the correct node already
        var newNode = nodeIn.getNodeType().newNodeFor(dist);
        newNode.read(nodeIn.write(new CompoundNBT()));
        log.debug("Converted node from type '" + nodeIn.getClass().getSimpleName() + "' to type '" + newNode.getClass().getSimpleName() + "' for distribution '" + dist.name().toLowerCase() + "'");
        return newNode;
    }


}
