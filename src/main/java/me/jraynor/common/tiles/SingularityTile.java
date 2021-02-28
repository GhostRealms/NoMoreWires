package me.jraynor.common.tiles;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.link.LinkServer;
import me.jraynor.api.manager.NodeController;
import me.jraynor.common.data.LinkData;
import me.jraynor.common.network.Network;
import me.jraynor.core.ModRegistry;
import me.jraynor.core.Side;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * This is the where the magic happens. This is the main utility tile.
 */
@Log4j2
public class SingularityTile extends TileEntity implements ITickableTileEntity {
    @Getter private  NodeController container = new NodeController(this);
    private boolean init = false;

    public SingularityTile() {
        super(ModRegistry.UTILITY_BLOCK_TILE.get());
    }


    /**
     * Called 20 times per second on the client and server
     */
    @Override
    public void tick() {
        if (!init) {
            if (!world.isRemote)
                container.initialize(pos, Side.SERVER);
            else
                container.initialize(pos, Side.CLIENT);
            init = true;
        }
    }

    /**
     * This will sync the transferData to the client
     */
    public void sync() {
        markDirty();
        this.getWorld().notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), Constants.BlockFlags.NOTIFY_NEIGHBORS | Constants.BlockFlags.BLOCK_UPDATE);
    }

    /**
     * Removes all the tile data from the world, also used for de initialization.
     */
    @Override
    public void remove() {
        super.remove();
        container.remove();
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
        container.read(tag);
    }

    /**
     * This will write our current transfer data
     *
     * @param tag the tag
     */
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        return container.write(tag);
    }

    @Nullable @Override public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        int tileEntityType = 42;  // arbitrary number; only used for vanilla TileEntities.  You can use it, or not, as you want.
        return new SUpdateTileEntityPacket(this.pos, tileEntityType, nbtTagCompound);
    }

    @Override public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        BlockState blockState = world.getBlockState(pos);
        read(blockState, pkt.getNbtCompound());   // read from the nbt in the packet
    }

    @Override
    public void handleUpdateTag(BlockState blockState, CompoundNBT parentNBTTagCompound) {
        this.read(blockState, parentNBTTagCompound);
    }

}
