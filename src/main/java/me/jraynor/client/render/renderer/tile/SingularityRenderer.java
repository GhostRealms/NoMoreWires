//package me.jraynor.client.render.renderer.tile;
//
//import com.mojang.blaze3d.matrix.MatrixStack;
//import me.jraynor.Nmw;
////import me.jraynor.common.tiles.UtilityTile;
//import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
//import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
//import net.minecraft.util.Direction;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.vector.Vector3f;
//
//import java.util.Objects;
//
///**
// * This will render our singularity
// */
//public class SingularityRenderer extends AbstractTileRenderer<UtilityTile> {
//    public static final ResourceLocation SINGULARITY_TEXTURE = new ResourceLocation(Nmw.MOD_ID, "textures/block/utilityblock.png");
//    private IBakedModel baseModel;
//    private TextureAtlasSprite baseTexture;
//
//    public SingularityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
//        super(rendererDispatcherIn);
//    }
//
//    /**
//     * Used to get the models at the start and setup things for the renderer.
//     */
//    @Override void init() {
//        this.baseModel = getBlockModelShapes().getModel(tile.getBlockState());
//        this.baseTexture = getBlockModelShapes().getTexture(tile.getBlockState(), Objects.requireNonNull(tile.getWorld()), tile.getPos());
//    }
//
//    @Override void render() {
//    }
//
//
//}
