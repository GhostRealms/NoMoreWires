package me.jraynor.api.operation.insert;

import lombok.Getter;
import me.jraynor.api.operation.OperationClient;
import me.jraynor.api.util.NodeType;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * This represents a renderable operation that can be applied to and from nodes
 */
public class InsertOperationClient extends OperationClient {
    @Getter private NodeType nodeType = NodeType.INSERT_OP;


    @Override
    public void render() {
        super.render();
    }

    /**
     * This will make the tool tips
     *
     * @param text the text to append to
     */
    @Override
    protected void makeTooltips(List<ITextProperties> text) {
        text.add(new StringTextComponent(TextFormatting.GOLD + "Insert"));
        text.add(new StringTextComponent(TextFormatting.STRIKETHROUGH + "                                "));
        text.add(new StringTextComponent(TextFormatting.DARK_GRAY + "  filter: " + TextFormatting.DARK_PURPLE + "n/a"));
    }
}
