package me.jraynor.common.data;

public enum TransferMode {
    ITEMS(0, 0x02c715), ENERGY(10, 0x1b5ef6), FLUID(20, 0xe9cf23);
    public final int xGuiStart;
    public final int color;

    TransferMode(int xGuiStart, int color) {
        this.xGuiStart = xGuiStart;
        this.color = color;
    }

    /**
     * @return the next state allowing for a circular loop
     */
    public TransferMode next() {
        return switch (this) {
            case ITEMS -> ENERGY;
            case ENERGY -> FLUID;
            case FLUID -> ITEMS;
        };
    }
}
