package me.jraynor.client.render.api.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An interface for holding child {@link IRenderer}
 */
public interface IContainer extends IRenderer {

    Map<RenderType, List<IRenderer>> getChildren();

    /**
     * This will add a renderer
     *
     * @param renderer the renderer to add
     */
    default <T extends IRenderer> T addChild(T renderer) {
        getChildren().computeIfAbsent(renderer.getType(), type -> new ArrayList<>()).add(renderer);
        return renderer;
    }

    /**
     * This will tick all of the children
     */
    default void tickChildren(RenderType type) {
        if (getChildren().containsKey(type))
            getChildren().get(type).forEach(IRenderer::tick);
    }

    /**
     * This will render the given render type of children
     *
     * @param type the type to render
     */
    default void renderChildren(RenderType type) {
        if (getChildren().containsKey(type))
            getChildren().get(type).forEach(renderer -> {
                if (renderer.ctx().isValid(type))
                    renderer.render();
            });
    }

    /**
     * This will pass this classes data to the children
     */
    default void passChildren() {
        this.getChildren().forEach((rendererType, renderers) -> renderers.forEach(child -> {
            child.ifIs(IAbsorbable.class, absorb -> absorb.fromParent(this));
            child.ifIs(IContainer.class, IContainer::passChildren);
        }));
    }
}
