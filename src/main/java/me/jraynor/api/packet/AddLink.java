package me.jraynor.api.packet;

import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * This will add the given node to the server.
 * This will be sent from the client
 */
public class AddLink extends NodePacket {
    @Getter private UUID from, to;

    public AddLink(BlockPos tilePos, UUID afterNode, UUID toNode) {
        super(tilePos);
        this.from = afterNode;
        this.to = toNode;
    }

    @Override
    public void writeBuffer(PacketBuffer buf) {
        super.writeBuffer(buf);
        buf.writeUniqueId(from);
        buf.writeUniqueId(to);
    }

    @Override
    public void readBuffer(PacketBuffer buf) {
        super.readBuffer(buf);
        this.from = buf.readUniqueId();
        this.to = buf.readUniqueId();
    }
}
