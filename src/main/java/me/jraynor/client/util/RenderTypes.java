package me.jraynor.client.util;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jraynor.Nmw;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public final class RenderTypes {

    private static final RenderState.TransparencyState translucentTransparency = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private static RenderState.DepthTestState noDepth = new RenderState.DepthTestState("always", GL11.GL_ALWAYS);
    private static RenderState.WriteMaskState colorMask = new RenderState.WriteMaskState(true, false);
    private static RenderState.LayerState viewOffsetZLayering = new RenderState.LayerState("view_offset_z_layering", () -> {
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.99975586f, 0.99975586f, 0.99975586f);
    }, RenderSystem::popMatrix);

    private static RenderType.State glState = RenderType.State.getBuilder()
            .line(new RenderState.LineState(OptionalDouble.of(3.0)))
            .layer(viewOffsetZLayering)
            .transparency(translucentTransparency)
            .writeMask(colorMask)
            .depthTest(noDepth)
            .build(false);

    public static final RenderType COLORED_QUAD = RenderType.makeType(Nmw.MOD_ID + ":quads", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, glState);
    public static final RenderType COLORED_LINE = RenderType.makeType(Nmw.MOD_ID + ":lines", DefaultVertexFormats.POSITION_COLOR, GL11.GL_LINE, 128, glState);
}