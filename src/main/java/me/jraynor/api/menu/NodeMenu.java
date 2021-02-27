package me.jraynor.api.menu;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.menu.action.MenuAction;
import me.jraynor.api.node.ClientNode;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IInputEvents;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITextRenderer;
import me.jraynor.client.render.api.hud.ITransform;
import net.minecraftforge.client.event.InputEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This will render the context menu.  It's per node and will be rendered when right clicked.
 */
public class NodeMenu extends AbstractRenderer implements ITransform, ITextRenderer, IInputEvents, IRenderer2d {
    @Getter @Setter private ClientNode node;
    @Getter private int padding = 10; //5 all around (10/2 per side)
    private final List<MenuAction> actions = new ArrayList<>();

    public NodeMenu(ClientNode active) {
        super(RenderType.SCREEN);
        this.node = active;
        setParent(active);
    }

    /**
     * This will initilazie all the actions
     */
    @Override public void initialize() {
        actions.forEach(IRenderer::initialize);
    }

    /**
     * Adds a new menu action, returns this instancel
     *
     * @param action the aciton to add
     * @return this istance
     */
    public NodeMenu add(MenuAction action) {
        action.setMenu(this);
        action.setParent(this);
        action.setIndex(actions.size());
        actions.add(action);
        return this;
    }

    /**
     * This will render the background
     */
    private void renderBackground() {
        actions.forEach(MenuAction::renderBackground);
    }

    /**
     * This will render our node menu
     */
    @Override public void render() {
        renderBackground();
        //TODO: render node background
        actions.forEach(MenuAction::render);
    }

    /**
     * This will tick all of the actions
     */
    @Override public void tick() {
        actions.forEach(MenuAction::tick);
        //TODO check for if the action is hovered and call it's onclick if so
    }

    /**
     * @return the left side the of the parent node
     */
    public int getX() {
        return node.getRelX() + node.getWidth();
    }

    /**
     * The relative y position
     *
     * @return
     */
    public int getY() {
        return node.getRelY();
    }

    /**
     * TODO: compute the max width
     *
     * @return the height
     */
    public int getWidth() {
        var maxWidth = 0;
        for (var action : actions)
            if (action.getWidth() > maxWidth)
                maxWidth = action.getWidth();
        return maxWidth;
    }

    /**
     * @return the total height where as each
     */
    public int getHeight() {
        var height = 0;
        for (var action : actions)
            height += action.getHeight();
        return height;
    }

    /**
     * Allows the user to pass the mouse event to a given class.
     *
     * @param event the mouse event
     */
    @Override public void onMouse(InputEvent.MouseInputEvent event) {
        actions.forEach(action -> action.onMouse(event));
    }

    /**
     * Allows the user to pass the key event to a given class.
     *
     * @param event the mouse event
     */
    @Override public void onKey(InputEvent.KeyInputEvent event) {
        actions.forEach(action -> action.onKey(event));
    }

    /**
     * @return true if any children are hovered
     */
    public boolean childrenHovered() {
        for (var action : actions)
            if (action.isHovered()) return true;
        return false;
    }
}
