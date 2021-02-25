package me.jraynor.client.render.renderer.screens.elements;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.core.IAbsorbable;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.client.render.api.util.RendererType;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * This allows us to have buttons with our own system. It's much easier.
 */
public class Button extends AbstractRenderer implements ITextureHolder, IAbsorbable {
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();

    public Button() {
        super(RendererType.SCREEN);
    }

    /**
     * This will be called whenever the renderer starts.
     */
    @Override public void initialize() {
        
    }

    /**
     * This can be ignored and implemented in other ways,
     * but all renderers will have a generic render method
     */
    @Override public void render() {

    }

    /**
     * This will be called every single tick from the tick event
     */
    @Override public void tick() {

    }
}
