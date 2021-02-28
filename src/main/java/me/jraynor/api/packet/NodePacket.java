package me.jraynor.api.packet;

import lombok.Getter;
import me.jraynor.common.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;

/**
 * This is the base packet for nodes it will ensure the node is sent to the correct tile entity
 */
public class NodePacket extends Event implements IPacket {
    @Getter protected BlockPos tilePos;

    public NodePacket(BlockPos tilePos) {
        this.tilePos = tilePos;
    }

    @Override public void readBuffer(PacketBuffer buf) {
        this.tilePos = buf.readBlockPos();
    }

    @Override public void writeBuffer(PacketBuffer buf) {
        buf.writeBlockPos(tilePos);
    }
}
