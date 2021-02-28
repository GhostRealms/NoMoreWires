package me.jraynor.api.menu.action;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.jraynor.api.menu.NodeMenu;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.node.INode;
import me.jraynor.api.packet.RemoveNode;
import me.jraynor.api.packet.RequestSync;
import me.jraynor.common.network.Network;
import me.jraynor.core.Side;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

/**
 * This will completely delete the node. It is first deleted on client,
 * then synchronized to the server
 */
@OnlyIn(Dist.CLIENT)
@Log4j2 public class RemoveAction extends MenuAction {
    public RemoveAction() {
        super(new StringTextComponent("Remove"));
    }

    /**
     * Called whenever the menu action is clicked
     */
    @Override public void onClick() {
        log.info("Removing node from client (for ui update): " + menu.getNode().getUuid().get().toString());
//        menu.getNode().getManager().remove(menu.getNode().getUuid().get());
//        Network.sendToServer(new RemoveNode(getTilePos(), menu.getNode().getUuid().get()));
//        Network.sendToServer(new RequestSync(getTilePos(), Side.CLIENT));
        MinecraftForge.EVENT_BUS.post(new RemoveNode(getTilePos(), menu.getNode().getUuid().get()));
    }
}
