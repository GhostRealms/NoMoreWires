package me.jraynor.client.render.api.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.jraynor.client.render.MasterRenderer;
import me.jraynor.client.render.api.util.RendererType;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.function.Consumer;

/**
 * Acts as a base for other interfaces to build off of.
 */
public interface IRenderer {

    World getWorld();

    void setWorld(World world);

    WorldRenderer getContext();

    void setContext(WorldRenderer context);

    MatrixStack getStack();

    void setStack(MatrixStack stack);

    Matrix4f getProMatrix();

    void setProMatrix(Matrix4f proMatrix);

    float getPartialTicks();

    void setPartialTicks(float partialTicks);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    RendererType getType();

    void setType(RendererType type);

    PlayerEntity getPlayer();

    void setPlayer(PlayerEntity player);

    MainWindow getWindow();

    void setWindow(MainWindow window);

    RenderGameOverlayEvent.ElementType getElement();

    void setElement(RenderGameOverlayEvent.ElementType element);

    FontRenderer getFont();

    void setFont(FontRenderer renderer);

    int getMouseX();

    void setMouseX(int x);

    int getMouseY();

    void setMouseY(int y);

    /**
     * This will be called whenever the renderer starts.
     */
    void initialize();

    /**
     * This can be ignored and implemented in other ways,
     * but all renderers will have a generic render method
     */
    void render();

    /**
     * This will be called every single tick from the tick event
     */
    void tick();

    /**
     * @return true if everything is not null
     */
    default boolean isValid() {
        if (this instanceof MasterRenderer) return true;
        if (getType() == RendererType.WORLD)
            return getWorld() != null
                    && getContext() != null
                    && getStack() != null
                    && getProMatrix() != null
                    && getPlayer() != null
                    && isEnabled();
        else if (getType() == RendererType.HUD)
            return getWorld() != null
                    && getPlayer() != null
                    && getStack() != null
                    && getWindow() != null
                    && getFont() != null
                    && isEnabled();
        else if (getType() == RendererType.SCREEN)
            return getStack() != null
                    && getMouseX() != -1
                    && getMouseY() != -1
                    && getWorld() != null
                    && isEnabled();
        return false;
    }

    /**
     * Checks to see if the current renderer is of the given type
     *
     * @param type the type to check to see that if this is
     * @return the type to check
     */
    default <T extends IRenderer> boolean is(Class<T> type) {
        return type.isAssignableFrom(this.getClass());
    }

    /**
     * casts this interface to the given type if possible otherwise returns null
     *
     * @param type the type to cast to
     * @param <T>  the generic type
     * @return this class as the give type
     */
    default <T extends IRenderer> T as(Class<T> type) {
        if (!is(type)) return null;
        return (T) this;
    }

    /**
     * Calls the consumer with the given type if possible
     *
     * @param type     the type to cast to
     * @param consumer the consuemr to call
     * @param <T>      the generic type
     */
    default <T extends IRenderer> void ifIs(Class<T> type, Consumer<T> consumer) {
        if (is(type))
            consumer.accept(as(type));
    }
}
