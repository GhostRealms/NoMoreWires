package me.jraynor.core;

import me.jraynor.NoMoreWires;
import me.jraynor.client.render.MasterRenderer;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.LeftClickAir;
import me.jraynor.common.network.packets.LeftClickBlock;
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
        if (event instanceof PlayerInteractEvent.LeftClickBlock)
            if (event.getItemStack().getItem() == ModRegistry.SYNTHESIZER_ITEM.get()) {
                Network.sendToServer(new LeftClickBlock(event.getItemStack(), event.getPlayer().isSneaking(), event.getPos(), event.getFace()));
                event.setCanceled(true);
            }
        if (event instanceof PlayerInteractEvent.LeftClickEmpty)
            if (event.getItemStack().getItem() == ModRegistry.SYNTHESIZER_ITEM.get())
                Network.sendToServer(new LeftClickAir(event.getItemStack(), event.getPlayer().isSneaking()));

    }

}
