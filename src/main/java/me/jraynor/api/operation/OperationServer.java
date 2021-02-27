package me.jraynor.api.operation;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.node.INode;
import me.jraynor.api.util.NodeType;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * This represents a renderable operation that can be applied to and from nodes
 */
public abstract class OperationServer implements IOperation {
    @Getter @Setter private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();
    @Getter@Setter protected NodeManager manager;
    @Getter @Setter private int x, y;

    /**
     * This is called when the operation is executed on the server
     */
    @Override public void execute() {}


}
