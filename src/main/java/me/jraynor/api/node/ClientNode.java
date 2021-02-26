package me.jraynor.api.node;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.util.NodeType;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.hud.IInputEvents;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.client.render.api.hud.ITransform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    @Getter @Setter private Optional<UUID> from = Optional.empty(), to = Optional.empty();
    @Getter @Setter private Optional<UUID> uuid = Optional.empty();
    @Getter private NodeType nodeType = null;

    public ClientNode() {
        super(RenderType.SCREEN);
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

                }
            }
        }
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

}
