package me.jraynor.api.packet;

import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

/**
 * This is used for syncing the node packet
 */
public class SyncRead extends NodePacket {
    @Getter private CompoundNBT tag;

    public SyncRead(BlockPos tilePos, CompoundNBT tag) {
        super(tilePos);
        this.tag = tag;
    }

    @Override public void readBuffer(PacketBuffer buf) {
        super.readBuffer(buf);
        tag = buf.readCompoundTag();
    }

    @Override public void writeBuffer(PacketBuffer buf) {
        super.writeBuffer(buf);
        buf.writeCompoundTag(tag);
    }
}
