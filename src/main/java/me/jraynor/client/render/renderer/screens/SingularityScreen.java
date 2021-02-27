package me.jraynor.client.render.renderer.screens;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import me.jraynor.api.link.LinkClient;
import me.jraynor.api.manager.NodeManager;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.operation.extract.ExtractOperationClient;
import me.jraynor.api.operation.insert.InsertOperationClient;
import me.jraynor.common.network.Network;
import me.jraynor.common.network.packets.AddLink;
import me.jraynor.common.network.packets.AddNode;
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
import org.lwjgl.glfw.GLFW;

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
    private long last = System.currentTimeMillis();

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
        forEachNode(clientNode -> {
            clientNode.setParent(this);
            clientNode.fromParent(this);
            clientNode.tryInitialize();
            clientNode.tick();
        });
    }

    /**
     * Here we add some subscribers for the root noddes.
     */
    @Override
    protected void subscribe() {
        super.subscribe();
        Consumer<InputEvent.KeyInputEvent> onKey = (event) -> {
            onKey(event);
            forEachNode(clientNode -> clientNode.onKey(event));
        };
        MinecraftForge.EVENT_BUS.addListener(onKey);
        Consumer<InputEvent.MouseInputEvent> onMouse = (event) -> forEachNode(clientNode -> clientNode.onMouse(event));
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
    @Override
    public void render() {
        super.render();
        drawBackground();
        forEachNode(ClientNode::drawBackground); //Render each of the
        forEachNode(ClientNode::render); //Render each of the
        forEachNode(ClientNode::drawForeground); //Render each of the
        forEachNode(clientNode -> {
            if (clientNode.isHovered() && !clientNode.getMenu().isEnabled()) //Only show the tool tip if the context menu is active
                clientNode.drawToolTip(clientNode.getTextCache());
        });
        forEachNode(ClientNode::drawContextMenu);
    }

    /**
     * This will add insert/extract nodes when the key is pressed
     */
    private void onKey(InputEvent.KeyInputEvent event) {
        var now = System.currentTimeMillis();
        if (now - last > 500) {
            last = System.currentTimeMillis();
            if ((event.getKey() == GLFW.GLFW_KEY_E || event.getKey() == GLFW.GLFW_KEY_I) && event.getAction() == GLFW.GLFW_RELEASE) {
                var node = event.getKey() == GLFW.GLFW_KEY_E ? new ExtractOperationClient() : new InsertOperationClient();
                var rand = new Random();
                node.setX(rand.nextInt(200) + 16);
                node.setY(rand.nextInt(150) + 16);
                node.setUuid(Optional.of(UUID.randomUUID()));
                node.setManager(getManager());
                node.setParent(this);
                getManager().add(node);
                System.out.println("Added new extract operation on client");
                Network.sendToServer(new AddNode(node));
            }
        }
    }


    /**
     * This will get all of the nodes that are client nodes and iterate them
     *
     * @param consumer the callback
     */
    protected void forEachNode(Consumer<ClientNode> consumer) {
        if (getManager() != null) {
            for (var iNode : getManager().getAllNodes().values()) {
                if (iNode instanceof ClientNode) {
                    var node = (ClientNode) iNode;
                    consumer.accept(node);
                }
            }
        }
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
        var tile = ctx().getWorld().getTileEntity(pos);
        if (tile == null) return null;
        if (!(tile instanceof SingularityTile))
            return null;
        return (SingularityTile) ctx().getWorld().getTileEntity(pos);
    }

    @Override public void closeScreen() {
        MinecraftForge.EVENT_BUS.unregister(this);
        super.closeScreen();
    }

    /**
     * @return the node manager
     */
    private NodeManager getManager() {
        if (getTile() == null) {
            MinecraftForge.EVENT_BUS.unregister(this);
            return null;
        }
        return getTile().getManager();
    }

}
