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
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.UUID;

/**
 * This will add the given node to the server.
 * This will be sent from the client
 */
public class AddLink implements IPacket {
    @Getter private UUID afterNode, toNode;

    public AddLink(UUID afterNode, UUID toNode) {
        this.afterNode = afterNode;
        this.toNode = toNode;
    }

    @Override
    public void writeBuffer(PacketBuffer buf) {
        buf.writeUniqueId(afterNode);
        buf.writeUniqueId(toNode);
    }

    @Override
    public void readBuffer(PacketBuffer buf) {
        this.afterNode = buf.readUniqueId();
        this.toNode = buf.readUniqueId();
    }
}
