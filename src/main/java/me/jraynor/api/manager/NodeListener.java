package me.jraynor.api.manager;

import lombok.extern.log4j.Log4j2;
import me.jraynor.api.packet.AddLink;
import me.jraynor.api.packet.AddNode;
import me.jraynor.api.packet.RemoveNode;
import me.jraynor.common.network.Network;
import me.jraynor.core.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * This will listen for the events and packets and update the node world data accordingly
 */
@Log4j2
public class NodeListener {
    private static NodeListener INSTANCE;

    private NodeListener() {
        Network.subscribe(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * This method is what will end up linking the blocks. It is called from the synthesizer item.
     */
    public void onNodeRemove(RemoveNode packet, NetworkEvent.Context ctx) {

    }

    /**
     * This is called when the remove event is sent across the local context
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onNodeRemoveEvent(RemoveNode event) {

    }

    /**
     * This is called when the node is added to the world
     */
    public void onAddNodePacket(AddNode packet, NetworkEvent.Context ctx) {
    }

    /**
     * This is called when the add event is sent across the local context
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onAddNodeEvent(AddNode event) {
    }

    /**
     * This is called when the node is added to the world
     */
    public void onAddLink(AddLink packet, NetworkEvent.Context ctx) {
    }

    /**
     * This is called when the add event is sent across the local context
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onAddLinkEvent(AddLink event) {

    }

    /**
     * @return the instance or creates a new one
     */
    public static NodeListener getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NodeListener();
        return INSTANCE;
    }
}
