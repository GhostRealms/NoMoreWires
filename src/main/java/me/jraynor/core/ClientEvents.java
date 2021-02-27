package me.jraynor.core;

import me.jraynor.NoMoreWires;
import me.jraynor.client.render.MasterRenderer;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.LeftClickAir;
import me.jraynor.common.network.packets.LeftClickBlock;
import me.jraynor.common.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This class subscribes to all of the mods events.
 */
@Mod.EventBusSubscriber(value = {Dist.CLIENT}, modid = NoMoreWires.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    /**
     * Here we want to draw our overlay system for the {@link me.jraynor.common.items.SynthesizerItem}
     *
     * @param event the render world last event
     */
    @SubscribeEvent public static void onDrawLast(RenderWorldLastEvent event) {
        MasterRenderer.getInstance().fromEvent(event);
        MasterRenderer.getInstance().renderChildren(RenderType.WORLD);
    }

    /**
     * Here we want to draw our overlay system for the {@link me.jraynor.common.items.SynthesizerItem}
     *
     * @param event the render world last event
     */
    @SubscribeEvent public static void onDrawHud(RenderGameOverlayEvent.Post event) {
        MasterRenderer.getInstance().fromEvent(event);
        MasterRenderer.getInstance().renderChildren(RenderType.HUD);
    }

    /**
     * This is called per client tick
     *
     * @param event the tick event
     */
    @SubscribeEvent public static void onTick(TickEvent event) {
        if (event instanceof TickEvent.ClientTickEvent) {
            MasterRenderer.getInstance().tickChildren(RenderType.WORLD);
            MasterRenderer.getInstance().tickChildren(RenderType.HUD);
        }
    }

    /**
     * Here we want to send a message to the server that we are left clicking air with the {@link me.jraynor.common.items.SynthesizerItem}
     *
     * @param event the interact event
     */
    @SubscribeEvent public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event instanceof PlayerInteractEvent.LeftClickBlock) {
            if (event.getItemStack().getItem() == Items.LAPIS_LAZULI) {
                copyCompound(event);
                event.setCanceled(true);
            } else if (event.getItemStack().getItem() == ModRegistry.SYNTHESIZER_ITEM.get()) {
                Network.sendToServer(new LeftClickBlock(event.getItemStack(), event.getPlayer().isSneaking(), event.getPos(), event.getFace()));
                event.setCanceled(true);
            }
        }
        if (event instanceof PlayerInteractEvent.LeftClickEmpty)
            if (event.getItemStack().getItem() == ModRegistry.SYNTHESIZER_ITEM.get())
                Network.sendToServer(new LeftClickAir(event.getItemStack(), event.getPlayer().isSneaking()));
    }


    /**
     * This will copy the compound nbt from the looked at block
     *
     * @param event the event to use
     */
    private static void copyCompound(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()) {
            Minecraft.getInstance().player.connection.getNBTQueryManager().queryTileEntity(event.getPos(), StringUtils::setCompoundToClipboard);
            event.getPlayer().sendStatusMessage(new StringTextComponent("Copied server side nbt data to clipboard"), true);
        } else {
            var tileentity = event.getWorld().getTileEntity(event.getPos());
            StringUtils.setCompoundToClipboard(tileentity != null ? tileentity.write(new CompoundNBT()) : null);
            event.getPlayer().sendStatusMessage(new StringTextComponent("Copied client side nbt data to clipboard"), true);
        }
    }
}
