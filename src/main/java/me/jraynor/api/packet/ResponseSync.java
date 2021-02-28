package me.jraynor.api.packet;

import lombok.Getter;
import me.jraynor.api.manager.NodeHolder;
import me.jraynor.core.Side;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

/**
 * This is used to synchronize the client/server
 */
public class ResponseSync extends NodePacket {
    private NodeHolder manager;
    @Getter private Side toSide;
    @Getter private CompoundNBT managerNbt;

    public ResponseSync(BlockPos tilePos, NodeHolder manager, Side toSide) {
        super(tilePos);
        this.manager = manager;
        this.toSide = toSide;
    }

    /**
     * This will read the manager from the given compound
     *
     * @param buf the buf to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buf) {
        super.readBuffer(buf);
        this.toSide = buf.readEnumValue(Side.class);
        this.managerNbt = buf.readCompoundTag();
    }

    /**
     * This will write the manager to the buffer
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        super.writeBuffer(buf);
        buf.writeEnumValue(toSide);
        buf.writeCompoundTag(manager.write(new CompoundNBT()));
    }
}
