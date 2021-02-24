package me.jraynor.client.ui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.jraynor.NoMoreWires;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * A tabbed panel is a container gui that can render a set of children, but it's self
 * is meant to be rendered in a parent. It has a location offset for the panel button,
 * and has bounds for the panel its self.
 */
@OnlyIn(Dist.CLIENT)
public class TabbedPanel extends Widget {
    private static final ResourceLocation ICONS = new ResourceLocation(NoMoreWires.MOD_ID, "textures/gui/icons.png");
    private int x, y, width, height, tabX, tabY;

    public TabbedPanel(int x, int y, int width, int height) {
        super(x, y, width, height, StringTextComponent.EMPTY);
    }

    /**
     * This is the main rendering loop. We first render the panel's background, then
     * the tabs on the left, and right, then the children
     */
    @Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }


}
