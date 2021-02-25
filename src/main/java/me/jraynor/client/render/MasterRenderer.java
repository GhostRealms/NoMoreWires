package me.jraynor.client.render;

import lombok.Getter;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IContainer;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.renderer.hud.ConnectionOverlay;
import me.jraynor.client.render.renderer.ConnectionRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a singleton class that render things on screen for debugging and other purposes.
 */
@OnlyIn(Dist.CLIENT)
public final class MasterRenderer extends AbstractRenderer implements IAbsorbable, IContainer {
    @Getter private static final MasterRenderer instance = new MasterRenderer();
    @Getter private final Map<RenderType, List<IRenderer>> children = new HashMap<>();

    private MasterRenderer() {
        super(RenderType.WORLD);
        addChild(new ConnectionRenderer());
        addChild(new ConnectionOverlay());
        initialize();
    }

}