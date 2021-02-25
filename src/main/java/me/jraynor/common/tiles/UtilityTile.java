package me.jraynor.common.tiles;

import com.google.common.collect.Queues;
import lombok.Getter;
import me.jraynor.common.data.IOMode;
import me.jraynor.common.data.LinkData;
import me.jraynor.common.data.TransferMode;
import me.jraynor.common.network.IPacket;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.TransferData;
import me.jraynor.common.network.packets.TransferUpdate;
import me.jraynor.core.ModRegistry;
import me.jraynor.core.node.ClientNode;
import me.jraynor.core.node.INode;
import me.jraynor.core.node.ServerNode;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * This is the where the magic happens. This is the main utility tile.
 */
public class UtilityTile extends TileEntity implements ITickableTileEntity {
    private final TransferData transferData = new TransferData();
    private final List<Connection> connections = new ArrayList<>();
    private final Queue<Connection> removable = Queues.newLinkedBlockingQueue();
    private long tick = System.currentTimeMillis();
    private long connTimer = 20; //Checks the connection every second
    private int itemsPerTick = 1;
    private int rfPerTick = 2500;
    @Getter private final Set<INode> rootNodes = new HashSet<>();

    public UtilityTile() {
        super(ModRegistry.UTILITY_BLOCK_TILE.get());
        Network.subscribe(this);
    }

    /**
     * This will add a node
     */
    public boolean addRoot(INode node) {
        if (!rootNodes.add(node)) return false;
        assert world != null;
        syncNodes(world.isRemote ? NetworkDirection.PLAY_TO_SERVER : NetworkDirection.PLAY_TO_CLIENT);
        return true;
    }

    /**
     * This will send all of the nodes to the client
     *
     * @param direction the direction to sync the nodes
     */
    public void syncNodes(NetworkDirection direction) {
        switch (direction) {
            case PLAY_TO_CLIENT -> rootNodes.forEach(Network::sendToAllClients);
            case PLAY_TO_SERVER -> rootNodes.forEach(Network::sendToServer);
        }
    }

    /**
     * This method is what will end up linking the blocks. It is called from the synthesizer item.
     *
     * @param other the block to link to this
     * @param self  a link data of this
     */
    public boolean onLink(LinkData self, LinkData other) {
        var node = new ServerNode();
        node.setMode(other.getType());
        node.setPos(other.getPos());
        node.setDir(other.getSide());
        return addRoot(node);
    }


    /**
     * This is called when we are syncing the nodes from either client to server or server to client. Both are valid.
     */
    private void onNodeSync(INode nodePacket, NetworkEvent.Context ctx) {
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.enqueueWork(() -> {
                var node = findNode(nodePacket);
                if (node == null) {
                    if (nodePacket instanceof ClientNode) {
                        var serverNode = new ServerNode();
                        serverNode.absorb(nodePacket);
                        rootNodes.add(serverNode);
                    }
                } else
                    node.absorb(nodePacket);
            });
            ctx.setPacketHandled(true);
        }
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            var node = findNode(nodePacket);
            if (node == null) {
                if (nodePacket instanceof ServerNode) {
                    var rand = new Random();
                    var clientNode = new ClientNode(rand.nextInt(200), rand.nextInt(150));
                    clientNode.absorb(nodePacket);
                    rootNodes.add(clientNode);
                }
            } else
                node.absorb(nodePacket);
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This will attempt to find the node based upon the
     *
     * @param toCheck the node to find
     * @return returns the found node based upon the passed node.
     */
    private INode findNode(INode toCheck) {
        for (var root : rootNodes) {
            var foundNode = root.findNode(toCheck);
            if (toCheck.matches(foundNode))
                return foundNode;
        }
        return null;
    }

    public void onServerNode(ServerNode nodePacket, NetworkEvent.Context ctx) {
        onNodeSync(nodePacket, ctx);
    }

    public void onClientNode(ClientNode nodePacket, NetworkEvent.Context ctx) {
        onNodeSync(nodePacket, ctx);
    }

    /**
     * This will update the transfer data.
     *
     * @param packet the packet to use to update the transfer data
     * @param ctx    the ctx
     */
    public void onTransferUpdate(TransferUpdate packet, NetworkEvent.Context ctx) {
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER && packet.getPos().equals(this.pos)) {
            //The client updated the transfer operation via the gui
            this.transferData.setOperation(packet.getDirection(), packet.getTransfer(), packet.getOperation());
            var conns = getConnectionsFor(connection -> connection.getSide() == packet.getDirection());
            if (conns != null)
                for (var connRef : conns) {
                    var conn = connRef.getAcquire();
                    conn.setIo(packet.getOperation());
                }
            sync();
            ctx.setPacketHandled(true);
        }
    }

    /**
     * This will sync the transferData to the client
     */
    private void sync() {
        markDirty();
        this.getWorld().notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), Constants.BlockFlags.NOTIFY_NEIGHBORS | Constants.BlockFlags.BLOCK_UPDATE);
    }

    /**
     * Called 20 times per second on the client and server
     */
    @Override public void tick() {
//        if (!world.isRemote) {
//            if (tick >= connTimer) {
//                checkConnections();
//                tick = 0;
//            }
//            removeNext();
//            processItems();
//            processEnergy();
//            tick++;
//        }
    }

    /**
     * This will check to make sure all of the connections are still valid
     */
    private void checkConnections() {
        System.out.println(connections.size());
        for (var conn : connections) {
            var correctMode = transferData.getOperation(conn.side, conn.mode);
            if (conn.io == IOMode.NONE || correctMode == IOMode.NONE) {
                System.out.println("Here");
                if (!removable.contains(conn))
                    removable.add(conn);
            }
        }
    }

    /**
     * This will remove the next connection that has an operation of none.
     */
    private void removeNext() {
        var next = removable.poll();
        if (next != null) {
            System.out.println("Removing connection at " + next.getPos().getCoordinatesAsString());
            this.connections.remove(next);
            sync();
        }
    }

    /**
     * This will process all of the input connection
     */
    private void processItems() {
        var itemInputs = getConnectionsFor(connection -> connection.io == IOMode.EXTRACT && connection.mode == TransferMode.ITEMS);
        var itemOutputs = getConnectionsFor(connection -> connection.io == IOMode.INSERT && connection.mode == TransferMode.ITEMS);
        itemInputs.forEach(inputRef -> {
            var input = inputRef.getAcquire();
            var inputHandlerOptional = input.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, world);
            if (inputHandlerOptional.isPresent()) {
                var inputHandler = inputHandlerOptional.resolve().get();
                itemOutputs.forEach(outputRef -> {
                    var output = outputRef.getAcquire();
                    var outputHandlerOptional = output.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, world);
                    if (outputHandlerOptional.isPresent()) {
                        var outputHandler = outputHandlerOptional.resolve().get();
                        distributeItems(inputHandler, outputHandler);
                    }
                });
            }
        });
    }

    /**
     * This will process all of the input connection
     */
    private void processEnergy() {
        var energyInputs = getConnectionsFor(connection -> connection.io == IOMode.EXTRACT && connection.mode == TransferMode.ENERGY);
        var energyOutputs = getConnectionsFor(connection -> connection.io == IOMode.INSERT && connection.mode == TransferMode.ENERGY);
        energyInputs.forEach(inputRef -> {
            var input = inputRef.getAcquire();
            var inputHandlerOptional = input.getCapability(CapabilityEnergy.ENERGY, world);
            if (inputHandlerOptional.isPresent()) {
                var inputHandler = inputHandlerOptional.resolve().get();
                energyOutputs.forEach(outputRef -> {
                    var output = outputRef.getAcquire();
                    var outputHandlerOptional = output.getCapability(CapabilityEnergy.ENERGY, world);
                    if (outputHandlerOptional.isPresent()) {
                        var outputHandler = outputHandlerOptional.resolve().get();
                        distributeEnergy(inputHandler, outputHandler);
                    }
                });
            }
        });
    }

    /**
     * This should distribute items into the output
     *
     * @param input  the input handler
     * @param output the output handler
     */
    private void distributeEnergy(IEnergyStorage input, IEnergyStorage output) {
        if (input.canExtract() && output.canReceive()) {
            var outputEnergy = input.extractEnergy(rfPerTick, false);
            var leftOver = output.receiveEnergy(outputEnergy, false);
            if (leftOver != 0) {
                input.receiveEnergy(leftOver, false);
            }
        }
    }

    /**
     * This should distribute items into the output
     *
     * @param input  the input handler
     * @param output the output handler
     */
    private void distributeItems(IItemHandler input, IItemHandler output) {
//        System.out.println("Distributing items!");
        for (var i = 0; i < input.getSlots(); i++) {
            var toExtract = input.extractItem(i, itemsPerTick, false);
            if (toExtract.getItem() != Items.AIR) {
                var leftOverInserted = ItemHandlerHelper.insertItem(output, toExtract, false);
                if (leftOverInserted.getItem() != Items.AIR) {
                    var leftOver = ItemHandlerHelper.insertItem(input, leftOverInserted, false);
                    if (leftOver.getItem() != Items.AIR)
                        System.out.println(leftOver);
                } else
                    break;
            }
        }
    }


    /**
     * Gets all of the connections for the given mode
     *
     * @param predicate the mode to get connections for
     * @return all connections with mode
     */
    public List<AtomicReference<Connection>> getConnectionsFor(Predicate<Connection> predicate) {
        var conns = new ArrayList<AtomicReference<Connection>>();
        for (var connection : connections) {
            if (predicate.test(connection))
                conns.add(new AtomicReference<>(connection));
        }
        return conns;
    }


    /**
     * Removes all the tile data from the world, also used for de initialization.
     */
    @Override public void remove() {
        super.remove();
        Network.unsubscribe(this); //Can't forget to unsubscribe or it can cause a memory leak.
    }

    /**
     * This will read our transfer data
     *
     * @param state the state of the block
     * @param tag   the tag to read from
     */
    @Override public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.transferData.read(tag);
        readConnections(tag);
    }

    /**
     * This will read all of the connection from the nbt tag
     *
     * @param tag the tag to read from
     */
    private void readConnections(CompoundNBT tag) {
        var count = tag.getInt("conn_count");
        connections.clear();
        for (var i = 0; i < count; i++) {
            var connection = new Connection();
            connection.read(tag.getCompound("conn_" + i));
            connections.add(connection);
        }
    }

    /**
     * This will write our current transfer data
     *
     * @param tag the tag
     */
    @Override public CompoundNBT write(CompoundNBT tag) {
        CompoundNBT nbt = super.write(tag);
        nbt = this.transferData.write(nbt);
        nbt = writeConnection(nbt);
        return nbt;
    }

    /**
     * This will write the connection to nbt
     *
     * @param tag the tag
     */
    private CompoundNBT writeConnection(CompoundNBT tag) {
        tag.putInt("conn_count", this.connections.size());
        var index = new AtomicInteger(0);
        this.connections.forEach(connection -> {
            var conn = connection.write(new CompoundNBT());
            tag.put("conn_" + index.getAndIncrement(), conn);
        });
        return tag;
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable @Override public SUpdateTileEntityPacket getUpdatePacket() {
        var nbt = new CompoundNBT();
        nbt = this.write(nbt);
        return new SUpdateTileEntityPacket(this.pos, 1, nbt);
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside
     */
    @Override public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    /**
     * this method gets called on the client when it receives the packet that was sent in the previous method
     */
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.read(getBlockState(), packet.getNbtCompound());
    }

    /**
     * Gets the capbility. This is used for allowing easy accesses between other mods that use item handlers,
     * energy handlers etc.
     */
    @Nonnull @Override public <
            T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }


    public TransferData getTransferData() {
        return transferData;
    }

    /**
     * Stores some connection data for a given side
     */
    public static final class Connection implements IPacket {
        private Direction side, linkedSide;
        private BlockPos pos, linkedPos;
        private IOMode io;
        private TransferMode mode;

        public Connection() {}

        public Connection(Direction side, Direction linkedSide, BlockPos pos, BlockPos linkedPos, IOMode io, TransferMode mode) {
            this.side = side;
            this.linkedSide = linkedSide;
            this.pos = pos;
            this.linkedPos = linkedPos;
            this.io = io;
            this.mode = mode;
        }

        /**
         * This will allow you to read your packet to a compound
         *
         * @param tag the compound to read from
         */
        @Override public void read(CompoundNBT tag) {
            this.side = Direction.values()[(tag.getInt("side"))];
            this.linkedSide = Direction.values()[(tag.getInt("link_side"))];
            this.pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
            this.linkedPos = new BlockPos(tag.getInt("link_x"), tag.getInt("link_y"), tag.getInt("link_z"));
            this.io = IOMode.valueOf(tag.getString("io"));
            this.mode = TransferMode.valueOf(tag.getString("mode"));
        }

        /**
         * This will allow you to write your packet to a compound
         *
         * @param tag the compound to write to
         * @return the passed compound instance
         */
        @Override public CompoundNBT write(CompoundNBT tag) {
            tag.putInt("side", this.side.ordinal());
            tag.putInt("link_side", this.linkedSide.ordinal());
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            tag.putInt("link_x", linkedPos.getX());
            tag.putInt("link_y", linkedPos.getY());
            tag.putInt("link_z", linkedPos.getZ());
            tag.putString("io", io.name());
            tag.putString("mode", mode.name());
            return tag;
        }

        public Direction getSide() {
            return side;
        }

        public Direction getLinkedSide() {
            return linkedSide;
        }

        public BlockPos getPos() {
            return pos;
        }

        public BlockPos getLinkedPos() {
            return linkedPos;
        }

        public IOMode getIo() {
            return io;
        }

        public TransferMode getMode() {
            return mode;
        }

        public void setSide(Direction side) {
            this.side = side;
        }

        public void setLinkedSide(Direction linkedSide) {
            this.linkedSide = linkedSide;
        }

        public void setPos(BlockPos pos) {
            this.pos = pos;
        }

        public void setLinkedPos(BlockPos linkedPos) {
            this.linkedPos = linkedPos;
        }

        public void setIo(IOMode io) {
            this.io = io;
        }

        public void setMode(TransferMode mode) {
            this.mode = mode;
        }

        /**
         * Gets the capability from the connection
         *
         * @return
         */
        public <T> LazyOptional<T> getCapability(Capability<T> capability, World world) {
            var tile = world.getTileEntity(this.linkedPos);
            if (tile != null) {
                var cap = tile.getCapability(capability, this.linkedSide);
                return cap;
            }
            return LazyOptional.empty();
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Connection that = (Connection) o;
            return side == that.side && linkedSide == that.linkedSide && Objects.equals(pos, that.pos) && Objects.equals(linkedPos, that.linkedPos) && io == that.io && mode == that.mode;
        }

        @Override public int hashCode() {
            return Objects.hash(side, linkedSide, pos, linkedPos, io, mode);
        }

        @Override public String toString() {
            return "Linked pos: " + linkedPos.getCoordinatesAsString() + ", local side: " + getSide().name().toLowerCase() + ", io: " + io.name().toLowerCase() + ", mode: " + mode.name().toLowerCase();
        }
    }
}
