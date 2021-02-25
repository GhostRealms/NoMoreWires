package me.jraynor.client.render.api.core;

import me.jraynor.client.render.api.hud.ITextureHolder;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * This allows us to set the data inside the class
 */
public interface IAbsorbable extends IRenderer {
    /**
     * This will update the renderer based upon the world last event
     *
     * @param event the event
     */
    default void fromEvent(RenderWorldLastEvent event) {
        ctx().setProMatrix(event.getProjectionMatrix());
        ctx().setPartialTicks(event.getPartialTicks());
        ctx().setStack(event.getMatrixStack());
    }

    /**
     * This will update the renderer based upon the world last event
     *
     * @param event the event
     */
    default void fromEvent(RenderGameOverlayEvent event) {
        ctx().setStack(event.getMatrixStack());
        ctx().setElement(event.getType());
        ctx().setPartialTicks(event.getPartialTicks());
        ifIs(IContainer.class, IContainer::passChildren);
    }


    /**
     * This will update the renderer from the parent
     *
     * @param parent the parent
     */
    default void fromParent(IRenderer parent) {
        if (this instanceof ITextureHolder && parent instanceof ITextureHolder) {
            var from = (ITextureHolder) parent;
            var self = (ITextureHolder) this;
            self.getTextures().putAll(from.getTextures());
        }
    }

}
