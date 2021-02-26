package me.jraynor.client.render.renderer.screens;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.jraynor.api.link.LinkClient;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.node.ClientNode;
import me.jraynor.common.tiles.SingularityTile;
import me.jraynor.old.INode2;
import me.jraynor.client.render.api.AbstractScreenRenderer;
import me.jraynor.client.render.api.hud.IRenderer2d;
import me.jraynor.client.render.api.hud.ITransform;
import me.jraynor.client.render.api.hud.ITextureHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.function.Consumer;

/**
 * This is the main screen for the singularity
 */
public class SingularityScreen extends AbstractScreenRenderer implements ITextureHolder, IRenderer2d, ITransform {
    @Getter private int y = 20, width = 256, height = 205;
    @Getter private final BlockPos pos;
    @Getter private final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> textures = new HashMap<>();
    private final Set<INode2> hovered = new HashSet<>();

    public SingularityScreen(BlockPos pos) {
        super(new TranslationTextComponent("screen.nmw.singularity"));
        this.pos = pos;
        subscribe();
    }

    /**
     * This will be called whenever the renderer starts.
     */
    @Override public void initialize() {
        addTexture("bg", "textures/gui/bg.png", 256, 256);
    }

    /**
     * This will loop over all of the root nodes inside the tile.
     * It will check to see if they're client nodes, and if they are it'll render them.
     */
    @Override public void tick() {
        super.tick();
        /**
         * This will iterate over all of the nodes
         * on the client
         */
        if (getManager() != null)
            getManager().getAllNodes().forEach((uuid, iNode) -> {
                if (iNode instanceof ClientNode) {
                    var clientNode = (ClientNode) iNode;
                    clientNode.setParent(this);
                    clientNode.fromParent(this);
                    clientNode.tryInitialize();
                    clientNode.tick();
                }
            });
    }

    /**
     * Here we add some subscribers for the root noddes.
     */
    @Override protected void subscribe() {
        super.subscribe();
        Consumer<InputEvent.KeyInputEvent> onKey = (event) -> {
            if (getManager() != null)
                getManager().getAllNodes().forEach((uuid, iNode) -> {
                    if (iNode instanceof ClientNode) {
                        var clientNode = (ClientNode) iNode;
                        clientNode.onKey(event);
                    }
                });
        };
        MinecraftForge.EVENT_BUS.addListener(onKey);
        Consumer<InputEvent.MouseInputEvent> onMouse = (event) -> {
            if (getManager() != null)
                getManager().getAllNodes().forEach((uuid, iNode) -> {
                    if (iNode instanceof ClientNode) {
                        var clientNode = (ClientNode) iNode;
                        clientNode.onMouse(event);
                    }
                });
        };
        MinecraftForge.EVENT_BUS.addListener(onMouse);
    }

    /**
     * This will recursivley get everything hovered
     */
    public Collection<INode2> getHovered() {
        return hovered;
    }

    /**
     * Here we want render all of the children
     */
    @Override public void render() {
        drawBackground();
        if (getManager() != null) {
            getManager().getAllNodes().forEach((uuid, iNode) -> {
                if (iNode instanceof ClientNode) {
                    var clientNode = (ClientNode) iNode;
                    clientNode.render();
                }
            });
        }
        super.render();
    }

    /**
     * This will render the background
     */
    private void drawBackground() {
        bindTexture("bg");
        drawTexture("bg", getX(), getY(), 0, 0, getWidth(), getHeight());
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
        return (SingularityTile) ctx().getWorld().getTileEntity(pos);
    }

    /**
     * @return the node manager
     */
    private NodeManager getManager() {
        if (getTile() == null) return null;
        return getTile().getManager();
    }

}
