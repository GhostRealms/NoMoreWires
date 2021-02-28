package me.jraynor.api.operation;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.manager.NodeHolder;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

/**
 * This represents a renderable operation that can be applied to and from nodes
 */
public abstract class OperationServer implements IOperation {
    @Getter @Setter private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();
    @Getter @Setter protected NodeHolder manager;
    @Getter @Setter private int x, y;
    @Getter @Setter private BlockPos tilePos;

    /**
     * This is called when the operation is executed on the server
     */
    @Override public void execute() {}


}
