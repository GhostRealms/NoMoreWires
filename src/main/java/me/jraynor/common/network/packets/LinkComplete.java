package me.jraynor.common.network.packets;

import lombok.Getter;
import me.jraynor.common.data.IOMode;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class LinkComplete implements IPacket {
    @Getter private BlockPos from, to;
    @Getter private Direction fromDir, toDir;

    public LinkComplete(BlockPos from, BlockPos to, Direction fromDir, Direction toDir) {
        this.from = from;
        this.to = to;
        this.fromDir = fromDir;
        this.toDir = toDir;
    }

    /**
     * This will construct the packet from the buffer.
     *
     * @param buffer the buffer to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buffer) {
        this.from = buffer.readBlockPos();
        this.to = buffer.readBlockPos();
        this.fromDir = buffer.readEnumValue(Direction.class);
        this.toDir = buffer.readEnumValue(Direction.class);
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        buf.writeBlockPos(from);
        buf.writeBlockPos(to);
        buf.writeEnumValue(fromDir);
        buf.writeEnumValue(toDir);
    }

}
