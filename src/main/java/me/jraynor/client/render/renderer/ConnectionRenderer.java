package me.jraynor.client.render.renderer;

import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
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
        super(RenderType.WORLD);
    }

    /**
     * This will subscribe us to the event network system.
     */
    @Override public void initialize() {
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
     * Here we want to render our connections between the various machines
     */
    @Override public void render() {
        ctx().getStack().push();
        drawLine(new Vector3d(ctx().getPlayer().getPosX(), ctx().getPlayer().getPosY(), ctx().getPlayer().getPosZ()), new Vector3d(ctx().getPlayer().getPosX(), ctx().getPlayer().getPosY() + 100, ctx().getPlayer().getPosZ()), 255, 255, 255, 255);
        ctx().getStack().pop();
    }


}
