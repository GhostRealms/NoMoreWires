package me.jraynor.common.containers;

import me.jraynor.common.tiles.UtilityTile;
import me.jraynor.core.ModRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


/**
 * This is what is used to store items and sync with the gui
 */
public class UtilityContainer extends BaseContainer<UtilityTile> {

    public UtilityContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
        super(ModRegistry.UTILITY_BLOCK_CONTAINER.get(), UtilityTile.class, windowId, world, pos, inventory);
        layoutPlayerInventorySlots(8, 115);
    }

    /**
     * Determines whether supplied player can use this container
     *
     * @param playerIn
     */
    @Override public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (!this.mergeItemStack(stack, 0, 1, false))
                    return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }
        return itemstack;
    }
}
