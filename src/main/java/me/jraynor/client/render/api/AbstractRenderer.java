package me.jraynor.client.render.api;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.client.render.api.core.IRenderer;
import me.jraynor.client.render.api.core.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The abstract renderer that allows for many different types of rendering
 * via a streamlined interface that uses lombok to be clean.
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderer implements IRenderer {
    @Getter @Setter protected RenderType type;
    @Getter @Setter protected IRenderer parent;
    @Getter @Setter protected boolean enabled = true;
    @Getter @Setter private boolean initialized = false;

    public AbstractRenderer(RenderType type) {
        this.type = type;
    }


}
