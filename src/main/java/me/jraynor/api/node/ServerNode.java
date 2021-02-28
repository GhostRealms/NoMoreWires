package me.jraynor.api.node;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

/**
 * This is used for simple data storage of the stuff
 */
public abstract class ServerNode implements INode {
    @Getter @Setter private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();
    @Getter @Setter private int x, y;
    @Getter @Setter private BlockPos tilePos;

}
