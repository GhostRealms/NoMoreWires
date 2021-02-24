package me.jraynor.client.render.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This is a utility class that allows for easy utility functions for renderering
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractTileRenderer<T extends TileEntity> extends TileEntityRenderer<T> {
    protected T tile;
    protected float partialTicks;
    protected MatrixStack stack;
    protected IRenderTypeBuffer buffer;
    protected int combinedLight, combinedOverlay;
    private ModelManager modelManager;
    private TextureManager textureManager;
    protected BlockPos pos;

    public AbstractTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    /**
     * This will store all of the variables and call the render method
     */
    @Override public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        this.pos = tileEntityIn.getPos();
        this.tile = tileEntityIn;
        this.stack = matrixStackIn;
        this.buffer = bufferIn;
        this.combinedLight = combinedLightIn;
        this.combinedOverlay = combinedOverlayIn;
        this.modelManager = Minecraft.getInstance().getModelManager();
        render();
    }

    abstract void render();
    
    /**
     * This will add a new quad
     */
    protected void point(IVertexBuilder builder, Vector3f point, Vector2f uv) {
        builder.pos(stack.getLast().getMatrix(), point.getX(), point.getY(), point.getZ())
                .color(1.0f, 1.0f, 1.0f, 1.0f)
                .tex(uv.x, uv.y)
                .normal(0, 1, 0)
                .endVertex();
    }

    /**
     * This will render a quad at the given point
     */
    protected void quad(IVertexBuilder builder, Vector3f pos, Vector3f offset, Direction face, TextureAtlasSprite sprite) {
        switch (face) {
            case DOWN -> {
                builder.pos(pos.getX(), pos.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX(), pos.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
            }
            case UP -> {
                builder.pos(pos.getX(), pos.getY() + offset.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
            }
            case EAST -> {
                builder.pos(pos.getX() + offset.getX(), pos.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
            }
            case WEST -> {
                builder.pos(pos.getX(), pos.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX(), pos.getY() + offset.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX(), pos.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
            }
            case NORTH -> {
                builder.pos(pos.getX(), pos.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX(), pos.getY() + offset.getY(), pos.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
            }
            case SOUTH -> {
                builder.pos(pos.getX(), pos.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
                builder.pos(pos.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ())
                        .color(1.0f, 1.0f, 1.0f, 1.0f).normal(offset.getX(), offset.getY(), offset.getZ()).endVertex();
            }
        }

    }


    /**
     * This will generate a new vertex builder for the given render type
     *
     * @param type the type of renderer
     * @return the new builder
     */
    protected IVertexBuilder getBuilder(RenderType type) {
        return buffer.getBuffer(type);
    }

    /**
     * This will get the sprite from the given resource location
     *
     * @return the sprite for the give location
     */
    protected TextureAtlasSprite getSprite(ResourceLocation location) {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(location);
    }

}
