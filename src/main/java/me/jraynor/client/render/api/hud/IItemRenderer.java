package me.jraynor.client.render.api.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jraynor.client.render.api.core.IRenderer;
import net.minecraft.item.ItemStack;

public interface IItemRenderer extends IRenderer {

    /**
     * This will render a item at the given position
     */
    default void drawItem(ItemStack itemStack, int x, int y) {
        RenderSystem.pushMatrix();
        ctx().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, x, y);
        ctx().getItemRenderer().renderItemOverlayIntoGUI(ctx().getFont(), itemStack, x, y, "");
        RenderSystem.popMatrix();
    }


    /**
     * This will render a item at the given position
     */
    default void drawItem(ItemStack itemStack, int x, int y, float xScale, float yScale) {
        RenderSystem.pushMatrix();
        ctx().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, x, y);
        ctx().getItemRenderer().renderItemOverlayIntoGUI(ctx().getFont(), itemStack, x, y, "");
        RenderSystem.popMatrix();
    }
}
