package me.jraynor.core;

import me.jraynor.NoMoreWires;
import me.jraynor.client.screens.UtilityScreen;
import me.jraynor.common.data.LinkData;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.LeftClickAir;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class subscribes to all of the mods events.
 */
@Mod.EventBusSubscriber(value = {Dist.CLIENT, Dist.DEDICATED_SERVER}, modid = NoMoreWires.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    /**
     * This is used to initialize things on both the client and server.
     *
     * @param event the common setup event
     */
    @SubscribeEvent public static void onCommonInit(final FMLCommonSetupEvent event) {
        Network.initializeNetwork();
        NoMoreWires.logger.debug("Initialized network");
    }


    /**
     * This is used to initialize things on both the client and server.
     *
     * @param event the common setup event
     */
    @SubscribeEvent public static void onClientInit(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModRegistry.UTILITY_BLOCK_CONTAINER.get(), UtilityScreen::new);
        NoMoreWires.logger.debug("Initialized client");
    }

}
