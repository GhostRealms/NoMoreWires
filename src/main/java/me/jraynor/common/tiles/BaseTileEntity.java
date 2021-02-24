package me.jraynor.common.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

public class BaseTileEntity extends TileEntity implements ITickableTileEntity {
    private boolean initialized = false;

    public BaseTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }


    @Override public void tick() {
        if (!initialized) {
            if (world.isRemote) initClient();
            else initServer();
            initCommon();
            initialized = true;
        }
    }


    /**
     * Initialize the server
     */
    protected void initServer() {}

    /**
     * Initialize the client
     */
    protected void initClient() {}

    /**
     * This will initializeon the client and server
     */
    protected void initCommon() {}
}
