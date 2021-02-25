package me.jraynor.client.render.api.hud;

import me.jraynor.client.render.api.core.IRenderer;
import net.minecraft.client.gui.AbstractGui;

/**
 * This will allow for the rendering of basic shapes in the world.
 */
public interface IRenderer2d extends IRenderer, ITextureHolder {
    /**
     * This will draw a texture at the given position on screen with the given offsets.
     */
    default void drawTexture(String texture, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        if (getTextures().containsKey(texture)) {
            bindTexture(texture);
            var size = getSize(texture);
            AbstractGui.blit(getStack(), x, y, uOffset, vOffset, uWidth, vHeight, size.getFirst(), size.getSecond());
        }
    }

    /**
     * This will draw a texture at the given position on screen with the given offsets.
     */
    default void drawTexture(String texture, int x, int y, int uOffset, int vOffset) {
        if (getTextures().containsKey(texture)) {
            bindTexture(texture);
            var size = getSize(texture);
            AbstractGui.blit(getStack(), x, y, uOffset, vOffset, size.getFirst(), size.getSecond(), size.getFirst(), size.getSecond());
        }
    }


    /**
     * This will draw a texture at the given position on screen with the given offsets.
     */
    default void drawTexture(String texture, int x, int y) {
        drawTexture(texture, x, y, 0, 0);
    }

    /**
     * This will draw a quad of the given color at the given position with the given size
     */
    default void drawRect(int x, int y, int width, int height, int color) {

    }
}
