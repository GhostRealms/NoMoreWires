package me.jraynor.client.render.api;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * This will allow for the rendering of basic shapes in the world.
 */
public interface ITextRenderer extends IRenderer {

    /**
     * This will render a quad for the given face in the given direction.
     */
    default void drawString(String text, int x, int y, int color) {
        getFont().drawStringWithShadow(getStack(), text, x, y, color);
    }

    /**
     * Renders a string with the given color
     */
    default void drawCenterString(String text, int x, int y, int color) {
        getFont().drawStringWithShadow(getStack(), text, (float) (x - getFont().getStringWidth(text) / 2), (float) y, color);
    }
}
