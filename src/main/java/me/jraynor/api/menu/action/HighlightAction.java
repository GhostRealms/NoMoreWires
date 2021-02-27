package me.jraynor.api.menu.action;

import lombok.extern.log4j.Log4j2;
import me.jraynor.api.event.OverlayStartEvent;
import me.jraynor.api.event.OverlayStopEvent;
import me.jraynor.api.link.ILink;
import me.jraynor.api.packet.RemoveNode;
import me.jraynor.common.network.Network;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * This will completely delete the node. It is first deleted on client,
 * then synchronized to the server
 */
@OnlyIn(Dist.CLIENT)
@Log4j2 public class HighlightAction extends MenuAction {
    private boolean highlighting = false;

    public HighlightAction() {
        super(new StringTextComponent("Highlight: " + TextFormatting.RED + "off"));
    }

    /**
     * Called whenever the menu action is clicked
     */
    @Override public void onClick() {
        if (!highlighting) {
            this.text = new StringTextComponent("Highlight: " + TextFormatting.GREEN + "on");
            highlighting = true;
            MinecraftForge.EVENT_BUS.post(new OverlayStartEvent((ILink) menu.getNode()));
        } else {
            this.text = new StringTextComponent("Highlight: " + TextFormatting.RED + "off");
            highlighting = false;
            MinecraftForge.EVENT_BUS.post(new OverlayStopEvent((ILink) menu.getNode()));
        }
    }
}
