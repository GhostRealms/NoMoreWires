package me.jraynor.common.data;

import me.jraynor.common.tiles.UtilityTile;
import me.jraynor.core.ModRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * This stores a link
 */
public class LinkData {
    private BlockPos pos;
    private Direction side;
    private TransferMode type;
    private IOMode operation;

    public LinkData(BlockPos pos, Direction side, TransferMode type, IOMode operation) {
        this.pos = pos;
        this.side = side;
        this.type = type;
        this.operation = operation;
    }

    /**
     * This will write the link data to an item stack
     *
     * @param stack the stack to write to
     */
    public void write(ItemStack stack) {
        var link = stack.getOrCreateTag();
        link.putInt("link_x", pos.getX());
        link.putInt("link_y", pos.getY());
        link.putInt("link_z", pos.getZ());
        link.putString("link_side", side.name());
        link.putString("link_type", type.name());
        link.putString("link_op", operation.name());
        stack.write(new CompoundNBT());
    }

    /**
     * Removes the current nbt data on an itemstack
     *
     * @param stack
     */
    public static void clear(ItemStack stack) {
        stack.setTag(new CompoundNBT());
        stack.write(new CompoundNBT());
    }

    /**
     * This will read the transfer data from an item stack
     *
     * @param stack teh stack to read from
     * @return the link data from the item stack
     */
    public static LinkData read(ItemStack stack) {
        if (isLinked(stack)) {
            var data = stack.getTag();
            return new LinkData(new BlockPos(data.getInt("link_x"),
                    data.getInt("link_y"), data.getInt("link_z")),
                    Direction.valueOf(data.getString("link_side")),
                    TransferMode.valueOf(data.getString("link_type")),
                    IOMode.valueOf(data.getString("link_op")));
        }
        return null;
    }

    /**
     * @return true if the given itemstack has nbt data
     */
    public static boolean isLinked(ItemStack stack) {
        if (stack.getItem() != ModRegistry.SYNTHESIZER_ITEM.get()) return false;
        var nbt = stack.getTag();
        if (nbt == null) return false;
        return nbt.contains("link_type");
    }

    /**
     * Checks to see if we can link to the block in the current state
     *
     * @return true if we can link
     */
    public static boolean isLinkable(TransferMode mode, World world, BlockPos pos, Direction face) {
        var state = world.getBlockState(pos);
        if (!state.hasTileEntity()) return false;
        var tile = world.getTileEntity(pos);
        if (tile instanceof UtilityTile) return true;
        switch (mode) {
            case ITEMS -> {
                var capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
                if (!capability.isPresent()) return false;
                return capability.resolve().get().getSlots() > 0; //We need to make sure there's more than 0 slots
            }
            case ENERGY -> {
                var capability = tile.getCapability(CapabilityEnergy.ENERGY, face);
                if (!capability.isPresent()) return false;
                return capability.resolve().get().getMaxEnergyStored() > 0; //We check to make sure there can be more than 0 energy stored
            }
            case FLUID -> {
                var capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face);
                if (!capability.isPresent()) return false;
                return capability.resolve().get().getTanks() > 0; //We need to make sure there's more than 0 tanks
            }
        }
        return false;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Direction getSide() {
        return side;
    }

    public void setSide(Direction side) {
        this.side = side;
    }

    public TransferMode getType() {
        return type;
    }

    public void setType(TransferMode type) {
        this.type = type;
    }

    public IOMode getOperation() {
        return operation;
    }

    public void setOperation(IOMode operation) {
        this.operation = operation;
    }
}
