package me.jraynor.api.menu.action;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.menu.NodeMenu;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IInputEvents;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITextRenderer;
import me.jraynor.client.render.api.hud.ITransform;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

/**
 * This is the base for all of the menu actions.
 */
public abstract class MenuAction extends AbstractRenderer implements ITransform, IInputEvents, ITextRenderer, IRenderer2d {
    @Setter @Getter protected ITextProperties text;
    @Getter private boolean absolute = true;
    @Setter protected NodeMenu menu;
    @Setter protected int index;
    private boolean press = false;
    private boolean clicked = false;

    public MenuAction(ITextProperties text) {
        super(RenderType.SCREEN);
        this.text = text;
    }


    /**
     * This will check to see if we're being hovered
     */
    @Override public void tick() {
        if (isHovered() && press && !clicked) {
            onClick();
            clicked = true;
        }
    }

    /**
     * This will render the background for the action
     */
    public void renderBackground() {
        var x = getX() - menu.getPadding() / 2;
        var y = getY() - 2;
        if (isHovered() && !press)
            drawQuad(x, y, getWidth(), getHeight(), 0xff302f2f);
        else
            drawQuad(x, y, getWidth(), getHeight(), 0xff000000);
        if (press && isHovered())
            drawQuad(x, y, getWidth(), getHeight(), 0xff525252);

        //Simply draws the text of the menu action
        drawString(getText().getString(), getX(), getY(), 0xffffff);
    }

    /**
     * Accounts for our corrections using the padding.
     *
     * @return true if hovered
     */
    @Override public boolean isHovered() {
        var x = getX() - menu.getPadding() / 2;
        var y = getY() - 2;
        var mx = ctx().getMouseX();
        var my = ctx().getMouseY();
        return mx >= x
                && mx <= x + getWidth()
                && my >= y
                && my <= y + getHeight();
    }

    /**
     * This will simply update the press field.
     *
     * @param event the passed event
     */
    @Override public void onMouse(InputEvent.MouseInputEvent event) {
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            switch (event.getAction()) {
                case GLFW.GLFW_PRESS -> {
                    this.press = true;
                    this.clicked = false;
                }
                case GLFW.GLFW_RELEASE -> this.press = false;
            }
        }
    }

    /**
     * This will render the text
     */
    @Override public void render() {

    }

    public int getX() {
        return menu.getX() + menu.getPadding() / 2;
    }

    @Override public int getY() {
        return menu.getY() + (index * getHeight()) + 2;
    }

    /**
     * Called whenever the menu action is clicked
     */
    public abstract void onClick();

    /**
     * @return the computed width of a menu action based upon the text
     */
    @Override public int getWidth() {
        if (text == null)
            return 0;
        return ctx().getFont().getStringPropertyWidth(text) + menu.getPadding();
    }

    /**
     * The height of a menu action
     *
     * @return always 20
     */
    @Override public int getHeight() {
        return 12;
    }


}
