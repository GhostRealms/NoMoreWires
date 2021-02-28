package me.jraynor.api.packet;

import lombok.Getter;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * This is used to remove a node. it's like a request and will be sent to the server
 */
public class RemoveNode extends NodePacket {
    @Getter private UUID uuid;

    public RemoveNode(BlockPos tilePos, UUID uuid) {
        super(tilePos);
        this.uuid = uuid;
    }

    /**
     * This will construct the packet from the buf.
     *
     * @param buf the buf to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buf) {
        super.readBuffer(buf);
        uuid = buf.readUniqueId();
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        super.writeBuffer(buf);
        buf.writeUniqueId(uuid);
    }
}
