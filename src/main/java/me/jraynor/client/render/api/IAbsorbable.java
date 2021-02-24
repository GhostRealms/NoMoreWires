package me.jraynor.client.render.api;

import net.minecraft.client.Minecraft;
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
        setWorld(Minecraft.getInstance().world);
        setPlayer(Minecraft.getInstance().player);
        setFont(Minecraft.getInstance().fontRenderer);
        setContext(event.getContext());
        setStack(event.getMatrixStack());
        setProMatrix(event.getProjectionMatrix());
        setPartialTicks(event.getPartialTicks());
        ifIs(IParentable.class, IParentable::passChildren);
    }

    /**
     * This will update the renderer based upon the world last event
     *
     * @param event the event
     */
    default void fromEvent(RenderGameOverlayEvent event) {
        setWorld(Minecraft.getInstance().world);
        setPlayer(Minecraft.getInstance().player);
        setFont(Minecraft.getInstance().fontRenderer);
        setStack(event.getMatrixStack());
        setPartialTicks(event.getPartialTicks());
        setWindow(event.getWindow());
        setElement(event.getType());
        ifIs(IParentable.class, IParentable::passChildren);
    }


    /**
     * This will update the renderer from the parent
     *
     * @param parent the parent
     */
    default void fromParent(IRenderer parent) {
        setWorld(parent.getWorld());
        setPlayer(parent.getPlayer());
        setFont(parent.getFont());
        setStack(parent.getStack());
        setElement(parent.getElement());
        setWindow(parent.getWindow());
        setContext(parent.getContext());
        setProMatrix(parent.getProMatrix());
        setPartialTicks(parent.getPartialTicks());
    }

}
