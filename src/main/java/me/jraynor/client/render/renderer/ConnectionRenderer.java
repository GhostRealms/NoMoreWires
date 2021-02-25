package me.jraynor.client.render.renderer;

import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.util.RendererType;
import me.jraynor.client.render.api.world.IRenderer3d;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.LinkComplete;
import me.jraynor.common.network.packets.LinkReset;
import me.jraynor.common.network.packets.LinkStart;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * This will render the connections between blocks in the world
 */
public class ConnectionRenderer extends AbstractRenderer implements IAbsorbable, IRenderer3d {
    public ConnectionRenderer() {
        super(RendererType.WORLD);
        Network.subscribe(this);
    }

    /**
     * This will be called from the server to the client when a link start
     *
     * @param packet the link start packet
     */
    public void onLinkStart(LinkStart packet, NetworkEvent.Context context) {
        System.out.println("link started");
    }

    /**
     * This will be called from the server to the client when a link start
     *
     * @param packet the link start packet
     */
    public void onLinkReset(LinkReset packet, NetworkEvent.Context context) {
        System.out.println("link reset");
    }

    /**
     * This will be called from the server to the client when a link start
     *
     * @param packet the link start packet
     */
    public void onLinkComplete(LinkComplete packet, NetworkEvent.Context context) {
        System.out.println("link complete");

    }

    /**
     * This will be called 20 times per second and is used to update elements on the screen.
     * because we don't have children we don't need the super
     */
    @Override public void tick() {
    }

    /**
     * Here we want to render our connections between the various machines
     */
    @Override public void render() {
        stack.push();
        drawLine(new Vector3d(player.getPosX(), player.getPosY(), player.getPosZ()), new Vector3d(player.getPosX(), player.getPosY() + 100, player.getPosZ()), 255, 255, 255, 255);
        stack.pop();
    }


}
