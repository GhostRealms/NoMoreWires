package me.jraynor.common.network.packets;

import lombok.Getter;
import me.jraynor.api.link.LinkClient;
import me.jraynor.api.link.LinkServer;
import me.jraynor.api.node.INode;
import me.jraynor.api.operation.extract.ExtractOperationClient;
import me.jraynor.api.operation.extract.ExtractOperationServer;
import me.jraynor.api.operation.insert.InsertOperationClient;
import me.jraynor.api.operation.insert.InsertOperationServer;
import me.jraynor.api.util.NodeType;
import me.jraynor.common.network.IPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * This will add the given node to the server.
 * This will be sent from the client
 */
public class AddNode implements IPacket {
    @Getter private INode node;

    public AddNode(INode node) {
        this.node = node;
    }


    @Override
    public void writeBuffer(PacketBuffer buf) {
        buf.writeEnumValue(node.getNodeType());
        buf.writeCompoundTag(node.write(new CompoundNBT()));
    }

    @Override
    public void readBuffer(PacketBuffer buf) {
        var nodeType = buf.readEnumValue(NodeType.class);
        switch (nodeType) {
            case LINK -> node = FMLEnvironment.dist.isClient() ? new LinkClient() : new LinkServer();
            case EXTRACT_OP -> node = FMLEnvironment.dist.isClient() ? new ExtractOperationClient() : new ExtractOperationServer();
            case INSERT_OP -> node = FMLEnvironment.dist.isClient() ? new InsertOperationClient() : new InsertOperationServer();
        }
        node.read(buf.readCompoundTag());
    }
}
