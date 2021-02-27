package me.jraynor.client.render.api.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.util.RenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.dispenser.IPosition;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * This will allow for the rendering of basic shapes in the world.
 */
public interface IRenderer2d extends IRenderer, ITextureHolder, IItemRenderer {

    /**
     * This will draw a texture at the given position on screen with the given offsets.
     */
    default void drawTexture(String texture, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        if (getTextures().containsKey(texture)) {
            bindTexture(texture);
            var size = getSize(texture);
            AbstractGui.blit(ctx().getStack(), x, y, uOffset, vOffset, uWidth, vHeight, size.getFirst(), size.getSecond());
        }
    }

    /**
     * This will draw a texture at the given position on screen with the given offsets.
     */
    default void drawTexture(String texture, int x, int y, int uOffset, int vOffset) {
        if (getTextures().containsKey(texture)) {
            bindTexture(texture);
            var size = getSize(texture);
            AbstractGui.blit(ctx().getStack(), x, y, uOffset, vOffset, size.getFirst(), size.getSecond(), size.getFirst(), size.getSecond());
        }
    }

    /**
     * This will render all of the text properties
     *
     * @param text
     */
    default void drawToolTip(List<ITextProperties> text) {
        net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(ctx().getStack(), text, ctx().getMouseX(), ctx().getMouseY(), ctx().getWindow().getScaledWidth(), ctx().getWindow().getScaledHeight(), -1, ctx().getFont());
    }

    /**
     * This will draw a quad of the given color
     */
    default void drawQuad(int x, int y, int width, int height, int color) {
        RenderSystem.disableDepthTest();
        ctx().getStack().push();
        AbstractGui.fill(ctx().getStack(), x, y, x + width, y + height, color);
        RenderSystem.enableDepthTest();
        ctx().getStack().pop();
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
    default void drawLine(int startX, int startY, int stopX, int stopY, Vector3i color) {
        drawLine(startX, startY, stopX, stopY, color, color);
    }


    /**
     * This will draw a quad of the given color at the given position with the given size
     */
    default void drawLine(int startX, int startY, int stopX, int stopY, Vector3i color, Vector3i stopColor) {
        var buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        var builder = buffer.getBuffer(RenderType.getLines());
        RenderSystem.disableDepthTest();
        var matrixPos = ctx().getStack().getLast().getMatrix();
        ctx().getStack().push();
        builder.pos(matrixPos, (float) startX, (float) startY, 32f)
                .color(color.getX(), color.getX(), color.getZ(), 255)
                .endVertex();
        builder.pos(matrixPos, (float) stopX, (float) stopY, 32f)
                .color(stopColor.getX(), stopColor.getY(), stopColor.getZ(), 255)
                .endVertex();
        ctx().getStack().pop();
        RenderSystem.enableDepthTest();
        buffer.finish();
    }
}
