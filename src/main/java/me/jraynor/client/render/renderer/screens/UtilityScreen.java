//package me.jraynor.client.render.renderer.screens;
//
//import com.google.common.collect.Lists;
//import com.mojang.blaze3d.matrix.MatrixStack;
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.datafixers.util.Pair;
//import me.jraynor.Nmw;
//import me.jraynor.client.ui.IOStateButton;
//import me.jraynor.common.data.IOMode;
//import me.jraynor.common.data.TransferMode;
//import me.jraynor.client.ui.TransferStateButton;
////import me.jraynor.common.containers.UtilityContainer;
//import me.jraynor.common.network.Network;
//import me.jraynor.common.network.packets.TransferUpdate;
//import me.jraynor.common.util.StringUtils;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.screen.inventory.ContainerScreen;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.state.properties.BlockStateProperties;
//import net.minecraft.util.Direction;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraft.util.text.TextFormatting;
//
//import java.util.*;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Consumer;
//
///**
// * This is the main gui for the utility.
// */
//public class UtilityScreen extends ContainerScreen<UtilityContainer> {
//    private ResourceLocation GUI = new ResourceLocation(Nmw.MOD_ID, "textures/gui/base.png");
//    private ItemStack renderedStack;
//    private final Map<Facing, Pair<Integer, Integer>> faceOffset = new HashMap<>();
//    private final Map<Facing, Direction> faceToDir = new HashMap<>();
//
//    public UtilityScreen(UtilityContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
//        super(screenContainer, inv, titleIn);
//        ySize = 256;
//        xSize = 176;
//        Network.subscribe(this);
//    }
//
//    /**
//     * Adds our buttons amd initializes the rendered block
//     */
//    @Override protected void init() {
//        super.init();
//        this.renderedStack = new ItemStack(container.getTile().getBlockState().getBlock());
//        //West
//        addOffsets();
//        addFaces();
//        addIoButtons();
//        syncButtons();
//    }
//
//
//    /**
//     * This will add all of the the correct offsets
//     */
//    private void addOffsets() {
//        int centerOffset = 30;
//        int size = 18;
//        int height = 112;
//        int centerX = guiLeft + (this.xSize / 2) - (size / 2);
//        int centerY = guiTop + (height / 2) - (size / 2);
//        var leftPos = new Pair<>(centerX - (centerOffset), centerY);
//        this.faceOffset.put(Facing.LEFT, leftPos);
//        var rightPos = new Pair<>(centerX + centerOffset, centerY);
//        this.faceOffset.put(Facing.RIGHT, rightPos);
//        var topPos = new Pair<>(centerX, centerY - (centerOffset));
//        faceOffset.put(Facing.UP, topPos);
//        var bottomPos = new Pair<>(centerX, centerY + centerOffset);
//        faceOffset.put(Facing.DOWN, bottomPos);
//        var backPos = new Pair<>(centerX - centerOffset, centerY - centerOffset);
//        faceOffset.put(Facing.BACK, backPos);
//        var frontPos = new Pair<>(centerX - centerOffset, centerY + centerOffset);
//        faceOffset.put(Facing.FRONT, frontPos);
//        addButton(new TransferStateButton((centerX + centerOffset) + 4, (centerY + centerOffset) + 4, onTransferUpdate, (button, stack, mouseX, mouseY) -> {
//            TransferStateButton btn = (TransferStateButton) button;
//            func_243308_b(stack, Lists.newArrayList(new StringTextComponent(StringUtils.capitalize(btn.getState().name().toLowerCase()) + " mode")), mouseX, mouseY);
//        }));
//    }
//
//    /**
//     * This will correctly compute the {@link Facing} for each {@link Direction}
//     */
//    private void addFaces() {
//        var front = container.getWorld().getBlockState(container.getPos()).get(BlockStateProperties.FACING);
//        this.faceToDir.put(Facing.FRONT, front);
//        var back = front.getOpposite();
//        this.faceToDir.put(Facing.BACK, back);
//        this.faceToDir.put(Facing.UP, Direction.UP);
//        this.faceToDir.put(Facing.DOWN, Direction.DOWN);
//        switch (front) {
//            case NORTH -> {
//                this.faceToDir.put(Facing.LEFT, Direction.EAST);
//                this.faceToDir.put(Facing.RIGHT, Direction.WEST);
//            }
//            case SOUTH -> {
//                this.faceToDir.put(Facing.RIGHT, Direction.EAST);
//                this.faceToDir.put(Facing.LEFT, Direction.WEST);
//            }
//            case EAST -> {
//                this.faceToDir.put(Facing.LEFT, Direction.SOUTH);
//                this.faceToDir.put(Facing.RIGHT, Direction.NORTH);
//            }
//            case WEST -> {
//                this.faceToDir.put(Facing.LEFT, Direction.NORTH);
//                this.faceToDir.put(Facing.RIGHT, Direction.SOUTH);
//            }
//        }
//    }
//
//    /**
//     * This adds all of the io buttons.
//     */
//    private void addIoButtons() {
//        for (var face : faceOffset.keySet()) {
//            var offset = faceOffset.get(face);
//            var dir = faceToDir.get(face);
//            addButton(new IOStateButton(offset.getFirst(), offset.getSecond(), dir, onIOUpdate, (button, stack, mouseX, mouseY) -> func_243308_b(stack, getToolTip((IOStateButton) button), mouseX, mouseY)));
//        }
//
//    }
//
//    /**
//     * This will update the buttons locally tile data. It will normally only be used on startup
//     */
//    private void syncButtons() {
//        this.buttons.forEach(widget -> {
//            if (widget instanceof IOStateButton) {
//                var btn = (IOStateButton) widget;
//                var direction = btn.getDirection();
//                var transfer = btn.getTransferType();
//                var operation = container.getTile().getTransferData().getOperation(direction, transfer);
//                btn.setOperation(operation);
//            }
//        });
//    }
//
//
//    /**
//     * Called when the west button is clicked.
//     */
//    private Consumer<IOStateButton> onIOUpdate = button -> {
//        Network.sendToServer(new TransferUpdate(container.getPos(), button.getDirection(), button.getTransferType(), button.getOperation()));
//    };
//
//    /**
//     * Called when the transfer type is updated.
//     */
//    private Consumer<TransferMode> onTransferUpdate = newState -> {
//        //TODO synchronize the current operations with the tile entity.
//        buttons.forEach(btn -> {
//            if (btn instanceof IOStateButton) {
//                IOStateButton ioBtn = (IOStateButton) btn;
//                ioBtn.setActiveState(newState);
//            }
//        });
//        syncButtons();
//    };
//
//    /**
//     * Gets the Name for a give direction
//     */
//    private String getFace(Direction direction) {
//        var name = new AtomicReference<String>();
//        faceToDir.forEach((facing, direction1) -> {
//            if (direction1 == direction)
//                name.set(StringUtils.capitalize(facing.name().toLowerCase()));
//        });
//        return name.get();
//    }
//
//    /**
//     * l
//     * called when the user presses a mouse button. We use this to keep track of when the user
//     * wants to set a slot to none.
//     */
//    @Override public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
//        if (mouseButton == 1) {
//            this.buttons.stream()
//                    .filter(widget -> widget instanceof IOStateButton)
//                    .map(widget -> (IOStateButton) widget)
//                    .forEach(button -> {
//                        if (button.isHovered()) {
//                            button.setOperation(IOMode.NONE);
//                            onIOUpdate.accept(button);
//                        }
//                    });
//        }
//        return super.mouseReleased(mouseX, mouseY, mouseButton);
//    }
//
//    /**
//     * This will get the tool tip for the given button.
//     *
//     * @param button the button to generate for.
//     */
//    private List<ITextComponent> getToolTip(IOStateButton button) {
//        List<ITextComponent> list = new ArrayList<>();
//
//        list.add(new StringTextComponent(getFace(button.getDirection())));
//        var dir = button.getDirection();
//        var connections = container.getTile().getConnectionsFor(conn -> conn.getSide() == dir);
//        if (connections != null)
//            connections.forEach(connectionRef -> {
//                var connection = connectionRef.getAcquire();
//                if (button.getTransferType() == connection.getMode()) {
//                    var pos = connection.getLinkedPos();
//                    var name = container.getWorld().getBlockState(pos).getBlock().getTranslatedName().getString();
//                    list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.STRIKETHROUGH + "                                    "));
//                    list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Linked: " + TextFormatting.DARK_PURPLE + name));
//                    list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "Position: " + TextFormatting.DARK_PURPLE + "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"));
//                    list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "Transfer: " + TextFormatting.DARK_PURPLE + connection.getMode().name().toLowerCase()));
//                    list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "IOperation: " + TextFormatting.DARK_PURPLE + connection.getIo().name().toLowerCase()));
//                    list.add(new StringTextComponent(" - " + TextFormatting.DARK_GRAY + "Face: " + TextFormatting.DARK_PURPLE + connection.getLinkedSide().name().toLowerCase()));
//                }
//            });
//        return list;
//    }
//
//
//    /**
//     * This will draw the tile in the middle of the screen.
//     */
//    private void drawItemStack(MatrixStack matrixStack, ItemStack stack, int x, int y, String altText) {
//        RenderSystem.pushMatrix();
//        RenderSystem.scalef(2, 2, 2);
//        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
//        if (font == null) font = this.font;
//        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
//        this.itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, altText);
//        RenderSystem.popMatrix();
//    }
//
//    @Override
//    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground(matrixStack);
//        super.render(matrixStack, mouseX, mouseY, partialTicks);
//        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
//        drawString(matrixStack, Minecraft.getInstance().fontRenderer, "Utility Block", 10, 10, 0xffffff);
//        drawItemStack(null, renderedStack, (xSize / 4) - 8, (112 / 4) - 8, "");
//    }
//
//    /**
//     * This is for rendering the background of the container
//     */
//    @Override protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        this.minecraft.getTextureManager().bindTexture(GUI);
//        int relX = (this.width - this.xSize) / 2;
//        int relY = (this.height - this.ySize) / 2;
//        this.blit(matrixStack, relX, relY, 0, 0, this.xSize, this.ySize);
//    }
//
//    @Override public void onClose() {
//        super.onClose();
////        Network.unsubscribe(this);
//    }
//
//    private enum Facing {
//        UP, DOWN, FRONT, BACK, LEFT, RIGHT
//    }
//}
