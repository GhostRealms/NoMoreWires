package me.jraynor.old;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.common.data.IOMode;
import me.jraynor.common.data.TransferMode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * This represents the node on the server. It will be save to the tile in the future,
 * and be used in the tile to send/receive items
 */
public class ServerNode implements INode2 {
    @Getter @Setter private BlockPos pos;
    @Getter @Setter private Direction dir;
    @Getter @Setter private TransferMode mode;
    @Getter private Class<? extends INode2> nodeType = ServerNode.class;
    @Getter @Setter private Map<IOMode, Collection<INode2>> linkedNodes = Maps.newHashMap();
    @Getter @Setter private boolean written = false;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        INode2 other = (INode2) o;
        return other.matches(this);
    }

    @Override public String toString() {
        return getString();
    }

    @Override public int hashCode() {
        return Objects.hash(dir, mode, pos, linkedNodes, nodeType);
    }
}
