package me.jraynor.client.render.api.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.jraynor.client.render.MasterRenderer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Acts as a base for other interfaces to build off of.
 */
public interface IRenderer {

    /**
     * @return the context instance
     */
    default RenderContext ctx() {
        return RenderContext.INSTANCE;
    }

    boolean isEnabled();

    void setEnabled(boolean enabled);

    RenderType getType();

    void setType(RenderType type);

    @Nullable IRenderer getParent();

    void setParent(IRenderer parent);

    /**
     * This will be called whenever the renderer starts.
     */
    default void initialize() {}

    /**
     * This can be ignored and implemented in other ways,
     * but all renderers will have a generic render method
     */
    default void render() {}

    /**
     * This will be called every single tick from the tick event
     */
    default void tick() {}


    void setInitialized(boolean initialized);

    boolean isInitialized();

    /**
     * This will attempt to initialize if not already initialized
     */
    default void tryInitialize() {
        if (!isInitialized()) {
            initialize();
            setInitialized(true);
        }
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
