package me.jraynor.api.operation;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.menu.action.RemoveAction;
import me.jraynor.api.node.ClientNode;

import java.util.Optional;
import java.util.UUID;

/**
 * This represents a renderable operation that can be applied to and from nodes
 */
public abstract class OperationClient extends ClientNode implements IOperation {
    @Getter @Setter private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();

    /**
     * All operations should be removable
     */
    @Override public void initialize() {
        menu.add(new RemoveAction());
        super.initialize();
    }
}
