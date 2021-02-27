package me.jraynor.client.render.api.world;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.util.RenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import org.lwjgl.opengl.GL11;

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
        ctx().getStack().push();
        var buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        var builder = buffer.getBuffer(RenderType.LINES);
        builder.pos(ctx().getStack().getLast().getMatrix(), (float) start.x, (float) start.y, (float) start.z)
                .color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f)
                .tex(0, 0)
                .lightmap(0, 240)
                .normal(1, 0, 0)
                .endVertex();
        builder.pos(ctx().getStack().getLast().getMatrix(), (float) stop.x, (float) stop.y, (float) stop.z)
                .color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f)
                .tex(0, 0)
                .lightmap(0, 240)
                .normal(1, 0, 0)
                .endVertex();
        ctx().getStack().pop();
    }

    /**
     * TODO draw a quad see:
     * https://github.com/MrManiacc/Kratos/blob/ca6a1db4bbe685a732649cc33f22ddee6d3e7edd/src/main/kotlin/mod/kratos/render/RenderData.kt#L113
     */
    default void drawQuad(BlockPos posIn, Direction direction, Vector4f color) {
        var buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        var builder = buffer.getBuffer(RenderTypes.COLORED_QUAD);
        ctx().getStack().push();
        ctx().getStack().translate(-ctx().getProjectedView().x, -ctx().getProjectedView().y, -ctx().getProjectedView().z);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        var matrix = ctx().getStack().getLast().getMatrix();
        var min = new Vector3f(posIn.getX(), posIn.getY(), posIn.getZ());
        var max = new Vector3f(min.getX() + 1.0f, min.getY() + 1.0f, min.getZ() + 1.0f);
        {
            switch (direction) {
                case NORTH -> {
                    builder.pos(matrix, min.getX(), max.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                    builder.pos(matrix, max.getX(), max.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                    builder.pos(matrix, max.getX(), min.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                    builder.pos(matrix, min.getX(), min.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                }
                case SOUTH -> {
                    builder.pos(matrix, max.getX(), max.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                    builder.pos(matrix, min.getX(), max.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                    builder.pos(matrix, min.getX(), min.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                    builder.pos(matrix, max.getX(), min.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();
                }
                case EAST -> {
                    builder.pos(matrix, max.getX(), max.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top left
                    builder.pos(matrix, max.getX(), max.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top right
                    builder.pos(matrix, max.getX(), min.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot right
                    builder.pos(matrix, max.getX(), min.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot left
                }
                case WEST -> {
                    builder.pos(matrix, min.getX(), max.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top left
                    builder.pos(matrix, min.getX(), max.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top right
                    builder.pos(matrix, min.getX(), min.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot right
                    builder.pos(matrix, min.getX(), min.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot left
                }
                case UP -> {
                    builder.pos(matrix, max.getX(), max.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top left
                    builder.pos(matrix, min.getX(), max.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top right
                    builder.pos(matrix, min.getX(), max.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot right
                    builder.pos(matrix, max.getX(), max.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot left
                }
                case DOWN -> {
                    builder.pos(matrix, min.getX(), min.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top left
                    builder.pos(matrix, max.getX(), min.getY(), min.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//top right
                    builder.pos(matrix, max.getX(), min.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot right
                    builder.pos(matrix, min.getX(), min.getY(), max.getZ()).color(color.getX(), color.getY(), color.getZ(), color.getW()).endVertex();//bot left
                }
            }
        }
        ctx().getStack().pop();
    }
}
