package me.jraynor.api.node;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceArrayMap;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.link.ILink;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.util.NodeType;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.hud.IInputEvents;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.client.render.api.hud.ITransform;
import me.jraynor.client.render.renderer.screens.SingularityScreen;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.AddLink;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is used for simple data storage of the stuff
 */
public abstract class ClientNode extends AbstractRenderer implements INode, ITextureHolder, IAbsorbable, ITransform, IInputEvents, IRenderer2d {
    /*==================== Render related =====================*/
    @Getter protected int width = 16, height = 16;
    protected boolean dragging = false;
    @Getter protected int x, y;
    protected int offsetX = 0, offsetY = 0;
    protected boolean shiftDown = false;
    protected boolean linking = false;
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();
    /*==================== INode2 related =====================*/
    @Getter
    @Setter
    private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter
    @Setter
    private Optional<UUID> uuid = Optional.empty();
    @Getter private final List<ITextProperties> textCache = new ArrayList<>();
    @Setter protected NodeManager manager;

    public ClientNode() {
        super(RenderType.SCREEN);
    }

    @Override
    public void tick() {
        super.render();
        if (isHovered()) {
            textCache.clear();
            makeTooltips(textCache);
        }

        var collidesWith = manager.getAllNodes()
                .values()
                .stream()
                .filter(this::overlaps)
                .map(iNode -> (ClientNode) iNode)
                .collect(Collectors.toList());
        collidesWith.forEach(clientNode -> {
            var offset = findOffset(clientNode);
            offsetX += offset.getFirst();
            offsetY += offset.getSecond();

        });
    }

    private Pair<Integer, Integer> findOffset(ClientNode other) {
        var node = (ClientNode) other;
        var offsetX = 0;
        var offsetY = 0;
        var x1 = node.getRelX();
        var y1 = node.getRelY();
        var mx1 = node.getMaxX();
        var my1 = node.getMaxY();

        var x2 = getRelX();
        var y2 = getRelY();
        var mx2 = getMaxX();
        var my2 = getMaxY();
        // If one rectangle is on left side of other
        if (x1 >= mx2 || x2 >= mx1) {
            offsetX += 16;
        }

        // If one rectangle is above other
        if (y1 <= my2 || y2 <= my1) {
            offsetY += 16;
        }
        return new Pair<>(offsetX, offsetY);
    }

    @Override
    public void render() {
        super.render();
        if (parent != null) {
            if (parent instanceof SingularityScreen) {
                var screen = (SingularityScreen) parent;
                if (dragging && !shiftDown) {
                    drawLine(getRelX() + (getWidth() / 2), (getRelY() + getHeight() / 2), ctx().getMouseX(), ctx().getMouseY(), 3, new Vector3i(255, 255, 255));
                } else if (dragging) {
                    this.x = Math.min(Math.max((ctx().getMouseX() - offsetX) - screen.getX(), 0), screen.getWidth() - getWidth());
                    this.y = Math.min(Math.max((ctx().getMouseY() - offsetY) - screen.getY(), 0), screen.getHeight() - getHeight());
                }

                if (getTo().isPresent()) {
                    var toNode = manager.getAllNodes().get(getTo().get());
                    if (toNode instanceof ClientNode) {
                        var clientNode = (ClientNode) toNode;
                        drawLine(getRelX() + (getWidth() / 2), (getRelY() + getHeight() / 2), clientNode.getRelX() + clientNode.getWidth() / 2, clientNode.getRelY() + clientNode.getHeight() / 2, 3, new Vector3i(255, 255, 255));
                    }
                }
                bindTexture("bg");
                drawTexture("bg", getRelX(), getRelY(), 0, 205, 16, 16);
            }
        }
    }

    /**
     * This will make the tool tips
     *
     * @param text the text to append to
     */
    protected abstract void makeTooltips(List<ITextProperties> text);

    /**
     * This will be passed via the parent to all of the children of this type.
     * its called when there's some kind of mouse event
     *
     * @param event the passed event
     */
    @Override
    public void onMouse(InputEvent.MouseInputEvent event) {
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                //Left click
                if (isHovered()) {
                    dragging = true;
                    this.offsetX = ctx().getMouseX() - getRelX();
                    this.offsetY = ctx().getMouseY() - getRelY();
                }
            } else if (event.getAction() == GLFW.GLFW_RELEASE && dragging) {
                dragging = false;
                this.offsetX = 0;
                this.offsetY = 0;
                if (!shiftDown) {
                    //TODO add a link
                    if (manager != null) {
                        manager.getAllNodes().values().forEach(iNode -> {
                            if (iNode instanceof ClientNode) {
                                var clientNode = (ClientNode) iNode;
                                if (clientNode.isHovered()) {
                                    onLink(iNode);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * This  is called when a  new link is made
     *
     * @param toAdd link to add
     */
    private void onLink(INode toAdd) {
        Network.sendToServer(new AddLink(this.getUuid().get(), toAdd.getUuid().get()));
    }

    /**
     * This will fire a recuivse catch for the on key method inside the nodel
     *
     * @param event the key event
     */
    @Override
    public void onKey(InputEvent.KeyInputEvent event) {
        if (event.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                shiftDown = true;
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                shiftDown = false;
            }
        }
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

    /**
     * Checks to see if this is overlapping with any other
     */
    public boolean overlaps(INode other) {
        if (!(other instanceof ClientNode)) return false;
        var node = (ClientNode) other;
        var x1 = node.getRelX();
        var y1 = node.getRelY();
        var mx1 = node.getMaxX();
        var my1 = node.getMaxY();

        var x2 = getRelX();
        var y2 = getRelY();
        var mx2 = getMaxX();
        var my2 = getMaxY();
        // If one rectangle is on left side of other
        if (x1 >= mx2 || x2 >= mx1) {
            return false;
        }

        // If one rectangle is above other
        if (y1 <= my2 || y2 <= my1) {
            return false;
        }
        return true;
    }
}
