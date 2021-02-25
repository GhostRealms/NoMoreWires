package me.jraynor.client.render.api.hud;

import com.mojang.datafixers.util.Pair;
import me.jraynor.NoMoreWires;
import me.jraynor.client.render.api.core.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * This allows us to have resources accessible
 */
public interface ITextureHolder extends IRenderer {
    Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> getTextures();

    default ResourceLocation getResource(String name) {
        return getTextures().get(name).getFirst();
    }

    default Pair<Integer, Integer> getSize(String name) {
        return getTextures().get(name).getSecond();
    }

    /**
     * This will add a resource that can be bound to easily
     *
     * @param name the name of the resource
     * @param path the path to the resource, the mod id will be included.
     */
    default void addTexture(String name, String path, int width, int height) {
        getTextures().put(name, new Pair<>(new ResourceLocation(NoMoreWires.MOD_ID, path), new Pair<>(width, height)));
    }

    /**
     * This will bind the texture resource for rendering
     *
     * @param name the name of the resource
     */
    default void bindTexture(String name) {
        var resource = getResource(name);
        if (resource != null)
            Minecraft.getInstance().getTextureManager().bindTexture(resource);
    }

}
