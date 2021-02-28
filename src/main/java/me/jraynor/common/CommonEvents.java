package me.jraynor.common;

import lombok.extern.log4j.Log4j2;
import me.jraynor.Nmw;
import me.jraynor.common.network.Network;
import me.jraynor.core.ModRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


/**
 * This has forge events that are both the client and the server
 */
@Log4j2
@Mod.EventBusSubscriber(value = {Dist.CLIENT, Dist.DEDICATED_SERVER}, modid = Nmw.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    /**
     * This should be called when we're quiting. So we destroy the network
     *
     * @param event
     */
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
//        Network.initializeNetwork();
        ModRegistry.subscribeNeeded();
    }

    /**
     * This should be called when we're quiting. So we destroy the network
     *
     * @param event
     */
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
//        Network.delete();
    }
}
