package me.jraynor.client.render.renderer.screens;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.jraynor.api.manager.NodeController;
import me.jraynor.api.node.ClientNode;
import me.jraynor.client.render.api.AbstractRenderer;
import me.jraynor.client.render.api.AbstractScreenRenderer;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITextureHolder;
import me.jraynor.client.render.api.hud.ITransform;
import me.jraynor.common.tiles.SingularityTile;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This is the main screen for the singularity
 */
public class SingularityScreen extends AbstractScreenRenderer implements ITextureHolder, IRenderer2d, ITransform {
    @Getter private int y = 20, width = 256, height = 205;
    @Getter private final BlockPos pos;
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();

    public SingularityScreen(BlockPos pos) {
        super(new TranslationTextComponent("screen.nmw.singularity"));
        this.pos = pos;
        subscribe();
    }


    /**
     * This will be called whenever the renderer starts.
     */
    @Override
    public void initialize() {
        addTexture("bg", "textures/gui/bg.png", 256, 256);
    }

    /**
     * This will loop over all of the root nodes inside the tile.
     * It will check to see if they're client nodes, and if they are it'll render them.
     */
    @Override
    public void tick() {
        super.tick();
        //If the tile is broken externally we should close the screen
        if (getTile() == null) {
            closeScreen();
            return;
        }

        //This will prepare each of the client nodes
        forEachNodeFiltered(clientNode -> {
            clientNode.setParent(this);
            clientNode.fromParent(this);
            clientNode.tryInitialize();
        }, clientNode -> !clientNode.isEnabled() || !clientNode.isInitialized() || clientNode.getParent() == null);

        //We tick each of the enabled and initialed client nodes
        forEachNodeFiltered(ClientNode::tick, AbstractRenderer::isInitialized);
    }


    /**
     * Here we want render all of the children
     */
    @Override
    public void render() {
        super.render();
        drawBackground();
        controller().forEachAs(ClientNode.class, ClientNode::drawBackground); //Render each of the
        controller().forEachAs(ClientNode.class, ClientNode::render); //Render each of the
        controller().forEachAs(ClientNode.class, ClientNode::drawForeground); //Render each of the
        controller().forEachAs(ClientNode.class, clientNode -> {
            if (clientNode.isHovered() && !clientNode.getMenu().isEnabled()) //Only show the tool tip if the context menu is active
                clientNode.drawToolTip(clientNode.getTextCache());
        });
        controller().forEachAs(ClientNode.class, ClientNode::drawContextMenu);
    }

    @Override public void closeScreen() {
        if (getTile() != null)
            getTile().sync();
        super.closeScreen();
    }

    /**
     * This will render the background
     */
    private void drawBackground() {
        bindTexture("bg");
        drawTexture("bg", getX(), getY(), 0, 0, getWidth(), getHeight());
    }

    /**
     * This will pass to each of the children the key input.
     */
    @SubscribeEvent public void onKeyInput(InputEvent.KeyInputEvent event) {
        forEachNodeFiltered(clientNode -> clientNode.onKey(event), AbstractRenderer::isInitialized);
    }

    /**
     * This will pass the mouse input to each of the nodes that can receive it
     *
     * @param event the mouse event
     */
    @SubscribeEvent public void onOnInput(InputEvent.MouseInputEvent event) {
        forEachNodeFiltered(clientNode -> clientNode.onMouse(event), AbstractRenderer::isInitialized);
    }


    /**
     * This will get all of the nodes that are client nodes and iterate them
     *
     * @param consumer the callback
     */
    protected void forEachNodeFiltered(Consumer<ClientNode> consumer, Predicate<ClientNode> predicate) {
        controller().forEachAs(ClientNode.class, clientNode -> {
            if (predicate.test(clientNode))
                consumer.accept(clientNode);
        });
    }


    /**
     * @return returns the computed x. This will be useed for bounds checking.
     */
    public int getX() {
        return (ctx().getWindow().getScaledWidth() - this.width) / 2;
    }

    /**
     * @return the utility tile entity at the given position
     */
    public SingularityTile getTile() {
        if (ctx().getWorld() == null) return null;
        var tile = ctx().getWorld().getTileEntity(pos);
        if (tile == null) return null;
        if (!(tile instanceof SingularityTile))
            return null;
        return (SingularityTile) ctx().getWorld().getTileEntity(pos);
    }

    /**
     * @return the node manager
     */
    private NodeController controller() {
        if (getTile() == null) {
            return null;
        }
        return getTile().getContainer();
    }

}
