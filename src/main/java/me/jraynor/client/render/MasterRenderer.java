package me.jraynor.client.render;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.*;
import me.jraynor.client.render.renderer.ConnectionOverlay;
import me.jraynor.client.render.renderer.ConnectionRenderer;
import me.jraynor.client.render.renderer.IOModeRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a singleton class that render things on screen for debugging and other purposes.
 */
@OnlyIn(Dist.CLIENT)
public final class MasterRenderer extends AbstractRenderer implements IAbsorbable, IParentable {
    @Getter private static final MasterRenderer instance = new MasterRenderer();
    @Getter private final Map<RendererType, List<IRenderer>> children = new HashMap<>();

    private MasterRenderer() {
        super();
        addChild(new ConnectionRenderer());
        addChild(new IOModeRenderer());
        addChild(new ConnectionOverlay());
    }
}