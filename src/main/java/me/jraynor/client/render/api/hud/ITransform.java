package me.jraynor.client.render.api.hud;

import me.jraynor.client.render.api.core.IRenderer;

/**
 * Allows for easy storing of sizes.
 */
public interface ITransform extends IRenderer {

    int getX();

    int getY();

    int getWidth();

    int getHeight();

    /**
     * @return the max x value based upon the pos and size
     */
    default int getMaxX() {return getRelX() + getWidth();}

    /**
     * @return the max y value based upon the pos and size
     */
    default int getMaxY() {return getRelY() + getHeight();}


    /**
     * @return true if the element is hovered
     */
    default boolean isHovered() {
        var mx = ctx().getMouseX();
        var my = ctx().getMouseY();
        return mx >= getRelX()
                && mx <= getMaxX()
                && my >= getRelY()
                && my <= getMaxY();
    }

    /**
     * Computes the relative x pos
     */
    default int getRelX() {
        if (getParent() != null && getParent() instanceof ITransform) {
            var parent = (ITransform) getParent();
            return parent.getX() + getX();
        }
        return 0;
    }


    /**
     * Computes the relative x pos
     */
    default int getRelY() {
        if (getParent() != null && getParent() instanceof ITransform) {
            var parent = (ITransform) getParent();
            return parent.getY() + getY();
        }
        return 0;
    }

}
