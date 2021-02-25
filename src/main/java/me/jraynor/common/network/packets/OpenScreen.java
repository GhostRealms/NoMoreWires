package me.jraynor.common.network.packets;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.jraynor.client.render.api.AbstractScreenRenderer;
import me.jraynor.common.network.IPacket;
import me.jraynor.core.ModRegistry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

/**
 * Allows us to open the screen from the utility block.
 */
public class OpenScreen implements IPacket {
    @Getter @Setter private BlockPos tilePos;
    @Getter private TileEntityType<?> type;

    public OpenScreen(BlockPos tilePos, TileEntityType<?> type) {
        this.tilePos = tilePos;
        this.type = type;
    }

    /**
     * This will construct the packet from the buffer.
     *
     * @param buf the buffer to construct the packet from
     */
    @SneakyThrows @Override public void readBuffer(PacketBuffer buf) {
        tilePos = buf.readBlockPos();
        type = buf.readRegistryId();
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buf) {
        buf.writeBlockPos(tilePos);
        buf.writeRegistryId(type);
    }
}
