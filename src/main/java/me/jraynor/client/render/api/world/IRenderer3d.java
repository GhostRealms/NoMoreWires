package me.jraynor.client.render.api.world;

import me.jraynor.client.render.api.core.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * This will allow for the rendering of basic shapes in the world.
 */
public interface IRenderer3d extends IRenderer {

    /**
     * Draw's a line see
     * https://github.com/MrManiacc/Kratos/blob/ca6a1db4bbe685a732649cc33f22ddee6d3e7edd/src/main/kotlin/mod/kratos/render/RenderData.kt#L85
     */
    default void drawLine(Vector3d start, Vector3d stop, int r, int g, int b, int a) {
        //TODO translate
        getStack().push();
        var buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        var builder = buffer.getBuffer(RenderType.LINES);
        builder.pos(getStack().getLast().getMatrix(), (float) start.x, (float) start.y, (float) start.z)
                .color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f)
                .tex(0, 0)
                .lightmap(0, 240)
                .normal(1, 0, 0)
                .endVertex();
        builder.pos(getStack().getLast().getMatrix(), (float) stop.x, (float) stop.y, (float) stop.z)
                .color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f)
                .tex(0, 0)
                .lightmap(0, 240)
                .normal(1, 0, 0)
                .endVertex();
        getStack().pop();
    }

    /**
     * TODO draw a quad see:
     * https://github.com/MrManiacc/Kratos/blob/ca6a1db4bbe685a732649cc33f22ddee6d3e7edd/src/main/kotlin/mod/kratos/render/RenderData.kt#L113
     */
    default void drawQuad(BlockPos pos, Direction face, int r, int g, int b, int a) {
    }
}
