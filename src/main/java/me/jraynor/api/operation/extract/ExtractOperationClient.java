package me.jraynor.api.operation.extract;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.menu.NodeMenu;
import me.jraynor.api.menu.action.RemoveAction;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.node.INode;
import me.jraynor.api.operation.IOperation;
import me.jraynor.api.operation.OperationClient;
import me.jraynor.api.util.NodeType;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This represents a renderable operation that can be applied to and from nodes
 */
public class ExtractOperationClient extends OperationClient {
    @Getter private NodeType nodeType = NodeType.EXTRACT_OP;

    /**
     * This will make the tool tips
     *
     * @param text the text to append to
     */
    @Override
    protected void makeTooltips(List<ITextProperties> text) {
        text.add(new StringTextComponent(TextFormatting.GOLD + "Extract"));
        text.add(new StringTextComponent(TextFormatting.STRIKETHROUGH + "                                "));
        text.add(new StringTextComponent(TextFormatting.DARK_GRAY + "  filter: " + TextFormatting.DARK_PURPLE + "n/a"));
    }
}
