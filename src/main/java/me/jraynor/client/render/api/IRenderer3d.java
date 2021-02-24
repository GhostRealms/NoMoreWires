package me.jraynor.client.render.api;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * This will allow for the rendering of basic shapes in the world.
 */
public interface IRenderer3d extends IRenderer {

    /**
     * This method will draw a box
     */
    default void drawLine(Vector3d start, Vector3d stop, int r, int g, int b, int a) {

    }

    /**
     * This will render a quad for the given face in the given direction.
     */
    default void drawQuad(BlockPos pos, Direction face, int r, int g, int b, int a) {
    }
}
