package me.jraynor.api.packet;

import lombok.Getter;
import me.jraynor.common.network.IPacket;
import me.jraynor.core.Side;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;

/**
 * This packet is used to request synchronization of the node manager.
 * When this packet is sent a resulting responseSync packet will be sent. It
 * will serialize the node manager and send it to the client
 */
public class RequestSync extends NodePacket {
    @Getter private Side from; //Either from the client or server (normally will be from the client)

    public RequestSync(BlockPos tilePos, Side from) {
        super(tilePos);
        this.from = from;
    }

    /**
     * Reads the from buffer.
     *
     * @param buf the buf to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buf) {
        super.readBuffer(buf);
        from = buf.readEnumValue(Side.class);
    }

    /**
     * Writes the from buffer
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        super.writeBuffer(buf);
        buf.writeEnumValue(from);
    }
}
