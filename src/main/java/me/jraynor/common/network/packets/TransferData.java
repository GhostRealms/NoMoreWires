package me.jraynor.common.network.packets;

import me.jraynor.common.data.IOMode;
import me.jraynor.common.data.TransferMode;
import me.jraynor.common.network.IPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class can be converted to and from a compound nbt.
 * It is used to sync data from the Utility screen to the client tile and vice versus.
 */
public class TransferData implements IPacket {
    private final Map<Direction, Map<TransferMode, IOMode>> data = new ConcurrentHashMap<>();

    public TransferData() {
        reset();
    }

    /**
     * This will fill the data with empty data
     * after first clearing the original data
     */
    private void reset() {
        data.clear();
        for (Direction d : Direction.values()) {
            data.put(d, new HashMap<>());
            data.get(d).put(TransferMode.ITEMS, IOMode.NONE);
            data.get(d).put(TransferMode.ENERGY, IOMode.NONE);
            data.get(d).put(TransferMode.FLUID, IOMode.NONE);
        }
    }

    /**
     * This will get all of the input/output state for a given transfer type on the given side.
     *
     * @param direction the direction to get the transfer type for.
     * @param type      the type of transfer to check for
     * @return the current state for the given direction/type
     */
    public IOMode getOperation(Direction direction, TransferMode type) {
        Map<TransferMode, IOMode> state = data.get(direction);
        if (state == null) return IOMode.NONE;
        if (!state.containsKey(type)) return IOMode.NONE;
        return state.get(type);
    }


    /**
     * This will set the state for the given direction/transfer type
     *
     * @param direction    the direction set the state for
     * @param transferMode the transfer type to set the state for
     * @param ioType       the io type to set the state to
     */
    public void setOperation(Direction direction, TransferMode transferMode, IOMode ioType) {
        if (!data.containsKey(direction)) data.put(direction, new HashMap<>());
        data.get(direction).put(transferMode, ioType);
    }

    /**
     * This will get all of the operations for the given transfer type.
     * It will output a map with the direction as key, and the IOMode
     * as the value.
     *
     * @param type the type of transfer to get all operations for.
     * @return a map containing all of the operations per side.
     */
    public Map<Direction, IOMode> getOperationsForTransfer(TransferMode type) {
        Map<Direction, IOMode> output = new HashMap<>();
        this.data.forEach((direction, map) -> {
            IOMode operation = map.get(type);
            output.put(direction, operation);
        });
        return output;
    }

    /**
     * This will set all of the operations with the given transfer type. All sides with the given type
     * will be set according to the the operations
     *
     * @param type      the type of transfer to set for
     * @param operation the operation to set to
     */
    public void setOperationsForTransfer(TransferMode type, IOMode operation) {
        data.forEach((direction, map) -> map.put(type, operation));
    }

    /**
     * This will allow you to read your packet to a compound
     *
     * @param tag the compound to read from
     */
    @Override public void read(CompoundNBT tag) {
        this.data.clear();
        for (var direction : Direction.values()) {
            var mapData = convertIntArrayToMap(tag.getIntArray(direction.name()));
            this.data.put(direction, mapData);
        }
    }

    /**
     * This will allow you to write your packet to a compound
     *
     * @param tag the compound to write to
     * @return the passed compound instance
     */
    @Override public CompoundNBT write(CompoundNBT tag) {
        this.data.forEach((direction, map) -> tag.putIntArray(direction.name(), convertMapToIntArray(map)));
        return tag;
    }

    /**
     * @return an array containg ints relative to the map
     */
    private int[] convertMapToIntArray(Map<TransferMode, IOMode> map) {
        var array = new int[TransferMode.values().length];
        map.forEach((transferType, operation) -> array[transferType.ordinal()] = operation.ordinal());
        return array;
    }

    /**
     * This will convert the int array back into a map
     *
     * @param array the array to convert
     * @return the new map
     */
    private Map<TransferMode, IOMode> convertIntArrayToMap(int[] array) {
        var map = new HashMap<TransferMode, IOMode>();
        for (var i = 0; i < array.length; i++) {
            var transfer = TransferMode.values()[i];
            var operation = IOMode.values()[array[i]];
            map.put(transfer, operation);
        }
        return map;
    }

    /**
     * This will basically clone from other set of data
     *
     * @param other
     */
    public void consume(TransferData other) {
        this.data.clear();
        this.data.putAll(other.data);
    }


//    public void setPos(BlockPos pos) {
//        this.pos = pos;
//    }
//
//    public BlockPos getPos() {
//        return pos;
//    }

//    @Override public String toString() {
//        return "TransferData{" +
//                "pos=" + pos +
//                ", data=" + data +
//                '}';
//    }
}

