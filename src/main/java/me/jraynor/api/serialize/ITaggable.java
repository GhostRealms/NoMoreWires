package me.jraynor.api.serialize;


import net.minecraft.nbt.CompoundNBT;

/**
 * This allows things to be written/read to/from
 */
public interface ITaggable {
    /**
     * This will allow for reading of things from a compound tag
     *
     * @param tag the mod tag
     */
    void read(CompoundNBT tag);

    /**
     * THis will write a mod tag
     *
     * @param tag the mod tag
     * @return the inputted mod tag
     */
    CompoundNBT write(CompoundNBT tag);

}
