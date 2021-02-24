package me.jraynor.client.ui;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.jraynor.NoMoreWires;
import me.jraynor.common.data.TransferMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TransferStateButton extends Button {
    private final ResourceLocation resourceLocation;
    private final int yTexStart;
    private final int textureWidth;
    private final int textureHeight;
    private Consumer<TransferMode> onStateChange;
    private TransferMode state;
    private Direction direction;

    public TransferStateButton(int xIn, int yIn, Consumer<TransferMode> onStateChange, ITooltip tooltip) {
        this(xIn, yIn, 10, 10, 0, 0, 10, null, tooltip);
        this.direction = direction;
        this.onStateChange = onStateChange;
        this.state = TransferMode.ITEMS;
    }

    public TransferStateButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int xDiffTexIn, IPressable onPressIn, ITooltip tooltip) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, xDiffTexIn, new ResourceLocation(NoMoreWires.MOD_ID, "textures/gui/icons.png"), 128, 36, onPressIn, tooltip);
    }

    public TransferStateButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int xDiffTexIn, ResourceLocation resourceLocationIn, int p_i51135_9_, int p_i51135_10_, IPressable onPressIn, ITooltip tooltip) {
        this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, xDiffTexIn, resourceLocationIn, p_i51135_9_, p_i51135_10_, onPressIn, StringTextComponent.EMPTY, tooltip);
    }

    public TransferStateButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int xDiffTexIn, ResourceLocation resourceLocation, int textureWidth, int textureHeight, IPressable onPress, ITextComponent title, ITooltip tooltip) {
        this(x, y, width, height, xTexStart, yTexStart, xDiffTexIn, resourceLocation, textureWidth, textureHeight, onPress, tooltip, title);
    }

    public TransferStateButton(int p_i244513_1_, int p_i244513_2_, int p_i244513_3_, int p_i244513_4_, int p_i244513_5_, int p_i244513_6_, int p_i244513_7_, ResourceLocation p_i244513_8_, int p_i244513_9_, int p_i244513_10_, IPressable p_i244513_11_, ITooltip p_i244513_12_, ITextComponent p_i244513_13_) {
        super(p_i244513_1_, p_i244513_2_, p_i244513_3_, p_i244513_4_, p_i244513_13_, p_i244513_11_, p_i244513_12_);
        this.textureWidth = p_i244513_9_;
        this.textureHeight = p_i244513_10_;
        this.yTexStart = p_i244513_6_;
        this.resourceLocation = p_i244513_8_;
    }

    /**
     * We want our own onPress event for updating the current state
     */
    @Override public void onPress() {
        if (onStateChange != null) {
            this.state = state.next();
            onStateChange.accept(state);
        }
    }


    /**
     * Here we render the button based upon the current state
     */
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.enableDepthTest();
        blit(matrixStack, this.x, this.y, (float) state.xGuiStart, (float) yTexStart, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    public TransferMode getState() {
        return state;
    }
}
