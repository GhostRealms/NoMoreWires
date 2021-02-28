package me.jraynor.core;

import lombok.extern.log4j.Log4j2;
import me.jraynor.Nmw;
//import me.jraynor.client.render.renderer.screens.UtilityScreen;
import me.jraynor.api.manager.NodeListener;
import me.jraynor.common.network.Network;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

/**
 * This class subscribes to all of the mods events.
 */
@Log4j2
@Mod.EventBusSubscriber(value = {Dist.CLIENT, Dist.DEDICATED_SERVER}, modid = Nmw.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    /**
     * This is used to initialize things on both the client and server.
     *
     * @param event the common setup event
     */
    @SubscribeEvent public static void onCommonInit(final FMLCommonSetupEvent event) {
        Network.initializeNetwork();
        ModRegistry.subscribeNeeded();
        NodeListener.getInstance();//Will subscribe the node listener.¬
    }



}
