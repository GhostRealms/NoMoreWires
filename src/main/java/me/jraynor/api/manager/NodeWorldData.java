package me.jraynor.api.manager;

import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.jraynor.Nmw;
import me.jraynor.api.node.INode;
import me.jraynor.common.util.TagUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * This allows for per world node data storage. It also extracts all of the
 * code out side of the tile entities for the most part which is good.
 */
@Log4j2
public class NodeWorldData extends WorldSavedData {
    private static final String DATA_NAME = Nmw.MOD_ID + "_NodeData";
    private final Map<BlockPos, NodeHolder> nodeHolders = Maps.newHashMap();

    public NodeWorldData() {
        super(DATA_NAME);
    }

    /**
     * This will add a node for the given position
     *
     * @param pos  the block pos of the node
     * @param node the node to add
     */
    public boolean addNode(BlockPos pos, INode node) {
        if (!nodeHolders.containsKey(pos))
            nodeHolders.put(pos, new NodeHolder(pos));
        var holder = nodeHolders.get(pos);
        if (holder.add(node)) {
            markDirty();
            return true;
        }
        return false;
    }

    /**
     * will remove a node at the given position with the given uuid
     *
     * @param pos  the pos of the node to remove
     * @param node the node uuid
     * @return true if removed
     */
    public boolean removeNode(BlockPos pos, UUID node) {
        if (!nodeHolders.containsKey(pos))
            return false;
        var holder = nodeHolders.get(pos);
        if (!holder.getAllNodes().containsKey(node))
            return false;
        if (holder.remove(node)) {
            markDirty();
            return true;
        }
        return false;
    }


    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     *
     * @param tag
     */
    @Override
    public void read(CompoundNBT tag) {
        var posHeader = (CompoundNBT) tag.get("block_header");
        var nodeHolders = (CompoundNBT) tag.get("node_holder");
        var size = posHeader.getInt("block_pos_header_size");
        for (var i = 0; i < size; i++) {
            var key = TagUtils.readBlockPos(tag, "block_pos_" + i);
            var nodeHolderTag = (CompoundNBT) nodeHolders.get("node_holder_" + i);
            if (!this.nodeHolders.containsKey(key))
                this.nodeHolders.put(key, new NodeHolder(key));
            this.nodeHolders.get(key).read(nodeHolderTag);
        }
    }

    /**
     * This will write the nodes to file
     *
     * @param tag the compound
     * @return the tag
     */
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        var blockHeader = new CompoundNBT();
        var nodeHolders = new CompoundNBT();
        blockHeader.putInt("block_pos_header_size", this.nodeHolders.size());
        var i = 0;
        for (var header : this.nodeHolders.keySet()) {
            TagUtils.writeBlockPos(blockHeader, "block_pos_" + i++, header);
            nodeHolders.put("node_holder_" + i, this.nodeHolders.get(header).write(new CompoundNBT()));
        }
        tag.put("block_header", blockHeader);
        tag.put("node_holder", nodeHolders);
        return tag;
    }

    /**
     * This will get the node world data for the given world
     *
     * @param world the world to get the data for
     * @return the node world data
     */
    public static NodeWorldData forWorld(ServerWorld world) {
        var storage = world.getSavedData();
        var saver = (NodeWorldData) storage.getOrCreate(NodeWorldData::new, DATA_NAME);
        if (saver == null) {
            saver = new NodeWorldData();
            storage.set(saver);
        }
        return saver;
    }
}
