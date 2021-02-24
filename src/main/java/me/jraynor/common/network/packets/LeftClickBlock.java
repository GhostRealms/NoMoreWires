package me.jraynor.common.network.packets;

import me.jraynor.common.network.IPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * This packet is will be used inside the {@link me.jraynor.common.items.SynthesizerItem} class to
 * detect on the server when left click has been pressed so we can write the data.
 */
public class LeftClickBlock implements IPacket {
    private ItemStack itemStack;
    private boolean sneaking;
    private BlockPos blockPos;
    private Direction face;

    public LeftClickBlock(ItemStack itemStack, BlockPos blockPos, Direction face) {
        this(itemStack, false, blockPos, face);
    }

    public LeftClickBlock(ItemStack itemStack, boolean sneaking, BlockPos blockPos, Direction face) {
        this.itemStack = itemStack;
        this.sneaking = sneaking;
        this.blockPos = blockPos;
        this.face = face;
    }

    /**
     * This will construct the packet from the buffer.
     *
     * @param buffer the buffer to construct the packet from
     */
    @Override public void readBuffer(PacketBuffer buffer) {
        this.itemStack = buffer.readItemStack();
        this.sneaking = buffer.readBoolean();
        this.blockPos = buffer.readBlockPos();
        this.face = buffer.readEnumValue(Direction.class);
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buffer the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buffer) {
        buffer.writeItemStack(this.itemStack);
        buffer.writeBoolean(sneaking);
        buffer.writeBlockPos(blockPos);
        buffer.writeEnumValue(face);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Direction getFace() {
        return face;
    }
}
