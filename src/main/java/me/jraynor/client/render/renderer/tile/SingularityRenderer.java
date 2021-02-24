package me.jraynor.client.render.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.jraynor.NoMoreWires;
import me.jraynor.common.tiles.UtilityTile;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

/**
 * This will render our singularity
 */
public class SingularityRenderer extends AbstractTileRenderer<UtilityTile> {
    public static final ResourceLocation SINGULARITY_TEXTURE = new ResourceLocation(NoMoreWires.MOD_ID, "textures/block/utilityblock.png");

    public SingularityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override void render() {
        var sprite = getSprite(SINGULARITY_TEXTURE);
        var builder = getBuilder(RenderType.getTranslucent());
        stack.push();
        quad(builder, new Vector3f(0, 0, 0), new Vector3f(1, 1, 0.5f), Direction.UP, sprite);
        stack.pop();
    }


}
