package me.jraynor.client.render.api.hud;

import me.jraynor.client.render.api.core.IRenderer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import org.lwjgl.opengl.GL11;

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
     * This will draw a texture at the given position on screen with the given offsets.
     */
    default void drawTexture(String texture, int x, int y) {
        drawTexture(texture, x, y, 0, 0);
    }

    /**
     * This will draw a quad of the given color at the given position with the given size
     */
    default void drawLine(int startX, int startY, int stopX, int stopY, int width, Vector3i color) {
//        ctx().getStack().push();
//        var buffer = ctx().getBuilder(RenderType.LINES);
//        buffer.pos(ctx().getStack().getLast().getMatrix(), (float) startX, (float) startY, 32f)
//                .color(color.getX(), color.getX(), color.getZ(), 255);
//        buffer.pos(ctx().getStack().getLast().getMatrix(), (float) stopX, (float) stopY, 32f)
//                .color(color.getX(), color.getX(), color.getZ(), 255);
//        ctx().getStack().pop();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(width);
        GL11.glColor4f(color.getX() / 255.0f, color.getY() / 255.0f, color.getZ() / 255.0f, 1.0f);
        GL11.glBegin(2);
        GL11.glVertex2d(startX, startY);
        GL11.glVertex2d(stopX, stopY);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
