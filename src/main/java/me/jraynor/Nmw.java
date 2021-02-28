package me.jraynor;

import me.jraynor.common.CommonEvents;
import me.jraynor.core.ClientEvents;
import me.jraynor.core.ModEvents;
import me.jraynor.core.ModRegistry;
import me.jraynor.core.Side;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the main entry point for the mod.
 */
@Mod(Nmw.MOD_ID)
public class Nmw {
    public static final String MOD_ID = "nmw";
    public static Side CURRENT_SIDE = Side.SERVER;//Starts as server, will be changed to client in the client only initialization

    /**
     * Here we add our listeners and initialize our register
     */
    public Nmw() {
        ModRegistry.init();
        MinecraftForge.EVENT_BUS.register(CommonEvents.class);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }

}
