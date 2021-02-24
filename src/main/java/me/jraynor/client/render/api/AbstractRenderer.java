package me.jraynor.client.render.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The abstract renderer that allows for many different types of rendering
 * via a streamlined interface that uses lombok to be clean.
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderer implements IRenderer {
    @Getter @Setter protected World world;
    @Getter @Setter protected WorldRenderer context;
    @Getter @Setter protected MatrixStack stack;
    @Getter @Setter protected Matrix4f proMatrix;
    @Getter @Setter protected float partialTicks;
    @Getter @Setter protected RendererType type;
    @Getter @Setter protected boolean enabled;
    @Getter @Setter protected PlayerEntity player;
    @Getter @Setter private RenderGameOverlayEvent.ElementType element;
    @Getter @Setter private MainWindow window;
    @Getter @Setter private FontRenderer font;

    public AbstractRenderer(RendererType type) {
        this.type = type;
        this.enabled = true;
    }

    public AbstractRenderer() {
        this(RendererType.HUD);
    }

}
