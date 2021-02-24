package me.jraynor;

import me.jraynor.core.ClientEvents;
import me.jraynor.core.ModEvents;
import me.jraynor.core.ModRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the main entry point for the mod.
 */
@Mod(NoMoreWires.MOD_ID)
public class NoMoreWires {
    public static final String MOD_ID = "nmw";
    public static final Logger logger = LogManager.getLogger(NoMoreWires.MOD_ID);

    /**
     * Here we add our listeners and initialize our register
     */
    public NoMoreWires() {
        ModRegistry.init();
        MinecraftForge.EVENT_BUS.register(ModEvents.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }
}
