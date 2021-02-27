package me.jraynor.api.packet;

import lombok.Getter;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class RemoveNode implements IPacket {
    @Getter private UUID uuid;

    public RemoveNode(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * This will construct the packet from the buf.
     *
     * @param buf the buf to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buf) {
        uuid = buf.readUniqueId();
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        buf.writeUniqueId(uuid);
    }
}
