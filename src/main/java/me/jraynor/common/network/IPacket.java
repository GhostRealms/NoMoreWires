package me.jraynor.common.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A generic packet. Must have a toBytes method
 */
public interface IPacket {
    /**
     * This will construct the packet from the buf.
     *
     * @param buf the buf to construct the packet from
     */
    default void readBuffer(PacketBuffer buf) {
        read(buf.readCompoundTag());
    }

    /**
     * This will convert the current packet into a packet buffer.
     *
     * @param buf the buffer to convert
     */
    default void writeBuffer(PacketBuffer buf) {
        buf.writeCompoundTag(write(new CompoundNBT()));
    }

    /**
     * This will allow you to read your packet to a compound
     *
     * @param tag the compound to read from
     */
    default void read(CompoundNBT tag) {}

    /**
     * This will allow you to write your packet to a compound
     *
     * @param tag the compound to write to
     * @return the passed compound instance
     */
    default CompoundNBT write(CompoundNBT tag) {return tag;}

    /**
     * This will reroute the event back to the network so there can be subscribers for when this packet is handles.
     *
     * @param ctx the current network context
     * @return the handle state, true if successful
     */
    default boolean handle(Supplier<NetworkEvent.Context> ctx) {
        var map = Network.callbacks.get(this.getClass());
        if (map != null)
            map.forEach((instance, callbacks) -> callbacks.forEach(consumer -> {
                if (!ctx.get().getPacketHandled())
                    consumer.accept(this, ctx.get());
            }));
        return true;
    }


}
