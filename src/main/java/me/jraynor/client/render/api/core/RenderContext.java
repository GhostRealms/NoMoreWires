package me.jraynor.client.render.api.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.core.IRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * This class allows for updating/saving of the common variables in rendering
 */
@OnlyIn(Dist.CLIENT) public final class RenderContext {
    @Getter @Setter private MatrixStack stack;
    @Getter @Setter private Matrix4f proMatrix;
    @Getter @Setter private float partialTicks;
    @Getter @Setter private RenderGameOverlayEvent.ElementType element;
    @Getter @Setter private int mouseX, mouseY;

    private RenderContext() {}

    /**
     * true if we can render for the given type
     *
     * @param type the type to check against
     * @return the render type
     */
    public boolean isValid(RenderType type) {
        if (type == RenderType.WORLD)
            return stack != null && proMatrix != null;
        if (type == RenderType.HUD)
            return stack != null && element != null;
        return stack != null;
    }

    public IVertexBuilder getBuilder(net.minecraft.client.renderer.RenderType type) {
        return Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().getBuffer(type);
    }

    public FontRenderer getFont() {
        return Minecraft.getInstance().fontRenderer;
    }

    public MainWindow getWindow() {
        return Minecraft.getInstance().getMainWindow();
    }


    public ClientPlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }

    public Vector3d getProjectedView() {
        return Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
    }

    public World getWorld() {
        return Minecraft.getInstance().world;
    }

    public WorldRenderer getWorldRenderer() {
        return Minecraft.getInstance().worldRenderer;
    }

    public ItemRenderer getItemRenderer() {
        return Minecraft.getInstance().getItemRenderer();
    }

    static final RenderContext INSTANCE = new RenderContext();
}
