package me.jraynor.api.manager;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.node.INode;
import me.jraynor.api.packet.*;
import me.jraynor.common.network.Network;
import me.jraynor.common.tiles.SingularityTile;
import me.jraynor.core.Side;
import mezz.jei.startup.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This will keep track of all of the node managers.
 */
@Log4j2
public class NodeController {
    @Getter private boolean initialized = false;
    private BlockPos pos;
    private final SingularityTile tile;
    private NodeHolder holder;

    public NodeController(SingularityTile tile) {
        this.tile = tile;
        this.holder = new NodeHolder(tile);
        Network.subscribe(this);
    }

    /**
     * This is used to initialize the
     *
     * @param pos  the block pos that this container is linked to
     * @param side the side of the container.
     */
    public void initialize(BlockPos pos, Side side) {
        initialized = true;
        this.pos = pos;
        holder.setTilePos(pos);
        log.warn("Initialized node container at " + pos.getCoordinatesAsString() + " on side " + side.name().toLowerCase(Locale.ROOT));
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * This will allow you iterate over the nodes
     *
     * @param consumer the consumer
     */
    public void forEach(Consumer<INode> consumer) {
        for (var node : holder.allNodes.values())
            consumer.accept(node);
    }

    /**
     * This will allow you iterate over the nodes
     *
     * @param consumer the consumer
     */
    public <T extends INode> void forEachAs(Class<T> type, Consumer<T> consumer) {
        synchronized (holder.allNodes) {
            if (this.holder != null && this.holder.allNodes != null)
                for (var node : holder.allNodes.values()) {
                    if (type.isAssignableFrom(node.getClass()))
                        consumer.accept(type.cast(node));
                }
        }
    }

    /**
     * This will get a node from the holder with the given uuid.
     *
     * @param uuid the uuid of the node
     */
    @Nullable public INode getNode(UUID uuid) {
        if (uuid == null) return null;
        return holder.getNode(uuid);
    }

    /**
     * This will get a node from the holder with the given uuid. If the type is not assinable
     * from the passed class type it will return null
     *
     * @param type the type of class to check the node against
     * @param uuid the uuid of the node
     * @param <T>
     */
    @Nullable public <T extends INode> T getNode(Class<T> type, UUID uuid) {
        if (uuid == null) return null;
        return holder.getNodeAs(uuid, type);
    }


    /**
     * This is called when the tile entity is removed.l
     */
    public void remove() {
        if (initialized) {
            Network.unsubscribe(this);
            this.holder = null;
            log.warn("Removed container data and unsubscribed from network at " + pos.getCoordinatesAsString());
            this.pos = null;
            initialized = false;
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }


    /**
     * This is called when the server sends the sync response to the client
     *
     * @param packet the packet with the sync data
     * @param ctx    the network context
     */
    public void onResponseSync(ResponseSync packet, NetworkEvent.Context ctx) {
        if (!packet.getTilePos().equals(pos)) return;
        if (initialized) {
            log.info("Received valid sync node response packet on ");
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This is called when the client/server requests synchronization
     *
     * @param packet the packet requesting sync
     * @param ctx    the context
     */
    public void onRequestSync(RequestSync packet, NetworkEvent.Context ctx) {
        if (!packet.getTilePos().equals(pos)) return;
        if (initialized) {
            log.info("Received valid sync node request packet on ");
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This method is what will end up linking the blocks. It is called from the synthesizer item.
     */
    public void onNodeRemove(RemoveNode packet, NetworkEvent.Context ctx) {
        if (Side.getThreadSide() == Side.SERVER) {
            if (!packet.getTilePos().equals(pos)) return;
            if (initialized)
                if (holder.remove(packet.getUuid())) {
                    log.info("Removed node with uuid: " + packet.getUuid().toString() + " on the server");
                    tile.markDirty();
                }
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This is called when the remove event is sent across the local context
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onNodeRemoveEvent(RemoveNode event) {
        if (initialized) {
            if (!event.getTilePos().equals(pos)) return;
            if (holder.remove(event.getUuid())) {
                log.info("Successfully removed node " + event.getUuid().toString() + " on the client");
                Network.sendToAllClients(event);
            }
        } else log.error("Tried to remove node before being initialized" + " on side");
    }

    /**
     * This is called when the node is added to the world
     */
    public void onAddNodePacket(AddNode packet, NetworkEvent.Context ctx) {
        if (Side.getThreadSide() == Side.CLIENT) {
            if (!packet.getTilePos().equals(pos)) return;
            if (initialized)
                if (holder.add(packet.getNode())) {
                    log.info("Successfully added node " + packet.getNode().getUuid().toString() + " on the client");
                }
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This is called when the add event is sent across the local context
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onAddNodeEvent(AddNode event) {
        if (initialized) {
            if (!event.getTilePos().equals(pos)) return;
            if (Side.getThreadSide() == Side.SERVER)
                if (holder.add(event.getNode())) {
                    Network.sendToAllClients(event);
                    log.info("Successfully added node " + event.getNode().getUuid().toString() + " on the server");
                    tile.markDirty();
                } else
                    log.info("Failed to add node " + event.getNode().getUuid().toString() + " on side");
        } else log.error("Tried to add node before being initialized" + " on side");
    }

    /**
     * This is called when the node is added to the world
     */
    public void onAddLink(AddLink packet, NetworkEvent.Context ctx) {

    }

    /**
     * This is called when the add event is sent across the local context
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onAddLinkEvent(AddLink event) {
    }

    /**
     * This is used to synchronize the reading from the server
     *
     * @param packet the synchronized read packet
     * @param ctx    the network context
     */
    public void onSyncRead(SyncRead packet, NetworkEvent.Context ctx) {
        log.info("Syncing the holder on the client");
        ctx.setPacketHandled(true);
    }

    /**
     * Is called from a tile entities read packet.
     *
     * @param tag the tag to read
     */
    public void read(CompoundNBT tag) {
        if (tag.contains("x"))
            this.pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        holder.setTilePos(pos);
        holder.read(tag);
    }


    /**
     * This is called from the {@link me.jraynor.common.tiles.SingularityTile}.
     * It will only write it's data if it's initialized
     *
     * @param tag the tag to write to
     */
    public CompoundNBT write(CompoundNBT tag) {
        if (initialized) {
            return holder.write(tag);
        } else log.error("Tried to write NodeController before it was initialized!");
        return tag;
    }

}
