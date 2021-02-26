package me.jraynor.api.link;

import lombok.Getter;
import lombok.Setter;
import me.jraynor.api.node.ClientNode;
import me.jraynor.api.node.INode;
import me.jraynor.api.util.NodeType;
import me.jraynor.client.render.renderer.screens.SingularityScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.event.InputEvent;

import java.util.Objects;
import java.util.Random;

/**
 * This represent real blocks in the the world that can be extracted from/ inserted into
 */
public class LinkClient extends ClientNode implements ILink {
    @Getter @Setter private Direction face;
    @Getter @Setter private BlockPos pos;
    @Getter private NodeType nodeType = NodeType.LINK;

    @Override public void initialize() {
        var rand = new Random();
        this.x = rand.nextInt(200);
        this.y = rand.nextInt(150);
    }

    @Override public void tick() {
        super.tick();
    }

    /**
     * This will render the link client
     */
    @Override public void render() {
        if (parent != null) {
            if (parent instanceof SingularityScreen) {
                var screen = (SingularityScreen) parent;
                if (dragging && !shiftDown) {
                    drawLine(getRelX(), getRelY(), ctx().getMouseX(), ctx().getMouseY(), 3, new Vector3i(255, 1, 1));
                } else if (dragging) {
                    this.x = Math.min(Math.max((ctx().getMouseX() - offsetX) - screen.getX(), 0), screen.getWidth() - getWidth());
                    this.y = Math.min(Math.max((ctx().getMouseY() - offsetY) - screen.getY(), 0), screen.getHeight() - getHeight());
                }
                bindTexture("bg");
                drawTexture("bg", getRelX(), getRelY(), 0, 205, 16, 16);
//                System.out.println(pos);
//                drawItem(new ItemStack(ctx().getWorld().getBlockState(pos).getBlock()), getRelX(), getRelY());
            }
        }
    }

    /**
     * This will be passed via the parent to all of the children of this type.
     * its called when there's some kind of mouse event
     *
     * @param event the passed event
     */
    @Override public void onMouse(InputEvent.MouseInputEvent event) {
        super.onMouse(event);
    }

    /**
     * This will be passed via the parent to all of the children of this type.
     * its called when there's some kind of mouse event
     *
     * @param event the passed event
     */
    @Override public void onKey(InputEvent.KeyInputEvent event) {
        super.onKey(event);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getUuid().isEmpty()) return false;
        if (o instanceof INode) {
            var node = (INode) o;
            if (node.getUuid().isEmpty()) return false;
            return node.getUuid().get().equals(this.getUuid().get());
        }
        return false;
    }

    @Override public int hashCode() {
        return Objects.hash(getUuid());
    }
}
