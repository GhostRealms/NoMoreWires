package me.jraynor.client.render.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An interface for holding child {@link IRenderer}
 */
public interface IParentable extends IRenderer {

    Map<RendererType, List<IRenderer>> getChildren();

    /**
     * This will add a renderer
     *
     * @param renderer the renderer to add
     */
    default void addChild(AbstractRenderer renderer) {
        getChildren().computeIfAbsent(renderer.getType(), type -> new ArrayList<>()).add(renderer);
    }

    /**
     * This will tick all of the children
     */
    default void tickChildren(RendererType type) {
        getChildren().get(type).forEach(IRenderer::tick);
    }

    /**
     * This will render the given render type of children
     *
     * @param type the type to render
     */
    default void renderChildren(RendererType type) {
        getChildren().get(type).forEach(renderer -> {
            if (renderer.isValid())
                renderer.render();

        });
    }

    /**
     * This will pass this classes data to the children
     */
    default void passChildren() {
        this.getChildren().forEach((rendererType, renderers) -> renderers.forEach(child -> {
            child.ifIs(IAbsorbable.class, absorb -> absorb.fromParent(this));
            child.ifIs(IParentable.class, IParentable::passChildren);
        }));
    }
}
