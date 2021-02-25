package me.jraynor.common.network.packets;

import lombok.Getter;
import me.jraynor.common.data.IOMode;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class LinkStart implements IPacket {
    @Getter private BlockPos pos;
    @Getter private Direction direction;
    @Getter private IOMode mode;

    public LinkStart(BlockPos pos, Direction direction, IOMode mode) {
        super();
        this.pos = pos;
        this.direction = direction;
        this.mode = mode;
    }


    /**
     * This will construct the packet from the buffer.
     *
     * @param buf the buffer to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buf) {
        this.pos = buf.readBlockPos();
        this.direction = buf.readEnumValue(Direction.class);
        this.mode = buf.readEnumValue(IOMode.class);
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumValue(direction);
        buf.writeEnumValue(mode);
    }

}
