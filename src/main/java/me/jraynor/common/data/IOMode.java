package me.jraynor.common.data;

/**
 * Stores the current state of the button.
 * This is also used for rendering
 */
public enum IOMode {
    EXTRACT(31, 0x8447ff), INSERT(49, 0xffb2e6), NONE(85, 0xec0627);
    public final int xGuiStart;
    public final int color;

    IOMode(int xGuiStart, int color) {
        this.xGuiStart = xGuiStart;
        this.color = color;
    }

    /**
     * @return the next state allowing for a circular loop
     */
    public IOMode next() {
        switch (this) {
            case NONE:
                return EXTRACT;
            case EXTRACT:
                return INSERT;
            case INSERT:
                return NONE;
        }
        return EXTRACT;
    }
}