package me.jraynor.client.render.renderer;

import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.util.RendererType;
import me.jraynor.client.render.api.world.IRenderer3d;

/**
 * This is the world renderer, it can render the world
 */
public class IOModeRenderer extends AbstractRenderer implements IAbsorbable, IRenderer3d {
    public IOModeRenderer() { super(RendererType.WORLD); }

    /**
     * This will be called 20 times per second and is used to update elements on the screen.
     * because we don't have children we don't need the super
     */
    @Override public void tick() {
    }

    /**
     * Here we want to render our connections between the various machines
     */
    @Override public void render() {
    }


}
