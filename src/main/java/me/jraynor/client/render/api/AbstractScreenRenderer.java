package me.jraynor.client.render.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.core.IParentable;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.render.api.util.RendererType;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This allows us to use the renderer features in a screen
 */
public abstract class AbstractScreenRenderer extends Screen implements IRenderer, IParentable {
    @Getter @Setter protected World world;
    @Getter @Setter protected WorldRenderer context;
    @Getter @Setter protected MatrixStack stack;
    @Getter @Setter protected Matrix4f proMatrix;
    @Getter @Setter protected float partialTicks;
    @Getter @Setter protected RendererType type;
    @Getter @Setter protected boolean enabled;
    @Getter @Setter protected PlayerEntity player;
    @Getter @Setter protected RenderGameOverlayEvent.ElementType element;
    @Getter @Setter protected MainWindow window;
    @Getter @Setter protected FontRenderer font;
    @Getter @Setter protected int mouseX, mouseY;
    @Getter private final Map<RendererType, List<IRenderer>> children = new HashMap<>();

    protected AbstractScreenRenderer(ITextComponent titleIn) {
        super(titleIn);
    }

    /**
     * This will be called whenever the screen starts,
     * and we'll forward it to the initilaize method.
     */
    @Override protected void init() {
        this.initialize();
    }

    /**
     * This *should* be called from the whatever calls it from the screen. I think because
     * we're extending abstract screen it's tick function takes precedence.
     */
    @Override public void tick() {
        tickChildren(RendererType.SCREEN);
    }

    /**
     * This will render all of the children who are of the type screen
     */
    @Override public void render() {
        renderChildren(RendererType.SCREEN);
    }

    /**
     * here we want to render all of the children
     */
    @Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.world = Minecraft.getInstance().world;
        this.player = Minecraft.getInstance().player;
        this.font = Minecraft.getInstance().fontRenderer;
        this.stack = matrixStack;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
        render();
    }
}
