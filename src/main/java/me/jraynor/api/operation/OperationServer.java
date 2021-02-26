package me.jraynor.api.operation;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.node.INode;
import me.jraynor.api.util.NodeType;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This represents a renderable operation that can be applied to and from nodes
 */
public abstract class OperationServer implements IOperation {
    @Getter @Setter private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();
    @Setter protected NodeManager manager;

    /**
     * This is called when the operation is executed on the server
     */
    @Override public void execute() {}

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
