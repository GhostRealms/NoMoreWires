package me.jraynor.client.render.api.core;

import me.jraynor.client.render.api.core.IRenderer;
import net.minecraftforge.client.event.InputEvent;

/**
 * This allows for input callbacks from the parent. This will only be active inside the
 * screen renderer right now
 */
public interface IInputEvents extends IRenderer {
    /**
     * This will be passed via the parent to all of the children of this type.
     * its called when there's some kind of mouse event
     *
     * @param event the passed event
     */
    default void onMouse(InputEvent.MouseInputEvent event) {}


    /**
     * This is called when a key is pressed or released
     *
     * @param event the key event
     */
    default void onKey(InputEvent.KeyInputEvent event) {}

}