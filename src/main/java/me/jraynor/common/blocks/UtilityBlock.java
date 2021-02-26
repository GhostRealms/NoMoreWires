package me.jraynor.common.blocks;

import lombok.SneakyThrows;
import me.jraynor.client.render.renderer.screens.SingularityScreen;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.OpenScreen;
//import me.jraynor.common.tiles.UtilityTile;
import me.jraynor.common.tiles.SingularityTile;
import me.jraynor.core.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;

/**
 * This is the main block class for our utility block
 */
public class UtilityBlock extends Block {

    public UtilityBlock() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
                .setLightLevel(state -> state.get(BlockStateProperties.POWERED) ? 14 : 0)
        );
        Network.subscribe(this);
    }


    /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vanilla
     * blocks, and also want to have a tile entity on that block, may.
     * <p>
     * Return true from this function to specify this block has a tile entity.
     *
     * @param state State of the current block
     * @return True if block has a tile entity, false otherwise
     */
    @Override public boolean hasTileEntity(BlockState state) {
        return true;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param state The state of the current block
     * @param world The world to create the TE in
     * @return A instance of a class extending TileEntity
     */
    @Nullable @Override public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModRegistry.UTILITY_BLOCK_TILE.get().create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS; // on client side, don't do anything
        if (!(player instanceof ServerPlayerEntity))
            return ActionResultType.FAIL;  // should always be true, but just in case...
        var tile = worldIn.getTileEntity(pos);
        if (tile instanceof SingularityTile) {
            Network.sendToClient(new OpenScreen(pos, ModRegistry.UTILITY_BLOCK_TILE.get()), (ServerPlayerEntity) player);
        } else {
            throw new IllegalStateException("Our named container provider is missing!");
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * This will open the given screen
     *
     * @param screen the screen to open
     */
    @SneakyThrows public void onOpenScreen(OpenScreen screen, NetworkEvent.Context context) {
        if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            if (screen.getType().equals(ModRegistry.UTILITY_BLOCK_TILE.get())) {
                Minecraft.getInstance().displayGuiScreen(new SingularityScreen(screen.getTilePos()));
                context.setPacketHandled(true);
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(BlockStateProperties.FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }

    @Override public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return state.get(BlockStateProperties.POWERED) ? super.getLightValue(state, world, pos) : 0;
    }

}
