package me.jraynor.client.render.renderer.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITextRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.core.RenderType;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.common.items.SynthesizerItem;
import me.jraynor.core.ModRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders an overlay with the connection information for the player
 */
public class ConnectionOverlay extends AbstractRenderer implements IAbsorbable, ITextRenderer, ITextureHolder, IRenderer2d {
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();

    public ConnectionOverlay() {
        super(RenderType.HUD);
        addTexture("overlay", "textures/gui/overlay.png", 128, 128);
    }

    /**
     * This will be called whenever the renderer starts.
     */
    @Override public void initialize() {
        addTexture("overlay", "textures/gui/overlay.png", 128, 128);
    }

    /**
     * Here we want to check to see if the player is holding the {@link me.jraynor.common.items.SynthesizerItem}
     * to decided whether or not to be enabled
     */
    @Override public void tick() {
        if (ctx().getPlayer() != null) {
            var itemStack = ctx().getPlayer().getHeldItemMainhand();
            setEnabled(itemStack.getItem() == ModRegistry.SYNTHESIZER_ITEM.get());
        }
    }


    /**
     * Here we should be able to render the
     */
    @Override public void render() {
        var itemStack = ctx().getPlayer().getHeldItemMainhand();
        if (itemStack.getItem() == ModRegistry.SYNTHESIZER_ITEM.get()) {
            drawBackground(itemStack);
            drawForeground(itemStack);
        }
    }

    /**
     * This will draw the background
     */
    private void drawBackground(ItemStack itemStack) {
        drawTexture("overlay", 10, 10, 1, 40, 127, 47);
    }

    /**
     * This will draw all the text and other related things
     */
    private void drawForeground(ItemStack itemStack) {
        var syn = (SynthesizerItem) itemStack.getItem();
        var ioMode = syn.getOperation();
        var ioType = syn.getTransferMode();
        drawString("io mode:", 20, 15, TextFormatting.WHITE.getColor());
        drawString(ioMode.name().toLowerCase(), 65, 15, ioMode.color);
        drawString("io type:", 20, 33, TextFormatting.WHITE.getColor());
        drawString(ioType.name().toLowerCase(), 65, 33, ioType.color);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.75f, 0.75f, 0.75f);
        drawString("(LMB)", (int) ((20 * 0.25f) + 20), (int) ((27 * 0.25f) + 27), TextFormatting.GRAY.getColor());
        drawString("(Shift+LMB)", (int) ((20 * 0.25f) + 20), (int) ((46 * 0.25f) + 46), TextFormatting.GRAY.getColor());
        RenderSystem.popMatrix();
    }
}
