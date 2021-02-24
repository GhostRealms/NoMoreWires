package me.jraynor.client.render.renderer;

import me.jraynor.client.render.api.*;

/**
 * This will render the connections between blocks in the world
 */
public class ConnectionRenderer extends AbstractRenderer implements IAbsorbable, IRenderer3d {
    public ConnectionRenderer() { super(RendererType.WORLD);}

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
