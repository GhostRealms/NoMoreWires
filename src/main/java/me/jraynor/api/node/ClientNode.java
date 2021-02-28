package me.jraynor.api.node;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.manager.NodeController;
import me.jraynor.api.menu.NodeMenu;
import me.jraynor.api.manager.NodeHolder;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.core.IInputEvents;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.client.render.api.hud.ITransform;
import me.jraynor.client.render.renderer.screens.SingularityScreen;
import me.jraynor.common.network.Network;
import me.jraynor.api.packet.AddLink;
import me.jraynor.common.tiles.SingularityTile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is used for simple data storage of the stuff
 */
public abstract class ClientNode extends AbstractRenderer implements INode, ITextureHolder, IAbsorbable, ITransform, IInputEvents, IRenderer2d {
    /*==================== Render related =====================*/
    @Getter protected int width = 16, height = 16;
    protected boolean dragging = false;
    @Getter @Setter private int x, y;
    protected int offsetX = 0, offsetY = 0;
    protected boolean shiftDown = false;
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();
    /*==================== INode related =====================*/
    @Getter @Setter private Optional<UUID> to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();
    @Getter private final List<ITextProperties> textCache = new ArrayList<>();
    @Getter @Setter protected BlockPos tilePos;
    /*========================================================*/
    @Getter protected NodeMenu menu = new NodeMenu(this);


    public ClientNode() {
        super(RenderType.SCREEN);
    }


    /**
     * This will initialize our menu
     */
    @Override public void initialize() {
        super.initialize();
        menu.setNode(this);
        menu.setParent(this);
        menu.tryInitialize();
        menu.setEnabled(false); //Only enabled when right clicked
    }

    @Override
    public void tick() {
        super.tick();
        if (isHovered()) {
            textCache.clear();
            makeTooltips(textCache);
        }
        if (getMenu().isEnabled())
            getMenu().tick();
    }

    /**
     * Allows for the last bottom to be drawn
     */
    public void drawBackground() {
        bindTexture("bg");
        drawTexture("bg", getRelX(), getRelY(), 0, 205, 16, 16);
    }


    /**
     * The middle renderering
     */
    @Override
    public void render() {
        super.render();
        if (parent != null) {
            if (parent instanceof SingularityScreen) {
                var screen = (SingularityScreen) parent;
                if (dragging && !shiftDown) {
                    drawLine(getRelX() + (getWidth() / 2), (getRelY() + getHeight() / 2), ctx().getMouseX(), ctx().getMouseY(), new Vector3i(255, 50, 25), new Vector3i(150, 50, 25));
                } else if (dragging) {
                    this.x = Math.min(Math.max((ctx().getMouseX() - offsetX) - screen.getX(), 0), screen.getWidth() - getWidth());
                    this.y = Math.min(Math.max((ctx().getMouseY() - offsetY) - screen.getY(), 0), screen.getHeight() - getHeight());
                }
                var clientNode = getController().getNode(ClientNode.class, getTo().orElse(null));
                if (clientNode != null)
                    drawLine(getRelX() + (getWidth() / 2), (getRelY() + getHeight() / 2), clientNode.getRelX() + clientNode.getWidth() / 2, clientNode.getRelY() + clientNode.getHeight() / 2, new Vector3i(255, 255, 255));
            }
        }
    }

    /**
     * Allows for the last top to be drawn
     */
    public void drawForeground() {}

    /**
     * This is used for drawing the background
     */
    public void drawContextMenu() {
        if (getMenu().isEnabled()) {
            getMenu().setNode(this);
            getMenu().render();
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
        getMenu().onMouse(event);
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                if (isHovered()) {
                    getMenu().setEnabled(true);
                }
            }
        }
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                //Left click
                if (isHovered()) {
                    dragging = true;
                    this.offsetX = ctx().getMouseX() - getRelX();
                    this.offsetY = ctx().getMouseY() - getRelY();
                } else {
                    if (!getMenu().childrenHovered())
                        getMenu().setEnabled(false);
                }
            } else if (event.getAction() == GLFW.GLFW_RELEASE && dragging) {
                dragging = false;
                this.offsetX = 0;
                this.offsetY = 0;
                if (!shiftDown) {
                    var controller = getController();
                    //TODO add a link
                    if (controller != null) {
                        var linked = new AtomicBoolean(false);
                        controller.forEachAs(ClientNode.class, node -> {
                            if (node.isHovered() && !linked.get()) {
                                onLink(node);
                                linked.set(true);
                            }
                        });
                        if (!linked.get()) {
                            onUnlink();
                        }
                    }
                }
            }
        }
    }

    /**
     * This will get the manager using the tile positon
     *
     * @return the node controller
     */
    @OnlyIn(Dist.CLIENT)
    private NodeController getController() {
        var tile = Minecraft.getInstance().world.getTileEntity(tilePos);
        if (tile instanceof SingularityTile)
            return ((SingularityTile) tile).getContainer();
        return null;
    }


    /**
     * This can be overridden but the super must be called.
     */
    protected void onUnlink() {
//        Network.sendToServer(new RemoveLink());
    }

    /**
     * This  is called when a  new link is made
     *
     * @param toAdd link to add
     */
    private void onLink(INode toAdd) {
//        Network.sendToServer(new AddLink(tilePos, this.getUuid().get(), toAdd.getUuid().get()));
//        Network.sendToServer(new RequestSync(tilePos, Side.CLIENT));
    }

    /**
     * This will fire a recuivse catch for the on key method inside the nodel
     *
     * @param event the key event
     */
    @Override
    public void onKey(InputEvent.KeyInputEvent event) {
        getMenu().onKey(event);
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
