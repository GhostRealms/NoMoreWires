package me.jraynor.client.render.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.core.IContainer;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.hud.IInputEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This allows us to use the renderer features in a screen
 */
public abstract class AbstractScreenRenderer extends Screen implements IRenderer, IContainer {
    @Getter @Setter protected RenderType type;
    @Getter @Setter protected boolean enabled;
    @Getter private final Map<RenderType, List<IRenderer>> children = new HashMap<>();
    @Getter @Setter IRenderer parent;
    @Getter @Setter private boolean initialized = false;

    public AbstractScreenRenderer(ITextComponent titleIn) {
        super(titleIn);
    }

    /**
     * This will register all of the input events. This
     * allows them to be passed to the children
     */
    protected void subscribe() {
        /**
         * This is used to pass the event to the children
         */
        Consumer<InputEvent.MouseInputEvent> onMouse = (event) -> {
            getChildren().values().forEach(renderers -> renderers.forEach(renderer -> {
                if (renderer instanceof IInputEvents) {
                    var events = (IInputEvents) renderer;
                    events.onMouse(event);
                }
            }));
        };

        MinecraftForge.EVENT_BUS.addListener(onMouse);
        /**
         * This is used to pass the event to the children
         */
        Consumer<InputEvent.ClickInputEvent> onClick = (event) -> {
            getChildren().values().forEach(renderers -> renderers.forEach(renderer -> {
                if (renderer instanceof IInputEvents) {
                    var events = (IInputEvents) renderer;
                    events.onClick(event);
                }
            }));
        };
        MinecraftForge.EVENT_BUS.addListener(onClick);
        /**
         * This is used to pass the event to the children
         */
        Consumer<InputEvent.KeyInputEvent> onKey = (event) -> {
            getChildren().values().forEach(renderers -> renderers.forEach(renderer -> {
                if (renderer instanceof IInputEvents) {
                    var events = (IInputEvents) renderer;
                    events.onKey(event);
                }
            }));
        };
        MinecraftForge.EVENT_BUS.addListener(onKey);
        /**
         * This is used to pass the event to the children
         */
        Consumer<InputEvent.MouseScrollEvent> onScroll = (event) -> {
            getChildren().values().forEach(renderers -> renderers.forEach(renderer -> {
                if (renderer instanceof IInputEvents) {
                    var events = (IInputEvents) renderer;
                    events.onScroll(event);
                }
            }));
        };
        MinecraftForge.EVENT_BUS.addListener(onScroll);
    }


    /**
     * This will be called whenever the screen starts,
     * and we'll forward it to the initilaize method.
     */
    @Override protected void init() {
        tryInitialize();
        passChildren();
        getChildren().values().forEach(child -> {
            child.forEach(it -> {
                it.setParent(this);
                it.tryInitialize();
            });
        });
    }

    /**
     * This *should* be called from the whatever calls it from the screen. I think because
     * we're extending abstract screen it's tick function takes precedence.
     */
    @Override public void tick() {
        super.tick();
        tickChildren(RenderType.SCREEN);
    }

    /**
     * This will render all of the children who are of the type screen
     */
    @Override public void render() {
        renderChildren(RenderType.SCREEN);
    }


    /**
     * here we want to render all of the children
     */
    @Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ctx().setStack(matrixStack);
        ctx().setMouseX(mouseX);
        ctx().setMouseY(mouseY);
        ctx().setPartialTicks(partialTicks);
        render();
    }
}
