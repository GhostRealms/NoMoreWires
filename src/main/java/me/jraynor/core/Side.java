package me.jraynor.core;

import net.minecraftforge.fml.common.thread.SidedThreadGroups;

/**
 * This is used as a solid way of knowing whether or not we're on the client or server
 */
public enum Side {
    CLIENT, SERVER;

    /**
     * @return the opposite side
     */
    public Side opposite() {
        if (this == CLIENT)
            return SERVER;
        return CLIENT;
    }

    /**
     * This will get the current side based upon the current thread.
     *
     * @return the correct side
     */
    public static Side getThreadSide() {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            return Side.SERVER;
        return Side.CLIENT;
    }

}
