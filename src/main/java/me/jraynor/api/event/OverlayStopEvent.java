package me.jraynor.api.event;

import lombok.Getter;
import me.jraynor.api.link.ILink;
import me.jraynor.api.node.INode;
import net.minecraftforge.eventbus.api.Event;

/**
 * This will be called when the user starts highlighting a link
 */
public class OverlayStopEvent extends Event {
    @Getter private ILink node;

    public OverlayStopEvent(ILink node) {
        this.node = node;
    }
}
