package me.jraynor.api.operation;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.node.INode;
import me.jraynor.api.util.NodeType;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.RenderType;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This represents a renderable operation that can be applied to and from nodes
 */
public class OperationClient extends ClientNode implements IOperation {
    @Getter @Setter private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();
    @Getter private NodeType nodeType = NodeType.OPERATION;


    /**
     * This is called when the operation is executed on the client
     */
    @Override public void execute() {

    }

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
