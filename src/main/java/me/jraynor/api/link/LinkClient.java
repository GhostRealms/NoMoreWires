package me.jraynor.api.link;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.node.INode;
import me.jraynor.api.operation.extract.ExtractOperationClient;
import me.jraynor.api.operation.extract.ExtractOperationServer;
import me.jraynor.api.operation.insert.InsertOperationServer;
import me.jraynor.api.util.NodeType;
import me.jraynor.client.render.renderer.screens.SingularityScreen;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.AddNode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * This represent real blocks in the the world that can be extracted from/ inserted into
 */
public class LinkClient extends ClientNode implements ILink {
    @Getter
    @Setter
    private Direction face;
    @Getter
    @Setter
    private BlockPos pos;
    @Getter private NodeType nodeType = NodeType.LINK;


    @Override
    public void initialize() {
        var rand = new Random();
        this.x = rand.nextInt(200);
        this.y = rand.nextInt(150);
    }

    @Override
    public void tick() {
        super.tick();
    }

    /**
     * This will make the tool tips
     *
     * @param text the text to append to
     */
    @Override
    protected void makeTooltips(List<ITextProperties> text) {
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
        var state = ctx().getWorld().getBlockState(pos);
        return state.getBlock().getTranslatedName().getString();
    }

    /**
     * This will render the link client
     */
    @Override
    public void render() {
        super.render();
        drawItem(new ItemStack(ctx().getWorld().getBlockState(pos).getBlock()), getRelX(), getRelY());
    }

    /**
     * This will be passed via the parent to all of the children of this type.
     * its called when there's some kind of mouse event
     *
     * @param event the passed event
     */
    @Override
    public void onMouse(InputEvent.MouseInputEvent event) {
        super.onMouse(event);
    }

    /**
     * This will be passed via the parent to all of the children of this type.
     * its called when there's some kind of mouse event
     *
     * @param event the passed event
     */
    @Override
    public void onKey(InputEvent.KeyInputEvent event) {
        super.onKey(event);
        //Add new extract node that is linked to this.
        if (event.getKey() == GLFW.GLFW_KEY_E && event.getAction() == GLFW.GLFW_RELEASE) {
            Network.sendToServer(new AddNode(new ExtractOperationServer()));
        } else if (event.getKey() == GLFW.GLFW_KEY_I && event.getAction() == GLFW.GLFW_RELEASE) {
            Network.sendToServer(new AddNode(new InsertOperationServer()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getUuid().isEmpty()) return false;
        if (o instanceof INode) {
            var node = (INode) o;
            if (node.getUuid().isEmpty()) return false;
            return node.getUuid().get().equals(this.getUuid().get());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
