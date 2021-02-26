package me.jraynor.core;

import me.jraynor.NoMoreWires;
import me.jraynor.common.blocks.UtilityBlock;
//import me.jraynor.common.containers.UtilityContainer;
import me.jraynor.common.items.SynthesizerItem;
import me.jraynor.common.tiles.SingularityTile;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class keeps track of all of the blocks, items, tiles, containers, etc.
 */
public class ModRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NoMoreWires.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NoMoreWires.MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, NoMoreWires.MOD_ID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, NoMoreWires.MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, NoMoreWires.MOD_ID);
    /**
     * ============== Utility block start =============
     */
    public static final RegistryObject<UtilityBlock> UTILITY_BLOCK = BLOCKS.register("utilityblock", UtilityBlock::new);
    public static final RegistryObject<Item> UTILITY_BLOCK_ITEM = ITEMS.register("utilityblock", () -> new BlockItem(UTILITY_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));
    public static final RegistryObject<TileEntityType<SingularityTile>> UTILITY_BLOCK_TILE = TILES.register("utilityblock", () -> TileEntityType.Builder.create(SingularityTile::new, UTILITY_BLOCK.get()).build(null));
//    public static final RegistryObject<ContainerType<UtilityContainer>> UTILITY_BLOCK_CONTAINER = CONTAINERS.register("utilityblock", () -> IForgeContainerType.create((windowId, inv, data) -> {
//        BlockPos pos = data.readBlockPos();
//        World world = inv.player.getEntityWorld();
//        return new UtilityContainer(windowId, world, pos, inv);
//    }));

    /**
     * ============== synthesizer item start =============
     */
    public static final RegistryObject<SynthesizerItem> SYNTHESIZER_ITEM = ITEMS.register("synthesizer", SynthesizerItem::new);

    /**
     * This will initialize all of our registries
     */
    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
