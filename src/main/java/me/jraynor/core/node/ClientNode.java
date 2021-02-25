package me.jraynor.core.node;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.hud.IInputEvents;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITransform;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.client.render.renderer.screens.SingularityScreen;
import me.jraynor.common.data.IOMode;
import me.jraynor.common.data.TransferMode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * A node can is represented by a button that can be input/output,
 * it is rendered as the 3d model of the block that it's linked to.
 * You can drag nodes together to connect them.
 */
public class ClientNode extends AbstractRenderer implements INode, ITextureHolder, IAbsorbable, ITransform, IInputEvents, IRenderer2d {
    /*==================== Render related =====================*/
    @Getter private int width = 16, height = 16;
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();
    private boolean dragging = false;
    @Getter private int x, y;
    private int offsetX = 0, offsetY = 0;
    private boolean shiftDown = false;
    private boolean linking = false;
    /*==================== INode related =====================*/
    @Getter @Setter private Direction dir;
    @Getter @Setter private TransferMode mode;
    @Getter @Setter private BlockPos pos;
    @Getter private final Class<? extends INode> nodeType = ClientNode.class;
    @Getter @Setter private Map<IOMode, Collection<INode>> linkedNodes = Maps.newHashMap();
    @Getter @Setter private boolean written = false;

    /*========================================================*/
    public ClientNode(int x, int y) {
        super(RenderType.SCREEN);
        this.x = x;
        this.y = y;
    }

    /**
     * Renders the client side data.
     */
    @Override public void render() {
        if (parent != null) {
            if (parent instanceof SingularityScreen) {
                var screen = (SingularityScreen) parent;
                if (dragging && !shiftDown) {
                    drawLine(getRelX(), getRelY(), ctx().getMouseX(), ctx().getMouseY(), 3, new Vector3i(255, 1, 1));
                } else if (dragging) {
                    this.x = Math.min(Math.max((ctx().getMouseX() - offsetX) - screen.getX(), 0), screen.getWidth() - getWidth());
                    this.y = Math.min(Math.max((ctx().getMouseY() - offsetY) - screen.getY(), 0), screen.getHeight() - getHeight());
                }
                drawLinks();
                bindTexture("bg");
                drawTexture("bg", getRelX(), getRelY(), 0, 205, 16, 16);
                drawItem(new ItemStack(ctx().getWorld().getBlockState(pos).getBlock()), getRelX(), getRelY());
            }
        }
    }

    /**
     * This will recursively update all of the hovered nodes
     */
    public void appendHovered(Set<INode> hovered) {
        this.getLinkedNodes().values().forEach(nodes -> nodes.forEach(iNode -> {
            if (iNode instanceof ClientNode) {
                var node = (ClientNode) iNode;
                if (node.isHovered()) hovered.add(node);
                else hovered.remove(node);
                node.appendHovered(hovered);
            }
        }));
    }

    /**
     * This will attempt to add links to the hovered node
     */
    private void addLinks(Collection<INode> hovered) {
        hovered.forEach(iNode -> {
            if (iNode instanceof ClientNode) {
                var node = (ClientNode) iNode;
                System.out.println("Found linkable node: " + node);
                addLink(IOMode.EXTRACT, node);
                if (getParent() != null) {
                    ((SingularityScreen) getParent()).getTile().syncNodes(NetworkDirection.PLAY_TO_SERVER);
                }
            }
        });
    }

    /**
     * This will draw all of our links
     */
    private void drawLinks() {
    }

    /**
     * This will be passed via the parent to all of the children of this type.
     * its called when there's some kind of mouse event
     *
     * @param event the passed event
     */
    @Override public void onMouse(InputEvent.MouseInputEvent event) {
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                //Left click
                if (isHovered()) {
                    dragging = true;
                    this.offsetX = ctx().getMouseX() - getRelX();
                    this.offsetY = ctx().getMouseY() - getRelY();
                }
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                dragging = false;
                this.offsetX = 0;
                this.offsetY = 0;
                if (!shiftDown) {
                    //TODO add a link
                    var parent = (SingularityScreen) getParent();
                    if (parent != null)
                        addLinks(parent.getHovered());
                }
            }
        }
        getLinkedNodes().values().forEach(nodes -> nodes.forEach(node -> {
            if (node instanceof ClientNode) {
                var clientNode = (ClientNode) node;
                clientNode.onMouse(event);
            }
        }));
    }

    /**
     * This will fire a recuivse catch for the on key method inside the nodel
     *
     * @param event the key event
     */
    @Override public void onKey(InputEvent.KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                shiftDown = true;
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                shiftDown = false;
            }
        }

        getLinkedNodes().values().forEach(nodes -> nodes.forEach(node -> {
            if (node instanceof ClientNode) {
                var clientNode = (ClientNode) node;
                clientNode.onKey(event);
            }
        }));
    }

    /**
     * @return true if the element is hovered
     */
    public boolean isHovered() {
        var mx = ctx().getMouseX();
        var my = ctx().getMouseY();
        return mx >= getRelX()
                && mx <= getMaxX()
                && my >= getRelY()
                && my <= getMaxY();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        INode other = (INode) o;
        return other.matches(this);
    }

    @Override public int hashCode() {
        return Objects.hash(dir, mode, pos, linkedNodes, nodeType);
    }

    @Override public String toString() {
        return getString();
    }
}
