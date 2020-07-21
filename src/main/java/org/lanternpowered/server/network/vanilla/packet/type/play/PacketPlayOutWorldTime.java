/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.vanilla.packet.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.type.MoonPhase;
import org.lanternpowered.server.network.packet.Packet;

public final class PacketPlayOutWorldTime implements Packet {

    private final long age;

    // The current moon phase
    private final MoonPhase moonPhase;

    // The time of the day
    private final int time;

    // Whether the sky should rotate
    private final boolean enabled;

    /**
     * Creates a new world time message.
     * @param moonPhase The moon phase of the world
     * @param age The age of the world
     * @param time The day time of the world
     * @param enabled Whether the time is enabled
     */
    public PacketPlayOutWorldTime(MoonPhase moonPhase, long age, int time, boolean enabled) {
        this.moonPhase = checkNotNull(moonPhase, "moonPhase");
        this.enabled = enabled;
        this.time = time;
        this.age = age;
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

    public long getAge() {
        return this.age;
    }
}
