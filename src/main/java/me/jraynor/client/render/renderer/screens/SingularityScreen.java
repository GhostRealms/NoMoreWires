package me.jraynor.client.render.renderer.screens;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.jraynor.client.render.api.AbstractScreenRenderer;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.client.render.renderer.screens.elements.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the main screen for the singularity
 */
public class SingularityScreen extends AbstractScreenRenderer implements ITextureHolder {
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();

    protected SingularityScreen() {
        super(new TranslationTextComponent("screen.nmw.singularity"));
    }


    /**
     * This will be called whenever the renderer starts.
     */
    @Override public void initialize() {
        addTexture("bg", "textures/gui/bg.png", 256, 256);
    }

    /**
     * Here we want render all of the children
     */
    @Override public void render() {
        super.render();
    }

    @Override public void tick() {
        super.tick();
    }
}
