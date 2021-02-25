package me.jraynor.common.network.packets;

import me.jraynor.common.data.IOMode;
import me.jraynor.common.data.TransferMode;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * This is used to update the client and server on the transfer data
 */
public class TransferUpdate implements IPacket {
    private BlockPos pos;
    private Direction direction;
    private TransferMode transfer;
    private IOMode operation;

    public TransferUpdate(BlockPos pos, Direction direction, TransferMode transfer, IOMode operation) {
        this.pos = pos;
        this.direction = direction;
        this.transfer = transfer;
        this.operation = operation;
    }

    /**
     * This will construct the packet from the buffer.
     *
     * @param buf the buffer to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buf) {
        this.pos = buf.readBlockPos();
        this.direction = buf.readEnumValue(Direction.class);
        this.transfer = buf.readEnumValue(TransferMode.class);
        this.operation = buf.readEnumValue(IOMode.class);
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumValue(direction);
        buf.writeEnumValue(transfer);
        buf.writeEnumValue(operation);
    }

    public BlockPos getPos() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }

    public TransferMode getTransfer() {
        return transfer;
    }

    public IOMode getOperation() {
        return operation;
    }
}
