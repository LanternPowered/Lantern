package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.world.MoonPhase;
import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutWorldTime implements Message {

    // The current moon phase
    private final MoonPhase moonPhase;

    // The time of the day
    private final int time;

    // Whether the sky should rotate
    private final boolean enabled;

    /**
     * Creates a new world time message.
     * 
     * @param moonPhase the moon phase of the world
     * @param time the time of the world
     * @param enabled whether the time is enabled
     */
    public MessagePlayOutWorldTime(MoonPhase moonPhase, int time, boolean enabled) {
        this.moonPhase = checkNotNull(moonPhase, "moonPhase");
        this.enabled = enabled;
        this.time = time;
    }

    /**
     * Gets the moon phase of the world.
     * 
     * @return the moon phase
     */
    public MoonPhase getMoonPhase() {
        return this.moonPhase;
    }

    /**
     * Gets the time of the world. Scales between 0 and 24000.
     * 
     * @return the time
     */
    public int getTime() {
        return this.time;
    }

    /**
     * Gets whether the time enabled is, this will freeze the sky animation.
     * 
     * @return enabled
     */
    public boolean getEnabled() {
        return this.enabled;
    }

}
