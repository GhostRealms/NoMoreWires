package me.jraynor.client.render.renderer;

import me.jraynor.Nmw;
import me.jraynor.api.event.OverlayStartEvent;
import me.jraynor.api.event.OverlayStopEvent;
import me.jraynor.api.link.ILink;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.world.IRenderer3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

/**
 * This will highlight the selected node in the world
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Nmw.MOD_ID, value = Dist.CLIENT)
public class OverlayRenderer extends AbstractRenderer implements IAbsorbable, IRenderer3d {
    private final Set<ILink> highlighted = new HashSet<>();

    public OverlayRenderer() {
        super(RenderType.WORLD);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called when we want to start highlighting
     */
    @SubscribeEvent
    public void onHighlightStart(OverlayStartEvent event) {
        System.out.println("Started hightlighting: " + event.getNode().getPos().getCoordinatesAsString());
        highlighted.add(event.getNode());
    }

    /**
     * Called when we're finished highlighting a given block
     */
    @SubscribeEvent
    public void onHighlightStop(OverlayStopEvent event) {
        highlighted.remove(event.getNode());
    }


    /**
     * Here we want to render our connections between the various machines
     */
    @Override public void render() {
        //TODO render hightlight
        for (var overlay : highlighted) {
            drawQuad(overlay.getPos(), overlay.getFace(), new Vector4f(1, 1, 1, 0.8f));
        }
    }


}
