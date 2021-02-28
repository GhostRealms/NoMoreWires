package me.jraynor.client.render.api;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.core.IContainer;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.render.api.core.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The abstract renderer that allows for many different types of rendering
 * via a streamlined interface that uses lombok to be clean.
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderer implements IRenderer, IContainer {
    @Getter @Setter protected RenderType type;
    @Getter @Setter protected IRenderer parent;
    @Getter @Setter protected boolean enabled = true;
    @Getter @Setter private boolean initialized = false;
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();
    @Getter private final Map<RenderType, List<IRenderer>> children = Maps.newHashMap();

    public AbstractRenderer(RenderType type) {
        this.type = type;
    }

}
