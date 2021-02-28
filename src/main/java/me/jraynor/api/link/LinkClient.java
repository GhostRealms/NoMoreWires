package me.jraynor.api.link;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.menu.action.HighlightAction;
import me.jraynor.api.menu.action.RemoveAction;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.util.NodeType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent;

import java.util.List;

/**
 * This represent real blocks in the the world that can be extracted from/ inserted into
 */
public class LinkClient extends ClientNode implements ILink {
    @Getter @Setter private Direction face;
    @Getter @Setter private BlockPos pos;
    @Getter private NodeType nodeType = NodeType.LINK;

    /**
     * This will add a remove action to the menu
     */
    @Override public void initialize() {
        menu.add(new RemoveAction()).add(new HighlightAction());
        super.initialize();
    }

    /**
     * This will make the tool tips
     *
     * @param text the text to append to
     */
    @Override protected void makeTooltips(List<ITextProperties> text) {
        text.add(new StringTextComponent(TextFormatting.GOLD + "Linker"));
        text.add(new StringTextComponent(TextFormatting.STRIKETHROUGH + "                                "));
        text.add(new StringTextComponent(TextFormatting.DARK_GRAY + "  block: " + TextFormatting.DARK_PURPLE + getLinkedBlockName()));
        text.add(new StringTextComponent(TextFormatting.DARK_GRAY + "  pos: " + TextFormatting.DARK_PURPLE + pos.getCoordinatesAsString()));
        text.add(new StringTextComponent(TextFormatting.DARK_GRAY + "  face: " + TextFormatting.DARK_PURPLE + face.getName2()));
    }

    /**
     * @return the linked block's name.
     */
    private String getLinkedBlockName() {
        if (getPos() != null) {
            var state = ctx().getWorld().getBlockState(pos);
            return state.getBlock().getTranslatedName().getString();
        }
        return "N/A";
    }


    /**
     * Renders our block item on top
     */
    @Override public void drawForeground() {
        if (getPos() != null)
            drawItem(new ItemStack(ctx().getWorld().getBlockState(pos).getBlock()), getRelX(), getRelY());
    }


}
