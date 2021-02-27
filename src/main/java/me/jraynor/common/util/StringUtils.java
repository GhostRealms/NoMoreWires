package me.jraynor.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Stores utilities that are used on both the server and client
 */
public final class StringUtils {
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * This will set the compound to clipboard
     *
     * @param compound the compound to set
     */
    @OnlyIn(Dist.CLIENT)
    public static void setCompoundToClipboard(CompoundNBT compound) {
        if (compound != null) {
            compound.remove("id");
            Minecraft.getInstance().keyboardListener.setClipboardString(compound.toString());
        }
    }

}
