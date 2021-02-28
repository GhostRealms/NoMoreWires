package me.jraynor.api.packet;

import lombok.Getter;
import me.jraynor.api.link.LinkClient;
import me.jraynor.api.link.LinkServer;
import me.jraynor.api.node.INode;
import me.jraynor.api.operation.extract.ExtractOperationClient;
import me.jraynor.api.operation.extract.ExtractOperationServer;
import me.jraynor.api.operation.insert.InsertOperationClient;
import me.jraynor.api.operation.insert.InsertOperationServer;
import me.jraynor.api.util.NodeType;
import me.jraynor.common.network.IPacket;
import me.jraynor.core.Side;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * This will add the given node to the server.
 * This will be sent from the client
 */
public class AddNode extends NodePacket {
    @Getter private INode node;

    public AddNode(BlockPos tilePos, INode node) {
        super(tilePos);
        this.node = node;
    }

    @Override
    public void writeBuffer(PacketBuffer buf) {
        super.writeBuffer(buf);
        buf.writeEnumValue(node.getNodeType());
        buf.writeCompoundTag(node.write(new CompoundNBT()));
    }

    @Override
    public void readBuffer(PacketBuffer buf) {
        super.readBuffer(buf);
        var nodeType = buf.readEnumValue(NodeType.class);
        this.node = nodeType.newNodeFor(Side.getThreadSide());
        node.read(buf.readCompoundTag());
    }
}
