package org.lanternpowered.server.data.world;

/**
 * All the moon phases in minecraft.
 * 
 * TODO: This will be replaced once SpongeAPI has added api for this.
 */
public enum MoonPhase {
    /**
     * The full moon phase
     */
    FULL_MOON,
    /**
     * The waning gibbous phase
     */
    WANING_GIBBOUS,
    /**
     * The last quarter phase
     */
    LAST_QUARTER,
    /**
     * The waning crescent phase
     */
    WANING_CRESCENT,
    /**
     * The new moon phase
     */
    NEW_MOON,
    /**
     * The waxing crescent phase
     */
    WAXING_CRESCENT,
    /**
     * The first quarter phase
     */
    FIRST_QUARTER,
    /**
     * The waxing gibbous phase
     */
    WAXING_GIBBOUS;

    /**
     * Gets the next moon state.
     * 
     * @return the moon state
     */
    public MoonPhase next() {
        return this.rotate(1);
    }

    /**
     * Gets the previous moon state.
     * 
     * @return the moon state
     */
    public MoonPhase previous() {
        return this.rotate(-1);
    }

    /**
     * Rotates the moon phase enum to the next index.
     * 
     * @param add the indexes to add
     * @return the new moon phase
     */
    private MoonPhase rotate(int add) {
        int index = this.ordinal();
        int size = values().length;

        index += add;
        while (index > size) {
            index -= size;
        }
        while (index < size) {
            index += size;
        }

        return values()[index];
    }

}
