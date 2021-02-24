package me.jraynor.common.items;

import lombok.Getter;
import me.jraynor.common.data.IOMode;
import me.jraynor.common.data.LinkData;
import me.jraynor.common.data.TransferMode;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.LeftClickAir;
import me.jraynor.common.network.packets.LeftClickBlock;
import me.jraynor.common.tiles.UtilityTile;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * This is the go to item for the mod. It does all kinds of configurations.
 * It is also required in order to link things with the utility block
 */
public class SynthesizerItem extends Item {
    @Getter private IOMode operation = IOMode.NONE; //Input = insert, output = extract
    @Getter private TransferMode transferMode = TransferMode.ITEMS;

    public SynthesizerItem() {
        super(new Properties().group(ItemGroup.MISC).maxStackSize(1));
        Network.subscribe(this);
    }

    /**
     * This is called from the client when we left click the air
     *
     * @param event the event
     */
    public void onLeftClickAir(LeftClickAir event, NetworkEvent.Context ctx) {
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            var sender = ctx.getSender();
            var tag = event.getItemStack().getOrCreateTag();
            if (!event.isSneaking()) {
                this.operation = operation.next();
                sender.sendStatusMessage(new StringTextComponent(TextFormatting.WHITE + "io operation set to: " + TextFormatting.LIGHT_PURPLE + TextFormatting.UNDERLINE + operation.name().toLowerCase()), true);
            } else {
                this.transferMode = transferMode.next();
                sender.sendStatusMessage(new StringTextComponent(TextFormatting.WHITE + "io type set to: " + TextFormatting.BLUE + TextFormatting.UNDERLINE + transferMode.name().toLowerCase()), true);
            }
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This is called when the player left clicks on a block.
     */
    public void onLeftClickBlock(LeftClickBlock event, NetworkEvent.Context ctx) {
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            var world = Objects.requireNonNull(ctx.getSender()).getServerWorld();
            var player = ctx.getSender();
            if (!world.isRemote && player != null) {
                var pos = event.getBlockPos();
                var side = event.getFace();
                var stack = player.getHeldItemMainhand();
                var tile = world.getTileEntity(pos);
                if (LinkData.isLinkable(transferMode, world, pos, side)) {
                    writeLink(pos, side, stack, tile, world, player);
                } else {
                    var name = world.getBlockState(pos).getBlock().getTranslatedName().getString();
                    player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + "can't" + TextFormatting.WHITE + " link with " + TextFormatting.GREEN + name + TextFormatting.WHITE + " in " + TextFormatting.UNDERLINE + TextFormatting.DARK_PURPLE + transferMode.name().toLowerCase() + " mode!"), true);
                }
            }
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This will remove the connection from the synthesizer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            if (handIn == Hand.MAIN_HAND) {
                var stack = playerIn.getHeldItem(handIn);
                if (LinkData.isLinked(stack)) {
                    var block = LinkData.read(stack);
                    var name = worldIn.getBlockState(block.getPos()).getBlock().getTranslatedName().getString();
                    LinkData.clear(stack);
                    playerIn.sendStatusMessage(new StringTextComponent(TextFormatting.WHITE + "link " + TextFormatting.RED + "reset" + TextFormatting.WHITE + " from: " + TextFormatting.RED + TextFormatting.UNDERLINE + name + TextFormatting.WHITE + " at " + block.getPos().getCoordinatesAsString()), true);
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    /**
     * Called when the user right clicks
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
//        if (ctx.getWorld().isRemote)
//            return ActionResultType.SUCCESS;
//        var player = ctx.getPlayer();
//        var world = ctx.getWorld();
//        if (!world.isRemote && player != null) {
//            var pos = ctx.getPos();
//            var side = ctx.getFace();
//            var stack = ctx.getItem();
//            var tile = world.getTileEntity(pos);
//            if (LinkData.isLinkable(transferMode, world, pos, side)) {
//                writeLink(pos, side, stack, tile, world, player);
//                return ActionResultType.CONSUME;
//            } else {
//                var name = world.getBlockState(pos).getBlock().getTranslatedName().getString();
//                ctx.getPlayer().sendStatusMessage(new StringTextComponent("Synthesizer " + TextFormatting.RED + "can't" + TextFormatting.WHITE + " link with " + TextFormatting.GREEN + name + TextFormatting.WHITE + " in " + TextFormatting.UNDERLINE + TextFormatting.DARK_PURPLE + transferMode.name().toLowerCase() + " mode!"), true);
//            }
//        }
        return ActionResultType.PASS;
    }

    /**
     * This will first if no link is on the tool, create a new link, which must be set to one of the sides of the tile.
     * Once the tile has been configured then you can link it to another tile.
     */
    private void writeLink(BlockPos pos, Direction side, ItemStack stack, TileEntity tile, World world, PlayerEntity player) {
        var newLink = new LinkData(pos, side, transferMode, operation);
        if (LinkData.isLinked(stack) && tile instanceof UtilityTile) {
            var oldLink = LinkData.read(stack);
            var utilityTile = (UtilityTile) tile;
            assert oldLink != null;
            utilityTile.onLink(newLink, oldLink);
            LinkData.clear(stack);
            var state = world.getBlockState(oldLink.getPos());
            player.sendStatusMessage(new StringTextComponent(TextFormatting.GREEN + "completed" + TextFormatting.WHITE + " link with: " + TextFormatting.LIGHT_PURPLE + TextFormatting.UNDERLINE + state.getBlock().getTranslatedName().getString() + TextFormatting.WHITE + " at " + side.name().toLowerCase() + ", " + TextFormatting.GOLD + pos.getCoordinatesAsString()), true);
        }
        if (!LinkData.isLinked(stack) && !(tile instanceof UtilityTile)) {
            newLink.write(stack);
            var state = world.getBlockState(pos);
            player.sendStatusMessage(new StringTextComponent(TextFormatting.AQUA + "started" + TextFormatting.WHITE + " link with: " + TextFormatting.LIGHT_PURPLE + TextFormatting.UNDERLINE + state.getBlock().getTranslatedName().getString() + TextFormatting.WHITE + " at " + side.name().toLowerCase() + ", " + TextFormatting.GOLD + pos.getCoordinatesAsString()), true);
        }
    }

    /**
     * Adds useful information for the player regarding the link data
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        if (LinkData.isLinked(stack)) {
            var data = LinkData.read(stack);
            var pos = data.getPos();
            var side = data.getSide();
            var state = worldIn.getBlockState(pos);
            list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Linked: " + TextFormatting.DARK_PURPLE + state.getBlock().getTranslatedName().getString()));
            if (Screen.hasShiftDown()) {
                list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "Position: " + TextFormatting.DARK_PURPLE + "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"));
                list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "Transfer: " + TextFormatting.DARK_PURPLE + data.getType().name().toLowerCase()));
                list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "Operation: " + TextFormatting.DARK_PURPLE + data.getOperation().name().toLowerCase()));
                list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "Face: " + TextFormatting.DARK_PURPLE + side.name().toLowerCase()));
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return LinkData.isLinked(stack);
    }

}
