package me.jraynor.common.network.packets;

import me.jraynor.common.network.IPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/**
 * This packet is will be used inside the {@link me.jraynor.common.items.SynthesizerItem} class to
 * detect on the server when left click has been pressed so we can write the data.
 */
public class LeftClickAir implements IPacket {
    private ItemStack itemStack;
    private boolean sneaking;

    public LeftClickAir(ItemStack itemStack) {
        this(itemStack, false);
    }

    public LeftClickAir(ItemStack itemStack, boolean sneaking) {
        this.itemStack = itemStack;
        this.sneaking = sneaking;
    }

    /**
     * This will construct the packet from the buffer.
     *
     * @param buf the buffer to construct the packet from
     * @return
     */
    @Override public void readBuffer(PacketBuffer buf) {
        this.itemStack = buf.readItemStack();
        this.sneaking = buf.readBoolean();
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buffer the buffer to convert
     */
    @Override public void writeBuffer(PacketBuffer buffer) {
        buffer.writeItemStack(this.itemStack);
        buffer.writeBoolean(sneaking);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isSneaking() {
        return sneaking;
    }
}
