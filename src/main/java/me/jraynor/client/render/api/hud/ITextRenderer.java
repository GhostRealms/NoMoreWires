package me.jraynor.client.render.api.hud;

import me.jraynor.client.render.api.core.IRenderer;

/**
 * This will allow for the rendering of basic shapes in the world.
 */
public interface ITextRenderer extends IRenderer {

    /**
     * This will render a quad for the given face in the given direction.
     */
    default void drawString(String text, int x, int y, int color) {
        ctx().getFont().drawStringWithShadow(ctx().getStack(), text, x, y, color);
    }

    /**
     * Renders a string with the given color
     */
    default void drawCenterString(String text, int x, int y, int color) {
        ctx().getFont().drawStringWithShadow(ctx().getStack(), text, (float) (x - ctx().getFont().getStringWidth(text) / 2), (float) y, color);
    }
}
