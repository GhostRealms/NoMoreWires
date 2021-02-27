package me.jraynor.api.link;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.node.INode;
import me.jraynor.api.node.ServerNode;
import me.jraynor.api.util.NodeType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

/**
 * This is what is rendered on screen for the user
 */
public class LinkServer extends ServerNode implements ILink {
    @Getter @Setter private Direction face;
    @Getter @Setter private BlockPos pos;
    @Getter private NodeType nodeType = NodeType.LINK;
    @Setter @Getter protected NodeManager manager;




    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getUuid().isEmpty()) return false;
        if (o instanceof INode) {
            var node = (INode) o;
            if (node.getUuid().isEmpty()) return false;
            return node.getUuid().get().equals(this.getUuid().get());
        }
        return false;
    }

    @Override public int hashCode() {
        return Objects.hash(getUuid());
    }
}
