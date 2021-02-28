package me.jraynor.client.render.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.core.IContainer;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.render.api.core.RenderType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This allows us to use the renderer features in a screen
 */
public abstract class AbstractScreenRenderer extends Screen implements IRenderer, IContainer {
    @Getter @Setter protected RenderType type;
    @Getter @Setter protected boolean enabled = true;
    @Getter @Setter private boolean initialized = false;
    @Getter @Setter IRenderer parent;
    @Getter private final Map<RenderType, List<IRenderer>> children = new HashMap<>();
    private final List<Consumer<? extends Event>> subscribers = new ArrayList<>();

    public AbstractScreenRenderer(ITextComponent titleIn) {
        super(titleIn);
    }

    /**
     * When this is overridden it call the super last
     */
    public void subscribe() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * This will remove all of the subscribers in the class
     */
    public void unsubscribe() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    /**
     * This will unsubscribe the subscribers before closing the screen
     */
    @Override public void closeScreen() {
        unsubscribe();
        super.closeScreen();
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
