package me.jraynor.common.network.packets;

import lombok.Getter;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

/**
 * This will add the given node to the server.
 * This will be sent from the client
 */
public class RemoveLink implements IPacket {
    @Getter private UUID afterNode, toNode;

    public RemoveLink(UUID afterNode, UUID toNode) {
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
