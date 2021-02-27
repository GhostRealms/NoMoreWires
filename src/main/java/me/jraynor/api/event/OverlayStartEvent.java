package me.jraynor.api.event;

import lombok.Getter;
import me.jraynor.api.link.ILink;
import me.jraynor.api.node.INode;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event;

/**
 * This will be called when the user starts highlighting a link
 */
public class OverlayStartEvent extends Event {
   @Getter private ILink node;

    public OverlayStartEvent(ILink node) {
        this.node = node;
    }
}
