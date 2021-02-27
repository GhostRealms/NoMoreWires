package me.jraynor.common.tiles;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.link.ILink;
import me.jraynor.api.link.LinkServer;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.packet.RemoveNode;
import me.jraynor.api.util.NodeType;
import me.jraynor.common.data.LinkData;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.AddLink;
import me.jraynor.common.network.packets.AddNode;
import me.jraynor.common.util.TagUtils;
import me.jraynor.core.ModRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This is the where the magic happens. This is the main utility tile.
 */
@Log4j2
public class SingularityTile extends TileEntity implements ITickableTileEntity {
    @Getter private NodeManager manager;
    private boolean init = false;

    public SingularityTile() {
        super(ModRegistry.UTILITY_BLOCK_TILE.get());
        Network.subscribe(this);
        this.manager = new NodeManager();
        this.manager.setDist(FMLEnvironment.dist);
    }

    /**
     * This method is what will end up linking the blocks. It is called from the synthesizer item.
     */
    public void onNodeRemove(RemoveNode packet, NetworkEvent.Context ctx) {
        log.info("Removing node from client (for ui update): " + packet.getUuid().toString());
        manager.remove(packet.getUuid());
        sync();
        ctx.setPacketHandled(true);
    }

    /**
     * This method is what will end up linking the blocks. It is called from the synthesizer item.
     */
    public void onNodeAdd(AddNode self, NetworkEvent.Context ctx) {
        manager.add(self.getNode());
        sync();
        ctx.setPacketHandled(true);
    }

    /**
     * This method is what will end up linking the blocks. It is called from the synthesizer item.
     */
    public void onLinkAdd(AddLink self, NetworkEvent.Context ctx) {
        manager.addLink(self.getAfterNode(), self.getToNode());
        sync();
        ctx.setPacketHandled(true);
    }

    /**
     * This method is what will end up linking the blocks. It is called from the synthesizer item.
     *
     * @param other the block to link to this
     * @param self  a link data of this
     */
    public void onLink(LinkData self, LinkData other) {
        var newLink = new LinkServer();
        var rand = new Random();
        newLink.setPos(other.getPos());
        newLink.setFace(other.getSide());
        newLink.setX(rand.nextInt(200) + 10);
        newLink.setY(rand.nextInt(150) + 10);
        manager.add(newLink);
        sync();
    }

    /**
     * This will attempt to sync from the server to the client
     */
    private void doSync() {
        if (!world.isRemote)
            if (!init) {
                init = true;
                sync();
            }
    }

    /**
     * This will sync the transferData to the client
     */
    private void sync() {
        markDirty();
        this.getWorld().notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), Constants.BlockFlags.NOTIFY_NEIGHBORS | Constants.BlockFlags.BLOCK_UPDATE);
    }

    /**
     * Called 20 times per second on the client and server
     */
    @Override
    public void tick() {
        doSync();
    }


    /**
     * Removes all the tile data from the world, also used for de initialization.
     */
    @Override
    public void remove() {
        super.remove();
        Network.unsubscribe(this); //Can't forget to unsubscribe or it can cause a memory leak.
    }

    /**
     * This will read our transfer data
     *
     * @param state the state of the block
     * @param tag   the tag to read from
     */
    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        manager.read(tag);
    }

    /**
     * This will write our current transfer data
     *
     * @param tag the tag
     */
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        manager.write(tag);
        return tag;
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside
     */
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    /**
     * this method gets called on the client when it receives the packet that was sent in the previous method
     */
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.read(getBlockState(), packet.getNbtCompound());
    }

    /**
     * Gets the capbility. This is used for allowing easy accesses between other mods that use item handlers,
     * energy handlers etc.
     */
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }
}
